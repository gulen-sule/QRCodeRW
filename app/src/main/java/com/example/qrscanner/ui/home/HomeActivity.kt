package com.example.qrscanner.ui.home

import android.app.StatusBarManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qrscanner.databinding.ActivityHomeBinding
import com.example.qrscanner.ui.admin.AdminActivity
import com.example.qrscanner.ui.user.UserActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        setActions()
    }

    private fun setActions() {
        val context =baseContext
        binding.buttonController.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }
        binding.buttonUser.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setBottomNavigation() {
//        val navView: BottomNavigationView? = null// = binding.navView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_scanner, R.id.navigation_generator
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView?.setupWithNavController(navController)
//    }
    }


}