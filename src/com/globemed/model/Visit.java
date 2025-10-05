package com.globemed.model;

import com.globemed.reports.IReportVisitor;
import java.util.Date;

public class Visit {
    private Date visitDate;
    private String notes;

    public Visit(Date visitDate, String notes) {
        this.visitDate = visitDate;
        this.notes = notes;
    }

    public String accept(IReportVisitor visitor) {
        return visitor.visit(this);
    }

    public Date getVisitDate() { return visitDate; }
    public String getNotes() { return notes; }
}