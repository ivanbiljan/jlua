package com.jlua.luainterop;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public final class JLuaApi {
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

        void lua_pushstring(Pointer luaState, byte[] bytes);

        void lua_setglobal(Pointer luaState, String name);

        void lua_settop(Pointer luaState, int n);

        int lua_toboolean(Pointer luaState, int index);

        long lua_tointegerx(Pointer luaState, int index, Pointer isNum);

        String lua_tolstring(Pointer luaState, int index, Pointer size);

        float lua_tonumberx(Pointer luaState, int index, Pointer isNum);

        int lua_type(Pointer luaState, int index);
    }
}
