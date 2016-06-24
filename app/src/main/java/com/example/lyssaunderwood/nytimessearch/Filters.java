package com.example.lyssaunderwood.nytimessearch;

import org.parceler.Parcel;

import java.io.Serializable;

@Parcel
public class Filters {
    public void setArts(boolean arts) {
        this.arts = arts;
    }

    public void setSports(boolean sports) {
        this.sports = sports;
    }

    public void setFashion(boolean fashion) {
        this.fashion = fashion;
    }

    public void setSpinnerVal(String spinnerVal) {
        this.spinnerVal = spinnerVal;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String date;
    String spinnerVal;
    boolean arts;
    boolean sports;
    boolean fashion;

    public Filters() {
        
    }

    public Filters(boolean art, boolean fash, boolean sport, String spinner, String begin_date) {
        date = begin_date;
        spinnerVal = spinner;
        arts = art;
        sports = sport;
        fashion = fash;
    }

    public boolean isArts() {
        return arts;
    }

    public boolean isFashion() {
        return fashion;
    }

    public boolean isSports() {
        return sports;
    }

    public String getDate() {
        return date;
    }

    public String getSpinnerVal() {
        return spinnerVal;
    }
}
