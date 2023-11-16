
package classes;

import java.util.Date;

public class User {
  String username, name, role;
int id,leave;  
Date join;

    public User(String username, String name, int id,String role, int leave, Date join) {
        this.username = username;
        this.name = name;
        this.id = id;
        this.role = role;
        this.leave = leave;
        this.join = join;
    }

   public User(String username, String name, String role, int leaveBalance,Date join) {
       this.username = username;
        this.name = name;
        this.role = role;
        this.leave = leaveBalance;
        this.join = join;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeave() {
        return leave;
    }

    public void setLeave(int leave) {
        this.leave = leave;
    }

    public Date getJoin() {
        return join;
    }

    public void setJoin(Date join) {
        this.join = join;
    }

}
