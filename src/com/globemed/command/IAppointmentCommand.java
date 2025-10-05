package com.globemed.command;

import java.sql.SQLException;

public interface IAppointmentCommand {
    void execute() throws SQLException;
}