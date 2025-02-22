package ru.sicampus.bootcamp2025.ui.entry.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.databinding.SignupFragmentBinding
import ru.sicampus.bootcamp2025.ui.mainscreen.MainActivity
import ru.sicampus.bootcamp2025.ui.utils.collectWithLifecycle
import ru.sicampus.bootcamp2025.ui.utils.getText
import ru.sicampus.bootcamp2025.ui.utils.visibleOrGone

class SignUpFragment : Fragment(R.layout.signup_fragment) {

    private var _binding: SignupFragmentBinding? = null
    private val binding: SignupFragmentBinding get() = _binding!!

    private var _navController: NavController? = null
    private val navController: NavController get() = _navController!!


    private val viewModel: SignUpViewModel by viewModels<SignUpViewModel> { SignUpViewModel.Factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = SignupFragmentBinding.bind(view)
        _navController = findNavController()

        binding.repeatPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.wrapRepeatPassword.error = getText(R.string.password_does_not_matches, requireContext())
                binding.wrapRepeatPassword.isErrorEnabled =
                    binding.repeatPassword.text.toString() != binding.password.text.toString()
            }
        })

        viewModel.state.collectWithLifecycle(this) { state ->
            binding.error.visibility = visibleOrGone(state is SignUpViewModel.State.Error)
            binding.login.isEnabled = state !is SignUpViewModel.State.Loading
            binding.password.isEnabled = state !is SignUpViewModel.State.Loading
            binding.repeatPassword.isEnabled = state !is SignUpViewModel.State.Loading
            when (state) {
                is SignUpViewModel.State.Error -> {
                    binding.error.text = state.errorMessage
                }
                is SignUpViewModel.State.Loading -> Unit
                is SignUpViewModel.State.Waiting -> Unit
            }
        }

        viewModel.action.collectWithLifecycle(this) { action ->
            when (action) {
                SignUpViewModel.Action.OpenApp -> startActivity(
                    Intent(activity, MainActivity::class.java)
                )

                SignUpViewModel.Action.GoToLogin -> toLogin()
            }
        }

        binding.toLogin.setOnClickListener { toLogin() }

        binding.process.setOnClickListener {
            viewModel.onProcessClick(
                binding.login.text.toString(),
                binding.password.text.toString(),
                binding.name.text.toString(),
                binding.lastname.text.toString()
            )
        }
    }

    private fun toLogin() {
        navController.navigate(R.id.login_fragment)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}