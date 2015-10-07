package com.sds.core;

import com.sds.core.conf.JobStatus;

/**
 *
 * @author Samuel Kamochu
 */
public class Job {

    private int id;
    private String jobDate;
    private String batchId;
    private long totalProduced;
    private long noOfDateMatches;
    private long noOfDatingTips;
    private long noOfInfoSMS;
    private int status;
    private String reason;
    private int initiator;
    private String initiatorComments;

    public Job() {
        this(0);
    }

    public Job(int id) {
        this(id, null, null, 0, 0, 0, 0, JobStatus.IN_PROGRESS, "started", 0, null);
    }

    public Job(int id, String jobDate, String batchId, long totalProduced, long noOfDateMatches, long noOfDatingTips, long noOfInfoSMS, int status, String reason, int initiator, String initiatorComments) {
        this.id = id;
        this.jobDate = jobDate;
        this.batchId = batchId;
        this.totalProduced = totalProduced;
        this.noOfDateMatches = noOfDateMatches;
        this.noOfDatingTips = noOfDatingTips;
        this.noOfInfoSMS = noOfInfoSMS;
        this.status = status;
        this.reason = reason;
        this.initiator = initiator;
        this.initiatorComments = initiatorComments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobDate() {
        return jobDate;
    }

    public void setJobDate(String jobDate) {
        this.jobDate = jobDate;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public long getTotalProduced() {
        return totalProduced;
    }

    public void setTotalProduced(long totalProduced) {
        this.totalProduced = totalProduced;
    }

    public long getNoOfDateMatches() {
        return noOfDateMatches;
    }

    public void setNoOfDateMatches(long noOfDateMatches) {
        this.noOfDateMatches = noOfDateMatches;
    }

    public long getNoOfDatingTips() {
        return noOfDatingTips;
    }

    public void setNoOfDatingTips(long noOfDatingTips) {
        this.noOfDatingTips = noOfDatingTips;
    }

    public long getNoOfInfoSMS() {
        return noOfInfoSMS;
    }

    public void setNoOfInfoSMS(long noOfInfoSMS) {
        this.noOfInfoSMS = noOfInfoSMS;
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

    public int getInitiator() {
        return initiator;
    }

    public void setInitiator(int initiator) {
        this.initiator = initiator;
    }

    public String getInitiatorComments() {
        return initiatorComments;
    }

    public void setInitiatorComments(String initiatorComments) {
        this.initiatorComments = initiatorComments;
    }

    @Override
    public String toString() {
        return "Job{" + "id=" + id + ", jobDate=" + jobDate + ", batchId=" + batchId + ", totalProduced=" + totalProduced + ", noOfDateMatches=" + noOfDateMatches + ", noOfDatingTips=" + noOfDatingTips + ", noOfInfoSMS=" + noOfInfoSMS + ", status=" + status + ", reason=" + reason + ", initiator=" + initiator + ", initiatorComments=" + initiatorComments + '}';
    }

}
