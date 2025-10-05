package com.globemed.application.form.other;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import com.globemed.model.TreatmentPlanEntry;
import java.util.Calendar; 

public class TreatmentPlanDialog extends JDialog {
    private JTextArea txtPlanDetails;
    private JDateChooser dateStartDate;
    private JDateChooser dateEndDate;
    private JButton btnOk;
    private JButton btnCancel;

    private TreatmentPlanEntry planEntry = null;

    public TreatmentPlanDialog(Frame parent) {
        super(parent, "Enter Treatment Plan", true);
        
        setLayout(new BorderLayout(10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Plan Details:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        txtPlanDetails = new JTextArea(5, 20);
        formPanel.add(new JScrollPane(txtPlanDetails), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        dateStartDate = new JDateChooser();
        dateStartDate.setDate(new Date()); 
        dateStartDate.setPreferredSize(new Dimension(150, dateStartDate.getPreferredSize().height));
        formPanel.add(dateStartDate, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        dateEndDate = new JDateChooser();
        dateEndDate.setPreferredSize(new Dimension(150, dateEndDate.getPreferredSize().height));
        formPanel.add(dateEndDate, gbc);
        
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnOk = new JButton("OK");
        btnCancel = new JButton("Cancel");
        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);

        btnOk.addActionListener(e -> onOk());
        btnCancel.addActionListener(e -> onCancel());

        pack();
        setLocationRelativeTo(parent);
    }

    private void onOk() {
        if (txtPlanDetails.getText().trim().isEmpty() || dateStartDate.getDate() == null || dateEndDate.getDate() == null) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date startDate = dateStartDate.getDate();
        Date endDate = dateEndDate.getDate();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        
        if (startDate.before(today)) {
            JOptionPane.showMessageDialog(this, "Start date cannot be in the past.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (endDate.before(startDate)) {
            JOptionPane.showMessageDialog(this, "End date cannot be before the start date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        planEntry = new TreatmentPlanEntry(
            txtPlanDetails.getText(),
            startDate,
            endDate
        );
        setVisible(false); 
    }

    private void onCancel() {
        planEntry = null;
        setVisible(false);
    }

    public TreatmentPlanEntry showDialog() {
        setVisible(true);
        return planEntry;
    }
}