package com.example.meteorix

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meteorix.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var meteoriteAdapter: MeteoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMeteors.adapter = MeteoriteAdapter { meteorite ->

            val bundle = Bundle().apply {

                try {
                    putString("meteoriteName", meteorite.name)
                    Log.d("XLOG", "meteorite name: ${meteorite.name}")

                    putString("meteoriteReclat", meteorite.reclat.toString())
                    Log.d("XLOG", "meteorite reclat: ${meteorite.reclat}")

                    putString("meteoriteReclong", meteorite.reclong.toString())

                    Log.d("XLOG", "meteorite reclong: ${meteorite.reclong}")
                } catch (e: Exception) {
                    Log.e("XLOG", "Error saving meteorite data: ${e.message}")
                }

            }

            val fragment = MeteoriteDetailFragment()
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment)
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
                Log.d("XLOG", "meteorites: ${meteoriteAdapter.meteorites}")
            } else {
                binding.progressBar.isVisible = false
            }
        }


        setupRecyclerView()

    }


    private fun setupRecyclerView() = binding.rvMeteors.apply {
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
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null).commit()
        }



        layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = meteoriteAdapter
        Log.d("XLOG", "Meteorites: $meteoriteAdapter")
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