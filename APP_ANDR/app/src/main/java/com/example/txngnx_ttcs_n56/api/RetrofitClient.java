package com.example.txngnx_ttcs_n56.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // 💡 ĐỔI IP Ở ĐÂY NẾU IP MÁY TÍNH THAY ĐỔI (Dùng 'ipconfig' trong CMD để xem IPv4)
    public static final String SERVER_HOST = "192.168.22.53";

    public static final String BASE_URL = "http://" + SERVER_HOST + ":3000/";
    public static final String WEB_URL = "http://" + SERVER_HOST + ":5500/index.html?batchId=";

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