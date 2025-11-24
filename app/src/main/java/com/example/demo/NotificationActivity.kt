package com.example.demo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
        val intent = IntentFilter(NotificationService.action)
        registerReceiver(br, intent)
        NotificationHelper.createNotificationChannel(context = this)
        binding.tvTimer.text = "30"
        clickListener()
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