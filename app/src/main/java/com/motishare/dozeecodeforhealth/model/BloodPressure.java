
package com.motishare.dozeecodeforhealth.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BloodPressure {

    @SerializedName("Systole")
    @Expose
    private Integer systole;
    @SerializedName("Diastole")
    @Expose
    private Integer diastole;

    public Integer getSystole() {
        return systole;
    }

    public void setSystole(Integer systole) {
        this.systole = systole;
    }

    public Integer getDiastole() {
        return diastole;
    }

    public void setDiastole(Integer diastole) {
        this.diastole = diastole;
    }

    @Override
    public String toString() {
        return "BloodPressure{" +
                "systole=" + systole +
                ", diastole=" + diastole +
                '}';
    }
}