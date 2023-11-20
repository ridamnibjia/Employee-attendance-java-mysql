
package classes;

import connect.DBConnection;
import java.awt.List;
import java.sql.*;
import java.util.ArrayList;
public class LeaveOperations {
    
    /**
     *
     * @return
     */
    public ArrayList<LeaveInfo> getAllLeaves() {
        ArrayList<LeaveInfo> leaves = new ArrayList<>();

        String query = "SELECT leaves.id AS leaveid, emp.id AS empid, emp.name AS employeename, "
                + "leaves.datefrom, leaves.dateto, leaves.size AS days, "
                + "leaves.reason, leaves.type, leaves.status, emp.leave_balance "
                + "FROM leaves "
                + "JOIN emp ON leaves.empid = emp.id";

        try{
            
            Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int leaveId = resultSet.getInt("leaveid");
                int empId = resultSet.getInt("empid");
                String employeeName = resultSet.getString("employeename");
                Date dateFrom = resultSet.getDate("datefrom");
                Date dateTo = resultSet.getDate("dateto");
                int days = resultSet.getInt("days");
                String reason = resultSet.getString("reason");
                String leaveType = resultSet.getString("type");
                String status = resultSet.getString("status");
                int leaveBalance = resultSet.getInt("leave_balance");

                LeaveInfo leaveInfo = new LeaveInfo(leaveId, empId, employeeName, dateFrom, dateTo, days,
                        reason, leaveType, status, leaveBalance);

                leaves.add(leaveInfo);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception as needed
        }

        return leaves;
    }
    
    
    public boolean updateLeaveStatus(int leaveId, String newStatus, int leaveDays) {
        Connection connection = null;
        PreparedStatement updateLeaveStatement = null;
        PreparedStatement updateBalanceStatement = null;

        try {
            connection = DBConnection.getConnection();

            // Start a transaction
            connection.setAutoCommit(false);

            // Update the leave status
            String updateLeaveQuery = "UPDATE leaves SET status = ? WHERE id = ?";
            updateLeaveStatement = connection.prepareStatement(updateLeaveQuery);
            updateLeaveStatement.setString(1, newStatus);
            updateLeaveStatement.setInt(2, leaveId);

            int leaveRowsUpdated = updateLeaveStatement.executeUpdate();

            if (leaveRowsUpdated > 0) {
                // If leave is rejected, add leave balance back to the employee
                if ("Rejected".equalsIgnoreCase(newStatus)) {
                    // Retrieve empid and size (leave days) for the rejected leave
                    String getLeaveDetailsQuery = "SELECT empid, size FROM leaves WHERE id = ?";
                    PreparedStatement getLeaveDetailsStatement = connection.prepareStatement(getLeaveDetailsQuery);
                    getLeaveDetailsStatement.setInt(1, leaveId);
                    var leaveDetailsResultSet = getLeaveDetailsStatement.executeQuery();

                    if (leaveDetailsResultSet.next()) {
                        int empId = leaveDetailsResultSet.getInt("empid");
                        int size = leaveDetailsResultSet.getInt("size");

                        // Update leave balance
                        String updateBalanceQuery = "UPDATE emp SET leave_balance = leave_balance + ? WHERE id = ?";
                        updateBalanceStatement = connection.prepareStatement(updateBalanceQuery);
                        updateBalanceStatement.setInt(1, size);
                        updateBalanceStatement.setInt(2, empId);

                        int balanceRowsUpdated = updateBalanceStatement.executeUpdate();

                        if (balanceRowsUpdated > 0) {
                            // Commit the transaction if everything is successful
                            connection.commit();
                            return true;
                        }
                    }
                } else {
                    // Commit the transaction if leave status is updated (e.g., Approved)
                    connection.commit();
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println(e); // Handle or log the exception as needed
            try {
                if (connection != null) {
                    connection.rollback(); // Rollback the transaction in case of an exception
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        } finally {
            try {
                if (updateLeaveStatement != null) {
                    updateLeaveStatement.close();
                }
                if (updateBalanceStatement != null) {
                    updateBalanceStatement.close();
                }
                if (connection != null) {
                    connection.setAutoCommit(true); // Reset auto-commit to true
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        }

        return false; // Leave status update failed
    }
    
    public ArrayList<Leave> getLeavesByEmployeeId(int empId) {
    ArrayList<Leave> leaves = new ArrayList<>();

    String query = "Select * from leaves where empid = ?"; // Use a parameterized query to prevent SQL injection

    try {
        Connection connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, empId); // Set the parameter value

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            int leaveId = resultSet.getInt("id");
            Date dateFrom = resultSet.getDate("datefrom");
            Date dateTo = resultSet.getDate("dateto");
            int days = resultSet.getInt("size");
            String reason = resultSet.getString("reason");
            String leaveType = resultSet.getString("type");
            String status = resultSet.getString("status");

            Leave leaveInfo = new Leave(leaveId, empId, days,
                    reason, leaveType, status, dateFrom, dateTo);

            leaves.add(leaveInfo);
        }

    } catch (SQLException e) {
        System.out.println(e); // Handle or log the exception as needed
    }

    return leaves;
}
public boolean addLeave(int empId, java.util.Date dateFrom, java.util.Date dateTo, int days, String reason, String leaveType) {
    Connection connection = null;
    PreparedStatement insertLeaveStatement = null;
    PreparedStatement updateBalanceStatement = null;

    try {
        connection = DBConnection.getConnection();

        // Check if the employee exists in the user table
        String checkUserQuery = "SELECT id FROM user WHERE id = ?";
        PreparedStatement checkUserStatement = connection.prepareStatement(checkUserQuery);
        checkUserStatement.setInt(1, empId);
        ResultSet userResultSet = checkUserStatement.executeQuery();

        if (!userResultSet.next()) {
            System.out.println("Employee with ID " + empId + " does not exist.");
            return false; // Employee does not exist
        }

        // Check if the leave balance is sufficient
        String checkBalanceQuery = "SELECT leave_balance FROM emp WHERE id = ?";
        updateBalanceStatement = connection.prepareStatement(checkBalanceQuery);
        updateBalanceStatement.setInt(1, empId);

        ResultSet balanceResultSet = updateBalanceStatement.executeQuery();

        if (balanceResultSet.next()) {
            int leaveBalance = balanceResultSet.getInt("leave_balance");

            if (leaveBalance >= days) {
                // Deduct leave balance
                String updateBalanceQuery = "UPDATE emp SET leave_balance = ? WHERE id = ?";
                updateBalanceStatement = connection.prepareStatement(updateBalanceQuery);
                updateBalanceStatement.setInt(1, leaveBalance - days);
                updateBalanceStatement.setInt(2, empId);

                updateBalanceStatement.executeUpdate();

                // Convert java.util.Date to java.sql.Date
                Date sqlDateFrom = new Date(dateFrom.getTime());
                Date sqlDateTo = new Date(dateTo.getTime());

                // Insert leave record
                String insertLeaveQuery = "INSERT INTO leaves (empid, datefrom, dateto, size, reason, type, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
                insertLeaveStatement = connection.prepareStatement(insertLeaveQuery);
                insertLeaveStatement.setInt(1, empId);
                insertLeaveStatement.setDate(2, sqlDateFrom);
                insertLeaveStatement.setDate(3, sqlDateTo);
                insertLeaveStatement.setInt(4, days);
                insertLeaveStatement.setString(5, reason);
                insertLeaveStatement.setString(6, leaveType);
                insertLeaveStatement.setString(7, "Pending"); // You may set the initial status as needed

                insertLeaveStatement.executeUpdate();

                return true; // Leave added successfully
            } else {
                // Insufficient leave balance
                System.out.println("Insufficient leave balance for employee with ID " + empId);
            }
        }

    } catch (SQLException e) {
        System.out.println(e); // Handle or log the exception as needed
    } finally {
        try {
            if (insertLeaveStatement != null) {
                insertLeaveStatement.close();
            }
            if (updateBalanceStatement != null) {
                updateBalanceStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    return false; // Leave addition failed
}
  
    

}
