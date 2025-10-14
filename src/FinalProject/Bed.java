package FinalProject;

public class Bed {
	public String ID;
	public resident resident;
	public Bed(String ID, resident resident) {
		this.ID=ID;
		this.resident=null;
	}
	
	/// Checks if the bed is vacant
	
	public boolean isVacant() {
		return resident==null;
	} 
	
	/// Assigns resident
 
	public void assignResident(resident r) {
		this.resident=r;
	} 
	
	///Remove resident
	
	public void removeResident() {
	        this.resident = null;
	}
	public resident getResident() {
	        return resident;
    }
}
