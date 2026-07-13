package com.example.messageapp.data.mapper

import com.example.messageapp.data.local.db.entity.MessageEntity
import com.example.messageapp.data.network.model.User as UserDto
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.data.network.model.MessageResponse
import com.example.messageapp.data.network.model.NewsResponse
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.data.network.model.FriendRequest as FriendRequestDto
import com.example.messageapp.domain.model.MessageStatus
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.model.UserCredentials
import com.example.messageapp.domain.model.LoggedInUser
import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.model.NewsPost
import com.example.messageapp.domain.model.FriendRequest

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    userName = userName,
    friends = friend ?: emptyList(),
    token = token
)

fun User.toDto(): UserDto = UserDto(
    id = id,
    name = name,
    userName = userName,
    friend = friends,
    token = token,
    password = null
)

fun UserResponse.toDomain(): User = User(
    id = 0,
    name = name,
    userName = username,
    friends = emptyList(),
    token = ""
)

fun UserCredentials.toRegisterDto(): UserDto = UserDto(
    id = 0,
    name = name,
    userName = userName,
    friend = emptyList(),
    token = "",
    password = password
)

fun UserCredentials.toLoginRequest(): com.example.messageapp.data.network.model.LoginRequest =
    com.example.messageapp.data.network.model.LoginRequest(
        name = name,
        userName = userName,
        password = password
    )

fun LoginResponse.toDomain(): LoggedInUser = LoggedInUser(
    token = token,
    expiresAt = expiresAt,
    user = user.toDomain()
)

fun MessageResponse.toDomain(currentUser: String): Message = Message(
    id = id,
    senderUsername = senderUsername,
    recipientUsername = recipientUsername,
    text = message,
    isFromMe = senderUsername == currentUser,
    type = messageType ?: "text"
)

fun NewsResponse.toDomain(): NewsPost = NewsPost(
    id = id,
    userNameAuthor = userNameAuthor,
    nameAuthor = nameAuthor,
    date = date,
    countLike = countLike,
    countComment = countComment,
    avatarAuthor = avatarAuthor,
    description = description,
    comments = comment ?: emptyList(),
    imageUrl = newsImage,
    likedUsers = likedUsers ?: emptyList()
)

fun NewsPost.toDto(): NewsRequest = NewsRequest(
    userNameAuthor = userNameAuthor,
    nameAuthor = nameAuthor,
    date = date,
    countLike = countLike,
    countComment = countComment,
    avatarAuthor = avatarAuthor,
    description = description,
    comment = comments
)

fun FriendRequestDto.toDomain(): FriendRequest = FriendRequest(
    id = id,
    senderUserName = senderUserName,
    receiverUserName = receiverUserName,
    status = status
)

fun MessageEntity.toDomain(): Message = Message(
    id = id.toInt(),
    clientMessageId = clientMessageId,
    senderUsername = senderUsername,
    recipientUsername = recipientUsername,
    text = text,
    isFromMe = isFromMe,
    type = type,
    status = status,
    timestamp = timestamp
)

fun Message.toEntity(chatId: String): MessageEntity = MessageEntity(
    clientMessageId = clientMessageId.ifBlank { java.util.UUID.randomUUID().toString() },
    chatId = chatId,
    senderUsername = senderUsername,
    recipientUsername = recipientUsername,
    text = text,
    isFromMe = isFromMe,
    type = type,
    status = status,
    timestamp = timestamp
)
