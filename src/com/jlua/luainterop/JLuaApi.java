package com.jlua.luainterop;
import com.jlua.exceptions.LuaException;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Pointer;
import jnr.ffi.Struct;
import jnr.ffi.StructLayout;
import jnr.ffi.annotations.Delegate;
import jnr.ffi.types.size_t;
import org.jetbrains.annotations.Contract;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;

// TODO:
//      Get rid of the JNA dependency

public final class JLuaApi {
    private static lua53 lua53Instance;

    private final static HashMap<Pointer, Object> pointerToObject = new HashMap<>();
    private static byte[] getEncodedString(String string) {
        return StandardCharsets.UTF_8.encode(string).array();
    }

    public static lua53 getLua53Instance() {
        if (lua53Instance == null) {
            // In order to obtain the architecture we will rely on JNA's kernel32 implementation as os.arch is not feasible
            final String executingDirectory = System.getProperty("user.dir");
            final String architecture = getSystemArchitecture();
            final String lua53Path = Paths.get(executingDirectory, "targets", architecture).toAbsolutePath().toString();
            lua53Instance = LibraryLoader.create(lua53.class).search(lua53Path).load("lua53");
        }

        return lua53Instance;
    }

    public static String getLuaString(Pointer luaState, int stackIndex) {
        Pointer stringPointer = getLua53Instance().lua_tolstring(luaState, stackIndex, null);
        return stringPointer.getString(0, (int) stringPointer.size(), Charset.defaultCharset());
    }

    public static Object getObject(Pointer pointer, int stackIndex) throws LuaException {
        JLuaApi.lua53 lua53 = getLua53Instance();
        LuaType luaType = LuaType.values()[lua53.lua_type(pointer, stackIndex)];
        switch (luaType) {
            case LUA_TNIL:
                return null;
            case LUA_TBOOLEAN:
                return lua53.lua_toboolean(pointer, stackIndex) == 1;
            case LUA_TLIGHTUSERDATA:
                throw new LuaException("Light Userdata is not supported");
            case LUA_TNUMBER:
                return lua53.lua_isinteger(pointer, stackIndex) > 0
                        ? lua53.lua_tointegerx(pointer, stackIndex, null)
                        : lua53.lua_tonumberx(pointer, stackIndex, null);
            case LUA_TSTRING:
                return JLuaApi.getLuaString(pointer, stackIndex);
            case LUA_TTABLE:
                throw new LuaException("Tables are not supported");
            case LUA_TFUNCTION:
                //return new LuaObject(this, stackIndex);
            case LUA_TUSERDATA:
                return getJavaObjectFromPointer(pointer, lua53.lua_topointer(pointer, stackIndex));
            case LUA_TTHREAD:
                throw new LuaException("Threads are not supported");
        }

        return null;
    }

    @Contract(pure = true)
    private static String getSystemArchitecture() {
        // If we are running on a 32bit system there is no way we are running an x64 application
        String environment = System.getenv("ProgramW6432");
        if (environment == null || environment.length() == 0) {
            return "x86";
        }

        // There's still a possibility of running WOW64, though
        Kernel32 kernel32 = Kernel32.INSTANCE;
        WinNT.HANDLE handle = kernel32.GetCurrentProcess();
        IntByReference pointer = new IntByReference();
        kernel32.IsWow64Process(handle, pointer);
        return pointer.getValue() != 0 ? "x86" : "x64";
    }

    private static Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (sun.misc.Unsafe) field.get(null);
    }

    public static void pushLuaString(Pointer luaState, String string) {
        byte[] encodedBytes = getEncodedString(string);
        getLua53Instance().lua_pushlstring(luaState, encodedBytes, encodedBytes.length);
    }

    public static void pushObject(Pointer luaState, Object object) {
        JLuaApi.lua53 lua53 = getLua53Instance();
        if (object == null) {
            lua53.lua_pushnil(luaState);
        } else if (object instanceof Boolean) {
            lua53.lua_pushboolean(luaState, (Boolean) object == true ? 1 : 0);
        } else if (object instanceof Byte || object instanceof Short || object instanceof Integer || object instanceof Long) {
            lua53.lua_pushinteger(luaState, (Long) object);
        } else if (object instanceof Float) {
            lua53.lua_pushnumber(luaState, (Float) object);
        } else if (object instanceof String) {
            JLuaApi.pushLuaString(luaState, (String) object);
        } else {
            JLuaApi.pushUserdata(luaState, object);
        }
    }

    public static void pushUserdata(jnr.ffi.Pointer luaState, Object object) {
        lua53 lua53 = getLua53Instance();
        Pointer ptr = lua53.lua_newuserdata(luaState, getSystemArchitecture() == "x86" ? 4 : 8);
        lua53.lua_getfield(luaState, LuaConstants.REGISTRY_INDEX, "jlua_object");
        lua53.lua_setmetatable(luaState, -2);
        pointerToObject.put(ptr, object);
    }

    private static Object getJavaObjectFromPointer(Pointer luaState, Pointer userdataPointer) {
        return pointerToObject.getOrDefault(userdataPointer, null);
    }

    public static void releaseObject(Object object) {
        pointerToObject.remove(object);
    }

    public interface lua_CFunction {
        @Delegate
        int Invoke(Pointer luaState) throws LuaException, IllegalAccessException;
    }

    // See: https://www.lua.org/manual/5.3/manual.html
    public interface lua53 {
        int luaL_loadstring(Pointer luaState, String s);

        Pointer luaL_newstate();

        void luaL_openlibs(Pointer luaState);

        int luaL_ref(Pointer luaState, int t);

        int luaL_unref(Pointer luaState, int t, int ref);

        void lua_close(Pointer luaState);

        int lua_getglobal(Pointer luaState, String name);

        int lua_gettop(Pointer luaState);

        int lua_isfunction(Pointer luaState, int n);

        int lua_isinteger(Pointer luaState, int index);

        int lua_istable(Pointer luaState, int n);

        int lua_isthread(Pointer luaState, int n);

        int lua_isuserdata(Pointer luaState, int n);

        int lua_pcallk(Pointer luaState, int nargs, int nresults, int msgh, Pointer ctx, Pointer k);

        default void lua_pop(Pointer luaState, int n) {
            lua_settop(luaState, -n - 1);
        }

        default void lua_pushlstring(Pointer luaState, String string) {

        }

        void lua_pushboolean(Pointer luaState, int b);

        void lua_pushcclosure(Pointer luaState, lua_CFunction lua_cFunction, int n);

        void lua_pushinteger(Pointer luaState, long n);

        void lua_pushlstring(Pointer luaState, byte[] bytes, int size);

        void lua_pushnil(Pointer luaState);

        void lua_pushnumber(Pointer luaState, float n);

        void lua_pushvalue(Pointer luaState, int index);

        int lua_rawgeti(Pointer luaState, int t, long n);

        void lua_setglobal(Pointer luaState, String name);

        void lua_settop(Pointer luaState, int n);

        int lua_toboolean(Pointer luaState, int index);

        long lua_tointegerx(Pointer luaState, int index, Pointer isNum);

        Pointer lua_tolstring(Pointer luaState, int index, Pointer size);

        float lua_tonumberx(Pointer luaState, int index, Pointer isNum);

        int lua_type(Pointer luaState, int index);

        jnr.ffi.Pointer lua_newuserdata(jnr.ffi.Pointer luaState, long size);

        int lua_getfield(Pointer luaState, int index, String k);

        void lua_setmetatable(Pointer luaState, int index);

        Pointer lua_topointer(Pointer luaState, int index);

        void lua_createtable(Pointer luaState, int narr, int nrec);

        void lua_pushcfunction(Pointer luaState, lua_CFunction lua_cFunction);

        void lua_settable(Pointer luaState, int index);
    }
}
