package com.example.qrscanner.applications

import android.app.Application
import android.util.Log

class QrScannerApplication : Application() {
    private var token: String? = null

    companion object {

        private var app: QrScannerApplication? = null

        fun getInstance(): QrScannerApplication {

            if (app == null) {
                app = QrScannerApplication()
            }
            return app as QrScannerApplication
        }
    }

    fun getToken(): String? {
        return token;
    }

    fun setToken(token: String) {
        this.token = token;
    }
}