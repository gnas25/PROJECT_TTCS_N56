package com.example.txngnx_ttcs_n56.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // CHÚ Ý: 10.0.2.2 là IP đặc biệt để máy ảo Android kết nối được với localhost của máy tính.
    // (Nếu bạn cắm cáp chạy trên điện thoại thật, hãy đổi dòng này thành IPv4 của máy tính, vd: http://192.168.1.5:3000/)
    private static final String BASE_URL = "http://192.168.1.9:3000/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}