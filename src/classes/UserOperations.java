
package classes;
import java.sql.*;
import javax.swing.*;
import connect.DBConnection;
import java.awt.List;
import java.util.ArrayList;

public class UserOperations {
    Connection con;

    public UserOperations() {
    }
    public boolean deleteEmployee(int userId) {
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);  // Disable auto-commit to perform a transaction

            // Delete from the leaves table
            String leavesQuery = "DELETE FROM leaves WHERE empid = ?";
            try (PreparedStatement leavesPst = con.prepareStatement(leavesQuery)) {
                leavesPst.setInt(1, userId);
                leavesPst.executeUpdate();
            }

            // Delete from the emp table
            String empQuery = "DELETE FROM emp WHERE id = ?";
            try (PreparedStatement empPst = con.prepareStatement(empQuery)) {
                empPst.setInt(1, userId);
                empPst.executeUpdate();
            }

            // Delete from the user table
            String userQuery = "DELETE FROM user WHERE id = ?";
            try (PreparedStatement userPst = con.prepareStatement(userQuery)) {
                userPst.setInt(1, userId);
                userPst.executeUpdate();
            }

            con.commit();  // Commit the transaction
            return true;

        } catch (SQLException ex) {
            try {
                if (con != null) {
                    con.rollback();  // Rollback the transaction in case of an error
                }
            } catch (SQLException rollbackEx) {
                JOptionPane.showMessageDialog(null, rollbackEx);
            }
            JOptionPane.showMessageDialog(null, ex);
            return false;

        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);  // Re-enable auto-commit
                    con.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
public User getUserProfileById(int empId) {
        User userProfile = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();

            // Query to retrieve user details and leave balance based on empid
            String query = "SELECT u.id AS userId, u.username, u.role,e.leave_balance, e.location, e.name, e.dateofjoin "
                         + "FROM user u "
                         + "JOIN emp e ON u.id = e.id "
                         + "WHERE u.id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, empId);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Retrieve user details and leave balance from the result set
                int userId = resultSet.getInt("userId");
                String username = resultSet.getString("username");
                String role = resultSet.getString("role");
                String name = resultSet.getString("name");
                int leaveBalance = resultSet.getInt("leave_balance");

                // Create a UserProfile object with the retrieved details
                userProfile = new User(username, name, empId,role, leaveBalance, resultSet.getDate("dateofjoin"), resultSet.getString("location"));
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception as needed
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userProfile;
    }
    
  public ArrayList<User> getAllUsers() {
        ArrayList<User> userList = new ArrayList<>();
        try {
            con = DBConnection.getConnection();

            String query = "SELECT u.id, u.username, u.role, e.name, e.dateofjoin, e.location, e.leave_balance " +
                           "FROM user u LEFT JOIN emp e ON u.id = e.id";
            try (PreparedStatement pst = con.prepareStatement(query);
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String role = rs.getString("role");
                    String name = rs.getString("name");
                    Date dateOfJoin = rs.getDate("dateofjoin");
                    int leaveBalance = rs.getInt("leave_balance");

                    User user = new User(username, name, id, role, leaveBalance, dateOfJoin, rs.getString("location"));
                    userList.add(user);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            // Close the connection in a finally block to ensure it is closed
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        }

        return userList;
    }
  
   public boolean saveEmployee(User user, String password) {
        boolean success = false;
        try {
            con = DBConnection.getConnection();
            
            // Insert into the user table
            String userQuery = "INSERT INTO user (username, password, role) VALUES (?, md5(?), ?)";
            try (PreparedStatement userPst = con.prepareStatement(userQuery)) {
                // Assuming you want to set default values for password and role
                userPst.setString(1, user.getUsername());
                userPst.setString(2, password); // You may want to handle password securely
                userPst.setString(3, user.getRole());
                
                // Execute the query
                userPst.executeUpdate();
            }
            
            // Get the generated ID from the user insertion
            int userId = 0;
            String getIdQuery = "SELECT LAST_INSERT_ID() AS id";
            try (PreparedStatement getIdPst = con.prepareStatement(getIdQuery);
                 var rs = getIdPst.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("id");
                }
            }
            
            // Insert into the emp table
            String empQuery = "INSERT INTO emp (id, name, leave_balance, dateofjoin) VALUES (?, ?, ?, ?)";
            try (PreparedStatement empPst = con.prepareStatement(empQuery)) {
                empPst.setInt(1, userId);
                empPst.setString(2, user.getName());
                empPst.setInt(3, user.getLeave());
empPst.setDate(4, new java.sql.Date(user.getJoin().getTime()));

                
                // Execute the query
                int rowsAffected = empPst.executeUpdate();
                success = rowsAffected > 0;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } finally {
            // Close the connection in a finally block to ensure it is closed
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        }
        return success;
    }
   
   public boolean updateUser(int id, User updatedUser, String newPassword) {
    boolean success = false;

    try {
        con = DBConnection.getConnection();

        // Update the user table
        String updateUserQuery = "UPDATE user SET username = ?, password = md5(?), role = ? WHERE id = ?";
        try (PreparedStatement updateUserPst = con.prepareStatement(updateUserQuery)) {
            updateUserPst.setString(1, updatedUser.getUsername());
            updateUserPst.setString(2, newPassword); // You may want to handle password securely
            updateUserPst.setString(3, updatedUser.getRole());
            updateUserPst.setInt(4, id);

            // Execute the query
            int userRowsAffected = updateUserPst.executeUpdate();
            if (userRowsAffected > 0) {
                // Update the emp table
                String updateEmpQuery = "UPDATE emp SET name = ?, leave_balance = ?, dateofjoin = ?, location = ? WHERE id = ?";
                try (PreparedStatement updateEmpPst = con.prepareStatement(updateEmpQuery)) {
                    updateEmpPst.setString(1, updatedUser.getName());
                    updateEmpPst.setInt(2, updatedUser.getLeave());
                    updateEmpPst.setDate(3, new java.sql.Date(updatedUser.getJoin().getTime()));
                    updateEmpPst.setString(4, updatedUser.getLocation());
                    updateEmpPst.setInt(5, id);

                    // Execute the query
                    int empRowsAffected = updateEmpPst.executeUpdate();
                    success = empRowsAffected > 0;
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, ex);
    } finally {
        // Close the connection in a finally block to ensure it is closed
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    return success;
}
   
   public boolean updateEmployeeDetails(int empId, String newName, String newUsername, String newPassword) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();

            // Update the name, username, and password for the given empid
            String updateQuery = "UPDATE user u "
                               + "JOIN emp e ON u.id = e.id "
                               + "SET u.username = ?, u.password = md5(?), e.name = ? "
                               + "WHERE u.id = ?";
            statement = connection.prepareStatement(updateQuery);
            statement.setString(1, newUsername);
            statement.setString(2, newPassword);
            statement.setString(3, newName);
            statement.setInt(4, empId);

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace(); // Handle or log the exception as needed
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false; // Update failed
    }

}
