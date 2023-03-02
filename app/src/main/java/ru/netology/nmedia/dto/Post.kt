package ru.netology.nmedia.dto

data class Post(
    var id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val saveRemote: Boolean
)

