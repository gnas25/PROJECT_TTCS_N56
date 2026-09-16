package com.example.txngnx_ttcs_n56; // Kiểm tra lại tên package của bạn

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.txngnx_ttcs_n56.adapter.LoHangAdapter;
import com.example.txngnx_ttcs_n56.api.ApiService;
import com.example.txngnx_ttcs_n56.api.RetrofitClient;
import com.example.txngnx_ttcs_n56.model.LoHang;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvLoHang;
    private LoHangAdapter adapter;
    private List<LoHang> danhSachLoHang;
    private FloatingActionButton fabAddLoHang;
    private ApiService apiService; // Khai báo ApiService

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvLoHang = findViewById(R.id.rvLoHang);
        rvLoHang.setLayoutManager(new LinearLayoutManager(this));
        fabAddLoHang = findViewById(R.id.fabAddLoHang);

        danhSachLoHang = new ArrayList<>();

        // Khởi tạo Adapter với danh sách rỗng ban đầu
        adapter = new LoHangAdapter(danhSachLoHang, loHang -> showPopupChiTiet(loHang));
        rvLoHang.setAdapter(adapter);

        // Khởi tạo Retrofit
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // GỌI HÀM LẤY DỮ LIỆU TỪ SERVER KHI MỞ APP
        taiDanhSachLoHang();

        fabAddLoHang.setOnClickListener(v -> showPopupTaoLoHang());
    }

    // --- HÀM 1: TẢI DỮ LIỆU TỪ MYSQL QUA NODE.JS ---
    private void taiDanhSachLoHang() {
        apiService.getDanhSachLoHang().enqueue(new Callback<List<LoHang>>() {
            @Override
            public void onResponse(Call<List<LoHang>> call, Response<List<LoHang>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    danhSachLoHang.clear();
                    danhSachLoHang.addAll(response.body()); // Nhét dữ liệu thật vào danh sách
                    adapter.notifyDataSetChanged(); // Cập nhật giao diện
                }
            }

            @Override
            public void onFailure(Call<List<LoHang>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối Server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- HÀM 2: GỬI LÔ HÀNG MỚI LÊN SERVER ---
    private void showPopupTaoLoHang() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_popup1);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        AutoCompleteTextView spLoaiNongSan = dialog.findViewById(R.id.spLoaiNongSan);
        AutoCompleteTextView spNoiNuoiTrong = dialog.findViewById(R.id.spNoiNuoiTrong);

        String[] dsLoai = {"Nông sản", "Thủy sản", "Cây trồng"};
        ArrayAdapter<String> adapterLoai = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dsLoai);
        spLoaiNongSan.setAdapter(adapterLoai);

        String[] dsTinhThanh = {"Hà Nội", "Hải Dương", "Tiền Giang", "Đồng Nai", "Cà Mau", "Lâm Đồng"};
        ArrayAdapter<String> adapterTinh = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dsTinhThanh);
        spNoiNuoiTrong.setAdapter(adapterTinh);

        EditText edtNgayKhoiTao = dialog.findViewById(R.id.edtNgayKhoiTao);
        edtNgayKhoiTao.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        edtNgayKhoiTao.setText(date);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        Button btnXacNhan = dialog.findViewById(R.id.btnXacNhanTaoLo);
        EditText edtTenLoHang = dialog.findViewById(R.id.edtTenLoHang);

        btnXacNhan.setOnClickListener(v -> {
            String ten = edtTenLoHang.getText().toString();
            String loai = spLoaiNongSan.getText().toString();
            String ngay = edtNgayKhoiTao.getText().toString();
            String noi = spNoiNuoiTrong.getText().toString();

            // Tạo đối tượng gửi đi (id để null vì MySQL tự tăng)
            LoHang loHangMoi = new LoHang(null, ten, loai, ngay, noi, "Mới khởi tạo");

            // Bắn dữ liệu qua API
            // Bắn dữ liệu qua API
            apiService.taoLoHang(loHangMoi).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Tạo thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        taiDanhSachLoHang(); // Load lại danh sách sau khi tạo xong
                    } else {
                        // NẾU SERVER TỪ CHỐI, IN MÃ LỖI RA MÀN HÌNH
                        Toast.makeText(MainActivity.this, "Lỗi Server Code: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Lỗi mạng/Mất kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }

    private void showPopupChiTiet(LoHang loHang) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_popup2);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvTen = dialog.findViewById(R.id.tvChiTietTen);
        TextView tvLoai = dialog.findViewById(R.id.tvChiTietLoai);
        TextView tvNgay = dialog.findViewById(R.id.tvChiTietNgay);
        TextView tvNoi = dialog.findViewById(R.id.tvChiTietNoi);

        tvTen.setText("Tên lô hàng: " + loHang.getTenLoHang());
        tvLoai.setText("Loại nông sản: " + loHang.getLoaiNongSan());
        tvNgay.setText("Ngày khởi tạo: " + loHang.getNgayKhoiTao());
        tvNoi.setText("Nơi nuôi trồng: " + loHang.getNoiNuoiTrong());

        Button btnXemNhatKy = dialog.findViewById(R.id.btnXemNhatKy);
        Button btnCapNhatDuLieu = dialog.findViewById(R.id.btnCapNhatDuLieu);

        btnXemNhatKy.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(MainActivity.this, NhatKyActivity.class);
            intent.putExtra("BATCH_ID", loHang.getId());
            startActivity(intent);
        });

        btnCapNhatDuLieu.setOnClickListener(v -> {
            dialog.dismiss();
            showPopupCapNhatDuLieu(loHang); // TRUYỀN NGUYÊN ĐỐI TƯỢNG VÀO ĐÂY
        });

        dialog.show();
    }

    private void showPopupCapNhatDuLieu(LoHang loHang) { // Đã đổi tham số nhận vào
        Dialog dialog = new Dialog(this);
        String loaiNongSan = loHang.getLoaiNongSan();

        if ("Nông sản".equals(loaiNongSan)) {
            dialog.setContentView(R.layout.dialog_popup3);
        } else if ("Thủy sản".equals(loaiNongSan)) {
            dialog.setContentView(R.layout.dialog_popup4);
        } else if ("Cây trồng".equals(loaiNongSan)) {
            dialog.setContentView(R.layout.dialog_popup5);
        } else {
            dialog.setContentView(R.layout.dialog_popup3);
        }

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        // TODO: KHAI BÁO CÁC Ô NHẬP LIỆU Ở ĐÂY
        // Ví dụ: EditText edtChiTiet = dialog.findViewById(R.id.id_o_nhap_lieu_cua_ban);

        Button btnLuuNhatKy = dialog.findViewById(R.id.btnLuuNhatKy);
        // Ánh xạ trực tiếp các ô nhập liệu chung ID từ file XML của bạn
        EditText edtTenThucAn = dialog.findViewById(R.id.edtTenThucAn);
        EditText edtLieuLuong = dialog.findViewById(R.id.edtLieuLuong);
        EditText edtTanSuat = dialog.findViewById(R.id.edtTanSuat);

        btnLuuNhatKy.setOnClickListener(v -> {
            // Lấy dữ liệu người dùng gõ vào
            String tenThucAn = edtTenThucAn != null ? edtTenThucAn.getText().toString().trim() : "";
            String lieuLuong = edtLieuLuong != null ? edtLieuLuong.getText().toString().trim() : "";
            String tanSuat = edtTanSuat != null ? edtTanSuat.getText().toString().trim() : "";

            // Kiểm tra xem người dùng đã nhập chưa
            if (tenThucAn.isEmpty() || lieuLuong.isEmpty()) {
                android.widget.Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin!", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            // Gom nhóm thành chuỗi chi tiết hành động phù hợp với từng loại
            String chiTietHanhDong = "";
            if ("Cây trồng".equals(loaiNongSan)) {
                chiTietHanhDong = "Phân/Thuốc: " + tenThucAn + " | Liều lượng: " + lieuLuong + " | Tần suất: " + tanSuat;
            } else {
                chiTietHanhDong = "Thức ăn: " + tenThucAn + " | Liều lượng: " + lieuLuong + " | Tần suất: " + tanSuat;
            }

            // Lấy thời gian thực tế của hệ thống
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            String thoiGian = sdf.format(new java.util.Date());

            // Đóng gói thành đối tượng NhatKy
            com.example.txngnx_ttcs_n56.model.NhatKy nhatKyMoi =
                    new com.example.txngnx_ttcs_n56.model.NhatKy(loHang.getId(), chiTietHanhDong, thoiGian);

            // Gửi lên Server qua API
            apiService.taoNhatKy(nhatKyMoi).enqueue(new retrofit2.Callback<Object>() {
                @Override
                public void onResponse(retrofit2.Call<Object> call, retrofit2.Response<Object> response) {
                    if (response.isSuccessful()) {
                        android.widget.Toast.makeText(MainActivity.this, "Đã lưu nhật ký bảo mật thành công!", android.widget.Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        android.widget.Toast.makeText(MainActivity.this, "Lỗi Server: " + response.code(), android.widget.Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<Object> call, Throwable t) {
                    android.widget.Toast.makeText(MainActivity.this, "Lỗi mạng: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }
}