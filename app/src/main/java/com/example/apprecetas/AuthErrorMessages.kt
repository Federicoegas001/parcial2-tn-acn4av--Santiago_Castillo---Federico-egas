package com.example.apprecetas

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

fun authErrorMessageRes(cause: Throwable, fallback: Int): Int = when (cause) {
    is FirebaseAuthInvalidUserException,
    is FirebaseAuthInvalidCredentialsException -> R.string.auth_wrong_credentials
    is FirebaseAuthUserCollisionException -> R.string.auth_email_in_use
    is FirebaseNetworkException -> R.string.auth_network_error
    is FirebaseTooManyRequestsException -> R.string.auth_too_many_requests
    else -> fallback
}
