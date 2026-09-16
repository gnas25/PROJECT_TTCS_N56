package com.example.txngnx_ttcs_n56.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.txngnx_ttcs_n56.R; // Hãy đảm bảo dòng này khớp với tên package của bạn
import com.example.txngnx_ttcs_n56.model.LoHang;

import java.util.List;

public class LoHangAdapter extends RecyclerView.Adapter<LoHangAdapter.ViewHolder> {

    private List<LoHang> danhSachLoHang;
    private OnItemClickListener listener; // Biến lắng nghe sự kiện bấm

    // Tạo một "Đường dây liên lạc" (Interface) để báo cho MainActivity biết khi có lô hàng bị bấm
    public interface OnItemClickListener {
        void onItemClick(LoHang loHang);
    }

    // Cập nhật hàm khởi tạo để nhận thêm listener
    public LoHangAdapter(List<LoHang> danhSachLoHang, OnItemClickListener listener) {
        this.danhSachLoHang = danhSachLoHang;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lo_hang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoHang loHang = danhSachLoHang.get(position);

        holder.tvTenLoHang.setText(loHang.getTenLoHang());
        holder.tvNgayBatDau.setText("Ngày bắt đầu: " + loHang.getNgayKhoiTao());
        holder.tvTrangThai.setText("Trạng thái: " + loHang.getTrangThai());

        // BẮT SỰ KIỆN: Khi người dùng bấm vào cả cái thẻ (CardView)
        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(loHang); // Báo ra ngoài là lô hàng này vừa bị bấm!
        });
    }

    @Override
    public int getItemCount() {
        return danhSachLoHang.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenLoHang, tvNgayBatDau, tvTrangThai;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenLoHang = itemView.findViewById(R.id.tvTenLoHang);
            tvNgayBatDau = itemView.findViewById(R.id.tvNgayBatDau);
            tvTrangThai = itemView.findViewById(R.id.tvTrangThai);
        }
    }
}