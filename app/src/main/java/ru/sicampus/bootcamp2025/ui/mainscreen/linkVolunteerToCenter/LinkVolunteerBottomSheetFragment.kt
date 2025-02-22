package ru.sicampus.bootcamp2025.ui.mainscreen.linkVolunteerToCenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.sicampus.bootcamp2025.Const.BUNDLE_KEY
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.databinding.LinkVolunteerBottomSheetBinding
import ru.sicampus.bootcamp2025.ui.utils.collectWithLifecycle
import ru.sicampus.bootcamp2025.ui.utils.visibleOrGone

class LinkVolunteerBottomSheetFragment : BottomSheetDialogFragment(R.layout.link_volunteer_bottom_sheet) {

    private var _binding: LinkVolunteerBottomSheetBinding? = null
    private val binding: LinkVolunteerBottomSheetBinding get() = _binding!!

    private var _profileId: Int? = null
    private val profileId: Int get() =  _profileId!!

    private val viewModel: LinkVolunteerViewModel by viewModels<LinkVolunteerViewModel> { LinkVolunteerViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LinkVolunteerBottomSheetBinding.inflate(layoutInflater)

        _profileId = requireArguments().getInt(BUNDLE_KEY)
        viewModel.setProfileId(profileId)

        val adapter = CenterListAdapter {centerId: Int ->
            viewModel.onLink(centerId)
        }
        binding.content.adapter = adapter

        viewModel.listState.collectWithLifecycle(this) { listState ->
            adapter.submitData(listState)
        }
        adapter.loadStateFlow.collectWithLifecycle(this) { data ->
            val state = data.refresh
            binding.error.visibility = visibleOrGone(state is LoadState.Error)

            if (state is LoadState.Error) {
                binding.error.text = state.error.toString()
            }
        }

        viewModel.state.collectWithLifecycle(this) { state ->
            binding.error.visibility = visibleOrGone(state is LinkVolunteerViewModel.State.Error)

            when (state) {
                is LinkVolunteerViewModel.State.Error -> binding.error.text = state.text
                is LinkVolunteerViewModel.State.Loading -> Unit
                is LinkVolunteerViewModel.State.Success -> dismiss()
            }
        }

        return binding.root
    }
}