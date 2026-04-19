package com.example.skga.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.skga.R
import com.example.skga.presentation.adminPage.AdminPageActivity
import data.local.UserSessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WelcomeScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        userSession()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun userSession() {
        val sessionManager = UserSessionManager(this)
        lifecycleScope.launch {
            val profile = sessionManager.userProfile.first()

            if (profile != null) {
                if (profile.role == "admin" && profile.isAdmin) {
                    startActivity(AdminPageActivity.newIntent(this@WelcomeScreenActivity))
                }
                if (profile.role == "student") {
                    startActivity(
                        MainMenuActivity.newIntent(
                            this@WelcomeScreenActivity
                        )
                    )
                }
            }
        }
    }

    companion object{
        fun newIntent(context: Context): Intent{
            val intent = Intent(context , WelcomeScreenActivity::class.java)
            return intent
        }
    }
}
