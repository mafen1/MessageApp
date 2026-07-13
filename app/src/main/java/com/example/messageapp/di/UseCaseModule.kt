package com.example.messageapp.di

import com.example.messageapp.domain.repository.AuthRepository
import com.example.messageapp.domain.repository.FriendRepository
import com.example.messageapp.domain.repository.MessageRepository
import com.example.messageapp.domain.repository.NewsRepository
import com.example.messageapp.domain.repository.PreferencesRepository
import com.example.messageapp.domain.repository.UserRepository
import com.example.messageapp.domain.usecase.AcceptFriendRequestUseCase
import com.example.messageapp.domain.usecase.AppPreferencesUseCase
import com.example.messageapp.domain.usecase.CommentPostUseCase
import com.example.messageapp.domain.usecase.CreatePostUseCase
import com.example.messageapp.domain.usecase.GetAllUsersUseCase
import com.example.messageapp.domain.usecase.GetChatHistoryUseCase
import com.example.messageapp.domain.usecase.GetCurrentUserUseCase
import com.example.messageapp.domain.usecase.GetFriendRequestsUseCase
import com.example.messageapp.domain.usecase.GetFriendsUseCase
import com.example.messageapp.domain.usecase.GetNewsFeedUseCase
import com.example.messageapp.domain.usecase.LikePostUseCase
import com.example.messageapp.domain.usecase.LoginUseCase
import com.example.messageapp.domain.usecase.RegisterUseCase
import com.example.messageapp.domain.usecase.RejectFriendRequestUseCase
import com.example.messageapp.domain.usecase.SaveMessageUseCase
import com.example.messageapp.domain.usecase.SearchUsersUseCase
import com.example.messageapp.domain.usecase.SendFriendRequestUseCase
import com.example.messageapp.domain.usecase.UpdateProfileUseCase
import com.example.messageapp.domain.usecase.UploadChatImageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideAppPreferencesUseCase(preferencesRepository: PreferencesRepository): AppPreferencesUseCase {
        return AppPreferencesUseCase(preferencesRepository)
    }

    @Provides
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    fun provideGetCurrentUserUseCase(authRepository: AuthRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(authRepository)
    }

    @Provides
    fun provideSearchUsersUseCase(userRepository: UserRepository): SearchUsersUseCase {
        return SearchUsersUseCase(userRepository)
    }

    @Provides
    fun provideGetAllUsersUseCase(userRepository: UserRepository): GetAllUsersUseCase {
        return GetAllUsersUseCase(userRepository)
    }

    @Provides
    fun provideGetFriendsUseCase(friendRepository: FriendRepository): GetFriendsUseCase {
        return GetFriendsUseCase(friendRepository)
    }

    @Provides
    fun provideGetFriendRequestsUseCase(friendRepository: FriendRepository): GetFriendRequestsUseCase {
        return GetFriendRequestsUseCase(friendRepository)
    }

    @Provides
    fun provideSendFriendRequestUseCase(friendRepository: FriendRepository): SendFriendRequestUseCase {
        return SendFriendRequestUseCase(friendRepository)
    }

    @Provides
    fun provideAcceptFriendRequestUseCase(friendRepository: FriendRepository): AcceptFriendRequestUseCase {
        return AcceptFriendRequestUseCase(friendRepository)
    }

    @Provides
    fun provideRejectFriendRequestUseCase(friendRepository: FriendRepository): RejectFriendRequestUseCase {
        return RejectFriendRequestUseCase(friendRepository)
    }

    @Provides
    fun provideGetChatHistoryUseCase(messageRepository: MessageRepository): GetChatHistoryUseCase {
        return GetChatHistoryUseCase(messageRepository)
    }

    @Provides
    fun provideUploadChatImageUseCase(messageRepository: MessageRepository): UploadChatImageUseCase {
        return UploadChatImageUseCase(messageRepository)
    }

    @Provides
    fun provideSaveMessageUseCase(messageRepository: MessageRepository): SaveMessageUseCase {
        return SaveMessageUseCase(messageRepository)
    }

    @Provides
    fun provideGetNewsFeedUseCase(newsRepository: NewsRepository): GetNewsFeedUseCase {
        return GetNewsFeedUseCase(newsRepository)
    }

    @Provides
    fun provideCreatePostUseCase(newsRepository: NewsRepository): CreatePostUseCase {
        return CreatePostUseCase(newsRepository)
    }

    @Provides
    fun provideLikePostUseCase(newsRepository: NewsRepository): LikePostUseCase {
        return LikePostUseCase(newsRepository)
    }

    @Provides
    fun provideCommentPostUseCase(newsRepository: NewsRepository): CommentPostUseCase {
        return CommentPostUseCase(newsRepository)
    }

    @Provides
    fun provideUpdateProfileUseCase(userRepository: UserRepository): UpdateProfileUseCase {
        return UpdateProfileUseCase(userRepository)
    }
}
