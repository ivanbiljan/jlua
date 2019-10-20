package com.jlua;

import com.jlua.exceptions.LuaException;
import com.jlua.luainterop.JLuaApi;
import com.jlua.luainterop.LuaType;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

public final class Lua {
    private IntByReference luaState;

    public Lua(Boolean openLuaLibs) {
        // In order to obtain the architecture we will rely on JNA's kernel32 implementation as os.arch is not feasible
        final String executingDirectory = System.getProperty("user.dir");
        final String architecture = getSystemArchitecture();
        final String lua53Path = Paths.get(executingDirectory, architecture).toAbsolutePath().toString();
        System.setProperty("jna.library.path", lua53Path);

        JLuaApi.lua53 lua53 = JLuaApi.lua53.INSTANCE;
        this.luaState = lua53.luaL_newstate();
        if (openLuaLibs) {
            lua53.luaL_openlibs(luaState.getPointer());
        }
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

    @Override
    protected void finalize() {
        JLuaApi.lua53.INSTANCE.lua_close(luaState.getPointer());
    }

    public Pointer getLuaState() {
        return luaState.getPointer();
    }

    public void setGlobal(@NotNull String name, Object value) {
        assert name != null : "name must not be null";
        pushObject(value);
        JLuaApi.lua53.INSTANCE.lua_setglobal(getLuaState(), name);
        JLuaApi.lua53.INSTANCE.lua_pop(getLuaState(), 1);
    }

    public Object getGlobal(@NotNull String name) throws LuaException {
        assert name != null : "name must not be null";
        JLuaApi.lua53.INSTANCE.lua_getglobal(getLuaState(), name);
        return getObject(-1);
    }
    
    private void pushObject(Object object) {
        JLuaApi.lua53 lua53 = JLuaApi.lua53.INSTANCE;
        Pointer pointer = getLuaState();
        if (object == null) {
            lua53.lua_pushnil(pointer);
        }
        if (object instanceof Boolean) {
            lua53.lua_pushboolean(pointer, (Boolean) object == true ? 1 : 0);
        }
        if (object instanceof Byte || object instanceof Short || object instanceof Integer || object instanceof Long) {
            lua53.lua_pushinteger(pointer, (Long) object);
        }
        if (object instanceof Float) {
            lua53.lua_pushnumber(pointer, (Float) object);
        }
        if (object instanceof String) {
            JLuaApi.pushLuaString(pointer, (String) object);
        }
    }

    private Object getObject(int stackIndex) throws LuaException {
        JLuaApi.lua53 lua53 = JLuaApi.lua53.INSTANCE;
        Pointer pointer = getLuaState();
        LuaType luaType = LuaType.values()[lua53.lua_type(pointer, stackIndex)];
        switch (luaType) {

            case LUA_TNIL:
                return null;
            case LUA_TBOOLEAN:
                return lua53.lua_toboolean(pointer, stackIndex) == 1;
            case LUA_TLIGHTUSERDATA:
                throw new LuaException("Light Userdata is not supported");
            case LUA_TNUMBER:
                return lua53.lua_isinteger(pointer, stackIndex) == 1
                        ? lua53.lua_tointegerx(pointer, stackIndex, null)
                        : lua53.lua_tonumberx(pointer, stackIndex, null);
            case LUA_TSTRING:
                return JLuaApi.getLuaString(pointer, stackIndex);
            case LUA_TTABLE:
                throw new LuaException("Tables are not supported");
            case LUA_TFUNCTION:
                throw new LuaException("Functions are not supported");
            case LUA_TUSERDATA:
                throw new LuaException("Userdata is not supported");
            case LUA_TTHREAD:
                throw new LuaException("Threads are not supported");
        }

        return null;
    }
}
