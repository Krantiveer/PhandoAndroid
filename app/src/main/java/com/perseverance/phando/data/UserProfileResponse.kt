package com.perseverance.phando.data

/**
 * Created by QAIT\amarkhatri.
 */
data class UserProfileResponse(val user: User,
                               val current_subscription: String?,
                               val end: String?)

data class User(val name: String?,
                val email: String?)