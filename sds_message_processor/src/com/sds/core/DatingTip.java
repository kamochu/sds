package com.sds.core;

/**
 *
 * @author Samuel Kamochu
 */
public class DatingTip {

    private int id;
    private String tip;
    private String effectiveDate;
    private String expiryDate;
    private int status;

    public DatingTip() {
        this(0);
    }

    public DatingTip(int id) {
        this(id, null, null, null, 0);
    }

    public DatingTip(int id, String tip, String effectiveDate, String expiryDate, int status) {
        this.id = id;
        this.tip = tip;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DatingTip{" + "id=" + id + ", tip=" + tip + ", effectiveDate=" + effectiveDate + ", expiryDate=" + expiryDate + ", status=" + status + '}';
    }

}
