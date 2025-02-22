package ru.sicampus.bootcamp2025.ui.mainscreen.centerinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import ru.sicampus.bootcamp2025.Const.BUNDLE_KEY
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.databinding.ViewCenterInfoFragmentBinding
import ru.sicampus.bootcamp2025.ui.utils.collectWithLifecycle
import ru.sicampus.bootcamp2025.ui.utils.visibleOrGone

class CenterInfoFragment : BottomSheetDialogFragment(R.layout.center_item) {
    private var _binding: ViewCenterInfoFragmentBinding? = null
    private val binding: ViewCenterInfoFragmentBinding get() = _binding!!

    private val viewModel: CenterInfoViewModel by viewModels<CenterInfoViewModel> { CenterInfoViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewCenterInfoFragmentBinding.inflate(inflater)
        val centerId: Int = requireArguments().getInt(BUNDLE_KEY)
        viewModel.setCenterId(centerId)

        val tagsAdapter = TagsAdapter()
        binding.tagsList.adapter = tagsAdapter

        val usersAdapter = UsersAdapter()
        binding.activeVolunteers.adapter = usersAdapter

        viewModel.state.collectWithLifecycle(this) { state ->
            binding.error.visibility = visibleOrGone(state is CenterInfoViewModel.State.Error)
            binding.content.visibility = visibleOrGone(state is CenterInfoViewModel.State.Show)
            when (state) {
                is CenterInfoViewModel.State.Error -> binding.error.text = state.text
                CenterInfoViewModel.State.Loading -> Unit
                is CenterInfoViewModel.State.Show -> {
                    val centerInfo = state.centerItem.first
                    binding.name.text = centerInfo.name
                    binding.type.text = centerInfo.type
                    binding.description.text = centerInfo.description
                    binding.address.text = centerInfo.address
                    binding.email.text = centerInfo.email
                    binding.link.text = centerInfo.link
                    tagsAdapter.submitList(centerInfo.tags)
                    usersAdapter.submitList(state.centerItem.second)
                    Picasso.get().load(centerInfo.imageUrl).into(binding.image)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}