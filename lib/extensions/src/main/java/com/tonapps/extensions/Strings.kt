package com.tonapps.extensions

import android.graphics.Color
import com.tonapps.icu.Punycode

val String.short12: String
    get() {
        if (length < 24) return this
        return substring(0, 12) + "…" + substring(length - 12, length)
    }

val String.short8: String
    get() {
        if (length < 16) return this
        return substring(0, 8) + "…" + substring(length - 8, length)
    }

val String.short6: String
    get() {
        if (length < 12) return this
        return substring(0, 6) + "…" + substring(length - 6, length)
    }

val String.short4: String
    get() {
        if (length < 8) return this
        return substring(0, 4) + "…" + substring(length - 4, length)
    }

val String.max12: String
    get() {
        if (length < 12) return this
        return substring(0, 12) + "…"
    }

val String.max18: String
    get() {
        if (length < 18) return this
        return substring(0, 18) + "…"
    }

val String.max24: String
    get() {
        if (length < 24) return this
        return substring(0, 24) + "…"
    }

fun String.ifPunycodeToUnicode(): String {
    return if (startsWith(Punycode.PREFIX_STRING)) {
        Punycode.decodeSafe(this)
    } else {
        this
    }
}

fun String.unicodeToPunycode(): String {
    return try {
        Punycode.encode(this) ?: throw IllegalArgumentException("Invalid punycode")
    } catch (e: Exception) {
        this
    }
}

val String.color: Int
    get() {
        return try {
            if (startsWith("#")) {
                Color.parseColor(this)
            } else {
                Color.parseColor("#$this")
            }
        } catch (e: Exception) {
            0
        }
    }
