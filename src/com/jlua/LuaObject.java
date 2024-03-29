package com.jlua;

import com.jlua.Lua;
import com.jlua.exceptions.LuaException;
import com.jlua.luainterop.JLuaApi;
import com.jlua.luainterop.LuaConstants;
import com.sun.jna.Pointer;

/*public final class LuaObject {
    private Lua lua;
    private int reference;

    public LuaObject(Lua lua, int stackIndex) {
        this.lua = lua;

        JLuaApi.lua53 lua53 = JLuaApi.getLua53Instance();
        lua53.lua_pushvalue(lua.getLuaState(), stackIndex);
        this.reference = lua53.luaL_ref(getParentLuaState(), LuaConstants.REGISTRY_INDEX);
    }

    @Override
    protected void finalize() {
        JLuaApi.getLua53Instance().luaL_unref(getParentLuaState(), LuaConstants.REGISTRY_INDEX, getReference());
    }

    public Lua getLua() {
        return lua;
    }

    public Pointer getParentLuaState() {
        return lua.getLuaState();
    }

    public int getReference() {
        return reference;
    }

    public Boolean isTable() {
        pushToStack();
        Boolean result = JLuaApi.getLua53Instance().lua_istable(getParentLuaState(), -1) == 1;
        JLuaApi.getLua53Instance().lua_pop(getParentLuaState(), 1);
        return result;
    }

    public Boolean isThread() {
        pushToStack();
        Boolean result = JLuaApi.getLua53Instance().lua_isthread(getParentLuaState(), -1) == 1;
        JLuaApi.getLua53Instance().lua_pop(getParentLuaState(), 1);
        return result;
    }

    public Boolean isUserdata() {
        pushToStack();
        Boolean result = JLuaApi.getLua53Instance().lua_isuserdata(getParentLuaState(), -1) == 1;
        JLuaApi.getLua53Instance().lua_pop(getParentLuaState(), 1);
        return result;
    }

    public Boolean isFunction() {
        pushToStack();
        Boolean result = JLuaApi.getLua53Instance().lua_isfunction(getParentLuaState(), -1) == 1;
        JLuaApi.getLua53Instance().lua_pop(getParentLuaState(), 1);
        return result;
    }

    public Object[] call(int numberOfResults, Object... args) throws LuaException {
        if (!isFunction() && !isThread()) {
            throw new LuaException("Cannot call an object that is not a Lua function or coroutine.");
        }

        pushToStack();
        if (args != null) {
            for (Object arg : args) {
                JLuaApi.pushObject(getParentLuaState(), arg);
            }
        }

        return lua.call(numberOfResults, args);
    }

    public Object[] call(Object... args) throws LuaException {
        return call(-1, args);
    }

    public void pushToStack() {
        pushToStack(getParentLuaState());
    }

    public void pushToStack(Pointer luaState) {
        JLuaApi.getLua53Instance().lua_rawgeti(luaState, LuaConstants.REGISTRY_INDEX, getReference());
    }
}*/
