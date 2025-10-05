package com.globemed.application.form.other;

import com.formdev.flatlaf.FlatClientProperties;
import com.globemed.model.Role;
import com.globemed.service.RoleBasedDashboardService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;
import raven.chart.bar.HorizontalBarChart;
import raven.chart.line.LineChart;
import raven.chart.pie.PieChart;

public class FormDashboard extends JPanel {
    
    private RoleBasedDashboardService dashboardService;
    private Role currentUserRole;
    private String currentUserId;
    private Set<String> allowedComponents;
    
    // Chart components
    private PieChart pieChartAppointmentStatus;
    private PieChart pieChartGenderDistribution;
    private PieChart pieChartStaffRoles;
    private PieChart pieChartInvoiceStatus;
    private PieChart pieChartTopDiagnoses;
    private PieChart pieChartInsuranceClaims;
    
    
    // Statistics panels
    private JPanel statisticsPanel;
    private JPanel chartsContainer;

    public FormDashboard(Role userRole, String userId) {
        this.currentUserRole = userRole;
        this.currentUserId = userId;
        this.dashboardService = new RoleBasedDashboardService(userRole, userId);
        this.allowedComponents = dashboardService.getAllowedDashboardComponents();
        
        init();
        loadDashboardData();
    }

    private void init() {
        setLayout(new MigLayout("wrap,fill,gap 10", "fill"));
        
        // Add role-specific welcome message
        createWelcomePanel();
        
        // Create statistics panel
        createStatisticsPanel();
        add(statisticsPanel, "height 200!, wrap");
        
        // Create charts container
        chartsContainer = new JPanel(new MigLayout("wrap,fill,gap 10", "fill"));
        createPieChartsPanel();
        add(chartsContainer);
    }
    
    private void createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.putClientProperty(FlatClientProperties.STYLE, 
            "background:$Component.accentColor;border:5,5,5,5,$Component.borderColor,,10");
        
        String roleMessage = getRoleSpecificMessage();
        JLabel welcomeLabel = new JLabel(roleMessage);
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 16f));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        add(welcomePanel, "height 50!, wrap");
    }
    
    private String getRoleSpecificMessage() {
        switch (currentUserRole) {
            case Admin:
                return "Administrator Dashboard";
            case Doctor:
                return "Doctor Dashboard";
            case Nurse:
                return "Nurse Dashboard";
            case Pharmacist:
                return "Pharmacist Dashboard";
            case Coordinator:
                return "Coordinator Dashboard";
            default:
                return "Healthcare Dashboard";
        }
    }
    
    private void createStatisticsPanel() {
        // Calculate grid size based on allowed components
        int allowedStatsCount = countAllowedStatistics();
        int cols = Math.min(4, allowedStatsCount);
        int rows = (int) Math.ceil((double) allowedStatsCount / cols);
        
        if (allowedStatsCount == 0) {
            statisticsPanel = new JPanel();
            statisticsPanel.putClientProperty(FlatClientProperties.STYLE, 
                "background:$Panel.background");
            return;
        }
        
        statisticsPanel = new JPanel(new GridLayout(rows, cols, 15, 15));
        statisticsPanel.putClientProperty(FlatClientProperties.STYLE, 
            "background:$Panel.background;border:5,5,5,5,$Component.borderColor,,10");
    }
    
    private int countAllowedStatistics() {
        int count = 0;
        String[] stats = {"TOTAL_PATIENTS", "TOTAL_STAFF", "TODAYS_APPOINTMENTS", 
                         "PENDING_INVOICES", "TOTAL_REVENUE", "MONTHLY_REVENUE", 
                         "NEW_PATIENTS", "ACTIVE_PATIENTS"};
        
        for (String stat : stats) {
            if (allowedComponents.contains(stat)) count++;
        }
        
        // Add role-specific stats
        if (currentUserRole == Role.Doctor || currentUserRole == Role.Nurse) {
            count += 2; // My Appointments, My Patients/Completed
        }
        
        return count;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.putClientProperty(FlatClientProperties.STYLE, 
            "background:$Component.background;border:8,8,8,8," + 
            String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()) + 
            ",,10");
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(12f));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "foreground:$Label.disabledForeground");
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 24f));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void createPieChartsPanel() {
        int chartCount = 0;
        String chartLayout = "";
        
        // First row of pie charts
        if (allowedComponents.contains("APPOINTMENT_STATUS_CHART")) {
            createPieChart1();
            chartsContainer.add(pieChartAppointmentStatus, "split " + getChartsPerRow() + ",height 290");
            chartCount++;
        }
        
        if (allowedComponents.contains("GENDER_DISTRIBUTION_CHART")) {
            createPieChart2();
            if (chartCount == 0) {
                chartsContainer.add(pieChartGenderDistribution, "split " + getChartsPerRow() + ",height 290");
            } else {
                chartsContainer.add(pieChartGenderDistribution, "height 290");
            }
            chartCount++;
        }
        
        if (allowedComponents.contains("STAFF_ROLES_CHART")) {
            createPieChart3();
            if (chartCount == 0) {
                chartsContainer.add(pieChartStaffRoles, "split " + getChartsPerRow() + ",height 290");
            } else if (chartCount < getChartsPerRow()) {
                chartsContainer.add(pieChartStaffRoles, "height 290");
            } else {
                chartsContainer.add(pieChartStaffRoles, "height 290,wrap");
            }
            chartCount++;
        }
        
        // Add wrap if we have charts in first row but no staff roles chart
        if (chartCount > 0 && chartCount < 3 && !allowedComponents.contains("STAFF_ROLES_CHART")) {
            // Add invisible spacer or handle wrapping
            if (chartCount == 2) {
                chartsContainer.add(new JPanel(), "height 290,wrap");
            } else if (chartCount == 1) {
                chartsContainer.add(new JPanel(), "height 290");
                chartsContainer.add(new JPanel(), "height 290,wrap");
            }
        }
        
        // Second row of pie charts
        chartCount = 0;
        if (allowedComponents.contains("INVOICE_STATUS_CHART")) {
            createPieChart4();
            chartsContainer.add(pieChartInvoiceStatus, "split " + getChartsPerRow() + ",height 290");
            chartCount++;
        }
        
        if (allowedComponents.contains("TOP_DIAGNOSES_CHART")) {
            createPieChart5();
            if (chartCount == 0) {
                chartsContainer.add(pieChartTopDiagnoses, "split " + getChartsPerRow() + ",height 290");
            } else {
                chartsContainer.add(pieChartTopDiagnoses, "height 290");
            }
            chartCount++;
        }
        
        if (allowedComponents.contains("INSURANCE_CLAIMS_CHART")) {
            createPieChart6();
            if (chartCount == 0) {
                chartsContainer.add(pieChartInsuranceClaims, "split " + getChartsPerRow() + ",height 290");
            } else if (chartCount < getChartsPerRow()) {
                chartsContainer.add(pieChartInsuranceClaims, "height 290");
            } else {
                chartsContainer.add(pieChartInsuranceClaims, "height 290,wrap");
            }
        }
    }
    
    private int getChartsPerRow() {
        int allowedCharts = 0;
        String[] charts = {"APPOINTMENT_STATUS_CHART", "GENDER_DISTRIBUTION_CHART", 
                          "STAFF_ROLES_CHART", "INVOICE_STATUS_CHART", 
                          "TOP_DIAGNOSES_CHART", "INSURANCE_CLAIMS_CHART"};
        
        for (String chart : charts) {
            if (allowedComponents.contains(chart)) allowedCharts++;
        }
        
        return Math.min(3, allowedCharts);
    }

    private void createPieChart1() {
        pieChartAppointmentStatus = new PieChart();
        String title = (currentUserRole == Role.Doctor) ? "My Appointment Status" : "Appointment Status";
        JLabel header = new JLabel(title);
        header.putClientProperty(FlatClientProperties.STYLE, "font:+2;foreground:$Component.accentColor");
        pieChartAppointmentStatus.setHeader(header);
        pieChartAppointmentStatus.getChartColor().addColor(
            Color.decode("#10b981"), // Completed - Green
            Color.decode("#f59e0b"), // Scheduled - Amber
            Color.decode("#ef4444")  // Cancelled - Red
        );
        pieChartAppointmentStatus.putClientProperty(FlatClientProperties.STYLE,
                "border:5,5,5,5,$Component.borderColor,,20");
    }

    private void createPieChart2() {
        pieChartGenderDistribution = new PieChart();
        String title = (currentUserRole == Role.Doctor) ? "My Patients Gender" : "Patient Gender Distribution";
        JLabel header = new JLabel(title);
        header.putClientProperty(FlatClientProperties.STYLE, "font:+2;foreground:$Component.accentColor");
        pieChartGenderDistribution.setHeader(header);
        pieChartGenderDistribution.getChartColor().addColor(
            Color.decode("#8b5cf6"), // Purple
            Color.decode("#06b6d4")  // Cyan
        );
        pieChartGenderDistribution.putClientProperty(FlatClientProperties.STYLE,
                "border:5,5,5,5,$Component.borderColor,,20");
    }

    private void createPieChart3() {
        pieChartStaffRoles = new PieChart();
        JLabel header = new JLabel("Staff Role Distribution");
        header.putClientProperty(FlatClientProperties.STYLE, "font:+2;foreground:$Component.accentColor");
        pieChartStaffRoles.setHeader(header);
        pieChartStaffRoles.getChartColor().addColor(
            Color.decode("#f97316"), // Doctor - Orange
            Color.decode("#22d3ee"), // Nurse - Light Blue
            Color.decode("#a3e635"), // Admin - Green
            Color.decode("#c084fc"), // Pharmacist - Purple
            Color.decode("#fb923c")  // Coordinator - Orange
        );
        pieChartStaffRoles.setChartType(PieChart.ChartType.DONUT_CHART);
        pieChartStaffRoles.putClientProperty(FlatClientProperties.STYLE,
                "border:5,5,5,5,$Component.borderColor,,20");
    }
    
    private void createPieChart4() {
        pieChartInvoiceStatus = new PieChart();
        JLabel header = new JLabel("Invoice Status");
        header.putClientProperty(FlatClientProperties.STYLE, "font:+2;foreground:$Component.accentColor");
        pieChartInvoiceStatus.setHeader(header);
        pieChartInvoiceStatus.getChartColor().addColor(
            Color.decode("#10b981"), // Paid - Green
            Color.decode("#f59e0b")  // Pending - Amber
        );
        pieChartInvoiceStatus.putClientProperty(FlatClientProperties.STYLE,
                "border:5,5,5,5,$Component.borderColor,,20");
    }
    
    private void createPieChart5() {
        pieChartTopDiagnoses = new PieChart();
        String title = (currentUserRole == Role.Doctor || currentUserRole == Role.Pharmacist) ? 
                      "My Top Diagnoses" : "Top Medical Diagnoses";
        JLabel header = new JLabel(title);
        header.putClientProperty(FlatClientProperties.STYLE, "font:+2;foreground:$Component.accentColor");
        pieChartTopDiagnoses.setHeader(header);
        pieChartTopDiagnoses.getChartColor().addColor(
            Color.decode("#f87171"), Color.decode("#fb923c"), Color.decode("#fbbf24"), 
            Color.decode("#a3e635"), Color.decode("#34d399"), Color.decode("#22d3ee"), 
            Color.decode("#818cf8"), Color.decode("#c084fc"), Color.decode("#f472b6"), 
            Color.decode("#f97316")
        );
        pieChartTopDiagnoses.putClientProperty(FlatClientProperties.STYLE,
                "border:5,5,5,5,$Component.borderColor,,20");
    }
    
    private void createPieChart6() {
        pieChartInsuranceClaims = new PieChart();
        JLabel header = new JLabel("Insurance Claims Status");
        header.putClientProperty(FlatClientProperties.STYLE, "font:+2;foreground:$Component.accentColor");
        pieChartInsuranceClaims.setHeader(header);
        pieChartInsuranceClaims.getChartColor().addColor(
            Color.decode("#10b981"), // Approved - Green
            Color.decode("#f59e0b"), // Submitted - Amber
            Color.decode("#ef4444")  // Rejected - Red
        );
        pieChartInsuranceClaims.setChartType(PieChart.ChartType.DONUT_CHART);
        pieChartInsuranceClaims.putClientProperty(FlatClientProperties.STYLE,
                "border:5,5,5,5,$Component.borderColor,,20");
    }

    private void loadDashboardData() {
        try {
            loadStatistics();
            
            // Load chart data based on role permissions
            if (allowedComponents.contains("APPOINTMENT_STATUS_CHART")) {
                if (currentUserRole == Role.Doctor) {
                    pieChartAppointmentStatus.setDataset(dashboardService.getMyAppointmentStatusData());
                } else {
                    pieChartAppointmentStatus.setDataset(dashboardService.getAppointmentStatusData());
                }
            }
            
            if (allowedComponents.contains("GENDER_DISTRIBUTION_CHART")) {
                pieChartGenderDistribution.setDataset(dashboardService.getPatientGenderDistribution());
            }
            
            if (allowedComponents.contains("STAFF_ROLES_CHART")) {
                pieChartStaffRoles.setDataset(dashboardService.getStaffRoleDistribution());
            }
            
            if (allowedComponents.contains("INVOICE_STATUS_CHART")) {
                pieChartInvoiceStatus.setDataset(dashboardService.getInvoiceStatusData());
            }
            
            if (allowedComponents.contains("TOP_DIAGNOSES_CHART")) {
                if (currentUserRole == Role.Doctor || currentUserRole == Role.Pharmacist) {
                    pieChartTopDiagnoses.setDataset(dashboardService.getMyTopDiagnosesData());
                } else {
                    pieChartTopDiagnoses.setDataset(dashboardService.getTopDiagnosesData());
                }
            }
            
            if (allowedComponents.contains("INSURANCE_CLAIMS_CHART")) {
                pieChartInsuranceClaims.setDataset(dashboardService.getInsuranceClaimStatusData());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadStatistics() {
        statisticsPanel.removeAll();
        
        NumberFormat currencyFormat = new DecimalFormat("$#,##0.00");
        NumberFormat numberFormat = new DecimalFormat("#,##0");
        
        // Load role-specific statistics
        if (allowedComponents.contains("TOTAL_PATIENTS")) {
            int totalPatients = dashboardService.getTotalPatients();
            statisticsPanel.add(createStatCard("Total Patients", 
                numberFormat.format(totalPatients), Color.decode("#3b82f6")));
        }
        
        if (allowedComponents.contains("TOTAL_STAFF")) {
            int totalStaff = dashboardService.getTotalStaff();
            statisticsPanel.add(createStatCard("Total Staff", 
                numberFormat.format(totalStaff), Color.decode("#10b981")));
        }
        
        if (allowedComponents.contains("TODAYS_APPOINTMENTS")) {
            if (currentUserRole == Role.Doctor) {
                int myAppointments = dashboardService.getMyAppointments();
                statisticsPanel.add(createStatCard("My Appointments Today", 
                    numberFormat.format(myAppointments), Color.decode("#f59e0b")));
            } else {
                int todaysAppointments = dashboardService.getTodaysAppointments();
                statisticsPanel.add(createStatCard("Today's Appointments", 
                    numberFormat.format(todaysAppointments), Color.decode("#f59e0b")));
            }
        }
        
        if (allowedComponents.contains("PENDING_INVOICES")) {
            int pendingInvoices = dashboardService.getPendingInvoices();
            statisticsPanel.add(createStatCard("Pending Invoices", 
                numberFormat.format(pendingInvoices), Color.decode("#ef4444")));
        }
        
        if (allowedComponents.contains("TOTAL_REVENUE")) {
            double totalRevenue = dashboardService.getTotalRevenue();
            statisticsPanel.add(createStatCard("Total Revenue", 
                currencyFormat.format(totalRevenue), Color.decode("#8b5cf6")));
        }
        
        if (allowedComponents.contains("MONTHLY_REVENUE")) {
            double monthlyRevenue = dashboardService.getMonthlyRevenue();
            statisticsPanel.add(createStatCard("Monthly Revenue", 
                currencyFormat.format(monthlyRevenue), Color.decode("#06b6d4")));
        }
        
        if (allowedComponents.contains("NEW_PATIENTS")) {
            int recentPatients = dashboardService.getRecentPatientRegistrations();
            statisticsPanel.add(createStatCard("New Patients (30d)", 
                numberFormat.format(recentPatients), Color.decode("#84cc16")));
        }
        
        if (allowedComponents.contains("ACTIVE_PATIENTS")) {
            if (currentUserRole == Role.Doctor) {
                int myPatients = dashboardService.getMyPatients();
                statisticsPanel.add(createStatCard("My Patients", 
                    numberFormat.format(myPatients), Color.decode("#f97316")));
            } else {
                int activePatients = dashboardService.getActivePatients();
                statisticsPanel.add(createStatCard("Active Patients", 
                    numberFormat.format(activePatients), Color.decode("#f97316")));
            }
        }
        
        // Add role-specific statistics
        if (currentUserRole == Role.Doctor || currentUserRole == Role.Nurse) {
            int completedToday = dashboardService.getMyCompletedAppointmentsToday();
            statisticsPanel.add(createStatCard("Completed Today", 
                numberFormat.format(completedToday), Color.decode("#10b981")));
                
            int pendingToday = dashboardService.getTodaysPendingAppointments();
            statisticsPanel.add(createStatCard("Pending Today", 
                numberFormat.format(pendingToday), Color.decode("#f59e0b")));
        }
        
        statisticsPanel.revalidate();
        statisticsPanel.repaint();
    }
    
    public void refreshDashboard() {
        loadDashboardData();
        repaint();
    }
    
    public void startAnimations() {
        if (allowedComponents.contains("APPOINTMENT_STATUS_CHART") && pieChartAppointmentStatus != null) {
            pieChartAppointmentStatus.startAnimation();
        }
        if (allowedComponents.contains("GENDER_DISTRIBUTION_CHART") && pieChartGenderDistribution != null) {
            pieChartGenderDistribution.startAnimation();
        }
        if (allowedComponents.contains("STAFF_ROLES_CHART") && pieChartStaffRoles != null) {
            pieChartStaffRoles.startAnimation();
        }
        if (allowedComponents.contains("INVOICE_STATUS_CHART") && pieChartInvoiceStatus != null) {
            pieChartInvoiceStatus.startAnimation();
        }
        if (allowedComponents.contains("TOP_DIAGNOSES_CHART") && pieChartTopDiagnoses != null) {
            pieChartTopDiagnoses.startAnimation();
        }
        if (allowedComponents.contains("INSURANCE_CLAIMS_CHART") && pieChartInsuranceClaims != null) {
            pieChartInsuranceClaims.startAnimation();
        }
    }
    
    // Method to update user role and refresh dashboard
    public void updateUserRole(Role newRole, String newUserId) {
        this.currentUserRole = newRole;
        this.currentUserId = newUserId;
        this.dashboardService.setCurrentUser(newRole, newUserId);
        this.allowedComponents = dashboardService.getAllowedDashboardComponents();
        
        // Rebuild the entire dashboard
        removeAll();
        init();
        loadDashboardData();
        revalidate();
        repaint();
    }
    
    // Getter methods
    public Role getCurrentUserRole() {
        return currentUserRole;
    }
    
    public String getCurrentUserId() {
        return currentUserId;
    }
    
    public Set<String> getAllowedComponents() {
        return allowedComponents;
    }
}