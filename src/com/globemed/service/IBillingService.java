package com.globemed.service;


import com.globemed.billing.ClaimRequest;
import com.globemed.model.InsuranceClaim;
import com.globemed.model.Invoice;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;


public interface IBillingService {


    void processInvoice(ClaimRequest request) throws SQLException;

    Invoice createInvoice(String patientId, BigDecimal amount) throws SQLException;

    List<Invoice> getAllInvoices() throws SQLException;

    void createAndLinkInsuranceClaim(int invoiceId, String provider, String policyNumber) throws SQLException;

    InsuranceClaim getInsuranceClaim(int claimId) throws SQLException;

    void updateClaimStatus(int claimId, String newStatus) throws SQLException;

    void markInvoiceAsPaid(int invoiceId) throws SQLException;

    List<Invoice> getInvoicesByDateRange(Date startDate, Date endDate) throws SQLException;
}
