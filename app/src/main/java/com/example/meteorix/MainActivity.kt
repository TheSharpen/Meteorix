package com.example.meteorix

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.database.ContentObserver
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meteorix.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.Normalizer
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var meteoriteAdapter: MeteoriteAdapter
    private lateinit var searchText: SearchView
    private var currentQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        setAppTheme()

        supportActionBar?.hide()
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchText = binding.svSearchText

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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setAppTheme()
        recreate()
    }

    private fun setAppTheme() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.AppThemeLight)
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.AppThemeDark)
        }
    }


    private fun setupSearchView() {
        searchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                binding.progressBar.isVisible = true
                lifecycleScope.launch {
                    val filteredList =
                        RetrofitInstance.api.getMeteorites().body()!!.filter { meteorite ->
                            val name = Normalizer.normalize(meteorite.name, Normalizer.Form.NFD)
                                .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                                .lowercase(Locale.getDefault())
                            val queryNormalized =
                                Normalizer.normalize(query.orEmpty(), Normalizer.Form.NFD)
                                    .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                                    .lowercase(Locale.getDefault())
                            name.contains(queryNormalized)
                        }
                    binding.progressBar.isVisible = false
                    updateRecyclerView(filteredList)
                }
                return true
            }

        })
        searchText.setOnCloseListener {
            searchText.setQuery("", false)
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