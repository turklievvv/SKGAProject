package com.example.skga.presentation.homePage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.skga.databinding.FragmentHomePageBinding

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
        setupObservers()
        viewModel.loadData()
        setClosetLesson()
        bindingView()
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
            if (_binding != null && lessons != null) {
                val adapter = ViewPagerAdapter(
                    viewModel.daysList,
                    lessons,
                )
                binding.swipeRefreshLayout.isRefreshing = false
                binding.scheduleViewPager.adapter = adapter
                setClosetLesson()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        fun newInstance(context: Context) = Intent(context, HomePageFragment::class.java)
    }
}
