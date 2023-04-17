package com.example.qrcodescanner

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityHistory: AppCompatActivity() {

    companion object {
        const val REQUEST_CODE = 1000
        private const val TAG = "MAIN_TAG"
        private lateinit var user: Client
        fun startActivity(activity: AppCompatActivity, client: Client) {
            val intent = Intent(activity, ActivityHistory::class.java)
            activity.startActivityForResult(intent, REQUEST_CODE)
            user = client
        }
    }

    private lateinit var historylist: ListView
    private lateinit var text_top: TextView
    private lateinit var history_list: List<HistoryBook>
    private var books: ArrayList<String> = arrayListOf<String>()
    private lateinit var arrayAdapter: ArrayAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        historylist = findViewById<ListView>(R.id.historylist)
        text_top = findViewById(R.id.text_top)

        var actionBar = getSupportActionBar()

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        Log.d(TAG, "History")

        App.getInstance().getApiServices().bookHistory(user.userId)
            .enqueue(object : Callback<List<HistoryBook>> {
                override fun onResponse(
                    call: Call<List<HistoryBook>>,
                    response: Response<List<HistoryBook>>
                ) {

                    history_list = response.body()!!
                    if (history_list.isEmpty()) {
                        text_top.text = "Brak wypożyczonych książek"
                    } else {
                        for (i in history_list.indices) {
                            books.add(history_list[i].book.title + "\nData zwrotu: " + history_list[i].dateOfOrder)
                        }
                        arrayAdapter = ArrayAdapter<String>(
                            this@ActivityHistory,
                            android.R.layout.simple_list_item_1, books.toTypedArray()
                        )
                        historylist.adapter = arrayAdapter
                    }
                }

                override fun onFailure(call: Call<List<HistoryBook>>, t: Throwable) {
                    Log.d(TAG, t.toString())
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                ActivityMainMenu.startActivity(this@ActivityHistory, user)
            }
        }

        return super.onContextItemSelected(item)
    }
}