package com.globemed.billing.state;

import com.globemed.model.InsuranceClaim;
import java.sql.SQLException;

public interface IClaimState {
    void approve(InsuranceClaim claim) throws SQLException;
    void deny(InsuranceClaim claim) throws SQLException;
    void processPayout(InsuranceClaim claim) throws SQLException;
}