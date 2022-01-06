package com.example.placi.activities


import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.placi.database.DatabaseHandler
import com.example.placi.databinding.ActivityAddHappyPlaceBinding
import com.example.placi.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityAddHappyPlaceBinding? = null

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage : Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

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

        dateSetListener = DatePickerDialog.OnDateSetListener {
                view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()

        }
        updateDateInView()
        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onClick(v: View?) {
        when(v!!.id){
           binding?.etDate?.id -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            binding?.tvAddImage?.id ->{
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select action")
                val pictureDialogItems = arrayOf("Select photo from gallery",
                    "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){
                    dialog, which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                        1 -> takePhoto()
                    }
                }
                pictureDialog.show()
            }
            binding?.btnSave?.id -> {
                Toast.makeText(this, "save clicked", Toast.LENGTH_SHORT).show()
                when{
                    (binding?.etTitle?.text.isNullOrEmpty() ||
                            binding?.etDescription?.text.isNullOrEmpty() ||
                            binding?.etDate?.text.isNullOrEmpty() ||
                            binding?.etLocation?.text.isNullOrEmpty()) -> {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                    }else ->{

                    Log.e("debug", "trying to save to the DB")
                        val happyPlaceModel = HappyPlaceModel(
                            0,
                            binding?.etTitle?.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding?.etDescription?.text.toString(),
                            binding?.etDate?.text.toString(),
                            binding?.etLocation?.text.toString(),
                            mLatitude,
                            mLongitude
                        )
                    val dbHandler = DatabaseHandler(this)
                    val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)

                    if(addHappyPlace > 0){
                        Log.e("debug", "everything seems to be fine")

                        finish()

                        }
                    }

                }

            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode ==  GALLERY){
                if(data != null){
                    val contentURI = data.data
                    val testURI = data.extras
                    try {
                        val selectedImageBitmap = MediaStore.Images.Media
                            .getBitmap(this.contentResolver, contentURI)
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("Saved image: ", "PAth :: $saveImageToInternalStorage")
                        binding?.ivPlaceImage?.setImageBitmap(selectedImageBitmap)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlaceActivity, "Failed to load the image",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }else if (requestCode == CAMERA){
                val thumbnail : Bitmap = data!!.extras!!.get("data") as Bitmap

                saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                Log.e("Saved image: ", "PAth :: $saveImageToInternalStorage")

                binding?.ivPlaceImage?.setImageBitmap(thumbnail)
            }
        }
    }

    private fun takePhoto(){
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?)
            {if (report!!.areAllPermissionsGranted()){
                val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(galleryIntent, CAMERA)
            }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken)
            {
                showRationaleDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?)
            {if (report!!.areAllPermissionsGranted()){
                val galleryIntent = Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, GALLERY)
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken)
            {
                showRationaleDialogForPermissions()
            }
        }).onSameThread().check()
    }

    private fun showRationaleDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It looks like the permissions weren't granted. That's unfortunate.")
            .setPositiveButton("GO TO SETTINGS")
            { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }

    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding?.etDate?.setText(sdf.format(cal.time).toString())
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }
}