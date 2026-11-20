# Derivative Calculator by Maan

Android Studio project based on the uploaded `derivative_calculator.c`.

## Features
- Polynomial derivative for degree 0–50.
- Coefficients entered from x^0 to x^n, comma-separated.
- Evaluates the derivative at a supplied x using native C (JNI).
- Standard functions: sin, cos, tan, e^x, ln, sqrt.
- Modern two-tab Android interface.

## Build APK
1. Install Android Studio (with Android SDK 35 and CMake/NDK components).
2. Open this folder (`DerivativeCalculatorByMaan`) in Android Studio.
3. Allow Gradle sync and install any prompted SDK/NDK components.
4. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

Package: `com.maan.derivativecalculator`
Minimum Android: 6.0 (API 23)

The original console input/output was adapted to a touch interface; the derivative calculations are implemented in the included C native library.
