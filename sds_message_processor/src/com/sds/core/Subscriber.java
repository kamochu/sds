package com.sds.core;

/**
 *
 * @author Samuel Kamochu
 */
public class Subscriber {

    public final static int ACTIVE = 1;
    public final static int INACTIVE = 0;

    private long id;
    private String msisdn;
    private String name;
    private int age;
    private String sex;
    private String location;
    private int regStatus;
    private int status;
    private int sdpStatus;
    private String reason;
    private int lastNode;
    private String data0;
    private String data1;
    private String data2;
    private String data3;
    private String data4;
    private String data5;
    private String data6;
    private String data7;
    private String data8;
    private String data9;
    private String pref0;
    private String pref1;
    private String pref2;
    private String pref3;
    private String pref4;
    private String pref5;
    private String pref6;
    private String pref7;
    private String pref8;
    private String pref9;

    public int getPreference() {
        return preference;
    }

    public void setPreference(int preference) {
        this.preference = preference;
    }
    private int preference;
    private boolean loaded;

    public Subscriber() {
        this(null, null, 0, "0", null);
    }

    public Subscriber(String msisdn, String name, int age, String sex, String location) {
        this(0, msisdn, name, age, sex, location, RegistrationStatus.INITIAL, ACTIVE, "registration", 0, 0, false);
    }

    public Subscriber(long id, String msisdn, String name, int age, String sex, String location, int regStatus, int status, String reason, int preference, int sdpStatus, boolean loaded) {
        this.id = id;
        this.msisdn = msisdn;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.location = location;
        this.regStatus = regStatus;
        this.status = status;
        this.reason = reason;
        this.preference = preference;
        this.loaded = loaded;
        this.sdpStatus = sdpStatus;
        this.lastNode = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(int regStatus) {
        this.regStatus = regStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the loaded
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * @param loaded the loaded to set
     */
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * @return the sdpStatus
     */
    public int getSdpStatus() {
        return sdpStatus;
    }

    /**
     * @param sdpStatus the sdpStatus to set
     */
    public void setSdpStatus(int sdpStatus) {
        this.sdpStatus = sdpStatus;
    }

    public int getLastNode() {
        return lastNode;
    }

    public void setLastNode(int lastNode) {
        this.lastNode = lastNode;
    }

    public String getData0() {
        return data0;
    }

    public void setData0(String data0) {
        this.data0 = data0;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public String getData4() {
        return data4;
    }

    public void setData4(String data4) {
        this.data4 = data4;
    }

    public String getData5() {
        return data5;
    }

    public void setData5(String data5) {
        this.data5 = data5;
    }

    public String getData6() {
        return data6;
    }

    public void setData6(String data6) {
        this.data6 = data6;
    }

    public String getData7() {
        return data7;
    }

    public void setData7(String data7) {
        this.data7 = data7;
    }

    public String getData8() {
        return data8;
    }

    public void setData8(String data8) {
        this.data8 = data8;
    }

    public String getData9() {
        return data9;
    }

    public void setData9(String data9) {
        this.data9 = data9;
    }

    public String getPref0() {
        return pref0;
    }

    public void setPref0(String pref0) {
        this.pref0 = pref0;
    }

    public String getPref1() {
        return pref1;
    }

    public void setPref1(String pref1) {
        this.pref1 = pref1;
    }

    public String getPref2() {
        return pref2;
    }

    public void setPref2(String pref2) {
        this.pref2 = pref2;
    }

    public String getPref3() {
        return pref3;
    }

    public void setPref3(String pref3) {
        this.pref3 = pref3;
    }

    public String getPref4() {
        return pref4;
    }

    public void setPref4(String pref4) {
        this.pref4 = pref4;
    }

    public String getPref5() {
        return pref5;
    }

    public void setPref5(String pref5) {
        this.pref5 = pref5;
    }

    public String getPref6() {
        return pref6;
    }

    public void setPref6(String pref6) {
        this.pref6 = pref6;
    }

    public String getPref7() {
        return pref7;
    }

    public void setPref7(String pref7) {
        this.pref7 = pref7;
    }

    public String getPref8() {
        return pref8;
    }

    public void setPref8(String pref8) {
        this.pref8 = pref8;
    }

    public String getPref9() {
        return pref9;
    }

    public void setPref9(String pref9) {
        this.pref9 = pref9;
    }

    @Override
    public String toString() {
        return "Subscriber{" + "id=" + id + ", msisdn=" + msisdn + ", name=" + name + ", age=" + age + ", sex=" + sex + ", location=" + location + ", regStatus=" + regStatus + ", status=" + status + ", sdpStatus=" + sdpStatus + ", reason=" + reason + ", nextNode=" + lastNode + ", data0=" + data0 + ", data1=" + data1 + ", data2=" + data2 + ", data3=" + data3 + ", data4=" + data4 + ", data5=" + data5 + ", data6=" + data6 + ", data7=" + data7 + ", data8=" + data8 + ", data9=" + data9 + ", pref0=" + pref0 + ", pref1=" + pref1 + ", pref2=" + pref2 + ", pref3=" + pref3 + ", pref4=" + pref4 + ", pref5=" + pref5 + ", pref6=" + pref6 + ", pref7=" + pref7 + ", pref8=" + pref8 + ", pref9=" + pref9 + ", preference=" + preference + ", loaded=" + loaded + '}';
    }

}
