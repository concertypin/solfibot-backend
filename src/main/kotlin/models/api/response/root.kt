package models.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseRoot(val joinedUser: Set<String>)

@Serializable
data class ResponseGeneralError(val errorCode: String, val errorMessage: String)