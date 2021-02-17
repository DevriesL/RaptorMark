package io.github.devriesl.raptormark.di

import android.content.res.Resources
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import javax.inject.Inject

class StringProvider @Inject constructor(private val resources: Resources) {

    @NonNull
    fun getString(@StringRes resId: Int): String {
        return resources.getString(resId)
    }

    @NonNull
    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return resources.getString(resId, *formatArgs)
    }
}
