package com.example.txngnx_ttcs_n56.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.txngnx_ttcs_n56.R;
import com.example.txngnx_ttcs_n56.model.NhatKy;

import java.util.List;

public class NhatKyAdapter extends RecyclerView.Adapter<NhatKyAdapter.ViewHolder> {

    private final List<NhatKy> danhSachNhatKy;

    public NhatKyAdapter(List<NhatKy> danhSachNhatKy) {
        this.danhSachNhatKy = danhSachNhatKy;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nhat_ky, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NhatKy nhatKy = danhSachNhatKy.get(position);

        holder.tvThoiGianNhatKy.setText(nhatKy.getThoiGian() != null ? nhatKy.getThoiGian() : "");
        holder.tvChiTietVatTu.setText(nhatKy.getChiTietHanhDong() != null ? nhatKy.getChiTietHanhDong() : "");
    }

    @Override
    public int getItemCount() {
        return danhSachNhatKy != null ? danhSachNhatKy.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvThoiGianNhatKy, tvChiTietVatTu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvThoiGianNhatKy = itemView.findViewById(R.id.tvThoiGianNhatKy);
            tvChiTietVatTu = itemView.findViewById(R.id.tvChiTietVatTu);
        }
    }
}
