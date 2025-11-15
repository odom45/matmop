# Requires: Android SDK platform-tools (adb) in PATH and Android Studio-installed build tools
# Usage: Right-click > Run with PowerShell (or: pwsh -File .\run_preview.ps1)
# This script builds the debug app, installs it to a connected device/emulator, and launches it.

param(
    [switch]$NoBuild
)

$ErrorActionPreference = 'Stop'

function Write-Info($msg) { Write-Host "[INFO] $msg" -ForegroundColor Cyan }
function Write-Warn($msg) { Write-Host "[WARN] $msg" -ForegroundColor Yellow }
function Write-Err($msg)  { Write-Host "[ERROR] $msg" -ForegroundColor Red }

# Move to androidApp directory if script launched from repo root
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location (Split-Path -Parent $scriptDir) # androidApp

# Verify adb
try {
    $adbVersion = & adb version 2>$null
} catch {
    Write-Err "adb not found. Install Android Studio and ensure platform-tools are in PATH."
    Write-Host "Tips: In Android Studio > SDK Manager > Android SDK > SDK Tools: check 'Android SDK Platform-Tools'."
    exit 1
}

# Ensure a device/emulator is connected
$devices = & adb devices | Select-String -Pattern "\t(device|emulator)" | ForEach-Object { $_.ToString().Split("`t")[0] }
if (-not $devices -or $devices.Count -eq 0) {
    Write-Warn "No Android device/emulator detected. Start an emulator from Android Studio (AVD Manager) or plug in a device with USB debugging enabled."
    Write-Host "Once connected, re-run this script."
    exit 2
}

# Optionally build
if (-not $NoBuild) {
    Write-Info "Building and installing Debug variant..."
    $gradlew = Join-Path (Get-Location) 'gradlew.bat'
    if (-not (Test-Path $gradlew)) {
        Write-Err "gradlew.bat not found. Ensure you're in the androidApp directory."
        exit 3
    }
    & $gradlew :app:installDebug
}

# Launch the app's SplashActivity
$component = 'com.matmop.app/com.matmop.app.SplashActivity'
Write-Info "Launching $component on device: $($devices[0])"
& adb shell am start -n $component | Out-Null

Write-Host "Done. If the app didn't appear, check the device screen and logs: adb logcat -s SplashActivity MainActivity GalleryActivity PdfViewerActivity" -ForegroundColor Green
