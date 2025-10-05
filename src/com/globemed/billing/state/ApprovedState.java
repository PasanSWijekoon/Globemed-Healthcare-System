package com.globemed.billing.state;

import com.globemed.model.InsuranceClaim;
import com.globemed.service.BillingService;
import java.sql.SQLException;

public class ApprovedState implements IClaimState {
    @Override
    public void approve(InsuranceClaim claim) {
        System.out.println("Claim is already approved.");
    }

    @Override
    public void deny(InsuranceClaim claim) {
        System.out.println("Cannot deny a claim that has already been approved.");
    }

    @Override
    public void processPayout(InsuranceClaim claim) throws SQLException {
        new BillingService().markInvoiceAsPaid(claim.getInvoiceId());
    }
}