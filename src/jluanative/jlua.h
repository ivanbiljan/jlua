#include <stdio.h>
#include <jni.h>
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_com_jlua_luainterop_JLuaApi_pushUserdata(JNIEnv *env, jobject thisObject, jobject luaStatePtr, jobject obj);

#ifdef __cplusplus
}
#endif
#endif