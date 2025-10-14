package FinalProject;

class Doctor extends Staff {
    public Doctor(String ID, String Name, String UserID, String Password) {
        super(ID, Name, "Doctor", UserID, Password);
    }
    
      /// Creates Prescription
   
    public Prescription createNewPrescription(String Name, String Dose, String Time) {
        Prescription p = new Prescription(Name, Dose, Time);
        ActionLog.log(this.ID, "Created prescription: " + Name + " " + Dose + " at " + Time);
        return p;
    }
      
    /// Only allows doctor to assign prescription to resident
    
    public void assignPrescriptionToResident(resident r, Prescription p) {
        if (!this.Role.equals("Doctor")) {
            throw new UnauthorizedActionException("Only doctors can assign prescriptions!");
        }
        r.addPresription(p);
        ActionLog.log(this.ID, "Assigned prescription " + p.Name + " to " + r.Name);
    }
}
