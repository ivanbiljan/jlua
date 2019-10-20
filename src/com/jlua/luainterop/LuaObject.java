package com.jlua.luainterop;

import com.sun.jna.Pointer;

public final class LuaObject {
    private Pointer luaState;
    public Pointer getParentLuaState() {
        return luaState;
    }

    private int reference;
    public int getReference() {
        return reference;
    }

    public LuaObject(Pointer luaState, int stackIndex) {
        this.luaState = luaState;

        JLuaApi.lua53 lua53 = JLuaApi.lua53.INSTANCE;
        lua53.lua_pushvalue(luaState, stackIndex);
        this.reference = lua53.luaL_ref(luaState, LuaConstants.REGISTRY_GLOBALENV_INDEX);
    }

    @Override
    protected void finalize() {
        JLuaApi.lua53.INSTANCE.luaL_unref(getParentLuaState(), LuaConstants.REGISTRY_GLOBALENV_INDEX, getReference());
    }

    public void pushToStack(Pointer luaState) {
        JLuaApi.lua53.INSTANCE.lua_rawgeti(luaState, LuaConstants.REGISTRY_GLOBALENV_INDEX, getReference());
    }

    public void pushToStack() {
        pushToStack(getParentLuaState());
    }
}
