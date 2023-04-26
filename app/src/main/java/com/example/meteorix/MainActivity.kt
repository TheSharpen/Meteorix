package com.example.meteorix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meteorix.databinding.ActivityMainBinding
import com.example.meteorix.databinding.MeteoriteDetailFragmentBinding
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var meteoriteAdapter: MeteoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        lifecycleScope.launch {
//            getData()
            setupRecyclerView()
        }


    }


    private suspend fun setupRecyclerView() = binding.rvMeteors.apply {
        meteoriteAdapter = MeteoriteAdapter { meteorite ->
            val bundle = Bundle().apply {
                putString("meteoriteName", meteorite.name)
            }

            val fragment = MeteoriteDetailFragment()
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null).commit()
        }

        binding.progressBar.isVisible = true
        val response = try {
            RetrofitInstance.api.getMeteorites()
        } catch (e: IOException) {
            Log.d("XLOG", "IOException, please check your internet connection")
            binding.progressBar.isVisible = false
            return@apply
        } catch (e: HttpException) {
            Log.d("XLOG", "Http Exception, please check your internet connection")
            binding.progressBar.isVisible = false
            return@apply
        }
        if (response.isSuccessful && response.body() != null) {
            binding.progressBar.isVisible = false
            meteoriteAdapter.meteorites = response.body()!!
            Log.d("XLOG", "Success: ${meteoriteAdapter.meteorites}")
        } else {
            binding.progressBar.isVisible = false
            Log.e("XLOG", "Response failed")
        }
        Log.d("XLOG", "Setup RecyclerView ${meteoriteAdapter.meteorites}")
        layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = meteoriteAdapter
    }



}