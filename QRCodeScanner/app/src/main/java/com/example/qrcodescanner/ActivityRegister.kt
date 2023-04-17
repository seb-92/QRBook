package com.example.qrcodescanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ActivityRegister: AppCompatActivity() {

    private lateinit var Register_btn: MaterialButton
    private lateinit var userAddress: EditText
    private lateinit var userFirstName: EditText
    private lateinit var userLastName: EditText
    private lateinit var userLgin: EditText
    private lateinit var userPsswd: EditText

    private var newClient = Client("error", "error", "error", "error", "error", -1)

    companion object{
        const val REQUEST_CODE = 1000
        private const val TAG = "MAIN_TAG"
        fun startActivity(activity: AppCompatActivity){
            val intent = Intent(activity, ActivityRegister::class.java)
            activity.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newuser)

        var actionBar = getSupportActionBar()

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        Register_btn = findViewById(R.id.Register_btn)
        userPsswd = findViewById(R.id.input_psswd)
        userLgin = findViewById(R.id.input_lgin)
        userAddress = findViewById(R.id.input_adres)
        userFirstName = findViewById(R.id.input_firstName)
        userLastName = findViewById(R.id.input_lastName)

        Register_btn.setOnClickListener {
            newClient.address = userAddress.text.toString()
            newClient.firstName = userFirstName.text.toString()
            newClient.lastName = userLastName.text.toString()
            newClient.login = userLgin.text.toString()
            newClient.password = userPsswd.text.toString()
            App.getInstance().getApiServices().newUser(newClient)
                .enqueue(object : Callback<ResponseObject> {
                    override fun onResponse(
                        call: Call<ResponseObject>,
                        response: Response<ResponseObject>
                    ) {
                        if (response.code() == 200) {
                            Toast.makeText(this@ActivityRegister, response.body()!!.message, Toast.LENGTH_SHORT).show()
                            ActivityLogin.startActivity(this@ActivityRegister)
                        }else if (response.code() == 400) {
                            val gson = Gson()
                            val message: ResponseObject = gson.fromJson(
                                response.errorBody()!!.charStream(),
                                ResponseObject::class.java
                            )
                            Toast.makeText(this@ActivityRegister, message.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseObject>, t: Throwable) {
                        Log.d(TAG, t.toString())
                    }
                })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                ActivityLogin.startActivity(this@ActivityRegister)
            }
        }
        return super.onContextItemSelected(item)
    }
}