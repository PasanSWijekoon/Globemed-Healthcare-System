package com.globemed.billing.state;

import com.globemed.model.InsuranceClaim;
import com.globemed.service.BillingService;
import java.sql.SQLException;

public class SubmittedState implements IClaimState {
    @Override
    public void approve(InsuranceClaim claim) throws SQLException {
        claim.setState(new ApprovedState());
        new BillingService().updateClaimStatus(claim.getClaimId(), "Approved");
    }

    @Override
    public void deny(InsuranceClaim claim) throws SQLException {
        claim.setState(new DeniedState());
        new BillingService().updateClaimStatus(claim.getClaimId(), "Denied");
    }

    @Override
    public void processPayout(InsuranceClaim claim) {
        System.out.println("Cannot process payout for a claim that is still in 'Submitted' state.");
    }
}