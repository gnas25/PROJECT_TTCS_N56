package com.example.txngnx_ttcs_n56.model;

import com.google.gson.annotations.SerializedName;

public class LoHang {

    @SerializedName("id")
    private String id;

    @SerializedName("batch_name")
    private String tenLoHang;

    @SerializedName("product_type")
    private String loaiNongSan;

    @SerializedName("start_date")
    private String ngayKhoiTao;

    @SerializedName("location")
    private String noiNuoiTrong;

    @SerializedName("status")
    private String trangThai;

    public LoHang(String id, String tenLoHang, String loaiNongSan, String ngayKhoiTao, String noiNuoiTrong, String trangThai) {
        this.id = id;
        this.tenLoHang = tenLoHang;
        this.loaiNongSan = loaiNongSan;
        this.ngayKhoiTao = ngayKhoiTao;
        this.noiNuoiTrong = noiNuoiTrong;
        this.trangThai = trangThai;
    }

    public String getId() { return id; }
    public String getTenLoHang() { return tenLoHang; }
    public String getLoaiNongSan() { return loaiNongSan; }
    public String getNgayKhoiTao() { return ngayKhoiTao; }
    public String getNoiNuoiTrong() { return noiNuoiTrong; }
    public String getTrangThai() { return trangThai; }
}