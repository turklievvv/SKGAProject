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
        val recyclerView = binding.recyclerViewScheduleFragment
        viewModel.scheduleList.observe(viewLifecycleOwner) { lessons ->
            if (lessons != null) {
                val adapter = DayAdapter(
                    viewModel.daysList,
                    lessons
                )
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = adapter
            }
        }
    }
}