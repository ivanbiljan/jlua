package tests.com.jlua;

import com.jlua.Lua;
import com.jlua.LuaObject;
import com.jlua.exceptions.LuaException;
import com.jlua.luainterop.JLuaApi;
import com.sun.jna.PointerType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public final class LuaTests {
    @Test
    public void test() throws LuaException, IllegalAccessException, NoSuchFieldException, IOException {
        Lua lua = new Lua(true);
        JLuaApi.pushUserdata(lua.getLuaState(), new String("Hello"));
        LuaObject function = lua.createFunction("return 'Hello, World!'");
        Assert.assertEquals("Hello, World!", function.call(null)[0]);
    }
}
