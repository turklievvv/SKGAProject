package com.example.skga.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.skga.databinding.FragmentLogInBinding

class LogInFragment : Fragment() {

    private lateinit var viewModel: LogInViewModel

    private var _binding: FragmentLogInBinding? = null

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
        viewModel.loginSuccess.observe(viewLifecycleOwner) {
            if (it) {
                startActivity(
                    MainMenuActivity.newIntent(
                        requireContext()
                    )
                )
                requireActivity().finish(
                )
                requireActivity().finish()
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner){
            Log.d("Error message", it)
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