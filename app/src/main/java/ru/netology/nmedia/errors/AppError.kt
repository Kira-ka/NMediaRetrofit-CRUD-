package ru.netology.nmedia.errors

import ru.netology.nmedia.R

sealed class AppError (var code: String): RuntimeException()
class ApiError(val status: Int, code: String): AppError(code)
object NetworkError : AppError(R.string.network_error.toString())
object UnkownError: AppError(R.string.unknown_error.toString())
