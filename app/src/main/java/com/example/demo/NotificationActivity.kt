package com.example.demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.demo.databinding.ActivityNotificationBinding
import kotlinx.coroutines.launch

class NotificationActivity : AppCompatActivity() {

    lateinit var binding: ActivityNotificationBinding

    private val br = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent?
        ) {
            val time = intent?.getIntExtra(NotificationService.EXTRA_TIME, NotificationService.timer)
            Log.d("BroadCast", time.toString())
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    binding.tvTimer.text = time.toString()
                    binding.btnStart.isEnabled = false
                    binding.btnStop.isEnabled = true
                    if(time == 0){
                        binding.btnStart.isEnabled = true
                        binding.btnStop.isEnabled = false
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        initPermission()
        clickListener()
    }

    private fun initViews() {
        val intent = IntentFilter(NotificationService.action)
        ContextCompat.registerReceiver(this , br, intent, ContextCompat.RECEIVER_EXPORTED)
        NotificationHelper.createNotificationChannel(context = this)
        binding.tvTimer.text = NotificationService.timer.toString()
    }

    private fun initPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun clickListener() {
        binding.btnStart.setOnClickListener {
            val intent = Intent(this, NotificationService::class.java)
            startForegroundService(intent)
            binding.btnStart.isEnabled = false
            binding.btnStop.isEnabled = true
        }
        binding.btnStop.setOnClickListener {
            NotificationService.timer = binding.tvTimer.text.toString().toInt()
            val intent = Intent(this, NotificationService::class.java)
            stopService(intent)
            binding.btnStart.isEnabled = true
            binding.btnStop.isEnabled = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(br)
    }

}