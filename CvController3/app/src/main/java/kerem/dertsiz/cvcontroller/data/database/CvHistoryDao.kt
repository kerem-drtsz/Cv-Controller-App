package kerem.dertsiz.cvcontroller.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CvHistoryDao {

    @Query("SELECT * FROM cv_history ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CvHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CvHistoryEntity)

    @Query("DELETE FROM cv_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM cv_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): CvHistoryEntity?
}
