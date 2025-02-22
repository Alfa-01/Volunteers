package ru.sicampus.bootcamp2025.ui.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.databinding.NoInternetBottomSheetBinding
import ru.sicampus.bootcamp2025.ui.utils.isOnline

class NoInternetBottomSheetFragment : BottomSheetDialogFragment(R.layout.no_internet_bottom_sheet) {

    private var _binding: NoInternetBottomSheetBinding? = null
    private val binding: NoInternetBottomSheetBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NoInternetBottomSheetBinding.inflate(layoutInflater)
        binding.refresh.setOnClickListener {
            if (isOnline(requireContext())) {
                dismissNow()
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}