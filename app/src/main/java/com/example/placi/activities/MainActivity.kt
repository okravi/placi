package com.example.placi.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.placi.adapters.HappyPlacesAdapter
import com.example.placi.database.DatabaseHandler
import com.example.placi.databinding.ActivityMainBinding
import com.example.placi.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_main.*
import pl.kitek.rvswipetodelete.SwipeToDeleteCallback
import pl.kitek.rvswipetodelete.SwipeToEditCallback

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val fabAddHappyPlace = binding?.fabAddHappyPlace

        fabAddHappyPlace?.setOnClickListener {

            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)

        }
        getHappyPLacesListFromLocalDB()
    }



    private fun setupHappyPlacesRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>){
        binding?.rvHappyPlacesList?.layoutManager = LinearLayoutManager(this)
        val placesAdapter = HappyPlacesAdapter(this, happyPlaceList)
        binding?.rvHappyPlacesList?.setHasFixedSize(true)
        binding?.rvHappyPlacesList?.adapter = placesAdapter

        placesAdapter.setOnClickListener(object : HappyPlacesAdapter.OnClickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity,
                    HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }

        })

        val editSwipeHandler = object :SwipeToEditCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_happy_places_list.adapter as HappyPlacesAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)
            }
        }

        val editItemTouchHandler = ItemTouchHelper(editSwipeHandler)
        editItemTouchHandler.attachToRecyclerView(rv_happy_places_list)

        val deleteSwipeHandler = object : SwipeToDeleteCallback(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_happy_places_list.adapter as HappyPlacesAdapter
                adapter.removeAt(viewHolder.adapterPosition)

                getHappyPLacesListFromLocalDB()
            }
        }

        val deleteItemTouchHandler = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHandler.attachToRecyclerView(rv_happy_places_list)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getHappyPLacesListFromLocalDB()
            } else {
                Log.e("Activity", "Cancelled or Back Pressed")
            }
        }
    }



    companion object {
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_PLACE_DETAILS = "extra_place_details"
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
