package com.example.placi.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.placi.database.DatabaseHandler
import com.example.placi.databinding.ActivityMainBinding
import com.example.placi.models.HappyPlaceModel

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val fabAddHappyPlace = binding?.fabAddHappyPlace
        fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }
        getHappyPLacesListFromLocalDB()
    }

    private fun getHappyPLacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList : ArrayList<HappyPlaceModel> = dbHandler.getHappyPlacesList()

        if(getHappyPlaceList.size > 0){
            for(i in getHappyPlaceList){
                Log.e("Title", i.title)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
