package FinalProject;
import java.util.*;

   // Creates resident and add prescription to the resident
public class resident {
	public String Name;
	public String Age;
	public String Gender;
	public ArrayList <Prescription> prescription=new ArrayList<>();
	public resident(String Name, String Age, String Gender) {
		this.Name= Name;
		this.Age=Age;
		this.Gender=Gender;
	}
	public void addPresription( Prescription p) {
		prescription.add(p);
	}
}
