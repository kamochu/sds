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
        this(0, msisdn, name, age, sex, location, RegistrationStatus.REG_INITIAL, ACTIVE, "registration", 0, 0, false);
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

    @Override
    public String toString() {
        return "Subscriber{" + "id=" + id + ", msisdn=" + msisdn + ", name=" + name + ", age=" + age + ", sex=" + sex + ", location=" + location + ", regStatus=" + regStatus + ", status=" + status + ", reason=" + reason + ", preference=" + preference + ", loaded=" + loaded + '}';
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

}
