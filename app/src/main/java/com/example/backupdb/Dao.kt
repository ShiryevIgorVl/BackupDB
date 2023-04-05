package com.example.backupdb

import androidx.room.*
import androidx.room.Dao
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPoint(point: Point)

    @Delete
    suspend fun deletePoint(point: Point)

    @Query("SELECT  * FROM  test1")
    fun getAllPoint(): Flow<List<Point>>

    @Query("DELETE FROM test1")
    suspend fun deleteAllTable()
}