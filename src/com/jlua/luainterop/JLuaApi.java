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

        void lua_setglobal(Pointer luaState, String name);

        void lua_pushnil(Pointer luaState);

        void lua_pushstring(Pointer luaState, byte[] bytes);

        void lua_pushboolean(Pointer luaState, int b);

        void lua_settop(Pointer luaState, int n);

        int lua_type(Pointer luaState, int index);

        int lua_toboolean(Pointer luaState, int index);
    }
}
