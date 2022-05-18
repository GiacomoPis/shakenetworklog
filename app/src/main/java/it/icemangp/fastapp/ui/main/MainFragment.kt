package it.icemangp.fastapp.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import it.icemangp.fastapp.databinding.MainFragmentBinding
import it.icemangp.shakenetworklog.data.NetworkLogManager

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNetworkCall200.setOnClickListener {
            viewModel.startNetworkCall200()
        }

        binding.buttonNetworkCall400.setOnClickListener {
            viewModel.startNetworkCall400()
        }

        binding.buttonOpenNetworkLog.setOnClickListener {
            NetworkLogManager.openLog()
        }
    }
}