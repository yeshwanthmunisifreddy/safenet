package com.thesubgraph.safenet

data class AccessToken(
    val tokenType: String,
    val token: String,
    val refreshToken: String?= null,
    val expiresInSecs: Int? = 60,
)

class SessionState {
    var accessToken: AccessToken? = null
        set(value) {
            field = value
        }
}
