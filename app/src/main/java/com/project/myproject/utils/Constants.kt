package com.project.myproject.utils

class Constants {
    companion object {
        const val MINIMUM_PASSWORD_LENGTH = 8
        const val MAXIMUM_PASSWORD_LENGTH = 16
        const val MINIMUM_NAME_LENGTH = 3
        const val MAXIMUM_NAME_LENGTH = 32
        const val MINIMUM_PHONE_LENGTH_REGEX = "^\\d{10,}\$"
        const val MAXIMUM_PHONE_LENGTH_REGEX = "^\\d{1,15}\$"
        const val ACCESS_TOKEN_KEY = "accessToken"
        const val REFRESH_TOKEN_KEY = "refreshToken"
        const val USER_ID_KEY = "userId"
        const val DATA_STORE_NAME = "MyPreferences"
        const val CARD_HEIGHT_FRACTION = 0.1
        const val CONTACT_INFO_KEY = "contactInfo"
        const val CONTACT_NAME_KEY = "contactName"
        const val CONTACT_CAREER_KEY = "contactCareer"
        const val CONTACT_ADDRESS_KEY = "contactAddress"
        const val FIRST_TAB_NUMBER = 0
        const val SECOND_TAB_NUMBER = 1
        const val FIRST_TAB_TEXT = "My profile"
        const val SECOND_TAB_TEXT = "Contacts"
        const val PASSWORD_REGEX = "^[a-zA-Z0-9@#\$%^&+=!]+\$"
        const val NAME_REGEX = "^[\\p{L}\\p{M}'\\- .]*\$"
        const val BEARER_TOKEN_START = "Bearer "
        const val RETRY_REQUEST_TIMES = 2
        const val FADE_DELAY = 1000L
        const val APPLICATION_JSON_TYPE = "application/json"
        const val DEFAULT_NAME_VALUE = "Name"
        const val DEFAULT_CAREER_VALUE = "Career"
        const val DEFAULT_ADDRESS_VALUE = "Address"
        const val UNAUTHORIZED_CODE = 401

        const val BASE_URL = "http://178.63.9.114:7777/api/"
    }
}