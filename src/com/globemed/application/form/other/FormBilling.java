package com.globemed.application.form.other;

import com.globemed.billing.ClaimRequest;
import com.globemed.model.InsuranceClaim;
import com.globemed.model.Invoice;
import com.globemed.model.PatientRecord;
import com.globemed.model.User;
import com.globemed.service.BillingService;
import com.globemed.service.IPatientDataService;
import com.globemed.service.proxy.PatientDataProxy;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Pasan
 */
public class FormBilling extends javax.swing.JPanel {

    private User currentUser;
    private BillingService billingService;
    private IPatientDataService patientDataService;
    private List<Invoice> invoiceList;
    private Invoice selectedInvoice = null;

    public FormBilling(User user) {
        initComponents();
        this.currentUser = user;
        this.billingService = new BillingService();
        this.patientDataService = new PatientDataProxy(user);
        jTabbedPane1.setEnabledAt(1, false);

        loadInvoiceData();

        clearBillingForm();

        tblInvoices.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && tblInvoices.getSelectedRow() != -1) {
                    displaySelectedInvoiceDetails();
                }
            }
        });
    }

    private void displaySelectedInvoiceDetails() {
        int selectedRow = tblInvoices.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        this.selectedInvoice = this.invoiceList.get(selectedRow);

        txtInvoiceId.setText(String.valueOf(selectedInvoice.getInvoiceId()));
        txtPatientId.setText(selectedInvoice.getPatientId());
        txtAmount.setText(selectedInvoice.getAmount().toString());
        txtStatus.setText(selectedInvoice.getStatus());

        try {
            PatientRecord patient = patientDataService.getPatientRecord(selectedInvoice.getPatientId());
            lblPatientName.setText(patient != null ? patient.getFName() + " " + patient.getLName() : "Patient Not Found");
        } catch (Exception e) {
            lblPatientName.setText("Error Loading Name");
        }

        resetClaimFieldsAndButtons();
        String status = selectedInvoice.getStatus();

        if ("Pending".equalsIgnoreCase(status)) {
            btnSubmitInsuranceClaim.setEnabled(true);
            btnMarkAsPaid.setEnabled(true);
        } else if ("Claimed".equalsIgnoreCase(status)) {
            jTabbedPane1.setEnabledAt(1, true);
            try {
                InsuranceClaim claim = billingService.getInsuranceClaim(selectedInvoice.getClaimId());
                if (claim != null) {
                    txtClaimId.setText(String.valueOf(claim.getClaimId()));
                    txtProvider.setText(claim.getInsuranceProvider());
                    txtClaimStatus.setText(claim.getClaimStatus());

                    String claimStatus = claim.getClaimStatus();
                    if ("Submitted".equalsIgnoreCase(claimStatus)) {
                        btnApproveClaim.setEnabled(true);
                        btnDenyClaim.setEnabled(true);
                    } else if ("Approved".equalsIgnoreCase(claimStatus)) {
                        btnProcessPayout.setEnabled(true);
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading claim details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleClaimAction(String action) {
        if (selectedInvoice == null) {
            return;
        }
        try {
            InsuranceClaim claim = billingService.getInsuranceClaim(selectedInvoice.getClaimId());
            if (claim == null) {
                return;
            }

            String successMessage = "";
            switch (action) {
                case "approve":
                    claim.approve();
                    successMessage = "Claim Approved.";
                    break;
                case "deny":
                    claim.deny();
                    successMessage = "Claim Denied.";
                    break;
                case "payout":
                    claim.processPayout();
                    successMessage = "Insurance Payout Processed. Invoice is now Paid.";
                    break;
            }
            JOptionPane.showMessageDialog(this, successMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
            loadInvoiceData();
            clearBillingForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error processing claim: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadInvoiceData() {
        try {
            DefaultTableModel model = (DefaultTableModel) tblInvoices.getModel();
            model.setRowCount(0);

            this.invoiceList = billingService.getAllInvoices();

            for (Invoice invoice : this.invoiceList) {
                Object[] row = new Object[]{
                    invoice.getInvoiceId(),
                    invoice.getPatientId(),
                    invoice.getServiceDate(),
                    invoice.getAmount(),
                    invoice.getStatus()
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading invoice data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearBillingForm() {
        txtInvoiceId.setText("");
        txtPatientId.setText("");
        lblPatientName.setText("");
        txtAmount.setText("");
        txtStatus.setText("");

        resetClaimFieldsAndButtons();

        selectedInvoice = null;
        tblInvoices.clearSelection();
    }

    private void resetClaimFieldsAndButtons() {
        jTabbedPane1.setSelectedIndex(0);
        jTabbedPane1.setEnabledAt(1, false);
        txtClaimId.setText("");
        txtProvider.setText("");
        txtClaimStatus.setText("");

        btnSubmitInsuranceClaim.setEnabled(false);
        btnMarkAsPaid.setEnabled(false);
        btnApproveClaim.setEnabled(false);
        btnDenyClaim.setEnabled(false);
        btnProcessPayout.setEnabled(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblInvoices = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtInvoiceId = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPatientId = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        lblPatientName = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JTextField();
        txtStatus = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtClaimId = new javax.swing.JTextField();
        txtProvider = new javax.swing.JTextField();
        txtClaimStatus = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        btnSubmitInsuranceClaim = new javax.swing.JButton();
        btnMarkAsPaid = new javax.swing.JButton();
        btnApproveClaim = new javax.swing.JButton();
        btnDenyClaim = new javax.swing.JButton();
        btnProcessPayout = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("All Invoices");

        tblInvoices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Invoice ID", "Patient ID", "Service Date", "Amount", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblInvoices);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Invoice Details");

        jLabel3.setText("Invoice ID:");

        txtInvoiceId.setEditable(false);

        jLabel4.setText("Patient ID:");

        txtPatientId.setEditable(false);

        jLabel5.setText("Patient Name:");

        lblPatientName.setText(".........");

        jLabel6.setText("Amount:");

        txtAmount.setEditable(false);

        txtStatus.setEditable(false);

        jLabel7.setText("Status:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtInvoiceId, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel7)
                                    .addGap(24, 24, 24)
                                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtPatientId, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPatientName)))))
                .addContainerGap(170, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtInvoiceId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(lblPatientName))
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtPatientId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Invoice Details", jPanel1);

        jLabel9.setText("Claim ID");

        jLabel10.setText("Insurance Provider");

        jLabel11.setText("Claim Status");

        txtClaimId.setEditable(false);

        txtProvider.setEditable(false);
        txtProvider.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProviderActionPerformed(evt);
            }
        });

        txtClaimStatus.setEditable(false);
        txtClaimStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClaimStatusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtClaimId, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(44, 44, 44)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtProvider)
                            .addComponent(txtClaimStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))))
                .addContainerGap(310, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtClaimId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtProvider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtClaimStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(70, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Claim Management", jPanel2);

        jLabel8.setText("Process Payment");

        btnSubmitInsuranceClaim.setText("Submit Insurance Claim");
        btnSubmitInsuranceClaim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitInsuranceClaimActionPerformed(evt);
            }
        });

        btnMarkAsPaid.setText("Mark As Paid");
        btnMarkAsPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarkAsPaidActionPerformed(evt);
            }
        });

        btnApproveClaim.setText("Approve Claim");
        btnApproveClaim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApproveClaimActionPerformed(evt);
            }
        });

        btnDenyClaim.setText("Deny Claim");
        btnDenyClaim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDenyClaimActionPerformed(evt);
            }
        });

        btnProcessPayout.setText("Process Payout");
        btnProcessPayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessPayoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8)
                    .addComponent(btnSubmitInsuranceClaim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnApproveClaim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMarkAsPaid, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDenyClaim, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnProcessPayout, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(102, 102, 102))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSubmitInsuranceClaim)
                    .addComponent(btnMarkAsPaid))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnApproveClaim)
                    .addComponent(btnDenyClaim))
                .addGap(18, 18, 18)
                .addComponent(btnProcessPayout)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1001, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 561, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(28, 28, 28))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtProviderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProviderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProviderActionPerformed

    private void txtClaimStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClaimStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClaimStatusActionPerformed

    private void btnSubmitInsuranceClaimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitInsuranceClaimActionPerformed
        if (selectedInvoice == null) {
            return;
        }

        String provider = JOptionPane.showInputDialog(this, "Enter Insurance Provider", "Submit Claim", JOptionPane.PLAIN_MESSAGE);
        if (provider == null || provider.trim().isEmpty()) {
            return;
        }

        try {

            ClaimRequest request = new ClaimRequest(selectedInvoice, provider);
            billingService.processInvoice(request);

            JOptionPane.showMessageDialog(this, "Claim submitted successfully via the processing chain.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadInvoiceData();
            clearBillingForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error submitting claim: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSubmitInsuranceClaimActionPerformed

    private void btnMarkAsPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarkAsPaidActionPerformed
        if (selectedInvoice == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Process direct payment for this invoice?", "Confirm Payment", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {

                ClaimRequest request = new ClaimRequest(selectedInvoice, "DirectPayment");
                billingService.processInvoice(request);

                JOptionPane.showMessageDialog(this, "Invoice marked as Paid successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadInvoiceData();
                clearBillingForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating invoice: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnMarkAsPaidActionPerformed

    private void btnApproveClaimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApproveClaimActionPerformed
        handleClaimAction("approve");
    }//GEN-LAST:event_btnApproveClaimActionPerformed

    private void btnDenyClaimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDenyClaimActionPerformed
        handleClaimAction("deny");
    }//GEN-LAST:event_btnDenyClaimActionPerformed

    private void btnProcessPayoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessPayoutActionPerformed
        handleClaimAction("payout");
    }//GEN-LAST:event_btnProcessPayoutActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApproveClaim;
    private javax.swing.JButton btnDenyClaim;
    private javax.swing.JButton btnMarkAsPaid;
    private javax.swing.JButton btnProcessPayout;
    private javax.swing.JButton btnSubmitInsuranceClaim;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblPatientName;
    private javax.swing.JTable tblInvoices;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtClaimId;
    private javax.swing.JTextField txtClaimStatus;
    private javax.swing.JTextField txtInvoiceId;
    private javax.swing.JTextField txtPatientId;
    private javax.swing.JTextField txtProvider;
    private javax.swing.JTextField txtStatus;
    // End of variables declaration//GEN-END:variables
}
