package com.globemed.model;

import com.globemed.reports.IReportVisitor;
import java.math.BigDecimal;
import java.util.Date;

public class Invoice {
    private int invoiceId;
    private String patientId;
    private Date serviceDate;
    private BigDecimal amount;
    private String status;
    private int claimId;

    public Invoice(int invoiceId, String patientId, Date serviceDate, BigDecimal amount, String status, int claimId) {
        this.invoiceId = invoiceId;
        this.patientId = patientId;
        this.serviceDate = serviceDate;
        this.amount = amount;
        this.status = status;
        this.claimId = claimId;
    }

    public String accept(IReportVisitor visitor) {
        return visitor.visit(this);
    }

    // Getters and Setters for all fields
    public int getInvoiceId() { return invoiceId; }
    public String getPatientId() { return patientId; }
    public Date getServiceDate() { return serviceDate; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public int getClaimId() { return claimId; }
}