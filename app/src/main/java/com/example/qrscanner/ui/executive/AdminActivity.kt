package com.example.qrscanner.ui.executive

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.example.qrscanner.R
import com.example.qrscanner.ui.home.HomeActivity

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val navHost=findNavController(R.id.controllerNavHostFragment)
       if( navHost.currentDestination?.id== R.layout.fragment_scanner)
       {
           val intent= Intent(applicationContext, HomeActivity::class.java)
           startActivity(intent)
       }
    }
}