package com.xayah.guardian.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xayah.guardian.databinding.FragmentHomeBinding
import com.xayah.guardian.util.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        binding.viewModel = viewModel

        val flag = ObservableBoolean(true)
        binding.buttonUp.setOnTouchListener { v, event ->
            v.performClick()
            CoroutineScope(Dispatchers.IO).launch {
                flag.set(true)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        while (flag.get()) {
                            Server.action("up") {}
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        flag.set(false)
                        Server.action("cancel") {}
                    }
                }
            }
            false
        }
        binding.buttonLeft.setOnTouchListener { v, event ->
            v.performClick()
            CoroutineScope(Dispatchers.IO).launch {
                flag.set(true)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        while (flag.get()) {
                            Server.action("left") {}
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        flag.set(false)
                        Server.action("cancel") {}
                    }
                }
            }
            false
        }
        binding.buttonDown.setOnTouchListener { v, event ->
            v.performClick()
            CoroutineScope(Dispatchers.IO).launch {
                flag.set(true)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        while (flag.get()) {
                            Server.action("down") {}
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        flag.set(false)
                        Server.action("cancel") {}
                    }
                }
            }
            false
        }
        binding.buttonRight.setOnTouchListener { v, event ->
            v.performClick()
            CoroutineScope(Dispatchers.IO).launch {
                flag.set(true)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        while (flag.get()) {
                            Server.action("right") {}
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        flag.set(false)
                        Server.action("cancel") {}
                    }
                }
            }
            false
        }

        viewModel.initialize(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}