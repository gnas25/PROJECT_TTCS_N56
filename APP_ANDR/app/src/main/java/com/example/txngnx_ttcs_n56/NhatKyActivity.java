package com.example.txngnx_ttcs_n56;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.txngnx_ttcs_n56.adapter.NhatKyAdapter;
import com.example.txngnx_ttcs_n56.api.ApiService;
import com.example.txngnx_ttcs_n56.api.RetrofitClient;
import com.example.txngnx_ttcs_n56.model.NhatKy;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NhatKyActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView imgQRCode;
    private TextView tvThongBaoXuatChuong;
    private Button btnXuatChuong;
    private RecyclerView rvDanhSachNhatKy;
    private NhatKyAdapter adapter;
    private List<NhatKy> danhSachNhatKy;
    private ApiService apiService;
    private String currentBatchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhat_ky);

        // 1. Ánh xạ giao diện
        btnBack = findViewById(R.id.btnBack);
        imgQRCode = findViewById(R.id.imgQRCode);
        tvThongBaoXuatChuong = findViewById(R.id.tvThongBaoXuatChuong);
        btnXuatChuong = findViewById(R.id.btnXuatChuong);
        rvDanhSachNhatKy = findViewById(R.id.rvDanhSachNhatKy);

        rvDanhSachNhatKy.setLayoutManager(new LinearLayoutManager(this));
        danhSachNhatKy = new ArrayList<>();
        adapter = new NhatKyAdapter(danhSachNhatKy);
        rvDanhSachNhatKy.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        btnBack.setOnClickListener(v -> finish());

        currentBatchId = getIntent().getStringExtra("BATCH_ID");

        if (currentBatchId != null && !currentBatchId.isEmpty()) {
            // Kiểm tra thông tin chi tiết lô hàng & danh sách nhật ký
            kiemTraChiTietLoHang(currentBatchId);
        }

        // Bắt sự kiện bấm nút XUẤT CHUỒNG & CHỐT MÃ QR
        btnXuatChuong.setOnClickListener(v -> xuatChuongVaChotQR());
    }

    private void kiemTraChiTietLoHang(String batchId) {
        apiService.getChiTietLoHang(batchId).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Map<?, ?> map = (Map<?, ?>) response.body();
                        Map<?, ?> batchMap = (Map<?, ?>) map.get("batch");
                        String status = batchMap != null && batchMap.get("status") != null ? batchMap.get("status").toString() : "";

                        if ("XUAT_CHUONG".equalsIgnoreCase(status)) {
                            hienThiTrangThaiDaXuatChuong();
                        } else {
                            hienThiTrangThaiChuaXuatChuong();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Tải danh sách nhật ký từ Server
                taiDanhSachNhatKy(batchId);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                taiDanhSachNhatKy(batchId);
            }
        });
    }

    private void hienThiTrangThaiChuaXuatChuong() {
        btnXuatChuong.setVisibility(View.VISIBLE);
        tvThongBaoXuatChuong.setText("Lô hàng đang nuôi trồng. Bấm nút dưới để Xuất Chuồng & Chốt Merkle Root QR!");
        tvThongBaoXuatChuong.setTextColor(android.graphics.Color.parseColor("#E65100"));
        imgQRCode.setVisibility(View.GONE); // Ẩn QR khi chưa xuất chuồng
    }

    private void hienThiTrangThaiDaXuatChuong() {
        btnXuatChuong.setVisibility(View.GONE);
        tvThongBaoXuatChuong.setText("✅ Đã Xuất Chuồng - Dữ liệu Merkle Root đã được chốt bảo mật trên CSDL!");
        tvThongBaoXuatChuong.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
        imgQRCode.setVisibility(View.VISIBLE); // Hiển thị mã QR
        hienThiMaQR(currentBatchId);
    }

    private void xuatChuongVaChotQR() {
        if (danhSachNhatKy == null || danhSachNhatKy.isEmpty()) {
            Toast.makeText(this, "Chưa có nhật ký chăm sóc nào để chốt Merkle Root!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Tính toán Merkle Root Hash từ danh sách nhật ký
        String finalMerkleRoot = tinhMerkleRoot(danhSachNhatKy);

        // 2. Gửi API Xuất chuồng lên Server
        Map<String, String> body = new HashMap<>();
        body.put("root_hash", finalMerkleRoot);

        apiService.finalizeBatch(currentBatchId, body).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NhatKyActivity.this, "Xuất chuồng & chốt Merkle Root thành công!", Toast.LENGTH_SHORT).show();
                    hienThiTrangThaiDaXuatChuong();
                } else {
                    Toast.makeText(NhatKyActivity.this, "Lỗi Server khi xuất chuồng: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(NhatKyActivity.this, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hienThiMaQR(String batchId) {
        String webUrl = RetrofitClient.WEB_URL + batchId;
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(webUrl, BarcodeFormat.QR_CODE, 400, 400);
            imgQRCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
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
                // Giữ danh sách rỗng
            }
        });
    }

    // --- THUẬT TOÁN BĂM MERKLE ROOT CHO ANDROID ---
    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String tinhMerkleRoot(List<NhatKy> logs) {
        if (logs == null || logs.isEmpty()) return sha256("GENESIS_EMPTY");

        // Sắp xếp danh sách nhật ký theo thứ tự thời gian cũ nhất đến mới nhất (ASC)
        List<NhatKy> sortedLogs = new ArrayList<>(logs);
        Collections.reverse(sortedLogs); // Đảo ngược từ DESC về ASC để khớp 100% với Web

        List<String> currentLevel = new ArrayList<>();
        for (NhatKy log : sortedLogs) {
            if (log.getRecordHash() != null && !log.getRecordHash().isEmpty()) {
                currentLevel.add(log.getRecordHash());
            }
        }
        if (currentLevel.isEmpty()) return sha256("GENESIS_EMPTY");

        while (currentLevel.size() > 1) {
            List<String> nextLevel = new ArrayList<>();
            for (int i = 0; i < currentLevel.size(); i += 2) {
                if (i + 1 < currentLevel.size()) {
                    nextLevel.add(sha256(currentLevel.get(i) + currentLevel.get(i + 1)));
                } else {
                    nextLevel.add(sha256(currentLevel.get(i) + currentLevel.get(i)));
                }
            }
            currentLevel = nextLevel;
        }
        return currentLevel.get(0);
    }
}
