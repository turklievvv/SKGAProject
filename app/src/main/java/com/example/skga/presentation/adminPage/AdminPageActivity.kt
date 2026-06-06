package com.example.skga.presentation.adminPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.skga.R
import com.example.skga.presentation.WelcomeScreenActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import data.local.UserSessionManager
import kotlinx.coroutines.launch

class AdminPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val profileBtn = findViewById<CardView>(R.id.topBar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerAdmin) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.setupWithNavController(navController)
        val sessionManager = UserSessionManager(this)
        profileBtn.setOnClickListener {
            lifecycleScope.launch {
                sessionManager.clearSession()
                val intent = WelcomeScreenActivity.newIntent(this@AdminPageActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
    companion object{
        fun newIntent(context: Context): Intent{
            val intent = Intent(context , AdminPageActivity::class.java)
            return intent
        }
    }
}