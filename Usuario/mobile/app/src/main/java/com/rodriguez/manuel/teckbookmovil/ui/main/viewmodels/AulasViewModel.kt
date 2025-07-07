package com.rodriguez.manuel.teckbookmovil.ui.main.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodriguez.manuel.teckbookmovil.data.models.aula.AulasResponse
import com.rodriguez.manuel.teckbookmovil.data.repositories.AulaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AulasViewModel(private val aulaRepository: AulaRepository) : ViewModel() {

    sealed class AulaState {
        object Idle : AulaState()
        object Loading : AulaState()
        data class Success(val data: AulasResponse) : AulaState()
        data class Error(val message: String) : AulaState()
    }

    private val _aulasState = MutableStateFlow<AulaState>(AulaState.Idle)
    val aulasState: StateFlow<AulaState> = _aulasState

    fun loadAulas() {
        _aulasState.value = AulaState.Loading

        viewModelScope.launch {
            val result = aulaRepository.getMyAulas()
            result.onSuccess {
                _aulasState.value = AulaState.Success(it)
            }.onFailure {
                _aulasState.value = AulaState.Error(it.localizedMessage ?: "Error desconocido")
            }
        }
    }
}
