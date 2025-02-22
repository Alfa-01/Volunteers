package ru.sicampus.bootcamp2025.ui.mainscreen.allusers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import ru.sicampus.bootcamp2025.Const.BUNDLE_KEY
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.databinding.UnemployeedVolunteersFragmentBinding
import ru.sicampus.bootcamp2025.ui.mainscreen.centerinfo.UsersAdapter
import ru.sicampus.bootcamp2025.ui.mainscreen.linkVolunteerToCenter.LinkVolunteerBottomSheetFragment
import ru.sicampus.bootcamp2025.ui.utils.collectWithLifecycle
import ru.sicampus.bootcamp2025.ui.utils.visibleOrGone

class AllUsersFragment : Fragment(R.layout.unemployeed_volunteers_fragment) {
    private var _binding: UnemployeedVolunteersFragmentBinding? = null
    private val binding: UnemployeedVolunteersFragmentBinding get() = _binding!!

    private val viewModel: AllUsersViewModel by viewModels<AllUsersViewModel> { AllUsersViewModel.Factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = UnemployeedVolunteersFragmentBinding.bind(view)

        val freeAdapter = UsersAdapter()
        binding.contentFree.adapter = freeAdapter

        val pageAdapter = UserListAdapter { profileId: Int ->
            val fragment = LinkVolunteerBottomSheetFragment()
            val args = Bundle()
            args.putInt(BUNDLE_KEY, profileId)
            fragment.arguments = args
            fragment.show(requireActivity().supportFragmentManager, profileId.toString())
        }

        binding.contentAll.adapter = pageAdapter


        viewModel.state.collectWithLifecycle(this) { state ->
            binding.error.visibility = visibleOrGone(state is AllUsersViewModel.State.Error)

            when (state) {
                is AllUsersViewModel.State.Error -> binding.error.text = state.error
                AllUsersViewModel.State.Loading -> Unit
                is AllUsersViewModel.State.Show -> freeAdapter.submitList(state.content)
            }
        }

        viewModel.listState.collectWithLifecycle(this) { listState ->
            pageAdapter.submitData(listState)
        }

        pageAdapter.loadStateFlow.collectWithLifecycle(this) { data ->
            val state = data.refresh
            binding.error.visibility = visibleOrGone(state is LoadState.Error)

            if (state is LoadState.Error) {
                binding.error.text = state.error.toString()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}