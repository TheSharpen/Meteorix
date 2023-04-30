package com.example.meteorix

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meteorix.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var meteoriteAdapter: MeteoriteAdapter
    private lateinit var searchText: SearchView
    private var currentQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchText = binding.svSearchText
        binding.rvMeteors.adapter = MeteoriteAdapter { meteorite ->

            val bundle = Bundle().apply {
                putString("meteoriteName", meteorite.name)
                putString("meteoriteReclat", meteorite.reclat.toString())
                putString("meteoriteReclong", meteorite.reclong.toString())
            }

            val fragment = MeteoriteDetailFragment()
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null).commit()
        }

        lifecycleScope.launch {
            binding.progressBar.isVisible = true
            val response = try {
                RetrofitInstance.api.getMeteorites()
            } catch (e: IOException) {
                binding.progressBar.isVisible = false
                return@launch
            } catch (e: HttpException) {
                binding.progressBar.isVisible = false
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                binding.progressBar.isVisible = false
                meteoriteAdapter.meteorites = response.body()!!
            } else {
                binding.progressBar.isVisible = false
            }
        }
        setupRecyclerView()
        setupSearchView()
    }

    private fun setupSearchView() {
        searchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                binding.progressBar.isVisible = true
                lifecycleScope.launch {
                val filteredList = RetrofitInstance.api.getMeteorites().body()!!.filter { meteorite ->
                    meteorite.name.contains(query.orEmpty(),ignoreCase = true)
                }
                    binding.progressBar.isVisible = false
                    updateRecyclerView(filteredList)
                }
                return true
            }
        })
        searchText.setOnCloseListener {
            searchText.setQuery("", false )
            val filteredList = meteoriteAdapter.filter("")
            updateRecyclerView(filteredList)
            true
        }
    }

    private fun setupRecyclerView() {
        binding.rvMeteors.apply {
            meteoriteAdapter = MeteoriteAdapter { meteorite ->
                val bundle = Bundle().apply {
                    putString("meteoriteName", meteorite.name)
                    putString("meteoriteReclong", meteorite.reclong.toString())
                    putString("meteoriteReclat", meteorite.reclat.toString())
                    putString("meteoriteFell", meteorite.fall)
                    putString("meteoriteYear", meteorite.year)
                    putString("meteoriteMass", meteorite.mass)
                    putString("meteoriteNametype", meteorite.nametype)
                }

                val fragment = MeteoriteDetailFragment()
                fragment.arguments = bundle
                binding.svSearchText.clearFocus()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentDetailContainer, fragment).addToBackStack(null).commit()
            }

            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = meteoriteAdapter
        }
    }

    private fun updateRecyclerView(list: List<Meteorite>) {
        meteoriteAdapter.meteorites = list
    }

    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            AlertDialog.Builder(this).setTitle("Quit app?")
                .setMessage("Are you sure you want to quit the app?")
                .setPositiveButton("Yes") { _, _ -> super.onBackPressed() }
                .setNegativeButton("No", null).show()

        } else {
            supportFragmentManager.popBackStack()
        }
    }
}