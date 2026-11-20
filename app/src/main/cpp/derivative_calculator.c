#include <jni.h>
#include <math.h>

JNIEXPORT jdouble JNICALL
Java_com_maan_derivativecalculator_MainActivity_polynomialDerivative(
        JNIEnv *env, jobject thiz, jdoubleArray coefficients, jdouble x) {
    jsize n = (*env)->GetArrayLength(env, coefficients);
    if (n <= 1) return 0.0;
    jdouble *c = (*env)->GetDoubleArrayElements(env, coefficients, 0);
    double result = 0.0;
    /* Horner evaluation of derivative coefficients: d[i-1] = i*c[i]. */
    for (jsize i = n - 1; i >= 1; --i) {
        result = result * x + ((double)i * c[i]);
    }
    (*env)->ReleaseDoubleArrayElements(env, coefficients, c, JNI_ABORT);
    return result;
}

JNIEXPORT jdouble JNICALL
Java_com_maan_derivativecalculator_MainActivity_standardDerivative(
        JNIEnv *env, jobject thiz, jint choice, jdouble x) {
    switch (choice) {
        case 1: return cos(x);
        case 2: return -sin(x);
        case 3: return 1.0 / (cos(x) * cos(x));
        case 4: return exp(x);
        case 5: return 1.0 / x;
        case 6: return 0.5 / sqrt(x);
        default: return NAN;
    }
}
