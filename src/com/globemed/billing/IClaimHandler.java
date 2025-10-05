package com.globemed.billing;


import java.sql.SQLException;

public interface IClaimHandler {
    void setNextHandler(IClaimHandler nextHandler);
    void handleRequest(ClaimRequest request) throws SQLException;
}