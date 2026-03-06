package com.example.skga.presentation

import androidx.databinding.BindingAdapter
import com.example.skga.R
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("errorInputFullName")
fun bindErrorInputFullName(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.full_name_error_text)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorInputGroup")
fun bindErrorInputGroup(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.group_error_text)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorInputPhone")
fun bindErrorInputPhone(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.phone_error_text)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorInputEmail")
fun bindErrorInputEmail(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.email_error_text)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorInputPassword")
fun bindErrorInputPassword(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.password_blank_error_text)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorRepeatPassword")
fun bindErrorRepeatPassword(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.password_repeat_error)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorCorrectPassword")
fun bindErrorCorrectPassword(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.password_correct_error)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorCorrectEmail")
fun bindErrorCorrectEmail(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.email_correct_error)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorCorrectGroup")
fun bindErrorCorrectGroup(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.group_correct_error)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorCorrectFaculties")
fun bindErrorCorrectFaculties(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.faculties_correct_error)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorInputFaculties")
fun bindErrorInputFaculties(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.faculties_correct_error)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorCorrectPhone")
fun bindErrorCorrectPhone(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.phone_correct_error)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("errorLoginOrPassword")
fun bindErrorLoginOrPassword(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.login_or_password_correct_error)
    } else {
        null
    }
    textInputLayout.error = message
}
@BindingAdapter("isBlankLoginOrPassword")
fun bindSsBlankLoginOrPassword(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.login_or_password_blank)
    } else {
        null
    }
    textInputLayout.error = message
}

@BindingAdapter("isUserExists")
fun bindIsUserExists(textInputLayout: TextInputLayout,isError: Boolean){
    val message = if (isError) {
        textInputLayout.context.getString(R.string.email_or_phone_already_registered)
    } else {
        null
    }
    textInputLayout.error = message
}


