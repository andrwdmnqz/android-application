package com.project.myproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.project.myproject.databinding.ActivityMainBinding
import com.project.myproject.fragments.ViewPagerFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInitialState: Bundle?) {
        super.onCreate(savedInitialState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.navHostFragmentContainerView)
        findNavController(R.id.navHostFragmentContainerView).setGraph(R.navigation.nav_graph, intent.extras)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}