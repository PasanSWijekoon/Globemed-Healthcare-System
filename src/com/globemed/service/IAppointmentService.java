package com.globemed.service;



import com.globemed.model.Appointment;
import com.globemed.model.AppointmentType;
import com.globemed.model.Facility;
import com.globemed.model.User;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


public interface IAppointmentService {

    void bookAppointment(Appointment appointment) throws SQLException;

    void updateAppointmentStatus(int appointmentId, String newStatus) throws SQLException;

    void cancelAppointment(int appointmentId) throws SQLException;

    List<Appointment> getAppointments(User user) throws SQLException;

    Appointment getAppointmentById(int appointmentId) throws SQLException;

    boolean isTimeSlotAvailable(String staffId, Timestamp newAppointmentStart, int newAppointmentDuration) throws SQLException;

    boolean isTimeSlotAvailable(String staffId, Date appointmentDate) throws SQLException;

    List<Appointment> getAppointmentsForDate(Date date) throws SQLException;

    List<Facility> getAllFacilities();

    List<AppointmentType> getAllAppointmentTypes();
}