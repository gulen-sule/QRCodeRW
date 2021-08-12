package com.example.qrscanner.data.api.models.login

import retrofit2.http.Body

class LoginBody(
    private var email: String,
    private var password: String
) {
    fun set(email: String, password: String) {
        this.email = email
        this.password = password
    }

    fun getEmail() = email
    fun getPassword() = password
}