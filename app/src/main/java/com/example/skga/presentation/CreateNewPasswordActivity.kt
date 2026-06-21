package com.example.skga.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.skga.R
import com.example.skga.databinding.ActivityCreateNewPasswordBinding

class CreateNewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateNewPasswordBinding
    private val viewModel: CreateNewPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

// Получаем токен из deep link
        val uri = intent.data
        android.util.Log.e("AUTH_DEBUG", "ПОЛНЫЙ URL ИЗ ПИСЬМА: -> $uri")
        var accessToken: String? = null
        var refreshToken: String? = null

        uri?.let {
            // Supabase присылает данные через хэш '#' (фрагмент).
            // Пример: skga://reset-password#access_token=xxx&refresh_token=yyy
            val fragment = it.fragment

            if (!fragment.isNullOrBlank()) {
                // Фокус: превращаем фрагмент в query-параметры для удобного парсинга
                val normalizedUri = android.net.Uri.parse("skga://reset-password?$fragment")
                accessToken = normalizedUri.getQueryParameter("access_token")
                refreshToken = normalizedUri.getQueryParameter("refresh_token")
            } else {
                // Резервный вариант, если вдруг прилетит обычная query-ссылка через '?'
                accessToken = it.getQueryParameter("access_token")
                refreshToken = it.getQueryParameter("refresh_token")
            }
        }

        // Логируем для отладки (потом можно удалить)
        android.util.Log.d("AUTH_DEBUG", "Access Token получен: ${accessToken != null}")
        android.util.Log.d("AUTH_DEBUG", "Refresh Token получен: ${refreshToken != null}")

        if (accessToken == null) {
            Toast.makeText(this, "Ссылка недействительна или устарела", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.saveBtn.setOnClickListener {
            val password = binding.etPassword.text.toString()
            val repeatPassword = binding.etPasswordRepeat.text.toString()

            if (password.isBlank() || repeatPassword.isBlank()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != repeatPassword) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "Пароль минимум 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.setupTeacherAccount(accessToken, refreshToken ?: "", password)
        }

        viewModel.result.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Пароль установлен! Войдите в приложение", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, WelcomeScreenActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            } else {
                Toast.makeText(this, "Ошибка. Попробуйте снова", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.saveBtn.isEnabled = !isLoading
        }
    }
}