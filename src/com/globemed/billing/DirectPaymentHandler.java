package com.globemed.billing;

import com.globemed.service.BillingService;
import java.sql.SQLException;

public class DirectPaymentHandler implements IClaimHandler {
    private IClaimHandler nextHandler;

    @Override
    public void setNextHandler(IClaimHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handleRequest(ClaimRequest request) throws SQLException {
        if ("DirectPayment".equalsIgnoreCase(request.getProvider())) {
            System.out.println("Processing Direct Payment for Invoice #" + request.getInvoice().getInvoiceId());
            new BillingService().markInvoiceAsPaid(request.getInvoice().getInvoiceId());
        } else if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
    }
}