package com.perseverance.phando.home.profile.login

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class SocialLoggedInUser(
        val socialid: String?,
        val name: String?,
        val email:String?,
        val type:String?
)