package com.example.txngnx_ttcs_n56; // Giữ nguyên tên package của bạn nhé

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class NhatKyActivity extends AppCompatActivity {

    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhat_ky);

        // 1. Ánh xạ nút Back từ giao diện XML
        btnBack = findViewById(R.id.btnBack);

        // 2. Bắt sự kiện khi người dùng bấm vào nút Back
        btnBack.setOnClickListener(v -> {
            finish(); // Lệnh đóng màn hình hiện tại, tự động quay về trang trước đó
        });
    }
}