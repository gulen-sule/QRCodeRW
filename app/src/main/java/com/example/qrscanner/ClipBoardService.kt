package com.example.qrscanner

import android.app.Service
import android.content.ClipboardManager
import android.content.Intent
import android.os.IBinder

class ClipBoardService : Service() {
    private var manager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }



}