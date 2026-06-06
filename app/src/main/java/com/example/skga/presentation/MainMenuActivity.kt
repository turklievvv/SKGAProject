package com.example.skga.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.messaging.FirebaseMessaging
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
                subscribeStudent(it?.facultyId ?: 2, it?.group ?: "ПМИ-221")
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

    fun subscribeStudent(faculty: Int, group: String) {
        val safeFacultyToken = "faculty_${formatTopicName(faculty.toString())}"
        val safeGroupToken = "group_${formatTopicName(group)}"

        FirebaseMessaging.getInstance().subscribeToTopic(safeFacultyToken)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM_DEBUG","Успешная подписка на факультетский топик: $safeFacultyToken")
                }
                if (!task.isSuccessful) {
                    Log.d("FCM_DEBUG", "Не удалось получить FCM-токен", task.exception)
                    return@addOnCompleteListener
                }
            }

        // 2. Подписываем на группу
        FirebaseMessaging.getInstance().subscribeToTopic(safeGroupToken)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Успешная подписка на групповой топик: $safeGroupToken")
                }
            }
    }

    fun formatTopicName(name: String): String {
        return name.lowercase()
            .replace(" ", "_")
            .replace("-", "_")
    }

    companion object{

        fun newIntent(context: Context): Intent{
            val intent = Intent(context , MainMenuActivity::class.java)
            return intent
        }
    }
}