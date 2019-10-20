package com.jlua.luainterop;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import org.jetbrains.annotations.Contract;

import javax.sound.sampled.AudioFormat;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public final class JLuaApi {
    public static String getLuaString(Pointer luaState, int stackIndex) {
        IntByReference size = new IntByReference();
        Pointer stringPointer = lua53.INSTANCE.lua_tolstring(luaState, stackIndex, size);
        byte[] bytes = new byte[size.getValue()];
        stringPointer.read(0, bytes, 0, bytes.length);
        return Native.toString(bytes);
    }

    public static void pushLuaString(Pointer luaState, String string) {
        byte[] encodedBytes = getEncodedString(string);
        lua53.INSTANCE.lua_pushlstring(luaState, encodedBytes, encodedBytes.length);
    }

    private static byte[] getEncodedString(String string) {
        return StandardCharsets.UTF_8.encode(string).array();
    }

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

        void lua_pushlstring(Pointer luaState, byte[] bytes, int size);

        void lua_setglobal(Pointer luaState, String name);

        void lua_settop(Pointer luaState, int n);

        int lua_toboolean(Pointer luaState, int index);

        long lua_tointegerx(Pointer luaState, int index, IntByReference isNum);

        Pointer lua_tolstring(Pointer luaState, int index, IntByReference size);

        float lua_tonumberx(Pointer luaState, int index, IntByReference isNum);

        int lua_isinteger(Pointer luaState, int index);

        int lua_type(Pointer luaState, int index);
    }
}
