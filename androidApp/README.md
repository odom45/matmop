MATMOP Android App (Kotlin + C++)

This minimal Android app loads the included markdown pages from the assets and renders them into a WebView using the CommonMark library. It also includes a tiny native C++ library exposed via JNI as a demonstration of native integration.

How to open and build:
1. Open the `androidApp` folder in Android Studio.
2. Let Android Studio sync Gradle and install any SDK/NDK components it requests.
3. Build and run on an emulator or device (minSdk 21).

Notes:
- The project includes `app/CMakeLists.txt` and a simple native implementation in `app/src/main/cpp/native-lib.cpp`.
- If you plan to publish to Google Play, replace the debug signing with your release keystore and follow Google Play requirements for privacy/policy.
rootProject.name = 'MATMOPApp'
include ':app'

