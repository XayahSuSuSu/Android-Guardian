package com.xayah.guardian.activity.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.xayah.guardian.databinding.ActivityPictureListBinding

class PictureListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPictureListBinding
    private lateinit var viewModel: PictureListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityPictureListBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[PictureListViewModel::class.java]
        binding.viewModel = viewModel
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}