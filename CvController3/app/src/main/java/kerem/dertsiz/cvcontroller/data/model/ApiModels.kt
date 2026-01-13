package kerem.dertsiz.cvcontroller.data.model

import com.google.gson.annotations.SerializedName

// Upload CV Response
data class UploadCvResponse(
    @SerializedName("message") val message: String,
    @SerializedName("cv_id") val cvId: Int
)

// Analyze CV Request
data class AnalyzeCvRequest(
    @SerializedName("cv_id") val cvId: Int
)

// Analyze CV Response
data class AnalysisResponse(
    @SerializedName("analysis") val analysis: CvAnalysis
)

data class CvAnalysis(
    @SerializedName("score") val score: Int,
    @SerializedName("strengths") val strengths: List<String>,
    @SerializedName("weaknesses") val weaknesses: List<String>,
    @SerializedName("recommendations") val recommendations: List<String>,
    @SerializedName("summary") val summary: String
)

// Job Listing Response
data class JobListingsResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("jobs") val jobs: List<JobListing>
)

data class JobListing(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("company") val company: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("location") val location: String
)

// Generate from Job Request
data class GenerateFromJobRequest(
    @SerializedName("cv_id") val cvId: Int,
    @SerializedName("job_id") val jobId: Int
)

// Generate from Job Response
data class GenerateFromJobResponse(
    @SerializedName("results") val results: List<GeneratedCvVersion>
)

data class GeneratedCvVersion(
    @SerializedName("level") val level: String, // "simple" or "aggressive"
    @SerializedName("language") val language: String, // "tr" or "en"
    @SerializedName("download_url") val downloadUrl: String,
    @SerializedName("score") val score: Int
)

// CV Management - Get CVs Response (Eğer varsa)
data class CvListResponse(
    @SerializedName("cvs") val cvs: List<CvInfo>
)

data class CvInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("filename") val filename: String?,
    @SerializedName("uploaded_at") val uploadedAt: String?,
    @SerializedName("has_analysis") val hasAnalysis: Boolean?
)

// Delete CV Response
data class DeleteCvResponse(
    @SerializedName("message") val message: String
)
