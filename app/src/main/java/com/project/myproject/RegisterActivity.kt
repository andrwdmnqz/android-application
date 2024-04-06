package com.project.myproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.project.myproject.databinding.RegisterActivityBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var viewBinding: RegisterActivityBinding

    override fun onCreate(savedInitialState: Bundle?) {
        super.onCreate(savedInitialState)
        viewBinding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.navHostFragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}