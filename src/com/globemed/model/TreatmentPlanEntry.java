package com.globemed.model;

import java.util.Date;

public class TreatmentPlanEntry {
    private String planDetails;
    private Date startDate;
    private Date endDate;

    public TreatmentPlanEntry(String planDetails, Date startDate, Date endDate) {
        this.planDetails = planDetails;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getPlanDetails() { return planDetails; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
}