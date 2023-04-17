package com.example.qrcodescanner

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_loan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var Scan_image: ImageView
    private lateinit var Scan_btn: MaterialButton
    private lateinit var Camera_btn: MaterialButton
    private lateinit var Id_result: TextView

    companion object{
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101
        const val REQUEST_CODE_RETURN = 1000
        private const val TAG = "MAIN_TAG"
        private lateinit var user: Client
        fun startActivity(activity: AppCompatActivity){
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivityForResult(intent, MainActivity.REQUEST_CODE_RETURN)
        }
        fun startActivity2(activity: AppCompatActivity, client: Client){
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivityForResult(intent, MainActivity.REQUEST_CODE_RETURN)
            Log.d(TAG, client.toString())
            user = client
        }
    }

    private lateinit var Camera_permission: Array<String>
    private lateinit var Storage_permission: Array<String>

    private var Image_Uri: Uri? = null

    private var barcodeScannerOptions: BarcodeScannerOptions? = null
    private var barcodeScanner: BarcodeScanner? = null

    private var book = Book(-1, "error", "error", -1, -1, false)
    private lateinit var serverResp: String
    private lateinit var txt: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar.visibility = View.GONE

        Scan_image = findViewById(R.id.Scan_image)
        Scan_btn = findViewById(R.id.Scan_btn)
        Camera_btn = findViewById(R.id.Camera_btn)
        Id_result = findViewById(R.id.Id_result)

        Camera_permission = arrayOf(android.Manifest.permission.CAMERA)
        Storage_permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        barcodeScannerOptions = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions!!)

        var actionBar = getSupportActionBar()

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        Camera_btn.setOnClickListener {
            if (checkCameraPermission()){
                pickImageCamera()
            } else {
                requestCameraPermission()
                requestStoragePermission()
            }
        }

        Scan_btn.setOnClickListener {
            if(Image_Uri == null){
                Toast.makeText(this, "Brak zdjecia", Toast.LENGTH_SHORT).show()
            }
            else{
                progressBar.visibility = View.VISIBLE
                detectResultFromImage()
            }
        }

    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result->
        if (result.resultCode == Activity.RESULT_OK){
            val data = result.data
            Log.d(TAG, "cameraActivityResultLauncher: imageUri: $Image_Uri")
            Scan_image.setImageURI(Image_Uri)
        }
    }

    private fun pickImageCamera(){
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Sample Image")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description")
        Image_Uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Image_Uri)
        Id_result.text = ""
        cameraActivityResultLauncher.launch(intent)
    }

    private fun detectResultFromImage(){
        Log.d(TAG, "detectResultFromImage: ")
        try{
            val inputImage = InputImage.fromFilePath(this, Image_Uri!!)
            val barcodeResult = barcodeScanner!!.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    extractBarcodeQrCodeInfo(barcodes)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "detectResultFromImage: ", e)
                    Toast.makeText(this, "Skanowanie nie powiodło się", Toast.LENGTH_SHORT).show()
                }
        }
        catch (e: Exception){
            Log.e(TAG, "detectResultFromImage: ", e)
            Toast.makeText(this, "Skanowanie nie powiodło się przez ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractBarcodeQrCodeInfo(barcodes: List<Barcode>){
        if (barcodes.isEmpty()){
            progressBar.visibility = View.GONE
            Id_result.text = "Ponów zdjęcie"
        } else {
            for (barcode in barcodes) {
                val rawValue = barcode.rawValue

                if (rawValue != null) {
                    App.getInstance().getApiServices().bookInfo(rawValue.toLong())
                        .enqueue(object : Callback<Book> {
                            override fun onResponse(
                                call: Call<Book>,
                                response: Response<Book>,
                            ) {
                                progressBar.visibility = View.GONE
                                Log.d(TAG, response.code().toString())
                                if (response.code() == 200) {
                                    book = response.body()!!
                                    Id_result.text = "Skanowanie udane"
                                    ActivityLoan.startActivity(this@MainActivity, response.body()!!, user)
                                }else if (response.code() == 400) {
                                    val gson = Gson()
                                    val message: ResponseObject = gson.fromJson(
                                        response.errorBody()!!.charStream(),
                                        ResponseObject::class.java
                                    )
                                    Toast.makeText(this@MainActivity, message.message, Toast.LENGTH_SHORT).show()
                                    Log.d(TAG, response.body().toString())
                                }
                            }

                            override fun onFailure(call: Call<Book>, t: Throwable) {
                                Toast.makeText(this@MainActivity,
                                    "Error loading",
                                    Toast.LENGTH_LONG).show()
                                Log.d(TAG, "extractBarcodeQrCodeInfo: $t")
                            }

                        })
                }
            }
        }
    }

    private fun checkCameraPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(this, Camera_permission, CAMERA_REQUEST_CODE)
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(this, Storage_permission, STORAGE_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if(cameraAccepted){
                        pickImageCamera()
                    }
                    else{
                        Toast.makeText(this, "Camera permissions are required", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE -> {
                if(grantResults.isNotEmpty()){
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if(storageAccepted){
                        pickImageCamera()
                    }
                    else{
                        Toast.makeText(this, "Camera permissions are required", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                ActivityMainMenu.startActivity(this@MainActivity, user)
            }
        }
        return super.onContextItemSelected(item)
    }
}