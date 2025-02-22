package ru.sicampus.bootcamp2025.ui.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.databinding.SplashFragmentBinding

class SplashFragment: Fragment(R.layout.splash_fragment) {

    private var _binding: SplashFragmentBinding? = null
    private val binding: SplashFragmentBinding get() =_binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SplashFragmentBinding.inflate(layoutInflater)
        return binding.root
    }
}