package com.example.qrcodescanner

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivitySearch : AppCompatActivity() {

    companion object{
        const val REQUEST_CODE = 1000
        private const val TAG = "MAIN_TAG"
        private lateinit var user: Client
        fun startActivity(activity: AppCompatActivity, client: Client){
            val intent = Intent(activity, ActivitySearch::class.java)
            activity.startActivityForResult(intent, REQUEST_CODE)
            user = client
        }
    }

    private lateinit var txt_search: EditText
    private lateinit var btn_search: Button
    private lateinit var searchText: String
    private lateinit var searchFilter: String
    private lateinit var bookslist: ListView
    private lateinit var book_list: List<Book>
    private var books: ArrayList<String> = arrayListOf<String>()
    private var copy_books: ArrayList<String> = arrayListOf<String>()
    private var books_ids: ArrayList<Long> = arrayListOf<Long>()
    private lateinit var arrayAdapter: ArrayAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        var actionBar = getSupportActionBar()

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        txt_search = findViewById(R.id.txt_search)
        btn_search = findViewById(R.id.btn_search)
        bookslist = findViewById<ListView>(R.id.bookslist)

        val choices = resources.getStringArray(R.array.Book_choices)
        val spinner = findViewById<Spinner>(R.id.spinner)

        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, choices)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    searchFilter = choices[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Toast.makeText(this@ActivitySearch, "Wybierz filtr wyszukiwania", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btn_search.setOnClickListener {
            copy_books = arrayListOf<String>()
            searchText = txt_search.text.toString()
            if (searchText.isEmpty()) {
                Toast.makeText(this@ActivitySearch, "Uzupełnij pasek wyszukiwania", Toast.LENGTH_SHORT).show()
            }else if(searchFilter.isEmpty()){
                Toast.makeText(this@ActivitySearch, "Uzupełnij filtr wyszukiwania", Toast.LENGTH_SHORT).show()
            }else{
                if(searchFilter == "Tytuł"){
                    Log.d(TAG, searchText + " tytuł")

                    App.getInstance().getApiServices().searchTitle(searchText)
                        .enqueue(object : Callback<List<Book>> {
                            override fun onResponse(
                                call: Call<List<Book>>,
                                response: Response<List<Book>>
                            ) {
                                book_list = response.body()!!
                                if (response.code() == 200) {
                                    for (i in book_list.indices) {
                                        if(book_list[i].available) {
                                            books.add(book_list[i].title + "\n" + "Dostępna")
                                            copy_books.add("[Tytuł: " + book_list[i].title + "\n" +
                                                    "Autor: " + book_list[i].author + "\n" +
                                                    "Rok wydania: " + book_list[i].yearOfPublish + "\n" +
                                                    "Ilość stron: " + book_list[i].amountOfPages + "\n" +
                                                    "Dostępność: " + "Dostępna]")
                                        }else{
                                            books.add(book_list[i].title + "\n" + "Wypożyczona")
                                            copy_books.add("[Tytuł: " + book_list[i].title + "\n" +
                                                    "Autor: " + book_list[i].author + "\n" +
                                                    "Rok wydania: " + book_list[i].yearOfPublish + "\n" +
                                                    "Ilość stron: " + book_list[i].amountOfPages + "\n" +
                                                    "Dostępność: " + "Wypożyczona]")
                                        }
                                        books_ids.add(book_list[i].bookId)
                                    }
                                    Log.d(TAG, books.toString())
                                    Log.d(TAG, copy_books.toString())
                                    arrayAdapter = ArrayAdapter<String>(this@ActivitySearch,
                                        android.R.layout.simple_list_item_1, books.toTypedArray())
                                    bookslist.adapter = arrayAdapter
                                    bookslist.setOnItemClickListener { parent, _, position, _ ->

                                        val dialogClickListener =
                                            DialogInterface.OnClickListener { dialog, which ->
                                                when (which) {
                                                    DialogInterface.BUTTON_NEGATIVE -> {
                                                        dialog.dismiss()
                                                    }
                                                }
                                            }

                                        val builder: AlertDialog.Builder = AlertDialog.Builder(this@ActivitySearch)
                                        Log.d(TAG, copy_books[position])
                                        builder.setMessage(copy_books[position].substring( 1, copy_books[position].length - 1 )).setNegativeButton("Zamknij", dialogClickListener).show()
                                    }
                                }else if (response.code() == 400) {
                                    val gson = Gson()
                                    val message: ResponseObject = gson.fromJson(
                                        response.errorBody()!!.charStream(),
                                        ResponseObject::class.java
                                    )
                                    Toast.makeText(this@ActivitySearch, message.message, Toast.LENGTH_SHORT).show()
                                }
                                books = arrayListOf<String>()
                            }

                            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                                Log.d(TAG, t.toString())
                            }
                        })

                }else if (searchFilter == "Autor"){
                    Log.d(TAG, searchText + " autor")

                    App.getInstance().getApiServices().searchAuthor(searchText)
                        .enqueue(object : Callback<List<Book>> {
                            override fun onResponse(
                                call: Call<List<Book>>,
                                response: Response<List<Book>>
                            ) {
                                book_list = response.body()!!
                                if (response.code() == 200) {
                                    for (i in book_list.indices) {
                                        if(book_list[i].available) {
                                            books.add(book_list[i].title + "\n" + "Dostępna")
                                            copy_books.add("[Tytuł: " + book_list[i].title + "\n" +
                                                    "Autor: " + book_list[i].author + "\n" +
                                                    "Rok wydania: " + book_list[i].yearOfPublish + "\n" +
                                                    "Ilość stron: " + book_list[i].amountOfPages + "\n" +
                                                    "Dostępność: " + "Dostępna]")
                                        }else{
                                            books.add(book_list[i].title + "\n" + "Wypożyczona")
                                            copy_books.add("[Tytuł: " + book_list[i].title + "\n" +
                                                    "Autor: " + book_list[i].author + "\n" +
                                                    "Rok wydania: " + book_list[i].yearOfPublish + "\n" +
                                                    "Ilość stron: " + book_list[i].amountOfPages + "\n" +
                                                    "Dostępność: " + "Wypożyczona]")
                                        }
                                        books_ids.add(book_list[i].bookId)
                                    }
                                    Log.d(TAG, books.toString())
                                    Log.d(TAG, copy_books.toString())
                                    arrayAdapter = ArrayAdapter<String>(this@ActivitySearch,
                                        android.R.layout.simple_list_item_1, books.toTypedArray())
                                    bookslist.adapter = arrayAdapter
                                    bookslist.setOnItemClickListener { parent, _, position, _ ->

                                        val dialogClickListener =
                                            DialogInterface.OnClickListener { dialog, which ->
                                                when (which) {
                                                    DialogInterface.BUTTON_NEGATIVE -> {
                                                        dialog.dismiss()
                                                    }
                                                }
                                            }

                                        val builder: AlertDialog.Builder = AlertDialog.Builder(this@ActivitySearch)
                                        Log.d(TAG, copy_books[position])
                                        builder.setMessage(copy_books[position].substring( 1, copy_books[position].length - 1 )).setNegativeButton("Zamknij", dialogClickListener).show()
                                    }
                                }else if (response.code() == 400) {
                                    val gson = Gson()
                                    val message: ResponseObject = gson.fromJson(
                                        response.errorBody()!!.charStream(),
                                        ResponseObject::class.java
                                    )
                                    Toast.makeText(this@ActivitySearch, message.message, Toast.LENGTH_SHORT).show()
                                }
                                books = arrayListOf<String>()
                            }

                            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                                Log.d(TAG, t.toString())
                            }
                        })

                }else{
                    Log.d(TAG, searchText + " rok")

                    App.getInstance().getApiServices().searchYear(searchText)
                        .enqueue(object : Callback<List<Book>> {
                            override fun onResponse(
                                call: Call<List<Book>>,
                                response: Response<List<Book>>
                            ) {
                                book_list = response.body()!!
                                if (response.code() == 200) {
                                    for (i in book_list.indices) {
                                        if(book_list[i].available) {
                                            books.add(book_list[i].title + "\n" + "Dostępna")
                                            copy_books.add("[Tytuł: " + book_list[i].title + "\n" +
                                                    "Autor: " + book_list[i].author + "\n" +
                                                    "Rok wydania: " + book_list[i].yearOfPublish + "\n" +
                                                    "Ilość stron: " + book_list[i].amountOfPages + "\n" +
                                                    "Dostępność: " + "Dostępna]")
                                        }else{
                                            books.add(book_list[i].title + "\n" + "Wypożyczona")
                                            copy_books.add("[Tytuł: " + book_list[i].title + "\n" +
                                                    "Autor: " + book_list[i].author + "\n" +
                                                    "Rok wydania: " + book_list[i].yearOfPublish + "\n" +
                                                    "Ilość stron: " + book_list[i].amountOfPages + "\n" +
                                                    "Dostępność: " + "Wypożyczona]")
                                        }
                                        books_ids.add(book_list[i].bookId)
                                    }
                                    Log.d(TAG, books.toString())
                                    Log.d(TAG, copy_books.toString())
                                    arrayAdapter = ArrayAdapter<String>(this@ActivitySearch,
                                        android.R.layout.simple_list_item_1, books.toTypedArray())
                                    bookslist.adapter = arrayAdapter
                                    bookslist.setOnItemClickListener { parent, _, position, _ ->

                                        val dialogClickListener =
                                            DialogInterface.OnClickListener { dialog, which ->
                                                when (which) {
                                                    DialogInterface.BUTTON_NEGATIVE -> {
                                                        dialog.dismiss()
                                                    }
                                                }
                                            }

                                        val builder: AlertDialog.Builder = AlertDialog.Builder(this@ActivitySearch)
                                        Log.d(TAG, copy_books[position])
                                        builder.setMessage(copy_books[position].substring( 1, copy_books[position].length - 1 )).setNegativeButton("Zamknij", dialogClickListener).show()
                                    }
                                }else if (response.code() == 400) {
                                    val gson = Gson()
                                    val message: ResponseObject = gson.fromJson(
                                        response.errorBody()!!.charStream(),
                                        ResponseObject::class.java
                                    )
                                    Toast.makeText(this@ActivitySearch, message.message, Toast.LENGTH_SHORT).show()
                                }
                                books = arrayListOf<String>()
                            }

                            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                                Log.d(TAG, t.toString())
                            }
                        })

                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                ActivityMainMenu.startActivity(this@ActivitySearch, user)
            }
        }
        return super.onContextItemSelected(item)
    }
}