package kerem.dertsiz.cvcontroller.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cv_history")
data class CvHistoryEntity(
    @PrimaryKey val id: String,           // UUID
    val fileName: String,
    val fileUri: String,
    val createdAt: Long,
    val resultSummary: String,            // kısa özet
    val resultDetails: String             // uzun metin
)
