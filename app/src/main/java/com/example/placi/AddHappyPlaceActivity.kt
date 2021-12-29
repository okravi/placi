package com.example.placi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.placi.databinding.ActivityAddHappyPlaceBinding
import com.example.placi.databinding.ActivityMainBinding

class AddHappyPlaceActivity : AppCompatActivity() {

    private var binding: ActivityAddHappyPlaceBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val toolbarAddPlace = binding?.toolbarAddPlace
        setSupportActionBar(toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}