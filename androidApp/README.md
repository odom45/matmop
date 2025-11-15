MATMOP Android App (Kotlin + C++)

This minimal Android app loads the included markdown pages from the assets and renders them into a WebView using the CommonMark library. It also includes a tiny native C++ library exposed via JNI as a demonstration of native integration.

How to open and build:
1. Open the `androidApp` folder in Android Studio.
2. Let Android Studio sync Gradle and install any SDK/NDK components it requests.
3. Build and run on an emulator or device (minSdk 21).

Quick preview (Windows):
- Right-click `androidApp\run_preview.ps1` > Run with PowerShell.
  - It will: check adb, ensure a device/emulator is connected, build and install Debug, and launch the Splash screen.
  - Prereqs: Android Studio installed; Android SDK platform-tools (adb) in PATH. Start an emulator from AVD Manager or connect a device with USB debugging enabled.
- Alternative (manual):
  - Open Android Studio > Run ▶ to deploy Debug on the selected emulator/device.

Build a release App Bundle (AAB) locally:
- Unsigned (for testing):
  - In Android Studio: Build > Build Bundle(s)/APK(s) > Build Bundle(s).
  - Or from terminal in `androidApp`: `./gradlew :app:bundleRelease` (macOS/Linux) or ` .\gradlew :app:bundleRelease` (Windows PowerShell).
  - Output: `androidApp/app/build/outputs/bundle/release/app-release.aab`.
- Signed (Play upload):
  1) Enable Play App Signing in Play Console (recommended).
  2) Create an upload keystore (keep it private):
     - Windows PowerShell example:
       `keytool -genkeypair -v -keystore C:\\path\\to\\your-upload.keystore -alias upload -keyalg RSA -keysize 2048 -validity 36500`
  3) In Android Studio: Build > Generate Signed Bundle / APK… > Android App Bundle.
  4) Choose the keystore and provide alias/passwords. Android Studio will produce a signed AAB suitable for upload.
  5) Alternatively, set these properties locally in `androidApp/gradle.properties` (do NOT commit real secrets):
     RELEASE_STORE_FILE=\\path\\to\\your-upload.keystore
     RELEASE_STORE_PASSWORD=***
     RELEASE_KEY_ALIAS=upload
     RELEASE_KEY_PASSWORD=***
     Then run `:app:bundleRelease` to produce a signed AAB.

CI build (GitHub Actions):
- A workflow is provided at `.github/workflows/android_build_aab.yml` triggered on pushes to `main` or via manual dispatch. It uploads `app-release.aab` as an artifact.
- By default this artifact is unsigned. To enable signing in CI, add repository secrets for the keystore and wire them as Gradle properties.

Notes:
- The project includes `app/CMakeLists.txt` and a simple native implementation in `app/src/main/cpp/native-lib.cpp`.
- Target/compile SDK 34; Play-ready manifest includes `android:exported` and adaptive icons.

