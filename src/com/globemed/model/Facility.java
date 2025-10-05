package com.globemed.model;

public class Facility {
    private int facilityId;
    private String facilityName;

    public Facility(int facilityId, String facilityName) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    @Override
    public String toString() {
        return facilityName;
    }
}