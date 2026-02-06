package com.aarav.chatapplication.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarav.chatapplication.domain.model.User
import com.aarav.chatapplication.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel
@Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {

    init {
        getUserList()
        getUser("lkwcgdykDwa8F7lgtYcmLo01tO83")
    }

    private val _userList: MutableStateFlow<List<User>> = MutableStateFlow(emptyList())
    val userList: StateFlow<List<User>> = _userList.asStateFlow()

    fun getUserList() {
        viewModelScope.launch {
            userRepository.getUserList()
                .catch {
                        e -> Log.i("MYTAG", e.message.toString())
                }
                .collect {
                        user ->
                    _userList.value = user
                }
        }
    }

    fun getUser(userId: String) {
        viewModelScope.launch {
            userRepository.findUserByUserId(userId)
        }
    }
}