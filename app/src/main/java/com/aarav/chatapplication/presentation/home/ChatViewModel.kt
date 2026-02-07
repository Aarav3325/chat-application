package com.aarav.chatapplication.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarav.chatapplication.data.model.Message
import com.aarav.chatapplication.data.model.MessageStatus
import com.aarav.chatapplication.data.model.Presence
import com.aarav.chatapplication.domain.model.User
import com.aarav.chatapplication.domain.repository.MessageRepository
import com.aarav.chatapplication.domain.repository.PresenceRepository
import com.aarav.chatapplication.domain.repository.TypingRepository
import com.aarav.chatapplication.domain.repository.UserRepository
import com.aarav.chatapplication.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel
@Inject constructor(
    val userRepository: UserRepository,
    val messageRepository: MessageRepository,
    val typingRepository: TypingRepository,
    val presenceRepository: PresenceRepository
) : ViewModel() {


    private var _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()


    private var currentChatId: String? = null
    private var currentUserId: String? = null

//    init {
//       // getUserList()
//        getUser()
//    }

    private val _userList: MutableStateFlow<List<User>> = MutableStateFlow(emptyList())
    val userList: StateFlow<List<User>> = _userList.asStateFlow()

    fun observePresence(otherUserId: String) {
        Log.i("MYTAG", otherUserId)

        viewModelScope.launch {
            presenceRepository.observePresence(otherUserId)
                .collect {
                    presence ->
                    _uiState.update {
                        it.copy(
                            presence = presence
                        )
                    }
                }
        }
    }

    fun observeTyping() {
        val chatId = currentChatId ?: return
        val myId = currentUserId ?: return

        viewModelScope.launch {
            typingRepository.observeTyping(chatId)
                .collect { typingUser ->
                    _uiState.update {
                        it.copy(
                            isOtherUserTyping = typingUser.any {
                                it != myId
                            }
                        )
                    }
                }
        }
    }

    fun onTypingStarted() {
        val chatId = currentChatId ?: return
        val myId = currentUserId ?: return

        viewModelScope.launch {
            typingRepository.setTyping(chatId, myId)
        }
    }

    fun onTypingStopped() {

        val chatId = currentChatId ?: return
        val myId = currentUserId ?: return

        viewModelScope.launch {
            typingRepository.clearTyping(chatId, myId)
        }
    }

    fun observeMessages(chatId: String, myId: String) {
        currentChatId = chatId
        currentUserId = myId

        viewModelScope.launch {
            observeTyping()

            messageRepository.listenMessages(chatId)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            error = e.message,
                            showErrorDialog = true
                        )
                    }
                }
                .collect { messageList ->
                    _uiState.update {
                        it.copy(
                            messages = messageList
                        )
                    }

//                    autoMarkDelivered(messageList)
                    autoMarksRead(messageList)
                }
        }
    }

    fun sendMessages(receiverId: String, text: String) {
        val chatId = currentChatId ?: return
        val senderId = currentUserId ?: return

        if (text.isBlank()) {
            _uiState.update {
                it.copy(
                    messageError = "Message cannot be blank"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState
                .update {
                    it.copy(
                        isSending = true
                    )
                }

            when (val result = messageRepository.sendMessage(
                chatId,
                senderId,
                receiverId,
                text
            )
            ) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isSending = false,
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            error = result.message,
                            showErrorDialog = true
                        )
                    }
                }
            }
        }
    }

//    private fun autoMarkDelivered(messages: List<Message>) {
//        val chatId = currentChatId ?: return
//        val myId = currentUserId ?: return
//
//        viewModelScope.launch {
//            val pending = messages.filter {
//                it.status == MessageStatus.SENT.name
//                        && it.senderId != myId
//            }
//
//            if (pending.isNotEmpty()) {
//                messageRepository.makeMessageDelivered(
//                    chatId,
//                    pending.map { it.messageId }
//                )
//            }
//        }
//    }

    fun autoMarksRead(messages: List<Message>) {
        val chatId = currentChatId ?: return
        val myId = currentUserId ?: return

        viewModelScope.launch {
            val unread = messages.filter {
                it.senderId != myId &&
                        it.status != MessageStatus.READ.name
            }

            if (unread.isNotEmpty()) {
                Log.i("MESSAGE", unread.size.toString())
                messageRepository.makeMessageRead(
                    chatId,
                    myId,
                    unread.map { it.messageId }
                )
                Log.i("MESSAGE", "MARKED SEEN")
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(
                showErrorDialog = false,
                error = null
            )
        }
    }


//    fun getUserList() {
//        viewModelScope.launch {
//            userRepository.getUserList()
//                .catch { e ->
//                    Log.i("MYTAG", e.message.toString())
//                }
//                .collect { user ->
//                    _userList.value = user
//                }
//        }
//    }

    fun getUser(userId: String) {
        viewModelScope.launch {
            userRepository.findUserByUserId(userId)
                .collect {
                    user ->
                    _uiState.update {
                        it.copy(
                            user = user
                        )
                    }
                }
        }
    }

//    override fun onCleared() {
//        super.onCleared()
//
//        if(currentChatId != null && currentUserId != null) {
//            viewModelScope.launch {
//                typingRepository.clearTyping(currentChatId.toString(), currentUserId.toString())
//            }
//        }
//    }
}

data class ChatUiState(
    val error: String? = null,
    val messageError: String? = null,
    val showErrorDialog: Boolean = false,
    val messages: List<Message> = emptyList(),
    val isSending: Boolean = false,
    val isOtherUserTyping: Boolean = false,
    val presence: Presence? = null,
    val user: User? = null
)