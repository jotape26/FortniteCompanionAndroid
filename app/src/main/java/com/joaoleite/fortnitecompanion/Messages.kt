package com.joaoleite.fortnitecompanion

enum class Messages(val message: String){
    FETCHING_UID("Fetching user ID."),
    FETCHING_STATS("User ID retrieved. Fetching user stats."),
    ERROR_UID("Error fetching player UID. Please verify that use've entered a valid username."),
    ERROR_STATS("Error fetching player stats. Please try again later."),
    ERROR_PLATFORM_NOT_SELECTED("Please select one platform to search the stats."),
    ERROR_NO_USERNAME("Please provide a player username")
}