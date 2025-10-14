package FinalProject;

class Nurse extends Staff {
    public Nurse(String ID, String Name, String UserID, String Password) {
        super(ID, Name, "Nurse", UserID, Password);
    }
   /// Moving resident from one bed to another if its empty
   
    public void moveResident(Bed from, Bed to) {
        if (from.getResident() != null) { 
            if (!to.isVacant()) {
                throw new IllegalStateException("Move failed: target bed is already occupied.");
            }
            // move resident
            to.assignResident(from.getResident());
            ActionLog.log(this.ID, "Moved resident to bed " + to.ID);
            from.removeResident();
        } else {
            throw new IllegalStateException("Move failed: source bed is empty.");
        }
    }
    
    /// Administer prescription 
    
    public void administerPrescription(resident r, Prescription p) {
        ActionLog.log(this.ID, "Administered " + p.Name + " (" + p.Dose + ") at " + p.Time +
                " to resident " + r.Name);
    }
}
