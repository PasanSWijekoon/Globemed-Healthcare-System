package com.globemed.reports;

import com.globemed.model.PatientRecord;
import com.globemed.model.Invoice;
import com.globemed.model.Visit;

public interface IReportVisitor {
    String visit(PatientRecord patientRecord);
    String visit(Invoice invoice);
    String visit(Visit visit);
}