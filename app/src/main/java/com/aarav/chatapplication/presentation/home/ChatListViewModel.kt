package com.aarav.chatapplication.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarav.chatapplication.domain.model.User
import com.aarav.chatapplication.domain.repository.AuthRepository
import com.aarav.chatapplication.domain.repository.ChatListRepository
import com.aarav.chatapplication.domain.repository.MessageRepository
import com.aarav.chatapplication.domain.repository.PresenceRepository
import com.aarav.chatapplication.domain.repository.UserRepository
import com.aarav.chatapplication.presentation.model.ChatListItem
import com.aarav.chatapplication.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatListViewModel
@Inject constructor(
    val messageRepository: MessageRepository,
    val userRepository: UserRepository,
    val chatListRepository: ChatListRepository,
    val authRepository: AuthRepository,
    val presenceRepository: PresenceRepository
) : ViewModel() {
    private var _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

        init {
            getUserList()
            observeUserPresence()
        }

    private fun observeUserPresence() {
        viewModelScope.launch {
            uiState
                .map { it.userId }
                .distinctUntilChanged()
                .collect { userId ->
                    userId?.let {
                        Log.i("PRESENCE", "Presence logged")
                        presenceRepository.setupPresence(it)
                    }
                }
        }
    }

    fun observeChatList(myId: String) {

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true) }

            delay(500)

            Log.i("MYTAG", "my id : " + myId)
            chatListRepository.observeUserChats(myId)
                .collect { chatIds ->

                    if (chatIds.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                chatList = emptyList()
                            )
                        }
                        return@collect
                    }

                    val chatFlows = chatIds.map { chatId ->

                        val otherUserId = chatId.split("_")
                            .first { it != myId }

                        combine(
                            chatListRepository.observeChatMeta(chatId),
                            chatListRepository.observeUnread(myId, chatId),
//                            presenceRepository.observePresence(otherUserId),
                            userRepository.findUserByUserId(otherUserId)
                        ) { meta, unread,
                            //presence,
                            user ->

                            ChatListItem(
                                chatId = chatId,
                                otherUserId = otherUserId,
                                otherUserName = user.name ?: "",
                                lastMessage = meta.first,
                                lastTimestamp = meta.second,
                                unreadCount = unread,
                                isOnline = false
                            )
                                .also {
                                    markChatDeliveredIfNeeded(
                                        chatId = chatId,
                                        myId = myId,
                                        unreadCount = unread
                                    )
                                }
                        }
                    }


                    combine(chatFlows) { itemsArray ->

                        Log.i("MYTAG", "final list : " + itemsArray.toString())
                        itemsArray
                            .toList()
                            .sortedByDescending { it.lastTimestamp }

                    }.collect { finalList ->

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                chatList = finalList
                            )
                        }
                    }
                }
        }
    }

    fun getUserId() {
        when (val result = userRepository.getCurrentUser()) {
            is Result.Success -> {
                _uiState.update {
                    it.copy(
                        userId = result.data
                    )
                }
            }

            is Result.Error -> {
                _uiState.update {
                    it.copy(
                        showErrorDialog = true,
                        error = result.message
                    )
                }
            }
        }
    }

    private fun markChatDeliveredIfNeeded(
        chatId: String,
        myId: String,
        unreadCount: Int
    ) {
        if (unreadCount <= 0) return

        viewModelScope.launch {
            messageRepository.makeChatMessagesDelivered(
                chatId = chatId,
                receiverId = myId
            )
        }
    }

    fun getUserList() {
        viewModelScope.launch {
            userRepository.getUserList()
                .catch { e ->
                    Log.i("MYTAG", e.message.toString())
                }
                .collect { user ->
                    _uiState
                        .update {
                            it.copy(
                                userList = user
                            )
                        }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }


}

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val showErrorDialog: Boolean = false,
    val chatList: List<ChatListItem> = emptyList(),
    val userList: List<User> = emptyList(),
    val userId: String? = null
)