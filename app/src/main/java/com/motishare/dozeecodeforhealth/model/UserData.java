package com.motishare.dozeecodeforhealth.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.motishare.dozeecodeforhealth.model.BP;
import com.motishare.dozeecodeforhealth.model.BloodPressure;

public class UserData {

    @SerializedName("HeartRate")
    @Expose
    private Integer heartRate;
    @SerializedName("BreathRate")
    @Expose
    private Integer breathRate;
    @SerializedName("O2")
    @Expose
    private Integer o2;
    @SerializedName("Blood Pressure")
    @Expose
    private BloodPressure bloodPressure;
    @SerializedName("Recovery")
    @Expose
    private Integer recovery;
    @SerializedName("sleepscore")
    @Expose
    private Integer sleepscore;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("BP")
    @Expose
    private BP bP;

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Integer getBreathRate() {
        return breathRate;
    }

    public void setBreathRate(Integer breathRate) {
        this.breathRate = breathRate;
    }

    public Integer getO2() {
        return o2;
    }

    public void setO2(Integer o2) {
        this.o2 = o2;
    }

    public BloodPressure getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(BloodPressure bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Integer getRecovery() {
        return recovery;
    }

    public void setRecovery(Integer recovery) {
        this.recovery = recovery;
    }

    public Integer getSleepscore() {
        return sleepscore;
    }

    public void setSleepscore(Integer sleepscore) {
        this.sleepscore = sleepscore;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BP getBP() {
        return bP;
    }

    public void setBP(BP bP) {
        this.bP = bP;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "heartRate=" + heartRate +
                ", breathRate=" + breathRate +
                ", o2=" + o2 +
                ", bloodPressure=" + bloodPressure +
                ", recovery=" + recovery +
                ", sleepscore=" + sleepscore +
                ", time=" + time +
                ", bP=" + bP +
                '}';
    }
}