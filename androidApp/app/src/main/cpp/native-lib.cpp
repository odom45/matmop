#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_matmop_app_NativeLib_stringFromJNI(JNIEnv* env, jobject /* this */) {
    std::string hello = "MATMOP Native Layer v1.0 - Built with C++";
    return env->NewStringUTF(hello.c_str());
}

