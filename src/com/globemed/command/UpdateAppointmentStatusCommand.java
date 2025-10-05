package com.globemed.command;

import com.globemed.service.AppointmentScheduler;
import java.sql.SQLException;

public class UpdateAppointmentStatusCommand implements IAppointmentCommand {
    private final AppointmentScheduler scheduler;
    private final int appointmentId;
    private final String newStatus;

    public UpdateAppointmentStatusCommand(AppointmentScheduler scheduler, int appointmentId, String newStatus) {
        this.scheduler = scheduler;
        this.appointmentId = appointmentId;
        this.newStatus = newStatus;
    }

    @Override
    public void execute() throws SQLException {
        scheduler.updateAppointmentStatus(appointmentId, newStatus);
        System.out.println("Appointment #" + appointmentId + " status updated to " + newStatus);
    }
}