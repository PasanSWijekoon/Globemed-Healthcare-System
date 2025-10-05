/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.service.decorator;

import com.globemed.model.Appointment;
import com.globemed.model.AppointmentType;
import com.globemed.model.Facility;
import com.globemed.model.User;
import com.globemed.service.IAppointmentService;
import com.globemed.service.SecurityService;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class AppointmentLoggingDecorator extends AppointmentServiceDecorator {

    private final SecurityService securityService;
    private final User currentUser;

    public AppointmentLoggingDecorator(IAppointmentService wrappedService, User currentUser) {
        super(wrappedService);
        this.securityService = new SecurityService();
        this.currentUser = currentUser;
    }

    @Override
    public void bookAppointment(Appointment appointment) throws SQLException {
        String action = String.format("Attempt to book appointment for patient '%s' with staff '%s' on %s.",
                appointment.getPatientId(),
                appointment.getStaffId(),
                appointment.getAppointmentDate().toString());
        securityService.logAction(currentUser.getUsername(), action);
        super.bookAppointment(appointment);
    }

    @Override
    public void updateAppointmentStatus(int appointmentId, String newStatus) throws SQLException {
        String action = String.format("Attempt to update appointment ID '%d' to status '%s'.",
                appointmentId, newStatus);
        securityService.logAction(currentUser.getUsername(), action);
        super.updateAppointmentStatus(appointmentId, newStatus);
    }

    @Override
    public void cancelAppointment(int appointmentId) throws SQLException {
        String action = String.format("Attempt to cancel appointment ID '%d'.", appointmentId);
        securityService.logAction(currentUser.getUsername(), action);
        super.cancelAppointment(appointmentId);
    }

    @Override
    public List<Appointment> getAppointments(User user) throws SQLException {
        String action = String.format("Attempt to retrieve appointments for user '%s'.", user.getUsername());
        securityService.logAction(currentUser.getUsername(), action);
        return super.getAppointments(user);
    }

    @Override
    public List<Appointment> getAppointmentsForDate(Date date) throws SQLException {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String action = String.format("Attempt to retrieve appointments for date '%s'.", dateStr);
        securityService.logAction(currentUser.getUsername(), action);
        return super.getAppointmentsForDate(date);
    }

    @Override
    public Appointment getAppointmentById(int appointmentId) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean isTimeSlotAvailable(String staffId, Timestamp newAppointmentStart, int newAppointmentDuration) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public List<Facility> getAllFacilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<AppointmentType> getAllAppointmentTypes() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}