
package com.globemed.service.proxy;

import com.globemed.billing.ClaimRequest;
import com.globemed.model.InsuranceClaim;
import com.globemed.model.Invoice;
import com.globemed.model.Role;
import com.globemed.model.User;
import com.globemed.service.IBillingService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.sql.SQLException;



public class BillingServiceProxy implements IBillingService {

    private final IBillingService realService;
    private final User currentUser;

    public BillingServiceProxy(IBillingService realService, User currentUser) {
        this.realService = realService;
        this.currentUser = currentUser;
    }


    private boolean canManageBilling() {
        return currentUser.getRole() == Role.Admin || currentUser.getRole() == Role.Pharmacist;
    }

    private boolean isAdmin() {
        return currentUser.getRole() == Role.Admin;
    }

    @Override
    public void processInvoice(ClaimRequest request) throws SQLException {
        if (!canManageBilling()) {
            throw new SecurityException("Access Denied: User " + currentUser.getUsername() + " cannot process invoices.");
        }
        realService.processInvoice(request);
    }

    @Override
    public Invoice createInvoice(String patientId, BigDecimal amount) throws SQLException {
        if (!canManageBilling()) {
            throw new SecurityException("Access Denied: User " + currentUser.getUsername() + " cannot create invoices.");
        }
        return realService.createInvoice(patientId, amount);
    }

    @Override
    public List<Invoice> getAllInvoices() throws SQLException {
        if (!canManageBilling()) {
            throw new SecurityException("Access Denied: User " + currentUser.getUsername() + " cannot view invoices.");
        }
        return realService.getAllInvoices();
    }

    @Override
    public void createAndLinkInsuranceClaim(int invoiceId, String provider, String policyNumber) throws SQLException {
        if (!canManageBilling()) {
            throw new SecurityException("Access Denied: User " + currentUser.getUsername() + " cannot create insurance claims.");
        }
        realService.createAndLinkInsuranceClaim(invoiceId, provider, policyNumber);
    }

    @Override
    public InsuranceClaim getInsuranceClaim(int claimId) throws SQLException {
        if (!canManageBilling()) {
            throw new SecurityException("Access Denied: User " + currentUser.getUsername() + " cannot view insurance claims.");
        }
        return realService.getInsuranceClaim(claimId);
    }

    @Override
    public void updateClaimStatus(int claimId, String newStatus) throws SQLException {
        if (!isAdmin()) {
            throw new SecurityException("Access Denied: Only Admins can update claim statuses.");
        }
        realService.updateClaimStatus(claimId, newStatus);
    }

    @Override
    public void markInvoiceAsPaid(int invoiceId) throws SQLException {
        if (!isAdmin()) {
            throw new SecurityException("Access Denied: Only Admins can mark invoices as paid directly.");
        }
        realService.markInvoiceAsPaid(invoiceId);
    }

    @Override
    public List<Invoice> getInvoicesByDateRange(Date startDate, Date endDate) throws SQLException {
        if (!canManageBilling()) {
            throw new SecurityException("Access Denied: User " + currentUser.getUsername() + " cannot view financial reports.");
        }
        return realService.getInvoicesByDateRange(startDate, endDate);
    }
}
