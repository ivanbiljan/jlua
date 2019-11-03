package com.jlua.luainterop;

import com.jlua.Lua;
import com.jlua.exceptions.LuaException;
import com.jlua.helpers.ReflectionHelper;
import jnr.ffi.Pointer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public final class JavaObjectHandler {
    private Lua lua;
    private final HashMap<String, JLuaApi.lua_CFunction> metamethods = new HashMap<>();

    @Contract(pure = true)
    public JavaObjectHandler(@NotNull Lua lua) {
        assert lua != null : "lua must not be null";
        this.lua = lua;

        metamethods.put("__index", this::indexObject);

        JLuaApi.lua53 lua53 = JLuaApi.getLua53Instance();
        if (lua53.lua_getfield(lua.getLuaState(), LuaConstants.REGISTRY_INDEX, "jlua_object") == LuaType.LUA_TNIL.getValue()) {
            lua53.lua_pop(lua.getLuaState(), 1); // Pop the 'nil' value
            lua53.lua_createtable(lua.getLuaState(), 0, 0);
        }

        for (var entry : metamethods.entrySet()) {
            JLuaApi.pushLuaString(lua.getLuaState(), entry.getKey());
            lua53.lua_pushcfunction(lua.getLuaState(), entry.getValue());
            lua53.lua_settable(lua.getLuaState(), -3);
        }

        lua53.lua_pop(lua.getLuaState(), 1); // Pop the table
    }

    private int gcObject(Pointer luaState) throws LuaException {
        Object obj = JLuaApi.getObject(luaState, 1);
        JLuaApi.releaseObject(obj);
        return 0;
    }

    private int indexObject(Pointer luaState) throws LuaException, IllegalAccessException {
        Object obj = JLuaApi.getObject(luaState, 1);
        if (obj == null) {
            throw new LuaException("obj must not be null");
        }

        String key = (String) JLuaApi.getObject(luaState, 2);
        Field field = ReflectionHelper.getFieldSafe(obj, key);
        if (field != null) {
            JLuaApi.pushObject(luaState, field.get(obj));
            return 1;
        }

        Method method = ReflectionHelper.getMethodSafe(obj, key);
        if (method == null) {
            return 0;
        }

        JLuaApi.lua53 lua53 = JLuaApi.getLua53Instance();
        lua53.lua_pushvalue(luaState, 1);
        lua53.lua_pushvalue(luaState, 2);
        lua53.lua_pushcclosure(luaState, ptr -> callMethodCallback(ptr), 2);

        return 0;
    }

    private int callMethodCallback(Pointer luaState) {
        return 0;
    }
}
