package com.rodriguez.manuel.teckbookmovil.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodriguez.manuel.teckbookmovil.databinding.FragmentAulasBinding
import com.rodriguez.manuel.teckbookmovil.ui.common.adapters.AulasAdapter
import com.rodriguez.manuel.teckbookmovil.ui.main.viewmodels.AulasViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.util.Log

class AulasFragment : Fragment() {

    private var _binding: FragmentAulasBinding? = null
    private val binding get() = _binding!!

    private val aulasViewModel: AulasViewModel by viewModels() // Si tienes Factory úsalo
    private val aulasAdapter = AulasAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAulasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewAulas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = aulasAdapter
        }

        lifecycleScope.launch {
            aulasViewModel.aulasState.collectLatest { state ->
                when (state) {
                    is AulasViewModel.AulaState.Loading -> {
                        // Muestra loading si quieres
                    }
                    is AulasViewModel.AulaState.Success -> {
                        aulasAdapter.submitList(state.data.aulas) // Ajusta a tu modelo
                    }
                    is AulasViewModel.AulaState.Error -> {
                        // Muestra error si quieres
                    }
                    is AulasViewModel.AulaState.Success -> {
                        Log.d("AulasFragment", "Aulas: ${state.data.aulas}")
                        aulasAdapter.submitList(state.data.aulas)
                    }
                    else -> Unit
                }
            }
        }

        aulasViewModel.loadAulas()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
