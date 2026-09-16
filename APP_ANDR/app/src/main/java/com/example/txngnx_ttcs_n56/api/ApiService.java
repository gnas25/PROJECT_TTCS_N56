package com.example.txngnx_ttcs_n56.api;

import com.example.txngnx_ttcs_n56.model.LoHang;
import com.example.txngnx_ttcs_n56.model.NhatKy;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // 1. API Lấy danh sách lô hàng từ Node.js
    @GET("api/batches")
    Call<List<LoHang>> getDanhSachLoHang();

    // 2. API Gửi dữ liệu lô hàng mới lên Node.js
    @POST("api/batches")
    Call<Object> taoLoHang(@Body LoHang loHang);

    // 3. API Gửi dữ liệu nhật ký lên Node.js
    @POST("api/logs")
    Call<Object> taoNhatKy(@Body NhatKy nhatKy);

    // 4. API Lấy danh sách nhật ký của lô hàng từ Node.js
    @GET("api/logs/{batchId}")
    Call<List<NhatKy>> getDanhSachNhatKy(@Path("batchId") String batchId);

    @GET("api/logs")
    Call<List<NhatKy>> getDanhSachNhatKyQuery(@Query("batch_id") String batchId);

    // 5. API Lấy thông tin chi tiết lô hàng
    @GET("api/batches/{id}")
    Call<Object> getChiTietLoHang(@Path("id") String batchId);

    // 6. API Xuất chuồng lô hàng (chốt Merkle Root Hash)
    @POST("api/batches/{id}/finalize")
    Call<Object> finalizeBatch(@Path("id") String batchId, @Body java.util.Map<String, String> body);
}