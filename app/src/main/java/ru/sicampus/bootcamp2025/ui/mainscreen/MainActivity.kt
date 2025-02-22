package ru.sicampus.bootcamp2025.ui.mainscreen

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.ui.utils.collectWithLifecycle
import ru.sicampus.bootcamp2025.ui.utils.visibleOrGone

class MainActivity : AppCompatActivity() {

    private lateinit var bottomBar: BottomNavigationView
    private lateinit var adminBottomBar: BottomNavigationView
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var error: TextView
    private lateinit var content: FragmentContainerView

    private val viewModel: AppConfigViewModel by viewModels<AppConfigViewModel> { AppConfigViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        bottomBar = findViewById(R.id.bottom_bar)
        adminBottomBar = findViewById(R.id.admin_bottom_bar)
        content = findViewById(R.id.main_nav_host_fragment)
        navHostFragment = content.getFragment() as NavHostFragment
        navController = navHostFragment.navController
        error = findViewById(R.id.error)

        viewModel.state.collectWithLifecycle(this) { state ->
            error.visibility = visibleOrGone(state is AppConfigViewModel.State.Error)
            content.visibility = visibleOrGone(state is AppConfigViewModel.State.Answer)

            when (state) {
                is AppConfigViewModel.State.Answer -> {
                    if (state.answer) {
                        navController.setGraph(R.navigation.admin_navgraph)
                        adminBottomBar.visibility = View.VISIBLE
                        bottomBar.visibility = View.GONE
                        adminBottomBar.setupWithNavController(navController)
                    } else {
                        navController.setGraph(R.navigation.main_screen_navgraph)
                        adminBottomBar.visibility = View.GONE
                        bottomBar.visibility = View.VISIBLE
                        bottomBar.setupWithNavController(navController)
                    }
                }
                is AppConfigViewModel.State.Error -> {
                    error.text = state.error
                }
                AppConfigViewModel.State.Loading -> Unit
            }
        }
    }

}