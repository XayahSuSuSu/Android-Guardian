package com.xayah.guardian.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drakeet.multitype.ItemViewDelegate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xayah.guardian.App
import com.xayah.guardian.data.Picture
import com.xayah.guardian.databinding.AdapterPictureListBinding
import com.xayah.guardian.util.GlobalString
import com.xayah.guardian.util.Server
import com.xayah.guardian.util.readServerAddress
import com.xayah.guardian.util.setLoading
import com.xayah.guardian.view.setWithConfirm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class PictureListAdapter(val callback: () -> Unit) :
    ItemViewDelegate<Picture, PictureListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(
            AdapterPictureListBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Picture) {
        val binding = holder.binding
        binding.textViewName.text = item.name
        Glide.with(binding.root.context)
            .load("${App.globalContext.readServerAddress()}/${item.path}")
            .into(binding.imageView)
        binding.materialButtonDelete.setOnClickListener {
            MaterialAlertDialogBuilder(it.context).apply {
                setWithConfirm(
                    GlobalString.confirmDelete,
                    cancelable = true,
                    hasNegativeBtn = true
                ) {
                    MaterialAlertDialogBuilder(context).apply {
                        BottomSheetDialog(context).apply {
                            setLoading()
                            CoroutineScope(Dispatchers.IO).launch {
                                Server.picturesDelete(item.path) {
                                    dismiss()
                                    CoroutineScope(Dispatchers.Main).launch {
                                        callback()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    class ViewHolder(val binding: AdapterPictureListBinding) :
        RecyclerView.ViewHolder(binding.root)
}