package com.bih.nic.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Consumer implements Serializable {

    private String _id;
    private String subDivId;
    private String sectionId;
    private String bookNo;
    private String conId;
    private String actNo;
    private String cName;
    private String cAddress;
    private String tariffId;
    private String phase;
    private String cLoad;
    private double fAmount;
    private String mobile;
    private String latitude;
    private String longitude;
    private String reading;
    private String dis_conn;
    private Bitmap image;
    private String read_status;
    private String remarks;
    private String date_time;
    private String refNo;
    private String meterImage;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSubDivId() {
        return subDivId;
    }

    public void setSubDivId(String subDivId) {
        this.subDivId = subDivId;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getConId() {
        return conId;
    }

    public void setConId(String conId) {
        this.conId = conId;
    }

    public String getActNo() {
        return actNo;
    }

    public void setActNo(String actNo) {
        this.actNo = actNo;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getcAddress() {
        return cAddress;
    }

    public void setcAddress(String cAddress) {
        this.cAddress = cAddress;
    }

    public String getTariffId() {
        return tariffId;
    }

    public void setTariffId(String tariffId) {
        this.tariffId = tariffId;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getcLoad() {
        return cLoad;
    }

    public void setcLoad(String cLoad) {
        this.cLoad = cLoad;
    }

    public double getfAmount() {
        return fAmount;
    }

    public void setfAmount(double fAmount) {
        this.fAmount = fAmount;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public String getDis_conn() {
        return dis_conn;
    }

    public void setDis_conn(String dis_conn) {
        this.dis_conn = dis_conn;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getRead_status() {
        return read_status;
    }

    public void setRead_status(String read_status) {
        this.read_status = read_status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getMeterImage() {
        return meterImage;
    }

    public void setMeterImage(String meterImage) {
        this.meterImage = meterImage;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }
}
