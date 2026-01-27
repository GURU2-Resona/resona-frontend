package com.example.resona.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val EXPIRE_AT = longPreferencesKey("expire_at")
    }

    // 토큰 읽기 (Flow 반환)
    val accessToken: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN] }
    val expireAtMillis: Flow<Long?> = context.dataStore.data.map { it[EXPIRE_AT] }

    // 토큰 저장 (suspend 함수)
    suspend fun saveTokens(access: String, refresh: String, expireAt: Date) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = access
            prefs[REFRESH_TOKEN] = refresh
            prefs[EXPIRE_AT] = expireAt.time
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }

    // 만료 체크
    suspend fun isExpired(): Boolean {
        val expireMillis = context.dataStore.data.map { it[EXPIRE_AT] }.firstOrNull() ?: return true
        return System.currentTimeMillis() >= expireMillis
    }
}