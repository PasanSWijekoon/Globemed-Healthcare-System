package com.globemed.application.form.other;

import com.globemed.model.Appointment;
import com.globemed.model.Invoice;
import com.globemed.model.PatientRecord;
import com.globemed.model.User;
import com.globemed.reports.IReportVisitor;
import com.globemed.reports.TextReportGenerator;
import com.globemed.service.AppointmentScheduler;
import com.globemed.service.BillingService;
import com.globemed.service.IPatientDataService;
import com.globemed.service.proxy.PatientDataProxy;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Pasan
 */
public class FormReports extends javax.swing.JPanel {

    private User currentUser;
    private IPatientDataService patientDataService;
    private AppointmentScheduler appointmentScheduler;
    private BillingService billingService;

    public FormReports(User user) {
        initComponents();
        initComponents();
        this.currentUser = user;
        this.patientDataService = new PatientDataProxy(user);
        this.appointmentScheduler = new AppointmentScheduler();
        this.billingService = new BillingService();

        setupReportSelector();
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new java.awt.Font("Monospaced", 0, 12));

        CardLayout cl = (CardLayout) parameterCardPanel.getLayout();
        cl.show(parameterCardPanel, "blankCard");
    }

    private void setupReportSelector() {
        String[] reports = {"-- Select a Report --", "Patient Treatment Summary", "Financial Report", "Appointment Schedule Report"};
        cmbReportType.setModel(new DefaultComboBoxModel<>(reports));
    }

    private void generatePatientSummaryReport() throws SQLException {
        String patientId = txtPatientId.getText(); // jTextField1 is for patient ID
        if (patientId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Patient ID.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        PatientRecord record = patientDataService.getPatientRecord(patientId);
        if (record == null) {
            reportTextArea.setText("No patient found with ID: " + patientId);
            return;
        }

        IReportVisitor visitor = new TextReportGenerator();
        String reportContent = record.accept(visitor);

        reportTextArea.setText(reportContent);
    }

    private void generateAppointmentScheduleReport() throws SQLException {
        Date selectedDate = dateSchedule.getDate();
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a date.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Appointment> appointments = appointmentScheduler.getAppointmentsForDate(selectedDate);
        if (appointments.isEmpty()) {
            reportTextArea.setText("No appointments found for the selected date.");
            return;
        }

        StringBuilder reportText = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm a");

        reportText.append("--- Appointment Schedule for ").append(dateFormat.format(selectedDate)).append(" ---\n\n");
        reportText.append(String.format("%-12s | %-15s | %-15s | %-12s\n", "Time", "Patient ID", "Staff ID", "Status"));
        reportText.append("----------------------------------------------------------------\n");

        for (Appointment app : appointments) {
            reportText.append(String.format("%-12s | %-15s | %-15s | %-12s\n",
                    timeFormat.format(app.getAppointmentDate()),
                    app.getPatientId(),
                    app.getStaffId(),
                    app.getStatus()));
        }

        reportTextArea.setText(reportText.toString());
    }

    private void findAndSetPatient() {
        String searchTerm = txtPatientId.getText();
        if (searchTerm.trim().isEmpty()) {
            return;
        }

        try {
            List<PatientRecord> results = patientDataService.searchPatients(searchTerm);

            if (!results.isEmpty()) {
                PatientRecord patient = results.get(0);

                txtPatientId.setText(patient.getPatientId());
                lblPatientName.setText(patient.getFName() + " " + patient.getLName());
            } else {
                lblPatientName.setText("Patient not found.");
                JOptionPane.showMessageDialog(this, "No patient found matching your search.", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException | SecurityException e) {
            lblPatientName.setText("Error.");
            JOptionPane.showMessageDialog(this, "Error searching for patient: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateFinancialReport() throws SQLException {
        Date startDate = dateStart.getDate();
        Date endDate = dateEnd.getDate();

        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both a start and an end date.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (endDate.before(startDate)) {
            JOptionPane.showMessageDialog(this, "End date cannot be before the start date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Invoice> invoices = billingService.getInvoicesByDateRange(startDate, endDate);
        if (invoices.isEmpty()) {
            CardLayout cl = (CardLayout) reportDisplayPanel.getLayout();
            cl.show(reportDisplayPanel, "textCard");
            reportTextArea.setText("No financial records found for the selected date range.");
            return;
        }

        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (Invoice inv : invoices) {
            if ("Paid".equalsIgnoreCase(inv.getStatus())) {
                totalRevenue = totalRevenue.add(inv.getAmount());
            }
        }

        Map<LocalDate, BigDecimal> dailyRevenue = invoices.stream()
                .filter(inv -> "Paid".equalsIgnoreCase(inv.getStatus()))
                .collect(Collectors.groupingBy(
                        inv -> inv.getServiceDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        Collectors.reducing(BigDecimal.ZERO, Invoice::getAmount, BigDecimal::add)
                ));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<LocalDate, BigDecimal> entry : dailyRevenue.entrySet()) {
            dataset.addValue(entry.getValue(), "Revenue", entry.getKey().toString());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Daily Revenue", "Date", "Revenue ($)", dataset);

        ChartPanel chartPanel = new ChartPanel(barChart);

        chartReportCardPanel.removeAll();
        chartReportCardPanel.add(chartPanel, BorderLayout.CENTER);
        chartReportCardPanel.revalidate();
        chartReportCardPanel.repaint();

        CardLayout cl = (CardLayout) reportDisplayPanel.getLayout();
        cl.show(reportDisplayPanel, "chartCard");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbReportType = new javax.swing.JComboBox<>();
        parameterCardPanel = new javax.swing.JPanel();
        patientParamsPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtPatientId = new javax.swing.JTextField();
        lblPatientName = new javax.swing.JLabel();
        financialParamsPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        dateStart = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        dateEnd = new com.toedter.calendar.JDateChooser();
        appointmentParamsPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        dateSchedule = new com.toedter.calendar.JDateChooser();
        blank = new javax.swing.JPanel();
        reportDisplayPanel = new javax.swing.JPanel();
        textReportCardPanel = new javax.swing.JPanel();
        reportScrollPane = new javax.swing.JScrollPane();
        reportTextArea = new javax.swing.JTextArea();
        chartReportCardPanel = new javax.swing.JPanel();
        btnGenerateReport = new javax.swing.JButton();

        jLabel1.setText("Select a Report :");

        cmbReportType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbReportTypeActionPerformed(evt);
            }
        });

        parameterCardPanel.setLayout(new java.awt.CardLayout());

        jLabel2.setText("Enter Patient ID:");

        txtPatientId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPatientIdKeyReleased(evt);
            }
        });

        lblPatientName.setText("Patient Name");

        javax.swing.GroupLayout patientParamsPanelLayout = new javax.swing.GroupLayout(patientParamsPanel);
        patientParamsPanel.setLayout(patientParamsPanelLayout);
        patientParamsPanelLayout.setHorizontalGroup(
            patientParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patientParamsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPatientId, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPatientName)
                .addContainerGap(90, Short.MAX_VALUE))
        );
        patientParamsPanelLayout.setVerticalGroup(
            patientParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patientParamsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(patientParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtPatientId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPatientName))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        parameterCardPanel.add(patientParamsPanel, "patientReportParamsCard");

        jLabel3.setText("Start Date:");

        jLabel4.setText("End Date:");

        javax.swing.GroupLayout financialParamsPanelLayout = new javax.swing.GroupLayout(financialParamsPanel);
        financialParamsPanel.setLayout(financialParamsPanelLayout);
        financialParamsPanelLayout.setHorizontalGroup(
            financialParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(financialParamsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateStart, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        financialParamsPanelLayout.setVerticalGroup(
            financialParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(financialParamsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(financialParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(dateEnd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dateStart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        parameterCardPanel.add(financialParamsPanel, "financialReportParamsCard");

        jLabel5.setText("Select Date:");

        javax.swing.GroupLayout appointmentParamsPanelLayout = new javax.swing.GroupLayout(appointmentParamsPanel);
        appointmentParamsPanel.setLayout(appointmentParamsPanelLayout);
        appointmentParamsPanelLayout.setHorizontalGroup(
            appointmentParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentParamsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateSchedule, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(204, Short.MAX_VALUE))
        );
        appointmentParamsPanelLayout.setVerticalGroup(
            appointmentParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentParamsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(appointmentParamsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dateSchedule, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        parameterCardPanel.add(appointmentParamsPanel, "appointmentReportParamsCard");

        javax.swing.GroupLayout blankLayout = new javax.swing.GroupLayout(blank);
        blank.setLayout(blankLayout);
        blankLayout.setHorizontalGroup(
            blankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
        );
        blankLayout.setVerticalGroup(
            blankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 34, Short.MAX_VALUE)
        );

        parameterCardPanel.add(blank, "blankCard");

        reportDisplayPanel.setLayout(new java.awt.CardLayout());

        textReportCardPanel.setLayout(new java.awt.BorderLayout());

        reportTextArea.setColumns(20);
        reportTextArea.setRows(5);
        reportScrollPane.setViewportView(reportTextArea);

        textReportCardPanel.add(reportScrollPane, java.awt.BorderLayout.CENTER);

        reportDisplayPanel.add(textReportCardPanel, "textCard");

        chartReportCardPanel.setLayout(new java.awt.BorderLayout());
        reportDisplayPanel.add(chartReportCardPanel, "chartCard");

        btnGenerateReport.setText("Generate");
        btnGenerateReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateReportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(reportDisplayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 826, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(19, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbReportType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGenerateReport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(parameterCardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGenerateReport)
                            .addComponent(jLabel1)
                            .addComponent(cmbReportType, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7))
                    .addComponent(parameterCardPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reportDisplayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 862, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 552, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbReportTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbReportTypeActionPerformed
        String selectedReport = cmbReportType.getSelectedItem().toString();
        CardLayout cl = (CardLayout) parameterCardPanel.getLayout();

        switch (selectedReport) {
            case "Patient Treatment Summary":
                cl.show(parameterCardPanel, "patientReportParamsCard");
                break;
            case "Financial Report":
                cl.show(parameterCardPanel, "financialReportParamsCard");
                break;
            case "Appointment Schedule Report":
                cl.show(parameterCardPanel, "appointmentReportParamsCard");
                break;
            default:
                cl.show(parameterCardPanel, "blankCard");
                break;
        }
    }//GEN-LAST:event_cmbReportTypeActionPerformed

    private void txtPatientIdKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPatientIdKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            findAndSetPatient();
        }
    }//GEN-LAST:event_txtPatientIdKeyReleased

    private void btnGenerateReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateReportActionPerformed
        String selectedReport = cmbReportType.getSelectedItem().toString();

        // Always clear the text area for text-based reports
        reportTextArea.setText("");

        try {
            switch (selectedReport) {
                case "Patient Treatment Summary":
                case "Appointment Schedule Report":
                    // For text reports, first make sure the text panel is visible.
                    CardLayout cl = (CardLayout) reportDisplayPanel.getLayout();
                    cl.show(reportDisplayPanel, "textCard"); // Assumes your text area is on a card named "textCard"

                    // Now, call the appropriate generation method
                    if (selectedReport.equals("Patient Treatment Summary")) {
                        generatePatientSummaryReport();
                    } else {
                        generateAppointmentScheduleReport();
                    }
                    break;

                case "Financial Report":
                    generateFinancialReport();
                    break;

                default:
                    JOptionPane.showMessageDialog(this, "Please select a report type.", "No Report Selected", JOptionPane.WARNING_MESSAGE);
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Helpful for debugging
        }
    }//GEN-LAST:event_btnGenerateReportActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel appointmentParamsPanel;
    private javax.swing.JPanel blank;
    private javax.swing.JButton btnGenerateReport;
    private javax.swing.JPanel chartReportCardPanel;
    private javax.swing.JComboBox<String> cmbReportType;
    private com.toedter.calendar.JDateChooser dateEnd;
    private com.toedter.calendar.JDateChooser dateSchedule;
    private com.toedter.calendar.JDateChooser dateStart;
    private javax.swing.JPanel financialParamsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblPatientName;
    private javax.swing.JPanel parameterCardPanel;
    private javax.swing.JPanel patientParamsPanel;
    private javax.swing.JPanel reportDisplayPanel;
    private javax.swing.JScrollPane reportScrollPane;
    private javax.swing.JTextArea reportTextArea;
    private javax.swing.JPanel textReportCardPanel;
    private javax.swing.JTextField txtPatientId;
    // End of variables declaration//GEN-END:variables
}
