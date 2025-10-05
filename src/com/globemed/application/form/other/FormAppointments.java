package com.globemed.application.form.other;

import com.globemed.command.BookAppointmentCommand;
import com.globemed.command.CancelAppointmentCommand;
import com.globemed.command.IAppointmentCommand;
import com.globemed.command.UpdateAppointmentStatusCommand;
import com.globemed.model.Appointment;
import com.globemed.model.AppointmentType;
import com.globemed.model.Facility;
import com.globemed.model.Invoice;
import com.globemed.model.MedicalHistoryEntry;
import com.globemed.model.PatientRecord;
import com.globemed.model.Staff;
import com.globemed.model.TreatmentPlanEntry;
import com.globemed.model.User;
import com.globemed.model.Visit;
import com.globemed.service.AppointmentScheduler;
import com.globemed.service.BillingService;
import com.globemed.service.IPatientDataService;
import com.globemed.service.StaffDataService;
import com.globemed.service.proxy.PatientDataProxy;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import raven.alerts.MessageAlerts;
import raven.popup.component.PopupCallbackAction;
import raven.popup.component.PopupController;
import raven.toast.Notifications;

/**
 *
 * @author Pasan
 */
public class FormAppointments extends javax.swing.JPanel {

    private final User currentUser;
    private final AppointmentScheduler appointmentScheduler;
    private final StaffDataService staffDataService;
    private final IPatientDataService patientDataService;
    private final BillingService billingService;

    public FormAppointments(User user) {
        initComponents();
        this.currentUser = user;
        this.appointmentScheduler = new AppointmentScheduler();
        this.staffDataService = new StaffDataService();
        this.patientDataService = new PatientDataProxy(user);
        this.billingService = new BillingService();

        initDropdowns();
        loadAppointmentData();
        setupTableSelectionListener();
        updateButtonStates();

        datePicker1.now();
        datePicker1.setEditor(Dateselecter);

        timePicker1.now();
        timePicker1.setEditor(timeselector);
    }

    private void initDropdowns() {

        List<Facility> facilities = appointmentScheduler.getAllFacilities();
        List<AppointmentType> appointmentTypes = appointmentScheduler.getAllAppointmentTypes();

        DefaultComboBoxModel<Facility> facilityModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<AppointmentType> typeModel = new DefaultComboBoxModel<>();

        facilityModel.addElement(new Facility(0, "--- Select a Facility ---"));
        typeModel.addElement(new AppointmentType(0, "--- Select a Type ---", 0));

        facilities.forEach(facilityModel::addElement);
        appointmentTypes.forEach(typeModel::addElement);

        cmbFacility.setModel(facilityModel);
        cmbAppointmentType.setModel(typeModel);
    }

    private void updateButtonStates() {
        int selectedRow = tblAppointments.getSelectedRow();
        boolean isRowSelected = selectedRow != -1;
        String status = "";

        if (isRowSelected) {
            status = tblAppointments.getValueAt(selectedRow, 4).toString();
        }

        boolean isEditable = isRowSelected && "Scheduled".equals(status);
        boolean isCompletable = isRowSelected && "Scheduled".equals(status);

        svappointment.setEnabled(!isRowSelected);
        cnclappoinment.setEnabled(isEditable);
        cmtappoinment.setEnabled(isCompletable);
        clearbtn.setEnabled(true);
    }

    private void setupTableSelectionListener() {
        tblAppointments.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    if (tblAppointments.getSelectedRow() != -1) {
                        displaySelectedAppointmentDetails();
                    }
                    updateButtonStates();
                }
            }
        });
    }

    private void displaySelectedAppointmentDetails() {
        DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();
        int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        String patientId = model.getValueAt(selectedRow, 1).toString();
        String staffId = model.getValueAt(selectedRow, 2).toString();
        Date appointmentDate = (Date) model.getValueAt(selectedRow, 3);
        String status = model.getValueAt(selectedRow, 4).toString();

        txtptid.setText(patientId);
        stfid.setText(staffId);

        LocalDate localAppointmentDate = appointmentDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        datePicker1.setSelectedDate(localAppointmentDate);

        currntstatus.setText(status);

        if (appointmentDate != null) {

            ZoneId defaultZoneId = ZoneId.systemDefault();
            Instant instant = appointmentDate.toInstant();
            LocalTime appointmentTime = instant.atZone(defaultZoneId).toLocalTime();
            timePicker1.setSelectedTime(appointmentTime);

        }

        try {
            Appointment selectedApp = appointmentScheduler.getAppointmentById((Integer) model.getValueAt(selectedRow, 0));
            if (selectedApp != null) {
                selectDropDownItem(cmbFacility, selectedApp.getFacilityId());
                selectDropDownItem(cmbAppointmentType, selectedApp.getAppointmentTypeId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        findAndSetPatient();
        findAndSetStaff();
    }

    private <T> void selectDropDownItem(javax.swing.JComboBox<T> comboBox, int idToSelect) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            T item = comboBox.getItemAt(i);
            if (item instanceof Facility && ((Facility) item).getFacilityId() == idToSelect) {
                comboBox.setSelectedIndex(i);
                return;
            }
            if (item instanceof AppointmentType && ((AppointmentType) item).getTypeId() == idToSelect) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void findAndSetPatient() {
        String searchTerm = txtptid.getText();
        if (searchTerm.isEmpty()) {
            return;
        }

        try {
            List<PatientRecord> results = patientDataService.searchPatients(searchTerm);
            if (!results.isEmpty()) {
                PatientRecord patient = results.get(0);
                txtptid.setText(patient.getPatientId());
                patientname.setText(patient.getFName() + " " + patient.getLName());
            } else {
                patientname.setText("Patient not found");
                Notifications.getInstance().show(Notifications.Type.WARNING, "No patient found with the provided details");
            }
        } catch (SQLException | SecurityException e) {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Error searching for patient");
        }
    }

    private void findAndSetStaff() {
        String searchTerm = stfid.getText();
        if (searchTerm.isEmpty()) {
            return;
        }

        try {
            Staff staff = staffDataService.findStaffMember(searchTerm);
            if (staff != null) {
                stfid.setText(staff.getStaffId());
                staffname.setText(staff.getFName() + " " + staff.getLName());
            } else {
                staffname.setText("Staff not found");
                Notifications.getInstance().show(Notifications.Type.WARNING, "No staff member found with the provided details.");
            }
        } catch (SQLException e) {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Error searching for staff");
        }
    }

    private void loadAppointmentData() {
        try {
            DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();
            model.setRowCount(0);
            List<Appointment> appointments = appointmentScheduler.getAppointments(currentUser);

            for (Appointment app : appointments) {
                Object[] row = new Object[]{
                    app.getAppointmentId(),
                    app.getPatientId(),
                    app.getStaffId(),
                    app.getAppointmentDate(),
                    app.getStatus()
                };
                model.addRow(row);
            }

        } catch (SQLException e) {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Error loading appointment data");
            e.printStackTrace();
        }
    }

    private void clearAppointmentForm() {
        txtptid.setText("");
        stfid.setText("");
        patientname.setText("Patient Name :");
        staffname.setText("Staff Name :");

        currntstatus.setText("............");

        cmbFacility.setSelectedIndex(0);
        cmbAppointmentType.setSelectedIndex(0);

        tblAppointments.clearSelection();
        timePicker1.now();
        datePicker1.now();

        updateButtonStates();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        timePicker1 = new raven.datetime.TimePicker();
        datePicker1 = new raven.datetime.DatePicker();
        Formtitle = new javax.swing.JLabel();
        apttblscroll = new javax.swing.JScrollPane();
        tblAppointments = new javax.swing.JTable();
        btnpnl = new javax.swing.JPanel();
        svappointment = new javax.swing.JButton();
        cnclappoinment = new javax.swing.JButton();
        cmtappoinment = new javax.swing.JButton();
        clearbtn = new javax.swing.JButton();
        schappoinmentpanel = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        patientlbl = new javax.swing.JLabel();
        txtptid = new javax.swing.JTextField();
        patientname = new javax.swing.JLabel();
        staffname = new javax.swing.JLabel();
        stfid = new javax.swing.JTextField();
        stafflbl = new javax.swing.JLabel();
        apponmentlbl = new javax.swing.JLabel();
        statuslbl = new javax.swing.JLabel();
        currntstatus = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cmbAppointmentType = new javax.swing.JComboBox<>();
        cmbFacility = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        timeselector = new javax.swing.JFormattedTextField();
        Dateselecter = new javax.swing.JFormattedTextField();

        Formtitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        Formtitle.setText("Upcoming Appointments");

        tblAppointments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Appointment ID", "Patient ID", "Staff ID", "Appointment Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        apttblscroll.setViewportView(tblAppointments);

        svappointment.setText("Save Appointment");
        svappointment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                svappointmentActionPerformed(evt);
            }
        });

        cnclappoinment.setText("Cancel Appoinment");
        cnclappoinment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cnclappoinmentActionPerformed(evt);
            }
        });

        cmtappoinment.setText("Complete Appoinment");
        cmtappoinment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmtappoinmentActionPerformed(evt);
            }
        });

        clearbtn.setText("Clear");
        clearbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout btnpnlLayout = new javax.swing.GroupLayout(btnpnl);
        btnpnl.setLayout(btnpnlLayout);
        btnpnlLayout.setHorizontalGroup(
            btnpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnpnlLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(btnpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmtappoinment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clearbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(svappointment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cnclappoinment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );
        btnpnlLayout.setVerticalGroup(
            btnpnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnpnlLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(svappointment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(cnclappoinment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(cmtappoinment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(22, 22, 22)
                .addComponent(clearbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        title.setText("Schedule or View Appointment");

        patientlbl.setText("Patient ID:");

        txtptid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtptidKeyReleased(evt);
            }
        });

        patientname.setText("Patient Name :");

        staffname.setText("Staff Name :");

        stfid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                stfidKeyReleased(evt);
            }
        });

        stafflbl.setText("Staff ID:");

        apponmentlbl.setText("Appointment Date:");

        statuslbl.setText("Status:");

        currntstatus.setText("............");

        jLabel1.setText("Type :");

        jLabel2.setText("Facility:");

        javax.swing.GroupLayout schappoinmentpanelLayout = new javax.swing.GroupLayout(schappoinmentpanel);
        schappoinmentpanel.setLayout(schappoinmentpanelLayout);
        schappoinmentpanelLayout.setHorizontalGroup(
            schappoinmentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(schappoinmentpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(schappoinmentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(schappoinmentpanelLayout.createSequentialGroup()
                        .addComponent(apponmentlbl)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Dateselecter, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(timeselector, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(statuslbl)
                        .addGap(18, 18, 18)
                        .addComponent(currntstatus)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(schappoinmentpanelLayout.createSequentialGroup()
                        .addGroup(schappoinmentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, schappoinmentpanelLayout.createSequentialGroup()
                                .addGroup(schappoinmentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, schappoinmentpanelLayout.createSequentialGroup()
                                        .addComponent(stafflbl)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(stfid, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, schappoinmentpanelLayout.createSequentialGroup()
                                        .addComponent(patientlbl)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtptid, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(schappoinmentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(patientname)
                                    .addComponent(staffname))
                                .addGap(18, 18, 18)
                                .addGroup(schappoinmentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1)))
                            .addGroup(schappoinmentpanelLayout.createSequentialGroup()
                                .addComponent(title)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(9, 9, 9)
                        .addGroup(schappoinmentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbAppointmentType, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbFacility, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(153, 153, 153))))
        );
        schappoinmentpanelLayout.setVerticalGroup(
            schappoinmentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(schappoinmentpanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(title)
                .addGap(18, 18, 18)
                .addGroup(schappoinmentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(patientlbl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, schappoinmentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtptid)
                        .addComponent(patientname)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbAppointmentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addGroup(schappoinmentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stafflbl)
                    .addComponent(stfid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(staffname)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbFacility, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(schappoinmentpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(apponmentlbl, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Dateselecter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timeselector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statuslbl)
                    .addComponent(currntstatus))
                .addGap(70, 70, 70))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(apttblscroll))
                    .addComponent(Formtitle))
                .addGap(17, 17, 17))
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(schappoinmentpanel, javax.swing.GroupLayout.PREFERRED_SIZE, 639, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnpnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(Formtitle)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(apttblscroll, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(schappoinmentpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnpnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtptidKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtptidKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            findAndSetPatient();
        }
    }//GEN-LAST:event_txtptidKeyReleased

    private void stfidKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_stfidKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            findAndSetStaff();
        }
    }//GEN-LAST:event_stfidKeyReleased

    private void svappointmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_svappointmentActionPerformed

        if (txtptid.getText().trim().isEmpty() || stfid.getText().trim().isEmpty() || datePicker1.getSelectedDate() == null) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Patient ID, Staff ID, and Date are required.");
            return;
        }

        if (cmbFacility.getSelectedIndex() == 0 || cmbAppointmentType.getSelectedIndex() == 0) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select a valid facility and appointment type.");
            return;
        }

        try {
            String patientId = txtptid.getText().trim();
            String staffId = stfid.getText().trim();

            Facility selectedFacility = (Facility) cmbFacility.getSelectedItem();
            AppointmentType selectedType = (AppointmentType) cmbAppointmentType.getSelectedItem();
            int duration = selectedType.getDurationMinutes();

            String selectedDate = datePicker1.getSelectedDateAsString();
            String selectedTime = timePicker1.getSelectedTimeAsString();

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date finalAppointmentDate = inputFormat.parse(selectedDate + " " + selectedTime);

            if (finalAppointmentDate.before(new Date())) {
                Notifications.getInstance().show(Notifications.Type.WARNING, "Cannot schedule an appointment for a past date or time.");
                return;
            }

            java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(finalAppointmentDate.getTime());

            if (!appointmentScheduler.isTimeSlotAvailable(staffId, sqlTimestamp, duration)) {
                Notifications.getInstance().show(Notifications.Type.WARNING, "This time slot conflicts with an existing appointment.");
                return;
            }

            Appointment newAppointment = new Appointment(
                    0,
                    patientId,
                    staffId,
                    finalAppointmentDate,
                    "Scheduled",
                    selectedFacility.getFacilityId(),
                    selectedType.getTypeId()
            );

            IAppointmentCommand command = new BookAppointmentCommand(appointmentScheduler, newAppointment);
            command.execute();

            loadAppointmentData();
            clearAppointmentForm();

            Notifications.getInstance().show(Notifications.Type.SUCCESS, "Data Saved Success");
            MessageAlerts.getInstance().showMessage("Success", "Appointment saved successfully!", MessageAlerts.MessageType.SUCCESS);

        } catch (ParseException e) {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Invalid date or time format");
        } catch (SQLException e) {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Database error");
        } catch (Exception e) {
            e.printStackTrace();
            Notifications.getInstance().show(Notifications.Type.ERROR, "An unexpected error occurred" + e.getMessage());
        }

    }//GEN-LAST:event_svappointmentActionPerformed

    private void cnclappoinmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cnclappoinmentActionPerformed

        int selectedRow = tblAppointments.getSelectedRow();

        if (selectedRow == -1) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select an appointment from the table to cancel.");
            return;
        }

        String currentStatus = tblAppointments.getValueAt(selectedRow, 4).toString();

        if (!currentStatus.equals("Scheduled")) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "This appointment Already Completed at this time.");
            return;
        }

        int appointmentId = (Integer) tblAppointments.getValueAt(selectedRow, 0);

        MessageAlerts.getInstance().showMessage("Confirm Cancellation", "Are you sure you want to cancel appointment #" + appointmentId + "?", MessageAlerts.MessageType.DEFAULT, MessageAlerts.YES_NO_OPTION, new PopupCallbackAction() {
            @Override
            public void action(PopupController pc, int i) {
                if (i == MessageAlerts.YES_OPTION) {
                    try {

                        IAppointmentCommand command = new CancelAppointmentCommand(appointmentScheduler, appointmentId);
                        command.execute();

                        Notifications.getInstance().show(Notifications.Type.SUCCESS, "Appointment #" + appointmentId + " has been cancelled.");

                        loadAppointmentData();
                        clearAppointmentForm();

                    } catch (SQLException e) {
                        Notifications.getInstance().show(Notifications.Type.ERROR, "Error cancelling appointment");
                    }
                }
            }
        });
    }//GEN-LAST:event_cnclappoinmentActionPerformed

    private void clearbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearbtnActionPerformed
        clearAppointmentForm();
    }//GEN-LAST:event_clearbtnActionPerformed

    private void cmtappoinmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmtappoinmentActionPerformed

        int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "Please select an appointment to complete.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblAppointments.getModel();
        int appointmentId = (Integer) model.getValueAt(selectedRow, 0);
        String patientId = model.getValueAt(selectedRow, 1).toString();
        Date appointmentDate = (Date) model.getValueAt(selectedRow, 3);
        String currentStatus = model.getValueAt(selectedRow, 4).toString();

        if (!currentStatus.equals("Scheduled") || appointmentDate.after(new Date())) {
            Notifications.getInstance().show(Notifications.Type.WARNING, "This appointment cannot be completed at this time");
            return;
        }

        MessageAlerts.getInstance().showMessage("Confirm Completion", "This will complete the appointment and log medical records. Continue?", MessageAlerts.MessageType.DEFAULT, MessageAlerts.YES_NO_OPTION, new PopupCallbackAction() {
            @Override
            public void action(PopupController pc, int i) {
                if (i == MessageAlerts.YES_OPTION) {
                    try {

                        String diagnosis = JOptionPane.showInputDialog(FormAppointments.this, "Enter Diagnosis:", "Step 1 of 3: Medical History", JOptionPane.PLAIN_MESSAGE);
                        if (diagnosis == null || diagnosis.trim().isEmpty()) {
                            Notifications.getInstance().show(Notifications.Type.WARNING, "Completion cancelled. Diagnosis is required.");
                            return;
                        }

                        TreatmentPlanDialog planDialog = new TreatmentPlanDialog((Frame) SwingUtilities.getWindowAncestor(FormAppointments.this));
                        TreatmentPlanEntry newPlan = planDialog.showDialog();

                        if (newPlan == null) { // User clicked Cancel or closed the dialog
                            JOptionPane.showMessageDialog(FormAppointments.this, "Completion cancelled. Treatment plan is required.", "Cancelled", JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        String notes = JOptionPane.showInputDialog(FormAppointments.this, "Enter Visit Notes:", "Step 3 of 3: Visit Notes", JOptionPane.PLAIN_MESSAGE);
                        if (notes == null || notes.trim().isEmpty()) {
                            Notifications.getInstance().show(Notifications.Type.WARNING, "Completion cancelled. Visit notes are required.");
                            return;
                        }

                        Date now = new Date();

                        MedicalHistoryEntry newHistory = new MedicalHistoryEntry(diagnosis, now);
                        patientDataService.addMedicalHistory(patientId, newHistory);

                        patientDataService.addTreatmentPlan(patientId, newPlan);

                        Visit newVisit = new Visit(now, notes);
                        patientDataService.addVisit(patientId, newVisit);

                        BigDecimal consultationFee = new BigDecimal("150.00");
                        Invoice createdInvoice = billingService.createInvoice(patientId, consultationFee);
                        if (createdInvoice == null) {
                            Notifications.getInstance().show(Notifications.Type.WARNING, "Medical records saved, but failed to create an invoice.");
                            return;
                        }

                        IAppointmentCommand command = new UpdateAppointmentStatusCommand(appointmentScheduler, appointmentId, "Completed");
                        command.execute();

                        MessageAlerts.getInstance().showMessage("Success", "Appointment completed and all medical records have been saved.", MessageAlerts.MessageType.SUCCESS);
                        loadAppointmentData();
                        clearAppointmentForm();

                    } catch (SQLException | SecurityException e) {
                        Notifications.getInstance().show(Notifications.Type.ERROR, "An error occurred");
                    }
                }
            }
        });


    }//GEN-LAST:event_cmtappoinmentActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField Dateselecter;
    private javax.swing.JLabel Formtitle;
    private javax.swing.JLabel apponmentlbl;
    private javax.swing.JScrollPane apttblscroll;
    private javax.swing.JPanel btnpnl;
    private javax.swing.JButton clearbtn;
    private javax.swing.JComboBox<com.globemed.model.AppointmentType> cmbAppointmentType;
    private javax.swing.JComboBox<com.globemed.model.Facility> cmbFacility;
    private javax.swing.JButton cmtappoinment;
    private javax.swing.JButton cnclappoinment;
    private javax.swing.JLabel currntstatus;
    private raven.datetime.DatePicker datePicker1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel patientlbl;
    private javax.swing.JLabel patientname;
    private javax.swing.JPanel schappoinmentpanel;
    private javax.swing.JLabel stafflbl;
    private javax.swing.JLabel staffname;
    private javax.swing.JLabel statuslbl;
    private javax.swing.JTextField stfid;
    private javax.swing.JButton svappointment;
    private javax.swing.JTable tblAppointments;
    private raven.datetime.TimePicker timePicker1;
    private javax.swing.JFormattedTextField timeselector;
    private javax.swing.JLabel title;
    private javax.swing.JTextField txtptid;
    // End of variables declaration//GEN-END:variables
}
