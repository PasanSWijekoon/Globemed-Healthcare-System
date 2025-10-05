package com.globemed.model;

import com.globemed.billing.state.ApprovedState;
import com.globemed.billing.state.DeniedState;
import com.globemed.billing.state.IClaimState;
import com.globemed.billing.state.SubmittedState;
import java.sql.SQLException;

public class InsuranceClaim {

    private int claimId;
    private int invoiceId;
    private String insuranceProvider;
    private String claimStatus;

    private transient IClaimState state;

    public InsuranceClaim(int claimId, int invoiceId, String insuranceProvider, String claimStatus) {
        this.claimId = claimId;
        this.invoiceId = invoiceId;
        this.insuranceProvider = insuranceProvider;
        this.claimStatus = claimStatus;
        this.setStateFromString(claimStatus);
    }

    private void setStateFromString(String status) {
        switch (status.toLowerCase()) {
            case "submitted":
                this.state = new SubmittedState();
                break;
            case "approved":
                this.state = new ApprovedState();
                break;
            case "denied":
                this.state = new DeniedState();
                break;
            default:
                // Handle unknown state
                this.state = new SubmittedState();
        }
    }

    public void approve() throws SQLException {
        state.approve(this);
    }

    public void deny() throws SQLException {
        state.deny(this);
    }

    public void processPayout() throws SQLException {
        state.processPayout(this);
    }

    public IClaimState getState() {
        return state;
    }

    public void setState(IClaimState state) {
        this.state = state;
    }

    public int getClaimId() {
        return claimId;
    }

    public void setClaimId(int claimId) {
        this.claimId = claimId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    
}
