package com.example.data.local

import androidx.room.*
import com.example.data.model.SiteSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM site_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<SiteSettingsEntity?>

    @Query("SELECT * FROM site_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): SiteSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: SiteSettingsEntity)
}
