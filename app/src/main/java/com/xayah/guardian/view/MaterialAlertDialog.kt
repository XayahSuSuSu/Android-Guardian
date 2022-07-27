package com.xayah.guardian.view

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xayah.guardian.R
import com.xayah.guardian.databinding.ViewDialogTextFieldBinding
import com.xayah.guardian.util.GlobalString
import com.xayah.guardian.util.getActivity

fun MaterialAlertDialogBuilder.setWithEdit(
    context: Context,
    title: String,
    defString: String,
    callback: (content: String) -> Unit
) {
    context.getActivity()?.apply {
        val binding =
            ViewDialogTextFieldBinding.inflate(this.layoutInflater, null, false)
        binding.textLayout.hint = title
        binding.textField.setText(defString)
        this@setWithEdit.apply {
            setTitle(title)
            setView(binding.root)
            setCancelable(true)
            setPositiveButton(GlobalString.confirm) { _, _ ->
                callback(binding.textField.text.toString())
            }
            setNegativeButton(GlobalString.cancel) { _, _ -> }
            show()
        }
    }
}

fun MaterialAlertDialogBuilder.setWithConfirm(
    message: String,
    cancelable: Boolean = true,
    hasNegativeBtn: Boolean = true,
    callback: () -> Unit
) {
    this.apply {
        setTitle(context.getString(R.string.tips))
        setCancelable(cancelable)
        setMessage(message)
        if (hasNegativeBtn)
            setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }
        setPositiveButton(context.getString(R.string.confirm)) { _, _ -> callback() }
        show()
    }
}
