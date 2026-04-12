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
import androidx.lifecycle.application
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.skga.databinding.FragmentUserProfileBinding
import com.example.skga.presentation.WelcomeScreenActivity
import data.local.StudentRepositoryImpl
import data.local.UserSessionManager
import domain.entity.StudentProfile
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: StudentRepositoryImpl

    private val viewModel: UserProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = StudentRepositoryImpl(requireActivity().application)
        bindingButtons()
        viewModel.takeStudentProfile()
        updateStudentProfileImage()
        loadStudentInfoOnView()
    }

    private fun updateStudentProfileImage() {
        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let { safeUri ->
                    val bytes = viewModel.uriToByteArray(safeUri)
                    if (bytes != null) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.uploadStudentPhoto(bytes,binding.profileImage,viewModel.student)
                        }
                    }
                }
            }


        var photoUri: Uri? = null
        val takePhotoLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) photoUri?.let {
                    val bytes = viewModel.uriToByteArray(it)
                    if (bytes != null) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.uploadStudentPhoto(bytes,binding.profileImage,viewModel.student)
                        }
                    }
                }
            }

        fun showImagePicker() {
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
         binding.profileImage.setOnClickListener {
             showImagePicker()
         }
    }


    private fun bindingButtons() {
        val sessionManager = UserSessionManager(requireContext())
        binding.profileImage.setOnClickListener {
            updateStudentProfileImage()
        }
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

    private fun loadStudentInfoOnView(){
        binding.userName.text = "${viewModel.student.firstName} ${viewModel.student.lastName}"
        Log.d("GLIDE_DEBUG", "Пытаюсь загрузить ${viewModel.student.firstName} ${viewModel.student.lastName}")
        Log.d("GLIDE_DEBUG", "Пытаюсь загрузить URL: ${viewModel.student.avatarUrl}")
        Glide.with(requireContext())
            .load(viewModel.student.avatarUrl)
            .signature(ObjectKey(System.currentTimeMillis().toString()))
            .circleCrop()
            .into(binding.profileImage)
    }
}