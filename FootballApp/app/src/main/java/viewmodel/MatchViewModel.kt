package com.example.footballapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.footballapp.data.model.Match
import com.example.footballapp.data.repository.FootballRepository
import kotlinx.coroutines.launch

class MatchViewModel : ViewModel() {

    private val repository = FootballRepository()

    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>> = _matches

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // 오늘 경기 로드 (전체 리그)
    fun loadTodayMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = repository.getTodayMatches()

            result.onSuccess { matchList ->
                _matches.value = matchList
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "알 수 없는 오류가 발생했습니다"
                _isLoading.value = false
            }
        }
    }

    // 특정 날짜 경기 로드
    fun loadMatchesByDate(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = repository.getMatchesByDate(date)

            result.onSuccess { matchList ->
                _matches.value = matchList
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "알 수 없는 오류가 발생했습니다"
                _isLoading.value = false
            }
        }
    }

    // 특정 리그 로드 (리그 코드로!)
    fun loadLeagueMatches(leagueCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = repository.getLeagueMatches(leagueCode)

            result.onSuccess { matchList ->
                _matches.value = matchList
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "알 수 없는 오류가 발생했습니다"
                _isLoading.value = false
            }
        }
    }
}