package com.example.lasheslam.core

import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class GralCtrlEditText {
    fun setDataEditText(
        layout: TextInputLayout,
        editText: TextInputEditText,
        hint: String,
        text: String? = null,
        isEnable: Boolean? = true
    ) {
        layout.hint = hint
        editText.doOnTextChanged { _, _, _, _ ->
            layout.error = null
        }
        if (text != null) {
            editText.setText(text)
        }

        if (isEnable != null) {
            layout.isEnabled = isEnable
            editText.isEnabled = isEnable
        }
    }

    fun validateEditText(
        layout: TextInputLayout,
        editText: TextInputEditText,
        textError: String, value: Boolean
    ): Boolean {
        val validationRetur: Boolean
        if (editText.text.isNullOrEmpty()) {
            layout.error = textError
            validationRetur = false
        } else {
            validationRetur = value
        }
        return validationRetur
    }
}