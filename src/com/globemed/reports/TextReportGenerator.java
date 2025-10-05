package com.globemed.reports;

import com.globemed.model.Invoice;
import com.globemed.model.MedicalHistoryEntry;
import com.globemed.model.PatientRecord;
import com.globemed.model.TreatmentPlanEntry;
import com.globemed.model.Visit;
import java.text.SimpleDateFormat;

public class TextReportGenerator implements IReportVisitor {

    @Override
    public String visit(PatientRecord patientRecord) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


        sb.append("--- Patient Treatment Summary ---\n\n");
        sb.append("Patient ID: ").append(patientRecord.getPatientId()).append("\n");
        sb.append("Name:       ").append(patientRecord.getFName()).append(" ").append(patientRecord.getLName()).append("\n");
        sb.append("Contact:    ").append(patientRecord.getContactNumber()).append("\n\n");

  
        sb.append("========================================\n");
        sb.append("          MEDICAL HISTORY\n");
        sb.append("========================================\n");
        if (patientRecord.getMedicalHistory() == null || patientRecord.getMedicalHistory().isEmpty()) {
            sb.append("No medical history on record.\n");
        } else {
            for (MedicalHistoryEntry entry : patientRecord.getMedicalHistory()) {
                sb.append("Date:    ").append(dateFormat.format(entry.getDateOfEntry())).append("\n");
                sb.append("Diagnosis: ").append(entry.getDiagnosis()).append("\n");
                sb.append("----------------------------------------\n");
            }
        }

 
        sb.append("\n========================================\n");
        sb.append("          TREATMENT PLANS\n");
        sb.append("========================================\n");
        if (patientRecord.getTreatmentPlans() == null || patientRecord.getTreatmentPlans().isEmpty()) {
            sb.append("No treatment plans on record.\n");
        } else {
            for (TreatmentPlanEntry plan : patientRecord.getTreatmentPlans()) {
                sb.append("Start Date: ").append(dateFormat.format(plan.getStartDate())).append("\n");
                sb.append("End Date:   ").append(dateFormat.format(plan.getEndDate())).append("\n");
                sb.append("Details:    ").append(plan.getPlanDetails()).append("\n");
                sb.append("----------------------------------------\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String visit(Invoice invoice) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();
        sb.append("--- Financial Report for Invoice ---\n\n");
        sb.append("Invoice ID: ").append(invoice.getInvoiceId()).append("\n");
        sb.append("Patient ID: ").append(invoice.getPatientId()).append("\n");
        sb.append("Service Date: ").append(dateFormat.format(invoice.getServiceDate())).append("\n");
        sb.append("Amount: $").append(invoice.getAmount()).append("\n");
        sb.append("Status: ").append(invoice.getStatus()).append("\n");

        return sb.toString();
    }

    @Override
    public String visit(Visit visit) {
        
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("--- Visit Details ---\n\n");
        sb.append("Visit Date: ").append(dateTimeFormat.format(visit.getVisitDate())).append("\n");
        sb.append("Notes:\n").append(visit.getNotes()).append("\n");

        return sb.toString();
    }
}
