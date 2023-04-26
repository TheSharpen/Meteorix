package com.example.meteorix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.meteorix.databinding.MeteoriteDetailFragmentBinding

class MeteoriteDetailFragment: Fragment() {

    private lateinit var binding: MeteoriteDetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = MeteoriteDetailFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val meteoriteName = arguments?.getString("meteoriteName")

        binding.tvMeteoriteName.text = meteoriteName
    }

    companion object {

        fun newInstance(meteoriteName: String): MeteoriteDetailFragment {
            val args = Bundle().apply {
                putString("meteoriteName",meteoriteName)
            }
            return MeteoriteDetailFragment().apply {
                arguments = args
            }
        }
    }

}