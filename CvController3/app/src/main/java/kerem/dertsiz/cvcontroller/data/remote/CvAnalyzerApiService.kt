package kerem.dertsiz.cvcontroller.data.remote

import kerem.dertsiz.cvcontroller.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface CvAnalyzerApiService {

    // ========== CV Management ==========
    @Multipart
    @POST("upload_cv")
    suspend fun uploadCv(
        @Header("X-User-UUID") userUuid: String,
        @Part file: MultipartBody.Part
    ): Response<UploadCvResponse>

    // GET /cv veya /cvs - Kullanıcının CV'lerini listele (eğer varsa)
    @GET("cv")
    suspend fun getCvs(
        @Header("X-User-UUID") userUuid: String
    ): Response<kerem.dertsiz.cvcontroller.data.model.CvListResponse>

    // DELETE /cv/{cv_id} - CV sil (eğer varsa)
    @DELETE("cv/{cv_id}")
    suspend fun deleteCv(
        @Header("X-User-UUID") userUuid: String,
        @Path("cv_id") cvId: Int
    ): Response<kerem.dertsiz.cvcontroller.data.model.DeleteCvResponse>

    // ========== Analytics ==========
    @POST("analyze_cv")
    suspend fun analyzeCv(
        @Header("X-User-UUID") userUuid: String,
        @Body request: AnalyzeCvRequest
    ): Response<AnalysisResponse>

    // GET /analyze_cv/{cv_id} veya /analytics/{cv_id} - Daha önce yapılan analizi getir (eğer varsa)
    @GET("analyze_cv/{cv_id}")
    suspend fun getAnalysis(
        @Header("X-User-UUID") userUuid: String,
        @Path("cv_id") cvId: Int
    ): Response<AnalysisResponse>

    // ========== Jobs ==========
    @GET("job_listings")
    suspend fun getJobListings(): Response<JobListingsResponse>

    // ========== Generation ==========
    @POST("generate_from_job")
    suspend fun generateFromJob(
        @Header("X-User-UUID") userUuid: String,
        @Body request: GenerateFromJobRequest
    ): Response<GenerateFromJobResponse>
}
