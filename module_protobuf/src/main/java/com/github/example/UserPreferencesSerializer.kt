package com.github.example

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.github.example.UserPreferencesProtos
import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<UserPreferencesProtos.UserPreferences> {

    override fun readFrom(input: InputStream): UserPreferencesProtos.UserPreferences {
        try {
            return UserPreferencesProtos.UserPreferences.parseFrom(input)
        } catch (exception: Exception) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(t: UserPreferencesProtos.UserPreferences, output: OutputStream) = t.writeTo(output)
}