package com.globemed.command;

import com.globemed.model.Appointment;
import com.globemed.service.AppointmentScheduler;
import java.sql.SQLException;

public class BookAppointmentCommand implements IAppointmentCommand {
    private final AppointmentScheduler scheduler;
    private final Appointment appointment;

    public BookAppointmentCommand(AppointmentScheduler scheduler, Appointment appointment) {
        this.scheduler = scheduler;
        this.appointment = appointment;
    }

    @Override
    public void execute() throws SQLException {
        scheduler.bookAppointment(appointment);
        System.out.println("Appointment booked successfully.");
    }
}