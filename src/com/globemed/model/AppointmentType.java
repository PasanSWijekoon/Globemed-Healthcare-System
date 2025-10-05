package com.globemed.model;

public class AppointmentType {
    private int typeId;
    private String typeName;
    private int durationMinutes; 

   
    public AppointmentType(int typeId, String typeName, int durationMinutes) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.durationMinutes = durationMinutes;
    }


    public int getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }

 
    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    public String toString() {
        return typeName;
    }
}