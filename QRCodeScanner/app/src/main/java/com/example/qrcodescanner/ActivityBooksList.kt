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

class ActivityBooksList : AppCompatActivity() {

    companion object{
        const val REQUEST_CODE = 1000
        private const val TAG = "MAIN_TAG"
        private lateinit var user: Client
        fun startActivity(activity: AppCompatActivity, client: Client){
            val intent = Intent(activity, ActivityBooksList::class.java)
            activity.startActivityForResult(intent, REQUEST_CODE)
            user = client
        }
    }

    private lateinit var bookslist: ListView
    private lateinit var text_top: TextView
    private lateinit var book_list: List<Book>
    private var books: ArrayList<String> = arrayListOf<String>()
    private var books_ids: ArrayList<Long> = arrayListOf<Long>()
    private var book_to_order = OrderBook( -1, -1)
    private lateinit var arrayAdapter: ArrayAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userbooks)
        bookslist = findViewById<ListView>(R.id.bookslist)
        text_top = findViewById(R.id.text_top)

        var actionBar = getSupportActionBar()

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        App.getInstance().getApiServices().userInfo(user.userId)
            .enqueue(object : Callback<List<Book>> {
                override fun onResponse(
                    call: Call<List<Book>>,
                    response: Response<List<Book>>
                ) {
                    book_list = response.body()!!
                    Log.d(TAG, response.body()!!.toString())
                    if (book_list.isEmpty()){
                        text_top.text = "Brak wypożyczonych książek"
                    }else {
                        for (i in book_list.indices) {
                            books.add(book_list[i].title)
                            books_ids.add(book_list[i].bookId)
                        }
                        arrayAdapter = ArrayAdapter<String>(this@ActivityBooksList,
                            android.R.layout.simple_list_item_1, books.toTypedArray())
                        bookslist.adapter = arrayAdapter
                        bookslist.setOnItemClickListener { parent, _, position, _ ->

                            val dialogClickListener =
                                DialogInterface.OnClickListener { dialog, which ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> {
                                            book_to_order.bookIdToOrder = books_ids[position]
                                            book_to_order.userId = user.userId
                                            App.getInstance().getApiServices().returnBook(book_to_order).enqueue(object : Callback<ResponseObject> {
                                                override fun onResponse(
                                                    call: Call<ResponseObject>,
                                                    response: Response<ResponseObject>
                                                ){
                                                    if (response.code() == 200) {
                                                        Toast.makeText(this@ActivityBooksList, response.body()!!.message, Toast.LENGTH_SHORT).show()
                                                        books.removeAt(position)
                                                        startActivity(this@ActivityBooksList, user)
                                                    }else if (response.code() == 400) {
                                                        val gson = Gson()
                                                        val message: ResponseObject = gson.fromJson(
                                                            response.errorBody()!!.charStream(),
                                                            ResponseObject::class.java
                                                        )
                                                        Toast.makeText(this@ActivityBooksList, message.message, Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                override fun onFailure(call: Call<ResponseObject>, t: Throwable) {
                                                    Log.d(TAG, t.toString())
                                                }
                                            })
                                        }

                                        DialogInterface.BUTTON_NEGATIVE -> {
                                            dialog.dismiss()
                                        }
                                    }
                                }

                            val builder: AlertDialog.Builder = AlertDialog.Builder(this@ActivityBooksList)

                            builder.setMessage("Czy chcesz zwrócić książkę:\n" + books[position])
                                .setPositiveButton("Tak", dialogClickListener)
                                .setNegativeButton("Nie", dialogClickListener)
                                .show()

                        }
                    }
                }
                override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                    Log.d(TAG, t.toString())
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                ActivityMainMenu.startActivity(this@ActivityBooksList, user)
            }
        }
        return super.onContextItemSelected(item)
    }
}