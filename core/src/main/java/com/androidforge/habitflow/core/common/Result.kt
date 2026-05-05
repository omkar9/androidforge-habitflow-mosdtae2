package com.androidforge.habitflow.core.common

/**
 * A sealed class representing the outcome of an operation, encapsulating success, error, loading, and offline states.
 * This provides a robust way to handle data flow and UI state changes across different layers of the application.
 *
 * @param T The type of data returned on a successful operation.
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation with data.
     * @param data The successful result data.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation with an exception.
     * @param exception The exception that caused the failure.
     */
    data class Error(val exception: Throwable) : Result<Nothing>()

    /**
     * Represents an ongoing operation, useful for showing loading indicators.
     */
    data object Loading : Result<Nothing>()

    /**
     * Represents an operation that failed due to lack of network connectivity.
     * @param exception An optional exception providing more details about the offline state.
     */
    data class Offline(val exception: Throwable? = null) : Result<Nothing>()
}