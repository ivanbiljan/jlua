package com.jlua.luainterop;

import com.jlua.Lua;
import com.jlua.exceptions.LuaException;
import com.sun.jna.Pointer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class JavaObjectHandler {
    private Lua lua;

    @Contract(pure = true)
    public JavaObjectHandler(@NotNull Lua lua) {
        assert lua != null : "lua must not be null";
        this.lua = lua;
    }

    private int Index(Pointer luaState) throws LuaException {
        Object obj = JLuaApi.getObject(luaState, 1);
        String key = (String) JLuaApi.getObject(luaState, 2);


        return 0;
    }

    private int CallMethod(Pointer luaState) {
        return 0;
    }
}
