package com.jlua.luainterop;

import com.jlua.Lua;
import com.sun.jna.Pointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class JavaObjectHandler {
    private Lua lua;

    private final HashMap<String, JLuaApi.lua_CFunction> _metamethods =
            new HashMap<String, JLuaApi.lua_CFunction>(Map.of(
                    "__index", new JLuaApi.lua_CFunction() {
                        @Override
                        public int Invoke(Pointer luaState) {
                            return Index(luaState);
                        }
                    },
                    "__call", new JLuaApi.lua_CFunction() {
                        @Override
                        public int Invoke(Pointer luaState) {
                            return Call(luaState);
                        }
                    }
            ));

    public JavaObjectHandler(@NotNull Lua lua) {
        assert lua != null : "lua must not be null";
        this.lua = lua;
    }

    private int Index(Pointer luaState) {
        return 0;
    }

    private int Call(Pointer luaState) {
        return 0;
    }
}
