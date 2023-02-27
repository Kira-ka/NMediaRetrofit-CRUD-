package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao())

    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    val data: LiveData<FeedModel> = repository.data().map { FeedModel(it, it.isEmpty()) }

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _state.value = FeedModelState(loading = true)
            repository.getAllAsync()
            _state.value = FeedModelState()
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
        }
    }

    fun refreshPost() = viewModelScope.launch {
        try {
            _state.value = FeedModelState(refreshing = true)
            repository.getAllAsync()
            _state.value = FeedModelState()
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
        }
    }


    fun save() = viewModelScope.launch {
//        edited.value?.let {
//            repository.save(it, object : PostRepository.Callback<Post> {
//                override fun onSuccess(data: Post) {
//                    loadPosts()
//                }
//
//                override fun onError(e: Exception) {
//                    _data.postValue(FeedModel(error = true))
//                }
//            })
//            _postCreated.value = Unit
//        }
//        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) = viewModelScope.launch {
        val old = data.value?.posts.orEmpty()
        val flagPost = old.find { it.id == id }
        try {
            _state.value = FeedModelState(refreshing = true)
            if (flagPost != null) {
                if (!flagPost.likedByMe) {
                    repository.likeById(id)
                } else repository.dislikeById(id)
            }
            _state.value = FeedModelState()
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
            data.value?.copy(posts = old)
        }
    }


    fun removeById(id: Long) = viewModelScope.launch {
        val old = data.value?.posts.orEmpty()
        try {
            _state.value = FeedModelState(refreshing = true)
           repository.removeById(id)
            _state.value = FeedModelState()
        } catch (e: Exception) {
            _state.value = FeedModelState(error = true)
            data.value?.copy(posts = old)
        }
    }
}

