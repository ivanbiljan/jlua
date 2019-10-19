package com.jlua.luainterop;

public enum LuaType {
    LUA_TNIL(-1), LUA_TBOOLEAN(0), LUA_TLIGHTUSERDATA(1), LUA_TNUMBER(2), LUA_TSTRING(3),
    LUA_TTABLE(4), LUA_TFUNCTION(5), LUA_TUSERDATA(6), LUA_TTHREAD(7);

    private final int value;

    LuaType(int value) {
        this.value = value;
    }

    private int getValue() {
        return value;
    }
}
