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
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var meteoriteAdapter = MeteoriteAdapter { meteorite ->

        val bundle = Bundle().apply {
            putString("meteoriteName", meteorite.name)
        }

        val fragment = MeteoriteDetailFragment()
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                binding.progressBar.isVisible = true
                val response = try {
                    RetrofitInstance.api.getMeteorites()
                } catch (e: IOException) {
                    Log.d("TAG","IOException, please check your internet connection")
                    binding.progressBar.isVisible = false
                    return@repeatOnLifecycle
                } catch (e: HttpException) {
                    Log.d("TAG","Http Exception, please check your internet connection")
                    binding.progressBar.isVisible = false
                    return@repeatOnLifecycle
                }
                if (response.isSuccessful && response.body() != null ) {
                    meteoriteAdapter.meteorites = response.body()!!
                    Log.d("XLOG","${meteoriteAdapter.meteorites}")
                } else {
                    Log.e("TAG", "Response failed")
                }
                binding.progressBar.isVisible = false
            }
        }
    }




//        private fun setupRecyclerView() = binding.rvMeteors.apply {
//            meteoriteAdapter = MeteoriteAdapter()
//            adapter = meteoriteAdapter
//            layoutManager = LinearLayoutManager(this@MainActivity)
//        }


    private fun setupRecyclerView() = binding.rvMeteors.apply {
        meteoriteAdapter = MeteoriteAdapter { meteorite ->
            val bundle = Bundle().apply {
                putString("meteoriteName", meteorite.name)
            }

            val fragment = MeteoriteDetailFragment()
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        adapter = meteoriteAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)
    }
}