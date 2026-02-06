package com.aarav.chatapplication.di

import com.aarav.chatapplication.data.repository.AuthRepositoryImpl
import com.aarav.chatapplication.data.repository.MessageRepositoryImpl
import com.aarav.chatapplication.data.repository.PresenceRepositoryImpl
import com.aarav.chatapplication.data.repository.TypingRepositoryImpl
import com.aarav.chatapplication.data.repository.UserRepositoryImpl
import com.aarav.chatapplication.domain.repository.AuthRepository
import com.aarav.chatapplication.domain.repository.MessageRepository
import com.aarav.chatapplication.domain.repository.PresenceRepository
import com.aarav.chatapplication.domain.repository.TypingRepository
import com.aarav.chatapplication.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindMessageRepository(
        messageRepositoryImpl: MessageRepositoryImpl
    ): MessageRepository

    @Binds
    abstract fun bindTypingRepository(
        typingRepositoryImpl: TypingRepositoryImpl
    ): TypingRepository

    @Binds
    abstract fun bindPresenceRepository(
        presenceRepositoryImpl: PresenceRepositoryImpl
    ): PresenceRepository
}