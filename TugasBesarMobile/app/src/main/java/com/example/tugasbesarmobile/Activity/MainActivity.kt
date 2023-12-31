package com.example.tugasbesarmobile.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.tugasbesarmobile.API.APIRequestData
import com.example.tugasbesarmobile.API.RetroServer
import com.example.tugasbesarmobile.Adapter.AdapterData
import com.example.tugasbesarmobile.Model.DataModel
import com.example.tugasbesarmobile.Model.ResponseModel
import com.example.tugasbesarmobile.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.SearchView;

class MainActivity : AppCompatActivity() {
    var rvData: RecyclerView? = null
    var adData: RecyclerView.Adapter<*>? = null
    var lmData: RecyclerView.LayoutManager? = null
    var listData: List<DataModel> = ArrayList<DataModel>()
    var srlData: SwipeRefreshLayout? = null
    var pbData: ProgressBar? = null

    var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvData = findViewById(R.id.rv_data)
        srlData = findViewById(R.id.srl_data)
        pbData = findViewById(R.id.pb_data)
        var btnTambah = findViewById<FloatingActionButton>(R.id.btn_tambah)

        lmData = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvData?.layoutManager = lmData



        //retrievedata();
        with(srlData) {
            this?.setOnRefreshListener(OnRefreshListener {
                setRefreshing(true)
                retrieveData()
                setRefreshing(false)
            })
        }

        btnTambah.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    TambahActivity::class.java
                )
            )
        })
        searchView = findViewById(R.id.searchView)
        setupSearchView()
    }

    override fun onResume() {
        super.onResume()
        retrieveData()
    }

    fun retrieveData() {
        pbData!!.visibility = View.VISIBLE
        val ardData: APIRequestData = RetroServer.konekRetrofit().create(APIRequestData::class.java)
        val tampilData: Call<ResponseModel> = ardData.ardRetrieveData()

        tampilData.enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (response.isSuccessful) {
                    val responseData = response.body()

                    if (responseData != null) {
                        // Periksa apakah getData() tidak null sebelum mengaksesnya
                        val data = responseData.getData()

                        if (data != null) {
                            listData = data
                            adData = AdapterData(this@MainActivity, listData)
                            rvData!!.adapter = adData
                            adData!!.notifyDataSetChanged()
                            pbData!!.visibility = View.INVISIBLE
                        } else {
                            // Tangani kasus ketika data null
                            Log.e("MainActivity", "Data is null")
                        }
                    } else {
                        // Tangani kasus respons null
                        Log.e("MainActivity", "Response body is null")
                    }
                } else {
                    // Tangani kasus respons tidak sukses
                    Log.e("MainActivity", "Response not successful: ${response.code()}")
                }

            }


            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "Gagel Terkoneksi: " + t.message,
                    Toast.LENGTH_SHORT
                ).show()
                pbData!!.visibility = View.INVISIBLE
                srlData?.isRefreshing = false

            }
        })
    }
    private fun setupSearchView() {
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle when search button is pressed (optional)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle when text in the SearchView changes
                filterData(newText)
                return true
            }
        })
    }

    private fun filterData(query: String?) {
        val filteredList = listData.filter { data ->
            data.judul.contains(query.orEmpty(), ignoreCase = true) ||
                    data.deskripsi.contains(query.orEmpty(), ignoreCase = true) ||
                    data.tanggal.contains(query.orEmpty(), ignoreCase = true)
        }

        adData?.let {
            (it as AdapterData).filterList(filteredList)
        }
    }
}


