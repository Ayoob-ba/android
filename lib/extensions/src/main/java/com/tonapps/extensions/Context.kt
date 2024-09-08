package com.tonapps.extensions

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RawRes
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.io.File
import java.security.spec.AlgorithmParameterSpec
import java.util.Locale

val isUIThread: Boolean
    get() = Thread.currentThread() == android.os.Looper.getMainLooper().thread

val Context.locale: Locale
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0)
        } else {
            resources.configuration.locale
        }
    }

val Context.isDebug: Boolean
    get() = 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE

fun Context.cacheFolder(name: String): File {
    return cacheDir.folder(name)
}

fun Context.raw(@RawRes id: Int): ByteArray {
    return resources.openRawResource(id).readBytes()
}

fun Context.rawText(@RawRes id: Int): String {
    return raw(id).toString(Charsets.UTF_8)
}

val Context.packageInfo: PackageInfo
    get() = packageManager.getPackageInfo(packageName, 0)

val Context.appVersionName: String
    get() = packageInfo.versionName

val Context.appVersionCode: Long
    get() = packageInfo.versionCodeCompat

val Context.isMainVersion: Boolean
    get() = packageInfo.packageName == "com.ton_keeper"

fun Context.prefs(name: String): SharedPreferences {
    return getSharedPreferences(name, Context.MODE_PRIVATE)
}

val Context.activity: ComponentActivity?
    get() {
        var context = this
        while (context is ContextWrapper) {
            if (context is ComponentActivity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }


fun Context.logError(e: Throwable) {
    //
}

