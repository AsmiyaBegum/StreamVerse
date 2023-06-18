package com.ab.streamverse

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.ab.streamverse.databinding.ActivityMainBinding
import com.ab.streamverse.streamversepreview.PreviewFragment
import com.ab.streamverse.util.IOrientationChanged

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val orientation = newConfig.orientation
        val fragmentManager = supportFragmentManager // or childFragmentManager if you're inside a fragment
        val navHostFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController
        val currentDestinationId = navController.currentDestination?.id
        val currentFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)?.childFragmentManager?.fragments?.get(0)
        if(currentDestinationId == R.id.SecondFragment) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.bottomNavigationView.visibility = View.GONE
            } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
            (currentFragment as PreviewFragment).onOrientationChanged(orientation)
        }
    }

}