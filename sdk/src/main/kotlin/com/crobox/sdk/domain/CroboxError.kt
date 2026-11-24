package com.crobox.sdk.domain

/**
 * Why a promotions request failed
 */
sealed class CroboxError(message: String, cause: Throwable? = null) : Exception(message, cause) {

    /** The server answered with an HTTP error, for example 404 for an unknown container */
    class HttpError(val statusCode: Int) : CroboxError("HTTP $statusCode")

    /** The request did not get an answer, for example because there is no network or it timed out */
    class RequestFailed(cause: Throwable) : CroboxError(cause.message ?: cause.javaClass.simpleName, cause)

    /** The server's answer could not be read */
    class InvalidResponse(message: String, cause: Throwable? = null) : CroboxError(message, cause)
}
