package kerem.dertsiz.cvcontroller.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CvHistoryType {
    ANALYSIS,      // CV analiz sonucu
    DOWNLOADED_CV  // İndirilen optimize edilmiş CV
}

@Entity(tableName = "cv_history")
data class CvHistoryEntity(
    @PrimaryKey val id: String,           // UUID
    val fileName: String,
    val fileUri: String,
    val createdAt: Long,
    val resultSummary: String,            // kısa özet
    val resultDetails: String,            // uzun metin
    val type: String = CvHistoryType.ANALYSIS.name,  // History tipi
    val downloadUrl: String? = null,      // İndirme linki (DOWNLOADED_CV için)
    val jobTitle: String? = null,         // İş ilanı başlığı (DOWNLOADED_CV için)
    val jobCompany: String? = null,       // İş ilanı şirketi (DOWNLOADED_CV için)
    val cvLevel: String? = null,          // CV seviyesi (simple/aggressive)
    val cvLanguage: String? = null,       // CV dili (tr/en)
    val cvScore: Int? = null              // CV skoru
)
