package ru.sicampus.bootcamp2025.ui.entry

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import ru.sicampus.bootcamp2025.Const.TOKEN_KEY
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.data.sources.locale.CredentialsLocalDataSource
import ru.sicampus.bootcamp2025.databinding.EnterActivityBinding
import ru.sicampus.bootcamp2025.ui.mainscreen.MainActivity
import ru.sicampus.bootcamp2025.ui.utils.collectWithLifecycle
import ru.sicampus.bootcamp2025.ui.utils.isOnline


class EntryActivity : AppCompatActivity(R.layout.enter_activity) {
    private var _binding: EnterActivityBinding? = null
    private val binding: EnterActivityBinding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels<AuthViewModel> {
        CredentialsLocalDataSource.buildSource(
            getSharedPreferences(
                TOKEN_KEY,
                Context.MODE_PRIVATE
            )
        )
        AuthViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = EnterActivityBinding.inflate(layoutInflater)


        val navHostFragment = binding.content.getFragment<NavHostFragment>()
        val navController = navHostFragment.navController

        if (!isOnline(this)) {
            val dialog = NoInternetBottomSheetFragment()
            dialog.isCancelable = false
            NoInternetBottomSheetFragment().show(supportFragmentManager, "NO_INTERNET")
        }


        viewModel.action.collectWithLifecycle(this) { action ->
            when (action) {
                is AuthViewModel.Action.OpenApp -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                is AuthViewModel.Action.GoToLogin -> {
                    navController.navigate(R.id.login_fragment)
                }

                is AuthViewModel.Action.GotToSignUp ->
                    navController.navigate(R.id.signup_fragment)

            }
        }

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}