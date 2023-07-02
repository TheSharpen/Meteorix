package com.example.meteorix.ui.main

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meteorix.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.*
import android.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.meteorix.*
import com.example.meteorix.data.model.Meteorite
import com.example.meteorix.data.network.MeteoriteApi
import com.example.meteorix.ui.meteoriteDetailFragment.MeteoriteDetailFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var meteoriteApi: MeteoriteApi

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var meteoriteAdapter: MeteoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setAppTheme()
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressBar.isVisible = isLoading
        })

        viewModel.meteoriteListLD.observe(this, Observer { meteorites ->
            meteoriteAdapter.meteorites = meteorites
        })

        setupRecyclerView()
        setupSearchView()
    }

    private fun setAppTheme() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.AppThemeLight)
            Configuration.UI_MODE_NIGHT_YES -> setTheme(R.style.AppThemeDark)
        }
    }

    private fun setupSearchView() {
        binding.svSearchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(query: String?): Boolean {
                binding.progressBar.isVisible = true
                lifecycleScope.launch {
                    val filteredList = meteoriteApi.getMeteorites().body()?.filter { meteorite ->
                        val name = Normalizer.normalize(meteorite.name, Normalizer.Form.NFD)
                            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                            .lowercase(Locale.getDefault())
                        val queryNormalized =
                            Normalizer.normalize(query.orEmpty(), Normalizer.Form.NFD)
                                .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                                .lowercase(Locale.getDefault())
                        name.contains(queryNormalized)
                    } ?: emptyList()

                    binding.progressBar.isVisible = false
                    updateRecyclerView(filteredList)
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
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
            val fragment = MeteoriteDetailFragment().apply { arguments = bundle }
            binding.svSearchText.clearFocus()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentDetailContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvMeteors.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = meteoriteAdapter
        }
    }

    private fun updateRecyclerView(list: List<Meteorite>) {
        meteoriteAdapter.meteorites = list
        binding.progressBar.isVisible = false
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            AlertDialog.Builder(this)
                .setTitle("Quit app?")
                .setMessage("Are you sure you want to quit the app?")
                .setPositiveButton("Yes") { _, _ -> super.onBackPressed() }
                .setNegativeButton("No", null)
                .show()
        } else {
            for (i in count downTo 0) {
                supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setAppTheme()
        recreate()
    }


}
