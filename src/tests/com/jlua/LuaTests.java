package tests.com.jlua;

import com.jlua.Lua;
import com.jlua.LuaObject;
import com.jlua.exceptions.LuaException;
import com.sun.jna.PointerType;
import org.junit.Assert;
import org.junit.Test;

public final class LuaTests {
    @Test
    public void test() throws LuaException {
        Lua lua = new Lua(true);
        LuaObject function = lua.createFunction("return 'Hello, World!'");
        Assert.assertEquals("Hello, World!", function.call(null)[0]);
    }
}
