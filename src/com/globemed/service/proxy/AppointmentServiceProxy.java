
package com.globemed.service.proxy;

import com.globemed.model.Appointment;
import com.globemed.model.AppointmentType;
import com.globemed.model.Facility;
import com.globemed.model.Role;
import com.globemed.model.User;
import com.globemed.service.IAppointmentService;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;




public class AppointmentServiceProxy implements IAppointmentService {

    private final IAppointmentService realService;
    private final User currentUser;

    public AppointmentServiceProxy(IAppointmentService realService, User currentUser) {
        this.realService = realService;
        this.currentUser = currentUser;
    }

    private boolean canManageAppointments() {
        return currentUser.getRole() == Role.Admin ||
               currentUser.getRole() == Role.Doctor ||
               currentUser.getRole() == Role.Coordinator;
    }

    private boolean canViewAppointments() {
        return canManageAppointments() || currentUser.getRole() == Role.Nurse;
    }


    @Override
    public void bookAppointment(Appointment appointment) throws SQLException {
        if (!canManageAppointments()) {
            throw new SecurityException("Access Denied: User " + currentUser.getUsername() + " cannot book appointments.");
        }
        realService.bookAppointment(appointment);
    }

    @Override
    public void updateAppointmentStatus(int appointmentId, String newStatus) throws SQLException {
        if (!canManageAppointments()) {
            throw new SecurityException("Access Denied: User " + currentUser.getUsername() + " cannot update appointment status.");
        }
        realService.updateAppointmentStatus(appointmentId, newStatus);
    }

    @Override
    public void cancelAppointment(int appointmentId) throws SQLException {
        if (!canManageAppointments()) {
            throw new SecurityException("Access Denied: User " + currentUser.getUsername() + " cannot cancel appointments.");
        }
        realService.cancelAppointment(appointmentId);
    }

    @Override
    public List<Appointment> getAppointments(User user) throws SQLException {
        if (!canViewAppointments()) {
            throw new SecurityException("Access Denied: User " + currentUser.getUsername() + " cannot view appointments.");
        }
        return realService.getAppointments(user);
    }

    @Override
    public boolean isTimeSlotAvailable(String staffId, Date appointmentDate) throws SQLException {
        if (!canManageAppointments()) {
            throw new SecurityException("Access Denied: User " + currentUser.getUsername() + " cannot check time slot availability.");
        }
        return realService.isTimeSlotAvailable(staffId, appointmentDate);
    }

    @Override
    public List<Appointment> getAppointmentsForDate(Date date) throws SQLException {
        if (!canViewAppointments()) {
            throw new SecurityException("Access Denied: User " + currentUser.getUsername() + " cannot view appointments for a specific date.");
        }
        return realService.getAppointmentsForDate(date);
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

