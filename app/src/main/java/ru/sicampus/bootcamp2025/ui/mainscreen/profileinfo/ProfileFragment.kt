package ru.sicampus.bootcamp2025.ui.mainscreen.profileinfo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.squareup.picasso.Picasso
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.databinding.ViewProfileFragmentBinding
import ru.sicampus.bootcamp2025.domain.entities.ProfileEntity
import ru.sicampus.bootcamp2025.ui.entry.EntryActivity
import ru.sicampus.bootcamp2025.ui.utils.collectWithLifecycle
import ru.sicampus.bootcamp2025.ui.utils.getText
import ru.sicampus.bootcamp2025.ui.utils.visibleOrGone

class ProfileFragment : Fragment(R.layout.view_profile_fragment) {

    private var _binding: ViewProfileFragmentBinding? = null
    private val binding: ViewProfileFragmentBinding get() = _binding!!

    private var _currentProfile: ProfileEntity? = null
    private val currentProfile: ProfileEntity get() = _currentProfile!!

    private val viewModel: ProfileViewModel by viewModels<ProfileViewModel> { ProfileViewModel.Factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = ViewProfileFragmentBinding.bind(view)

        binding.refresh.setOnRefreshListener { viewModel.onRefresh() }

        viewModel.state.collectWithLifecycle(this) { state ->
            binding.error.visibility = visibleOrGone(state is ProfileViewModel.State.Error)
            binding.content.visibility = visibleOrGone(state is ProfileViewModel.State.Show)
            binding.refresh.isRefreshing = state is ProfileViewModel.State.Loading

            when (state) {
                is ProfileViewModel.State.Error -> binding.error.text = state.text
                ProfileViewModel.State.Loading -> Unit
                is ProfileViewModel.State.Show -> {
                    binding.refresh.isRefreshing = false
                    _currentProfile = state.profile
                    if (currentProfile.photoUrl != null)
                        Picasso.get().load(currentProfile.photoUrl).into(binding.profilePicture)
                    binding.currentCenter.text =
                        "${currentProfile.centerId ?: getText(
                                R.string.free_volunteer,
                                requireContext()
                            )}"
                    binding.name.setText(currentProfile.name)
                    binding.lastname.setText(currentProfile.lastname)
                    binding.phone.setText(currentProfile.phoneNumber)
                    binding.email.setText(currentProfile.email)
                }

                is ProfileViewModel.State.Logout -> {
                    startActivity(Intent(this.context, EntryActivity::class.java))
                    requireActivity().finish()
                }
            }
        }

        binding.logout.setOnClickListener {
            viewModel.onLogout()
        }

        binding.save.setOnClickListener {
            viewModel.onSaveChanges(
                newProfile = ProfileEntity(
                    id = currentProfile.id,
                    centerId = currentProfile.centerId,
                    name = binding.name.text.toString(),
                    lastname = binding.lastname.text.toString(),
                    photoUrl = currentProfile.photoUrl,
                    phoneNumber = binding.phone.text.toString(),
                    email = binding.email.text.toString()
                )
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}