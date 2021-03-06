package com.motishare.dozeecodeforhealth.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BP {

    @SerializedName("Systole")
    @Expose
    private Integer systole;
    @SerializedName("Diastole")
    @Expose
    private Integer diastole;

    public BP(Integer systole, Integer diastole) {
        this.systole = systole;
        this.diastole = diastole;
    }

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
        return "BP{" +
                "systole=" + systole +
                ", diastole=" + diastole +
                '}';
    }
}