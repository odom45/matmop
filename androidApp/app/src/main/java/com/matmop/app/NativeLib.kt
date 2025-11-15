package com.matmop.app

object NativeLib {
    init { System.loadLibrary("native-lib") }
    external fun stringFromJNI(): String
}

