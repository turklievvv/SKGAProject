package com.example.skga.presentation.adminPage.teacherPage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skga.R
import com.example.skga.databinding.ActivityTeacherManageBinding

class TeacherManageActivity : AppCompatActivity() {

    private val viewModel: TeacherManageViewModel by viewModels()
    lateinit var binding: ActivityTeacherManageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherManageBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel.getTeacherList()
        val recyclerView = binding.recyclerViewTeacherManage
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.teacherFilteredItems.observe(this) {
            recyclerView.adapter = TeacherManageAdapter(it, onCallClick = { phoneNumber ->
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                startActivity(intent)
            }, onEmailClick = { emailAddress ->
                val subject = Uri.encode("Учебная часть (Администрация)")
                val uriString = "mailto:$emailAddress?subject=$subject"
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse(uriString)
                }
                try {
                    val chooserIntent = Intent.createChooser(intent, "Выберите почту:")
                    startActivity(chooserIntent)
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Не удалось открыть почтовое приложение",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            binding.teacherCountTv.text = it.size.toString()
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.topProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (!isLoading) {
                Toast.makeText(
                    this,
                    "Нет подключения. Повторная попытка через 10 сек...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.addTeacher.setOnClickListener {
            startActivity(TeacherAddEditActivity.newIntent(this,teacherItem = null))
        }
    }


    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, TeacherManageActivity::class.java)
        }
    }
}