package com.example.skga.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.skga.R
import com.example.skga.databinding.ActivityMainMenuBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import data.local.UserSessionManager
import kotlinx.coroutines.launch

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userSessionManager = UserSessionManager(this)
        lifecycleScope.launch {
            userSessionManager.userProfile.collect {
                val myName = it?.firstName?:"Ошибка"
                val name: TextView = findViewById(R.id.mainMenuFullNameText)
                name.text = myName
            }
        }
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _,destination, _ ->
            if (destination.id == R.id.userProfileFragment){
                binding.mainMenuFullNameText.visibility = View.GONE
                binding.mainMenuProfileImage.visibility = View.GONE
            }else{
                binding.mainMenuFullNameText.visibility = View.VISIBLE
                binding.mainMenuProfileImage.visibility = View.VISIBLE
            }
        }
    }

    companion object{

        fun newIntent(context: Context): Intent{
            val intent = Intent(context , MainMenuActivity::class.java)
            return intent
        }
    }
}