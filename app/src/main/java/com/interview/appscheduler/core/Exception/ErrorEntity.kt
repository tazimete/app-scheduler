package com.interview.appscheduler.core.Exception

sealed class ErrorEntity (
    var code: Int = 0,
    override var message: String? = null
): Throwable() {
    data class NetworkError(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
    data class UnauthorizedError(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
    data class ServerError(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
    data class DecodingError(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
    data class UnexpectedError(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
    data class DatabaseAccessingError(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
    data class DatabaseWritingError(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
    data class ObjectNotFoundInDatabaseError(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
    data class FileAccessingError(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
    data class FileWritingError(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
    data class FileRemovingError(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
    data class NotFound(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
    data class NotUniqueData(val errorCode: Int, val errorMessage: String? = null) : ErrorEntity(errorCode, errorMessage)
}