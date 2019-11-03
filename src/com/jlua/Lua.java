package com.jlua;

import com.jlua.exceptions.LuaException;
import com.jlua.luainterop.JLuaApi;
import com.jlua.luainterop.LuaConstants;
import com.sun.jna.ptr.IntByReference;
import jnr.ffi.Pointer;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

public final class Lua {
    private Pointer luaState;

    public Lua(Boolean openLuaLibs) {
        JLuaApi.lua53 lua53 = JLuaApi.getLua53Instance();
        this.luaState = lua53.luaL_newstate();
        if (openLuaLibs) {
            lua53.luaL_openlibs(luaState);
        }
    }

    @Override
    protected void finalize() {
        JLuaApi.getLua53Instance().lua_close(luaState);
    }

    public Pointer getLuaState() {
        return luaState;
    }

    public void setGlobal(@NotNull String name, Object value) {
        assert name != null : "name must not be null";
        JLuaApi.pushObject(getLuaState(), value);
        JLuaApi.getLua53Instance().lua_setglobal(getLuaState(), name);
        JLuaApi.getLua53Instance().lua_pop(getLuaState(), 1);
    }

    public Object getGlobal(@NotNull String name) throws LuaException {
        assert name != null : "name must not be null";
        JLuaApi.getLua53Instance().lua_getglobal(getLuaState(), name);
        return JLuaApi.getObject(getLuaState(), -1);
    }

    public Object[] doString(@NotNull String chunk, int numberOfResults, Object... args) throws LuaException {
        assert chunk != null : "chunk must not be null";
        JLuaApi.getLua53Instance().luaL_loadstring(getLuaState(), chunk);
        return call(numberOfResults, args);
    }

    protected Object[] call(int numberOfResults, Object... args) throws LuaException {
        JLuaApi.lua53 lua53 = JLuaApi.getLua53Instance();
        Pointer pointer = getLuaState();
        int oldStackTop = lua53.lua_gettop(pointer) - 1; // The function we're calling is at the top of the stack
        if (args != null) {
            for (Object arg : args){
                JLuaApi.pushObject(pointer, arg);
            }
        }

        int nargs = args == null ? 0 : args.length;
        if (lua53.lua_pcallk(pointer, nargs, -1, 0, null, null) != 0) {
            // If the invocation fails Lua will push an error message
            String errorMessage = (String) JLuaApi.getObject(getLuaState(), 1);
            lua53.lua_pop(pointer, 1);
            throw new LuaException(errorMessage);
        }

        Object[] objs = new Object[lua53.lua_gettop(pointer) - oldStackTop];
        for (int i = oldStackTop + 1; i <= lua53.lua_gettop(pointer); ++i) {
            objs[i - oldStackTop - 1] = JLuaApi.getObject(pointer, i);
        }

        return objs;
    }

    public Object[] doString(@NotNull String chunk, Object... args) throws LuaException {
        assert chunk != null : "chunk must not be null";
        return doString(chunk, LuaConstants.LUA_MULTRET, args);
    }

    /*public LuaObject createFunction(@NotNull String functionBody) throws LuaException {
        assert functionBody != null : "functionBody must not be null";

        JLuaApi.getLua53Instance().luaL_loadstring(getLuaState(), functionBody);
        LuaObject function = (LuaObject) JLuaApi.getObject(getLuaState(), -1);
        JLuaApi.getLua53Instance().lua_pop(getLuaState(), 1);
        return function;
    }*/
}
