package com.example.wallpapers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wallpapers.API.ApiInterface
import com.example.wallpapers.API.ClientApi
import com.example.wallpapers.Adapters.CategoryAdapter
import com.example.wallpapers.Adapters.ImageAdapter
import com.example.wallpapers.Response.Hit
import com.example.wallpapers.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.appcompat.widget.SearchView.OnQueryTextListener


const val apiKey = "37417144-894bbe8418f11837152e856d4"
const val orientation = "vertical"

class MainActivity : AppCompatActivity(), CategoryAdapter.RecyclerViewEvent, ImageAdapter.ImageAdapterEvent{
    private lateinit var binding: ActivityMainBinding
    private val retrofit: ApiInterface = ClientApi.retrofit.create(ApiInterface::class.java)
    private var imageslist = listOf<Hit>()
    private var list = listOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeCategoryAdapter()

        loadImagesAll()

        binding.search.setOnClickListener {
            toggleSearchBar()
        }

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                binding.pgBar.isIndeterminate = true
                binding.pgBackground.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    val result = retrofit.loadAll(apiKey, orientation,query)
                    imageslist = result.body()!!.hits

                    withContext(Dispatchers.Main){
                        if(imageslist.isNotEmpty()){
                            binding.sorry.visibility = View.GONE
                            binding.rvMain.visibility = View.VISIBLE
                            binding.rvMain.adapter = ImageAdapter(imageslist, this@MainActivity,this@MainActivity)
                            binding.rvMain.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                            binding.pgBar.isIndeterminate = false
                            binding.pgBackground.visibility = View.GONE
                        }else{
                            binding.sorry.visibility = View.VISIBLE
                            binding.rvMain.visibility = View.GONE
                            binding.pgBar.isIndeterminate = false
                            binding.pgBackground.visibility = View.GONE
                        }
                    }
                }
                binding.searchView.clearFocus()

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                binding.sorry.visibility = View.GONE

                return true
            }
        })

    }

    private fun toggleSearchBar() {
        if(binding.searchView.visibility == View.GONE){
            binding.search.setImageResource(R.drawable.up)
            binding.searchView.visibility = View.VISIBLE
        }else{
            binding.search.setImageResource(R.drawable.search)
            binding.searchView.visibility = View.GONE
            binding.searchView.setQuery("", false)
        }
    }

    private fun loadImagesAll() {
        binding.pgBar.isIndeterminate = true
        binding.pgBackground.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.loadAll(apiKey, orientation,"wallpapers")
            imageslist = response.body()!!.hits
            withContext(Dispatchers.Main){
                if(imageslist.isNotEmpty()){
                    binding.sorry.visibility = View.GONE
                    binding.rvMain.visibility = View.VISIBLE
                    binding.rvMain.adapter = ImageAdapter(imageslist, this@MainActivity,this@MainActivity)
                    binding.rvMain.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    binding.pgBar.isIndeterminate = false
                    binding.pgBackground.visibility = View.GONE
                }else{
                    binding.sorry.visibility = View.VISIBLE
                    binding.rvMain.visibility = View.GONE
                    binding.pgBar.isIndeterminate = false
                    binding.pgBackground.visibility = View.GONE
                }

            }
        }
    }

    private fun initializeCategoryAdapter() {
        list = listOf<String>(
            "science",
            "nature",
            "fashion",
            "education",
            "feelings",
            "computer",
            "food",
            "sports",
            "people",
            "industry",
            "places",
            "buildings",
            "business",
            "animals"
        )

        binding.rvCategory.adapter = CategoryAdapter(list,this)
        binding.rvCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun makeImageListForParcing(): List<String>{
        val list =  mutableListOf<String>()
        for(image in imageslist){
            list.add(image.largeImageURL)
        }
        return list
    }

    override fun onItemClick(position: Int) {
        binding.pgBar.isIndeterminate = true
        binding.pgBackground.visibility = View.VISIBLE
        val category = list[position]

        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.loadCategory(apiKey, orientation,category)
            imageslist = response.body()!!.hits

            withContext(Dispatchers.Main){
                if(imageslist.isNotEmpty()){
                    binding.sorry.visibility = View.GONE
                    binding.rvMain.visibility = View.VISIBLE
                    binding.rvMain.adapter = ImageAdapter(imageslist, this@MainActivity,this@MainActivity)
                    binding.rvMain.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    binding.pgBar.isIndeterminate = false
                    binding.pgBackground.visibility = View.GONE
                }else{
                    binding.sorry.visibility = View.VISIBLE
                    binding.rvMain.visibility = View.GONE
                    binding.pgBar.isIndeterminate = false
                    binding.pgBackground.visibility = View.GONE
                }
            }
        }
    }

    override fun onImageClick(position: Int) {
        val intent = Intent(this,PreviewActivity::class.java)
        intent.putExtra("url", imageslist[position].largeImageURL)
        startActivity(intent)
    }
}


