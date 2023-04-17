package com.example.qrcodescanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityLogin : AppCompatActivity() {

    private lateinit var Login_btn: MaterialButton
    private lateinit var Sign_btn: MaterialButton
    private lateinit var userPassword: EditText
    private lateinit var userLogin: EditText

    private lateinit var login: String
    private lateinit var password: String

    companion object{
        const val REQUEST_CODE = 1000
        private const val TAG = "MAIN_TAG"
        fun startActivity(activity: AppCompatActivity){
            val intent = Intent(activity, ActivityLogin::class.java)
            activity.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Login_btn = findViewById(R.id.Login_btn)
        userPassword = findViewById(R.id.input_psd)
        userLogin = findViewById(R.id.input_login)
        Sign_btn = findViewById(R.id.Sign_btn)

        Login_btn.setOnClickListener {
            password = userPassword.text.toString()
            login = userLogin.text.toString()

            App.getInstance().getApiServices().login(login, password)
                .enqueue(object : Callback<Client> {
                    override fun onResponse(
                        call: Call<Client>,
                        response: Response<Client>
                    ) {
                        if (response.code() == 200) {
                            ActivityMainMenu.startActivity(this@ActivityLogin, response.body()!!)
                        }else if (response.code() == 400) {
                            val gson = Gson()
                            val message: ResponseObject = gson.fromJson(
                                response.errorBody()!!.charStream(),
                                ResponseObject::class.java
                            )
                            Toast.makeText(this@ActivityLogin, message.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Client>, t: Throwable) {
                        Log.d(TAG, t.toString())
                    }
                })
        }

        Sign_btn.setOnClickListener {
            ActivityRegister.startActivity(this@ActivityLogin)
        }
    }
}