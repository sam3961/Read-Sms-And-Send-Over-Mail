package com.app.readsms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.readsms.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkForSmsReceivePermissions()

        val c: Calendar = Calendar.getInstance()
        val timeOfDay: Int = c.get(Calendar.HOUR_OF_DAY)

        when (timeOfDay) {
            in 0..11 -> {
                binding.textView.text = "Good Morning \n\n User"
            }
            in 12..15 -> {
                binding.textView.text = "Good Afternoon \n\n User"
            }
            in 16..20 -> {
                binding.textView.text = "Good Evening \n\n User"
            }
            in 21..23 -> {
                binding.textView.text = "Good Night \n\n User"
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intents = Intent(this, DetectedService::class.java)
            startForegroundService(intents)
        }

    }

    private fun checkForSmsReceivePermissions() {
        // Check if App already has permissions for receiving SMS
        if (
            ContextCompat.checkSelfPermission(baseContext, Manifest.permission.RECEIVE_SMS)
            == PackageManager.PERMISSION_GRANTED
            ||  ContextCompat.checkSelfPermission(baseContext, Manifest.permission.READ_SMS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // App has permissions to listen incoming SMS messages
            Log.d("adnan", "checkForSmsReceivePermissions: Allowed")
        } else {
            // App don't have permissions to listen incoming SMS messages
            Log.d("adnan", "checkForSmsReceivePermissions: Denied")

            // Request permissions from user
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS), 43391)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 43391) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("adnan", "Sms Receive Permissions granted")
            } else {
                Log.d("adnan", "Sms Receive Permissions denied")
            }
        }

    }


}