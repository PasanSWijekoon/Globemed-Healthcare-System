package com.globemed.service;

import com.globemed.billing.ClaimRequest;
import com.globemed.billing.DirectPaymentHandler;
import com.globemed.billing.IClaimHandler;
import com.globemed.billing.CeylincoInsuranceHandler;
import com.globemed.model.InsuranceClaim;
import com.globemed.model.Invoice;
import com.globemed.util.DBUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class BillingService implements IBillingService{

    private IClaimHandler chain;

    public BillingService() {
        this.chain = new CeylincoInsuranceHandler();
        IClaimHandler directPaymentHandler = new DirectPaymentHandler();
        this.chain.setNextHandler(directPaymentHandler);
    }

    public void processInvoice(ClaimRequest request) throws SQLException {
        System.out.println("Initiating invoice processing for ID: " + request.getInvoice().getInvoiceId());
        chain.handleRequest(request);
    }

    public Invoice createInvoice(String patientId, BigDecimal amount) throws SQLException {
        String sql = "INSERT INTO invoices (patient_id, service_date, amount, status) VALUES (?, ?, ?, ?)";
        Invoice newInvoice = null;
        Date serviceDate = new Date(); 

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, patientId);
            ps.setTimestamp(2, new java.sql.Timestamp(serviceDate.getTime()));
            ps.setBigDecimal(3, amount);
            ps.setString(4, "Pending");
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int invoiceId = generatedKeys.getInt(1);
                        newInvoice = new Invoice(invoiceId, patientId, serviceDate, amount, "Pending", 0);
                    }
                }
            }
        }
        return newInvoice;
    }

    public List<Invoice> getAllInvoices() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY service_date DESC"; 

        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Invoice invoice = new Invoice(
                        rs.getInt("invoice_id"),
                        rs.getString("patient_id"),
                        rs.getTimestamp("service_date"),
                        rs.getBigDecimal("amount"),
                        rs.getString("status"),
                        rs.getInt("claim_id")
                );
                invoices.add(invoice);
            }
        }
        return invoices;
    }

    public void createAndLinkInsuranceClaim(int invoiceId, String provider, String policyNumber) throws SQLException {
        Connection conn = DBUtil.getConnection();
        conn.setAutoCommit(false);

        try {
            String claimSql = "INSERT INTO insurance_claims (invoice_id, insurance_provider, claim_status) VALUES (?, ?, ?)";
            int claimId = 0;
            try (PreparedStatement psClaim = conn.prepareStatement(claimSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psClaim.setInt(1, invoiceId);
                psClaim.setString(2, provider);
                psClaim.setString(3, "Submitted");
                psClaim.executeUpdate();

                try (ResultSet generatedKeys = psClaim.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        claimId = generatedKeys.getInt(1);
                    }
                }
            }

            if (claimId == 0) {
                throw new SQLException("Creating insurance claim failed, no ID obtained.");
            }

            String invoiceSql = "UPDATE invoices SET status = 'Claimed', claim_id = ? WHERE invoice_id = ?";
            try (PreparedStatement psInvoice = conn.prepareStatement(invoiceSql)) {
                psInvoice.setInt(1, claimId);
                psInvoice.setInt(2, invoiceId);
                psInvoice.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback(); 
            throw e; 
        } finally {
            conn.setAutoCommit(true);
            if (conn != null) {
                conn.close();
            }
        }
    }

    public InsuranceClaim getInsuranceClaim(int claimId) throws SQLException {
        String sql = "SELECT * FROM insurance_claims WHERE claim_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, claimId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new InsuranceClaim(
                        rs.getInt("claim_id"),
                        rs.getInt("invoice_id"),
                        rs.getString("insurance_provider"),
                        rs.getString("claim_status")
                );
            }
        }
        return null;
    }

    public void updateClaimStatus(int claimId, String newStatus) throws SQLException {
        String sql = "UPDATE insurance_claims SET claim_status = ? WHERE claim_id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, claimId);
            ps.executeUpdate();
        }
    }
    
    public void markInvoiceAsPaid(int invoiceId) throws SQLException {
        String sql = "UPDATE invoices SET status = 'Paid' WHERE invoice_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.executeUpdate();
        }
    }
    
    public List<Invoice> getInvoicesByDateRange(java.util.Date startDate, java.util.Date endDate) throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices WHERE service_date BETWEEN ? AND ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(startDate.getTime()));
            ps.setDate(2, new java.sql.Date(endDate.getTime()));
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                invoices.add(new Invoice(
                    rs.getInt("invoice_id"),
                    rs.getString("patient_id"),
                    rs.getTimestamp("service_date"),
                    rs.getBigDecimal("amount"),
                    rs.getString("status"),
                    rs.getInt("claim_id")
                ));
            }
        }
        return invoices;
    }

}
