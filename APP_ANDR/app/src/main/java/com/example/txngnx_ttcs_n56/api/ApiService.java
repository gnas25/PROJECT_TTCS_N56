package com.example.txngnx_ttcs_n56.api;

import com.example.txngnx_ttcs_n56.model.LoHang;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    // 1. API Lấy danh sách lô hàng từ Node.js
    @GET("api/batches")
    Call<List<LoHang>> getDanhSachLoHang();

    // 2. API Gửi dữ liệu lô hàng mới lên Node.js
    @POST("api/batches")
    Call<Object> taoLoHang(@Body LoHang loHang);
    // 3. API Gửi dữ liệu nhật ký lên Node.js
    @POST("api/logs")
    Call<Object> taoNhatKy(@Body com.example.txngnx_ttcs_n56.model.NhatKy nhatKy);

}