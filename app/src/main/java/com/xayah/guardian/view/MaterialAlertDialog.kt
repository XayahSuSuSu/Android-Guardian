package com.xayah.guardian.view

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xayah.design.R
import com.xayah.guardian.databinding.ViewDialogTextFieldBinding
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
            setPositiveButton(context.getString(R.string.dialog_positive)) { _, _ ->
                callback(binding.textField.text.toString())
            }
            setNegativeButton(context.getString(R.string.dialog_negative)) { _, _ -> }
            show()
        }
    }
}
