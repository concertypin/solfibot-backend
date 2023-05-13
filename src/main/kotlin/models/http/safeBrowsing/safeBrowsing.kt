package models.http.safeBrowsing

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SafeBrowsingLookupRequest(
    @SerialName("client") val client: ClientInfo,
    @SerialName("threatInfo") val threatInfo: ThreatInfo
)

@Serializable
data class ClientInfo(
    @SerialName("clientId") val clientId: String,
    @SerialName("clientVersion") val clientVersion: String
)

@Serializable
data class ThreatInfo(
    @SerialName("threatTypes") val threatTypes: List<String>,
    @SerialName("platformTypes") val platformTypes: List<String>,
    @SerialName("threatEntryTypes") val threatEntryTypes: List<String>,
    @SerialName("threatEntries") val threatEntries: List<ThreatEntry>
)

@Serializable
data class ThreatEntry(
    @SerialName("url") val url: String
)

@Serializable
data class SafeBrowsingLookupResponse(
    @SerialName("matches") val matches: List<ThreatMatch>?
)

@Serializable
data class ThreatMatch(
    @SerialName("threatType") val threatType: String,
    @SerialName("platformType") val platformType: String,
    @SerialName("threatEntryType") val threatEntryType: String,
    @SerialName("threat") val threat: Threat
)

@Serializable
data class Threat(
    @SerialName("url") val url: String? = null,
    @SerialName("threatTypes") val threatTypes: List<String>? = null
)