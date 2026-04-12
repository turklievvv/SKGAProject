package com.example.skga.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.skga.databinding.FragmentRegistrationBinding
import domain.entity.StudentItem

class RegistrationFragment : Fragment() {
    private lateinit var viewModel: RegistrationViewModel

    private var _binding: FragmentRegistrationBinding? = null
    private val binding: FragmentRegistrationBinding
        get() = _binding ?: throw RuntimeException("FragmentRegistrationBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]
        viewModel.loadInitData()
        addGroupAdapter()
        addFacultiesAdapter()
        bindingViews()
        resetError()

    }

    private fun bindingViews() {
        binding.registrationButton.setOnClickListener {
            viewModel.register(
                returnStudentItemFromRegistration(), binding.etPasswordRepeat.text.toString()
            )
            viewModel.isRegistrationSuccess.observe(viewLifecycleOwner) { success ->
                if (success) findNavController().popBackStack()
            }
        }
        binding.alreadyHaveAccountButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun addGroupAdapter() {
        val autoCompleteTextViewGroup = binding.etGroup
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf<String>()
        )
        autoCompleteTextViewGroup.setAdapter(adapter)
        viewModel.groupsListLiveData.observe(viewLifecycleOwner) { groups ->
            adapter.clear()
            adapter.addAll(groups)
            adapter.notifyDataSetChanged()
        }
    }

    private fun addFacultiesAdapter() {
        val autoCompleteTextViewFaculties = binding.etFaculties
        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf<String>()
        )
        autoCompleteTextViewFaculties.setAdapter(adapter)
        viewModel.facultyListLiveData.observe(viewLifecycleOwner) { faculties ->
            adapter.clear()
            adapter.addAll(faculties)
            adapter.filter.filter(null)
        }
    }

    private fun resetError() {
        binding.etUsername.doOnTextChanged { _, _, _, _ ->
            viewModel.resetErrorFullName()
        }
        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            viewModel.resetErrorPassword()
        }
        binding.etPasswordRepeat.doOnTextChanged { _, _, _, _ ->
            viewModel.resetErrorRepeatPassword()
        }
        binding.etEmail.doOnTextChanged { _, _, _, _ ->
            viewModel.resetErrorEmail()
        }
        binding.etPhone.doOnTextChanged { _, _, _, _ ->
            viewModel.resetErrorPhone()
        }
        binding.etGroup.doOnTextChanged { _, _, _, _ ->
            viewModel.resetErrorGroup()
        }

    }

    private fun returnStudentItemFromRegistration(): StudentItem {
        val (lastName, firstName, middleName) = parseFio(binding.etUsername.text.toString())
        return StudentItem(
            lastName = lastName,
            firstName = firstName,
            middleName = middleName,
            email = binding.etEmail.text.toString(),
            group = binding.etGroup.text.toString(),
            phone = binding.etPhone.text.toString(),
            password = binding.etPassword.text.toString(),
            faculties = binding.etFaculties.text.toString()
        )

    }

    private fun parseFio(fio: String): Triple<String, String, String> {
        val parts = fio.trim().split("\\s+".toRegex())
        val lastName = parts.getOrNull(0) ?: ""
        val firstName = parts.getOrNull(1) ?: ""
        val middleName = parts.getOrNull(2) ?: ""

        return Triple(lastName, firstName, middleName)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}