package com.globemed.command;

import com.globemed.service.AppointmentScheduler;
import java.sql.SQLException;

public class CancelAppointmentCommand implements IAppointmentCommand {
    private final AppointmentScheduler scheduler;
    private final int appointmentId;

    public CancelAppointmentCommand(AppointmentScheduler scheduler, int appointmentId) {
        this.scheduler = scheduler;
        this.appointmentId = appointmentId;
    }

    @Override
    public void execute() throws SQLException {
        scheduler.cancelAppointment(appointmentId);
        System.out.println("Appointment cancelled successfully.");
    }
}