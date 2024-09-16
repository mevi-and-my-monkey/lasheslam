package com.example.lasheslam

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.lasheslam.databinding.FragmentGenericDialogBinding

class GenericDialogFragment : DialogFragment() {

    private var type: Int? = null
    private var title: String? = null
    private var message: String? = null
    private var positiveButtonText: String? = null
    private var negativeButtonText: String? = null
    private var onPositiveClick: (() -> Unit)? = null
    private var onNegativeClick: (() -> Unit)? = null

    fun setType(type: Int): GenericDialogFragment {
        this.type = type
        return this
    }

    fun setTitle(title: String): GenericDialogFragment {
        this.title = title
        return this
    }

    fun setMessage(message: String): GenericDialogFragment {
        this.message = message
        return this
    }

    fun setPositiveButton(text: String, onClick: () -> Unit): GenericDialogFragment {
        this.positiveButtonText = text
        this.onPositiveClick = onClick
        return this
    }

    fun setNegativeButton(text: String, onClick: () -> Unit): GenericDialogFragment {
        this.negativeButtonText = text
        this.onNegativeClick = onClick
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = FragmentGenericDialogBinding.inflate(layoutInflater)

        if (type == 1){
            val colorBackError = ContextCompat.getColor(requireContext(), R.color.colorBackError)
            binding.layoutBack.setBackgroundColor(colorBackError)
            binding.icon.setImageResource(R.drawable.ic_error)
            binding.acceptButton.visibility = View.GONE
            binding.cancelButton.visibility = View.VISIBLE
            binding.cancelButton.text = negativeButtonText
        }
        binding.dialogTitle.text = title
        binding.dialogMessage.text = message

        val builder = AlertDialog.Builder(requireContext())
            .setView(binding.root)
        if (positiveButtonText != null) {
            binding.acceptButton.setOnClickListener{
                onPositiveClick?.invoke()
                dismiss()
            }
        }
        if (negativeButtonText != null) {
            binding.cancelButton.setOnClickListener {
                onNegativeClick?.invoke()
                dismiss()
            }
        }

        dialog?.setOnShowListener {
            val window = dialog?.window
            window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.85).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window?.setGravity(Gravity.CENTER)
        }

        val dialog = builder.create()

        return dialog
    }
}