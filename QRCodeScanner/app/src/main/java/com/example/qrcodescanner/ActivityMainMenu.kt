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

class ActivityMainMenu : AppCompatActivity() {

    private lateinit var UserLoan_btn: MaterialButton
    private lateinit var Search_btn: MaterialButton
    private lateinit var UserBook_btn: MaterialButton
    private lateinit var Logout_btn: MaterialButton
    private lateinit var History_btn: MaterialButton


    companion object{
        const val REQUEST_CODE = 1000
        private const val TAG = "MAIN_TAG"
        private lateinit var user: Client
        fun startActivity(activity: AppCompatActivity, client: Client){
            val intent = Intent(activity, ActivityMainMenu::class.java)
            activity.startActivityForResult(intent, REQUEST_CODE)
            user = client
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        UserLoan_btn = findViewById(R.id.UserLoan_btn)
        Search_btn = findViewById(R.id.Search_btn)
        UserBook_btn = findViewById(R.id.UserBook_btn)
        Logout_btn = findViewById(R.id.Logout_btn)
        History_btn = findViewById(R.id.History_btn)

        UserLoan_btn.setOnClickListener {
            MainActivity.startActivity2(this@ActivityMainMenu, user)
        }

        Search_btn.setOnClickListener {
            ActivitySearch.startActivity(this@ActivityMainMenu, user)
        }

        UserBook_btn.setOnClickListener {
            ActivityBooksList.startActivity(this@ActivityMainMenu, user)
        }

        History_btn.setOnClickListener {
            ActivityHistory.startActivity(this@ActivityMainMenu, user)
        }

        Logout_btn.setOnClickListener {
            ActivityLogin.startActivity(this@ActivityMainMenu)
        }
    }
}