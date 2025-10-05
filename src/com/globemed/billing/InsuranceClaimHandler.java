package com.globemed.billing;

import java.sql.SQLException;

public abstract class InsuranceClaimHandler implements IClaimHandler {
    protected IClaimHandler nextHandler;

    @Override
    public void setNextHandler(IClaimHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handleRequest(ClaimRequest request) throws SQLException {
        if (canHandle(request)) {
            processClaim(request);
        } else if (nextHandler != null) {
            nextHandler.handleRequest(request);
        }
    }

    protected abstract boolean canHandle(ClaimRequest request);
    protected abstract void processClaim(ClaimRequest request) throws SQLException;
}