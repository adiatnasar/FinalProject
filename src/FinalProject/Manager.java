package FinalProject;

public class Manager extends Staff {
    public Manager(String ID, String Name, String UserID, String Password) {
        super(ID, Name, "Manager", UserID, Password);
    }
    
    // Method to add resident to bed
    public void addResidentToBed(resident r, Bed b) {
        if (b.isVacant()) {
            b.assignResident(r);
        } else {
            throw new IllegalStateException("Bed already occupied!");
        }
    }
    
    // Method to add staff
    public Staff addStaff(String ID, String name, String role, String user, String pass) {
        return switch (role.toLowerCase()) {
            case "doctor" -> new Doctor(ID, name, user, pass);
            case "nurse" -> new Nurse(ID, name, user, pass);
            case "manager" -> new Manager(ID, name, user, pass);
            default -> throw new IllegalArgumentException("Invalid role!");
        };
    }
}
