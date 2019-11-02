package com.jlua.luainterop;

import com.jlua.LuaObject;
import com.jlua.exceptions.LuaException;
import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import org.jetbrains.annotations.Contract;
import sun.misc.Unsafe;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public final class JLuaApi {
    static {
        System.loadLibrary("jluanative");
    }

    public static  void pushObject(Pointer luaState, Object object) {
        JLuaApi.lua53 lua53 = JLuaApi.lua53.INSTANCE;
        if (object == null) {
            lua53.lua_pushnil(luaState);
        }
        else if (object instanceof Boolean) {
            lua53.lua_pushboolean(luaState, (Boolean) object == true ? 1 : 0);
        }
        else if (object instanceof Byte || object instanceof Short || object instanceof Integer || object instanceof Long) {
            lua53.lua_pushinteger(luaState, (Long) object);
        }
        else if (object instanceof Float) {
            lua53.lua_pushnumber(luaState, (Float) object);
        }
        else if (object instanceof String) {
            JLuaApi.pushLuaString(luaState, (String) object);
        }
        else {
            JLuaApi.pushUserdata(luaState, object);
        }
    }

    public static Object getObject(Pointer pointer, int stackIndex) throws LuaException {
        JLuaApi.lua53 lua53 = JLuaApi.lua53.INSTANCE;
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
                throw new LuaException("Userdata is not supported");
            case LUA_TTHREAD:
                throw new LuaException("Threads are not supported");
        }

        return null;
    }

    public static String getLuaString(Pointer luaState, int stackIndex) {
        IntByReference size = new IntByReference();
        Pointer stringPointer = lua53.INSTANCE.lua_tolstring(luaState, stackIndex, size);
        byte[] bytes = new byte[size.getValue()];
        stringPointer.read(0, bytes, 0, bytes.length);
        return Native.toString(bytes);
    }

    public static void pushLuaString(Pointer luaState, String string) {
        byte[] encodedBytes = getEncodedString(string);
        lua53.INSTANCE.lua_pushlstring(luaState, encodedBytes, encodedBytes.length);
    }

    public static native void pushUserdata(Pointer luaState, Object object);

    private static Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (sun.misc.Unsafe) field.get(null);
    }

    private static byte[] getEncodedString(String string) {
        return StandardCharsets.UTF_8.encode(string).array();
    }

    // TODO: Figure this out since Delegates are not a thing in Java...
    public interface lua_CFunction {
        int Invoke(Pointer luaState);
    }

    public interface lua53 extends Library {
        lua53 INSTANCE = Native.load("lua53", lua53.class);

        IntByReference luaL_newstate();

        void luaL_openlibs(Pointer luaState);

        void lua_close(Pointer luaState);

        int lua_getglobal(Pointer luaState, String name);

        int lua_gettop(Pointer luaState);

        default void lua_pop(Pointer luaState, int n) {
            lua_settop(luaState, -n - 1);
        }

        void lua_pushboolean(Pointer luaState, int b);

        void lua_pushinteger(Pointer luaState, long n);

        void lua_pushnil(Pointer luaState);

        void lua_pushnumber(Pointer luaState, float n);

        void lua_pushlstring(Pointer luaState, byte[] bytes, int size);

        void lua_setglobal(Pointer luaState, String name);

        void lua_settop(Pointer luaState, int n);

        int lua_toboolean(Pointer luaState, int index);

        long lua_tointegerx(Pointer luaState, int index, IntByReference isNum);

        Pointer lua_tolstring(Pointer luaState, int index, IntByReference size);

        float lua_tonumberx(Pointer luaState, int index, IntByReference isNum);

        int lua_isinteger(Pointer luaState, int index);

        int lua_type(Pointer luaState, int index);

        int luaL_loadstring(Pointer luaState, String s);

        int lua_pcallk(Pointer luaState, int nargs, int nresults, int msgh, IntByReference ctx, IntByReference k);

        void lua_pushvalue(Pointer luaState, int index);

        int luaL_ref(Pointer luaState, int t);

        int luaL_unref(Pointer luaState, int t, int ref);

        int lua_rawgeti(Pointer luaState, int t, long n);

        int lua_istable(Pointer luaState, int n);

        int lua_isuserdata(Pointer luaState, int n);

        int lua_isthread(Pointer luaState, int n);

        int lua_isfunction(Pointer luaState, int n);

        IntByReference lua_newuserdata(Pointer luaState, long size);
    }
}
