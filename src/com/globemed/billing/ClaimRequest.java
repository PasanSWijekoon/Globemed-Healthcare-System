package com.globemed.billing;

import com.globemed.model.Invoice;

public class ClaimRequest {
    private final Invoice invoice;
    private final String provider;

    public ClaimRequest(Invoice invoice, String provider) {
        this.invoice = invoice;
        this.provider = provider;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public String getProvider() {
        return provider;
    }
}