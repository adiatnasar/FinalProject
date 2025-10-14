package FinalProject;

abstract class Staff {
    protected String ID;
    protected String Name;
    protected String UserID;
    protected String Password;
    protected String Role;
	
    public Staff(String ID, String Name, String Role, String UserID, String Password) {
        this.ID = ID;
        this.Name = Name;
        this.Role = Role;
        this.UserID = UserID;
        this.Password = Password;
    }

    public String getID() {
        return ID;
    }

    public String getRole() {
        return Role;
    }

    public String getName() {
        return Name;
    }

    public void setPassword(String newPassword) {
        this.Password = newPassword;
    }

    public String getUserID() {
        return UserID;
    }

    public boolean login(String user, String pass) {
        return this.UserID.equals(user) && this.Password.equals(pass);
    }

    public String getPassword() {
        return this.Password;
    }
}
