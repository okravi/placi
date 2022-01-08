package com.example.placi.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.placi.adapters.HappyPlacesAdapter
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
            resultLauncher.launch(intent)
        }
        getHappyPLacesListFromLocalDB()
    }

    private fun setupHappyPlacesRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>){
        binding?.rvHappyPlacesList?.layoutManager = LinearLayoutManager(this)
        val placesAdapter = HappyPlacesAdapter(this, happyPlaceList)
        binding?.rvHappyPlacesList?.setHasFixedSize(true)
        binding?.rvHappyPlacesList?.adapter = placesAdapter
    }

    private fun getHappyPLacesListFromLocalDB(){
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList : ArrayList<HappyPlaceModel> = dbHandler.getHappyPlacesList()

        if(getHappyPlaceList.size > 0){
                binding?.rvHappyPlacesList?.visibility = View.VISIBLE
                binding?.tvNoRecordsAvailable?.visibility = View.GONE
                setupHappyPlacesRecyclerView(getHappyPlaceList)
            }else {
            binding?.rvHappyPlacesList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }

    }


    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            getHappyPLacesListFromLocalDB()
            }else{
                Log.e("Activity", "Cancelled or Back pressed")
            }
        }


    companion object {
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
