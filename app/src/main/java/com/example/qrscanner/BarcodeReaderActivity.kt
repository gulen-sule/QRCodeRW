package com.example.qrscanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qrscanner.databinding.ActivityBarcodeReaderBinding

class BarcodeReaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBarcodeReaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }



}