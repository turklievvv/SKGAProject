package com.example.skga.presentation.profilePage

import BottomDialogFragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.skga.databinding.FragmentUserProfileBinding
import com.example.skga.presentation.WelcomeScreenActivity
import data.local.StudentRepositoryImpl
import data.local.UserSessionManager
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserProfileViewModel by viewModels()

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { safeUri ->
                val bytes = viewModel.uriToByteArray(safeUri)
                val profile = viewModel.student.value ?: return@let
                if (bytes != null) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.uploadStudentPhoto(bytes, binding.profileImage, profile, requireContext())
                    }
                }
            }
        }

    private var photoUri: Uri? = null
    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                val bytes = photoUri?.let { viewModel.uriToByteArray(it) }
                val profile = viewModel.student.value ?: return@registerForActivityResult
                if (bytes != null) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.uploadStudentPhoto(bytes, binding.profileImage, profile, requireContext())
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.takeStudentProfile()
        bindingButtons()

        viewModel.student.observe(viewLifecycleOwner) { profile ->
            binding.userName.text = "${profile.firstName} ${profile.lastName}"
            Glide.with(requireContext())
                .load(profile.avatarUrl)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .circleCrop()
                .into(binding.profileImage)
        }

        binding.profileImage.setOnClickListener {
            showImagePicker()
        }
    }

    private fun showImagePicker() {
        val picker = BottomDialogFragment { isCamera ->
            if (isCamera) {
                photoUri = viewModel.createTempUri()
                takePhotoLauncher.launch(photoUri)
            } else {
                pickImageLauncher.launch("image/*")
            }
        }
        picker.show(childFragmentManager, "ImagePicker")
    }

    private fun bindingButtons() {
        val sessionManager = UserSessionManager(requireContext())
        binding.buttonExitFromAccount.setOnClickListener {
            lifecycleScope.launch {
                sessionManager.clearSession()
                val intent = WelcomeScreenActivity.newIntent(requireContext())
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}