package com.example.qrcodescanner

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_loan.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityLoan : AppCompatActivity() {

    companion object{
        const val REQUEST_CODE_LOAN = 1000
        private const val TAG = "MAIN_TAG"
        private var info: String = ""
        private var copy_book = Book(-1, "error", "error", -1, -1, false)
        private var copy_user = Client("error", "error", "error", "error", "error", -1)
        fun startActivity(activity: AppCompatActivity, book: Book, user: Client){
            val intent = Intent(activity, ActivityLoan::class.java)
            activity.startActivityForResult(intent, REQUEST_CODE_LOAN)
            copy_book = book
            copy_user = user
            info = "Tytuł: " + book.title + "\nAutor: " + book.author + "\nIlość stron: " +
                    book.amountOfPages + "\nRok publikacji: " + book.yearOfPublish
        }
    }

    private lateinit var Loan_btn: MaterialButton
    private lateinit var Return_btn: MaterialButton
    private lateinit var Leave_btn: MaterialButton
    private lateinit var bookinfo: TextView
    private lateinit var top_loan: TextView
    private lateinit var book_list: List<Book>
    private var book_to_order = OrderBook( -1, -1)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loan)

        Loan_btn = findViewById(R.id.Loan_btn)
        Return_btn = findViewById(R.id.Return_btn)
        Leave_btn = findViewById(R.id.Leave_btn)
        bookinfo = findViewById(R.id.bookinfo)
        top_loan = findViewById(R.id.top_loan)

        Leave_btn.isVisible = false;

        bookinfo.text = info
        if(copy_book.available) {
            Log.d(TAG, "dostępna")
            top_loan.text = "Książka dostępna"
            Loan_btn.isVisible = true
            Leave_btn.isVisible = false
        }else{
            top_loan.text = "Książka wypożyczona"
            Log.d(TAG, "porównanie")
            Log.d(TAG, copy_user.userId.toString())
            App.getInstance().getApiServices().userInfo(copy_user.userId)
                .enqueue(object : Callback<List<Book>> {
                    override fun onResponse(
                        call: Call<List<Book>>,
                        response: Response<List<Book>>
                    ) {
                        book_list = response.body()!!
                        Log.d(TAG, "jestem tu")
                        Log.d(TAG, response.body()!!.toString())
                        Log.d(TAG, book_list.indices.toString())
                        if (book_list.isEmpty()){
                            Loan_btn.isVisible = false
                            Leave_btn.isVisible = false
                        }else {
                            for (i in book_list.indices) {
                                Log.d(TAG, i.toString())
                                Log.d(TAG, book_list[i].bookId.toString())
                                Log.d(TAG, copy_book.bookId.toString())
                                if (book_list[i].bookId == copy_book.bookId) {
                                    Loan_btn.isVisible = false
                                    Leave_btn.isVisible = true
                                    break
                                } else if (i == book_list.size - 1) {
                                    Loan_btn.isVisible = false
                                    Leave_btn.isVisible = false
                                }
                            }
                        }
                    }
                    override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                        Log.d(TAG, t.toString())
                    }
                })
        }

        Leave_btn.setOnClickListener {
            book_to_order.bookIdToOrder = copy_book.bookId
            book_to_order.userId = copy_user.userId
            App.getInstance().getApiServices().returnBook(book_to_order).enqueue(object : Callback<ResponseObject> {
                override fun onResponse(
                    call: Call<ResponseObject>,
                    response: Response<ResponseObject>
                ){
                    if (response.code() == 200) {
                        Toast.makeText(this@ActivityLoan, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        Loan_btn.isVisible = true;
                        Leave_btn.isVisible = false;
                        top_loan.text = "Książka dostępna"
                    }else if (response.code() == 400) {
                        val gson = Gson()
                        val message: ResponseObject = gson.fromJson(
                            response.errorBody()!!.charStream(),
                            ResponseObject::class.java
                        )
                        Toast.makeText(this@ActivityLoan, message.message, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ResponseObject>, t: Throwable) {
                    Log.d(TAG, t.toString())
                }
            })
        }

        Loan_btn.setOnClickListener {
            book_to_order.bookIdToOrder = copy_book.bookId
            book_to_order.userId = copy_user.userId
            App.getInstance().getApiServices().orderBook(book_to_order).enqueue(object : Callback<ResponseObject> {
            override fun onResponse(
                call: Call<ResponseObject>,
                response: Response<ResponseObject>
            ){
                Log.d(TAG, response.code().toString())
                if (response.code() == 200) {
                    Toast.makeText(this@ActivityLoan, response.body()!!.message, Toast.LENGTH_SHORT).show()
                    Loan_btn.isVisible = false;
                    Leave_btn.isVisible = true;
                    top_loan.text = "Książka wypożyczona"
                }else if (response.code() == 400) {
                    val gson = Gson()
                    val message: ResponseObject = gson.fromJson(
                        response.errorBody()!!.charStream(),
                        ResponseObject::class.java
                    )
                    Toast.makeText(this@ActivityLoan, message.message, Toast.LENGTH_SHORT).show()
                }
            }
                override fun onFailure(call: Call<ResponseObject>, t: Throwable) {
                    Log.d(TAG, t.toString())
                }
            })
        }

        Return_btn.setOnClickListener {
            MainActivity.startActivity(this@ActivityLoan)
        }
    }
}