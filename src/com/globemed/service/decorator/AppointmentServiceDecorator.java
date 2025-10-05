
package com.globemed.service.decorator;

import com.globemed.model.Appointment;
import com.globemed.model.User;
import com.globemed.service.IAppointmentService;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;


public abstract class AppointmentServiceDecorator implements IAppointmentServiceDecorator {

    protected final IAppointmentService wrappedService;

    public AppointmentServiceDecorator(IAppointmentService wrappedService) {
        this.wrappedService = wrappedService;
    }

    @Override
    public void bookAppointment(Appointment appointment) throws SQLException {
        wrappedService.bookAppointment(appointment);
    }

    @Override
    public void updateAppointmentStatus(int appointmentId, String newStatus) throws SQLException {
        wrappedService.updateAppointmentStatus(appointmentId, newStatus);
    }

    @Override
    public void cancelAppointment(int appointmentId) throws SQLException {
        wrappedService.cancelAppointment(appointmentId);
    }

    @Override
    public List<Appointment> getAppointments(User user) throws SQLException {
        return wrappedService.getAppointments(user);
    }

    @Override
    public boolean isTimeSlotAvailable(String staffId, Date appointmentDate) throws SQLException {
        return wrappedService.isTimeSlotAvailable(staffId, appointmentDate);
    }

    @Override
    public List<Appointment> getAppointmentsForDate(Date date) throws SQLException {
        return wrappedService.getAppointmentsForDate(date);
    }
}