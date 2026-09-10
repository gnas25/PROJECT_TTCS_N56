package com.example.txngnx_ttcs_n56;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.txngnx_ttcs_n56.adapter.NhatKyAdapter;
import com.example.txngnx_ttcs_n56.api.ApiService;
import com.example.txngnx_ttcs_n56.api.RetrofitClient;
import com.example.txngnx_ttcs_n56.model.NhatKy;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NhatKyActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView imgQRCode;
    private RecyclerView rvDanhSachNhatKy;
    private NhatKyAdapter adapter;
    private List<NhatKy> danhSachNhatKy;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhat_ky);

        // 1. Ánh xạ các thành phần từ giao diện XML
        btnBack = findViewById(R.id.btnBack);
        imgQRCode = findViewById(R.id.imgQRCode);
        rvDanhSachNhatKy = findViewById(R.id.rvDanhSachNhatKy);

        rvDanhSachNhatKy.setLayoutManager(new LinearLayoutManager(this));
        danhSachNhatKy = new ArrayList<>();
        adapter = new NhatKyAdapter(danhSachNhatKy);
        rvDanhSachNhatKy.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // 2. Bắt sự kiện khi người dùng bấm vào nút Back
        btnBack.setOnClickListener(v -> finish());

        // 3. Nhận ID lô hàng được truyền sang từ màn hình chính qua Intent (Dạng String)
        String batchId = getIntent().getStringExtra("BATCH_ID");

        // 4. Nếu có ID hợp lệ, tiến hành tạo mã QR và tải danh sách nhật ký
        if (batchId != null && !batchId.isEmpty()) {
            // Tải danh sách nhật ký từ Server
            taiDanhSachNhatKy(batchId);

            // Lấy URL từ RetrofitClient (tự động ghép IP máy tính)
            String webUrl = RetrofitClient.WEB_URL + batchId;

            try {
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.encodeBitmap(webUrl, BarcodeFormat.QR_CODE, 400, 400);
                imgQRCode.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void taiDanhSachNhatKy(String batchId) {
        apiService.getDanhSachNhatKy(batchId).enqueue(new Callback<List<NhatKy>>() {
            @Override
            public void onResponse(Call<List<NhatKy>> call, Response<List<NhatKy>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    danhSachNhatKy.clear();
                    danhSachNhatKy.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    taiDanhSachNhatKyQuery(batchId);
                }
            }

            @Override
            public void onFailure(Call<List<NhatKy>> call, Throwable t) {
                taiDanhSachNhatKyQuery(batchId);
            }
        });
    }

    private void taiDanhSachNhatKyQuery(String batchId) {
        apiService.getDanhSachNhatKyQuery(batchId).enqueue(new Callback<List<NhatKy>>() {
            @Override
            public void onResponse(Call<List<NhatKy>> call, Response<List<NhatKy>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    danhSachNhatKy.clear();
                    danhSachNhatKy.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<NhatKy>> call, Throwable t) {
                // Giữ danh sách rỗng nếu không kết nối được
            }
        });
    }
}
