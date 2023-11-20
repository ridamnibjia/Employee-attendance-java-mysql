
package classes;

import java.util.Date;

public class LeaveInfo {
    private int leaveId;
    private int empId;
    private String employeeName;
    private Date dateFrom;
    private Date dateTo;
    private int days;
    private String reason;
    private String leaveType;
    private String status;
    private int leaveBalance;

    // Constructors, getters, and setters

    public LeaveInfo(int leaveId, int empId, String employeeName, Date dateFrom, Date dateTo, int days,
                     String reason, String leaveType, String status, int leaveBalance) {
        this.leaveId = leaveId;
        this.empId = empId;
        this.employeeName = employeeName;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.days = days;
        this.reason = reason;
        this.leaveType = leaveType;
        this.status = status;
        this.leaveBalance = leaveBalance;
    }

    // Add getters and setters here

    public int getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLeaveBalance() {
        return leaveBalance;
    }

    public void setLeaveBalance(int leaveBalance) {
        this.leaveBalance = leaveBalance;
    }
}
