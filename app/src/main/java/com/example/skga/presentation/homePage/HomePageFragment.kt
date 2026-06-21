package com.example.skga.presentation.homePage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.skga.databinding.FragmentHomePageBinding
import domain.entity.EventItem
import domain.entity.ScheduleItem

class HomePageFragment : Fragment() {

    private val viewModel: HomePageViewModel by viewModels()
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadData()
        setClosetLesson()
        bindingView()
        setupObservers()
    }

    private fun bindingView() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadData()
        }
    }

    private fun setClosetLesson() {
        val closetLesson = viewModel.findClosestLesson()
        if (closetLesson == null) {
            binding.lessonNameTv.text = "Сегодня уроков нет, отдыхай"
            binding.lessonNameTv.gravity = Gravity.CENTER_VERTICAL
            binding.lessonStartTime.visibility = View.GONE
            binding.lessonEndTime.visibility = View.GONE
            binding.teacherNameTv.visibility = View.GONE
            binding.classRoomTV.visibility = View.GONE
        } else {
            binding.lessonStartTime.visibility = View.VISIBLE
            binding.lessonEndTime.visibility = View.VISIBLE
            binding.teacherNameTv.visibility = View.VISIBLE
            binding.classRoomTV.visibility = View.VISIBLE
            binding.lessonNameTv.text = closetLesson.lessonName
            binding.lessonStartTime.text = closetLesson.lessonStartTime
            binding.lessonEndTime.text = closetLesson.lessonEndTime
            binding.teacherNameTv.text = closetLesson.lessonTeacherFullName
            binding.classRoomTV.text = closetLesson.lessonClassRoom
        }
    }

    private fun setupObservers() {
        viewModel.scheduleList.observe(viewLifecycleOwner) { lessons ->
            if (lessons != null) {
                updateViewPager(lessons, viewModel.eventList.value ?: emptyList())
            }
        }

        viewModel.eventList.observe(viewLifecycleOwner) { events ->
            if (events != null) {
                updateViewPager(viewModel.scheduleList.value ?: emptyList(), events)
            }
        }
    }

    private fun updateViewPager(lessons: List<ScheduleItem>, events: List<EventItem>) {
        if (_binding == null) return

        val adapter = ViewPagerAdapter(
            viewModel.daysList,
            lessons,
            events
        )
        Log.d("REPO_DEBUG", "События $events")

        binding.scheduleViewPager.adapter = adapter
        binding.swipeRefreshLayout.isRefreshing = false
        setClosetLesson()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        fun newInstance(context: Context) = Intent(context, HomePageFragment::class.java)
    }
}
