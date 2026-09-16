package com.example.txngnx_ttcs_n56; // Tên package của bạn, ĐỪNG SỬA DÒNG NÀY

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ TextView từ XML sang Java
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Bắt sự kiện khi người dùng bấm vào dòng chữ
        tvGoToRegister.setOnClickListener(v -> {
            // Intent là lệnh dùng để chuyển đổi giữa các Activity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}