package com.globemed.billing;

import com.globemed.service.BillingService;
import java.sql.SQLException;

public class CeylincoInsuranceHandler extends InsuranceClaimHandler {
    @Override
    protected boolean canHandle(ClaimRequest request) {
       
        return "Ceylinco".equalsIgnoreCase(request.getProvider());
    }

    @Override
    protected void processClaim(ClaimRequest request) throws SQLException {
        System.out.println("Processing Ceylinco claim for Invoice #" + request.getInvoice().getInvoiceId());
        new BillingService().createAndLinkInsuranceClaim(
            request.getInvoice().getInvoiceId(),
            request.getProvider(),
            "DefaultPolicy" 
        );
    }
}