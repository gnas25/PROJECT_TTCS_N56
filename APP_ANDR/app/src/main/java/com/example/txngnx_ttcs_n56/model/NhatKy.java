package com.example.txngnx_ttcs_n56.model;

import com.google.gson.annotations.SerializedName;

public class NhatKy {
    @SerializedName("batch_id")
    private String batchId;

    @SerializedName("action_details")
    private String chiTietHanhDong;

    @SerializedName("log_time")
    private String thoiGian;

    public NhatKy(String batchId, String chiTietHanhDong, String thoiGian) {
        this.batchId = batchId;
        this.chiTietHanhDong = chiTietHanhDong;
        this.thoiGian = thoiGian;
    }

    public String getBatchId() { return batchId; }
    public String getChiTietHanhDong() { return chiTietHanhDong; }
    public String getThoiGian() { return thoiGian; }
}