package com.ab.streamverse

import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ab.streamverse.databinding.ActivityMainBinding
import com.ab.streamverse.streamversepreview.PreviewFragment
import com.ab.streamverse.util.Constants
import com.ab.streamverse.util.Utils.showVisibility

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleBottomNavigation()
        preventScreenSharing()
    }

    private fun preventScreenSharing() {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    private fun handleBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            return@setOnNavigationItemSelectedListener when (menuItem.itemId) {
                R.id.home -> {
                    navController.navigate(R.id.StreamVerseHomeFragment)
                    true
                }
                R.id.comingSoon,R.id.downloads,R.id.more,R.id.search -> {
                    val bundle = Bundle()
                    bundle.putString(Constants.MENU_NAME, menuItem.itemId.toString())
                    navController.navigate(R.id.OtherMenuFragment,bundle)
                    true
                }
                else -> false
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val orientation = newConfig.orientation
        val currentDestinationId = getCurrentDestinationId()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)?.childFragmentManager?.fragments?.get(0)
        if(currentDestinationId == R.id.PreviewFragment) {
            (currentFragment as PreviewFragment).onOrientationChanged(orientation)
        }
    }

    private fun getCurrentDestinationId() : Int? {
        val fragmentManager = supportFragmentManager // or childFragmentManager if you're inside a fragment
        val navHostFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.currentDestination?.id
    }

    fun showBottomNavigation(show : Boolean){
        binding.bottomNavigationView.showVisibility(show)
    }

}