package com.example.skga.presentation.schedulePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skga.databinding.FragmentScheduleBinding
import com.example.skga.presentation.homePage.ViewPagerAdapter
import domain.entity.EventItem
import domain.entity.ScheduleItem


class ScheduleFragment : Fragment() {

    private val viewModel: ScheduleViewModel by viewModels()

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadData()
        setupObservers()
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

        binding.recyclerViewScheduleFragment.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewScheduleFragment.adapter = adapter
    }
}