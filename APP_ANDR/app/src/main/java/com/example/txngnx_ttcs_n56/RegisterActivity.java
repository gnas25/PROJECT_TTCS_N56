package com.example.txngnx_ttcs_n56; // Tên package của bạn, ĐỪNG SỬA DÒNG NÀY

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        tvBackToLogin.setOnClickListener(v -> {
            // Lệnh finish() sẽ đóng trang Đăng ký hiện tại, tự động lùi về trang Đăng nhập trước đó
            finish();
        });
    }
}