package com.example.skga.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.skga.R
import com.example.skga.presentation.adminPage.AdminPageActivity
import com.google.firebase.messaging.FirebaseMessaging
import data.local.UserSessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WelcomeScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main) // ← перенесли до userSession()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userSession()
    }

    private fun userSession() {
        val sessionManager = UserSessionManager(this)
        lifecycleScope.launch {
            val profile = sessionManager.userProfile.first()
            if (profile != null) {
                subscribeToFcmTopics(profile) // выносим FCM в отдельный метод

                when {
                    profile.role == "admin" && profile.isAdmin -> {
                        startActivity(AdminPageActivity.newIntent(this@WelcomeScreenActivity))
                    }
                    profile.role == "student" -> {
                        startActivity(MainMenuActivity.newIntent(this@WelcomeScreenActivity))
                    }
                    profile.role == "teacher" -> {
                        startActivity(MainMenuActivity.newIntent(this@WelcomeScreenActivity))
                    }
                    else -> {
                        Toast.makeText(
                            this@WelcomeScreenActivity,
                            "Неизвестная роль пользователя",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                }
                finish()
            }
        }
    }

    private fun subscribeToFcmTopics(profile: domain.entity.UserProfile) {
        FirebaseMessaging.getInstance().subscribeToTopic("all_institute")

        when {
            profile.role == "admin" && profile.isAdmin -> {
                FirebaseMessaging.getInstance().subscribeToTopic("all_institute")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d("FCM", "Подписка на весь институт")
                    }
            }
            profile.role == "student" || profile.role == "teacher" -> {
                FirebaseMessaging.getInstance()
                    .subscribeToTopic("faculty_${profile.facultyId}")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d("FCM", "Подписка на факультет ${profile.facultyId}")
                    }
                FirebaseMessaging.getInstance()
                    .subscribeToTopic("group_${profile.group}")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d("FCM", "Подписка на группу ${profile.group}")
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
