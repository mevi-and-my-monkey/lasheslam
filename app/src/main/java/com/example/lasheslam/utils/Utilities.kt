package com.example.lasheslam.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowInsetsCompat

class Utilities {
    companion object{
        fun View.setOnClickListenerCloseUnfocus(
            context: Context,
            view: View,
            function: () -> Unit
        ) {
            setOnClickListener {
                closeKeyboard(context, view)
                function.invoke()
            }
        }

        fun closeKeyboard(context: Context, view: View, clarFocus: Boolean? = true) {
            try {
                val isShown = WindowInsetsCompat
                    .toWindowInsetsCompat(view.rootWindowInsets)
                    .isVisible(WindowInsetsCompat.Type.ime())
                if (isShown) {
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                if (clarFocus != false) {
                    view.clearFocus()
                }
            } catch (_: Exception) {
            }
        }


    }
}