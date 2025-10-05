package com.globemed.billing.state;

import com.globemed.model.InsuranceClaim;

public class DeniedState implements IClaimState {
    @Override
    public void approve(InsuranceClaim claim) { System.out.println("Cannot approve a denied claim."); }
    @Override
    public void deny(InsuranceClaim claim) { System.out.println("Claim is already denied."); }
    @Override
    public void processPayout(InsuranceClaim claim) { System.out.println("Cannot process payout for a denied claim."); }
}