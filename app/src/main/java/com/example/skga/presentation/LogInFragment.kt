package com.example.skga.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.skga.databinding.FragmentLogInBinding
import com.example.skga.presentation.adminPage.AdminPageActivity
import data.local.UserSessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LogInFragment : Fragment() {

    private lateinit var viewModel: LogInViewModel

    private var _binding: FragmentLogInBinding? = null

    private lateinit var userSession: UserSessionManager


    private val binding: FragmentLogInBinding
        get() = _binding ?: throw RuntimeException("FragmentLogInBinding == null")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(LogInViewModel::class.java)
        userSession = UserSessionManager(requireContext())

        viewModel.loginSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                lifecycleScope.launch {
                    val studentProfile = userSession.userProfile.first() // first() вместо collect
                        ?: return@launch

                    when {
                        studentProfile.role == "admin" && studentProfile.isAdmin -> {
                            startActivity(AdminPageActivity.newIntent(requireContext()))
                        }
                        studentProfile.role == "student" -> {
                            startActivity(MainMenuActivity.newIntent(requireContext()))
                        }
                        studentProfile.role == "teacher" -> {
                            startActivity(MainMenuActivity.newIntent(requireContext()))
                        }
                        else -> {
                            Toast.makeText(requireContext(), "Неизвестная роль пользователя", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                    }
                    requireActivity().finish() // ← один раз, после перехода
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Неверный логин или пароль",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        bindingViews()
        resetError()
    }

    private fun bindingViews() {
        binding.firstTimeIsHereButton.setOnClickListener {
            findNavController().navigate(LogInFragmentDirections.actionLogInFragmentToRegistrationFragment())
        }

        binding.logInButton.setOnClickListener {
            viewModel.login(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun resetError(){
        binding.etEmail.doOnTextChanged { _, _, _, _ ->
            viewModel.reserError()
        }
        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            viewModel.reserError()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}