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
        addTextChangeListenersForGroup()
        bindingViews()
        resetError()

    }

    private fun bindingViews(){
        binding.registrationButton.setOnClickListener {
            viewModel.register(
                returnStudentItemFromRegistration(),
                binding.etPasswordRepeat.text.toString()
            )
            viewModel.registrationSucces.observe(viewLifecycleOwner){success ->
                if (success)
                    findNavController().popBackStack()
            }
        }
        binding.alreadyHaveAccountButton.setOnClickListener {
            findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToLogInFragment())
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun addTextChangeListenersForGroup() {
        val autoCompleteTextView = binding.etGroup
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf<String>()
        )

        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                viewModel.loadGroups(query)
            }

            override fun afterTextChanged(s: Editable?) {}

        })
        viewModel.groupsListLiveData.observe(viewLifecycleOwner) { groups ->
            adapter.clear()
            adapter.addAll(groups)
            adapter.notifyDataSetChanged()
        }
    }

    private fun resetError() {
        binding.etUsername.doOnTextChanged { _, _, _, _ ->
            viewModel.resetErrorFullName()
        }
        binding.etPassword.doOnTextChanged  { _, _, _, _ ->
            viewModel.resetErrorPassword()
        }
        binding.etPasswordRepeat.doOnTextChanged  { _, _, _, _ ->
            viewModel.resetErrorRepeatPassword()
        }
        binding.etEmail.doOnTextChanged  { _, _, _, _ ->
            viewModel.resetErrorEmail()
        }
        binding.etPhone.doOnTextChanged  { _, _, _, _ ->
            viewModel.resetErrorPhone()
        }
        binding.etGroup.doOnTextChanged  { _, _, _, _ ->
            viewModel.resetErrorGroup()
        }

    }

    private fun returnStudentItemFromRegistration(): StudentItem {
        val (lastName, firstName, middleName) = parseFio(binding.etUsername.text.toString())
        return StudentItem(
            lastName = lastName,
            surName = firstName,
            middleName = middleName,
            email = binding.etEmail.text.toString(),
            group = binding.etGroup.text.toString(),
            phone = binding.etPhone.text.toString(),
            password = binding.etPassword.text.toString()
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