package com.example.skga.presentation.adminPage.eventsPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skga.R
import com.example.skga.databinding.ActivityEventsManageBinding

class EventsManageActivity : AppCompatActivity() {
    lateinit var binding: ActivityEventsManageBinding
    private val viewModel: EventsManageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEventsManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel.getEventsList()
        val recyclerView = binding.recyclerViewEventManage
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel.eventList.observe(this) {
            recyclerView.adapter = EventsManageAdapter(it)
            Log.d("EVENTS_ITEM", "События лист $it")
            if (it.isEmpty()) {
                binding.noEventsTV.visibility = View.VISIBLE
            } else {
                binding.noEventsTV.visibility = View.GONE
            }
        }
        binding.addNewEventButton.setOnClickListener {
            startActivity(EventsAddAndEditActivity.newIntent(this, null))
        }

    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, EventsManageActivity::class.java)
        }
    }
}