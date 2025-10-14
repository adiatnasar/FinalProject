package FinalProject;

public class Main {
    public static void main(String[] args) {
        // Create staff members
        Doctor doc1 = new Doctor("D01", "Dr. Charlie", "docuser1", "drpass123");
        Doctor doc2 = new Doctor("D02", "Dr. Adams", "docuser2", "drpass234");
        Nurse nurse1 = new Nurse("N01", "Nurse Mally", "nuser1", "nupass1");
        Nurse nurse2 = new Nurse("N02", "Nurse Tracy", "nuser2", "nupass2");
        Manager mgr1 = new Manager("M01", "Manager Manuel", "muser1", "mgpass1");
        Manager mgr2 = new Manager("M02", "Manager Alice", "muser2", "mgpass2");

        // Creates resident information
        resident r1 = new resident("Solomon Hartman", "59", "Male");
        resident r2 = new resident("Damien Carroll", "52", "Female");
        resident r3 = new resident("Troy Ward", "30", "Male");
        
        // Bed information
        Bed b1 = new Bed("B1", null);
        Bed b2 = new Bed("B2", null);
        Bed b3 = new Bed("B3", null);

        // Manager assigns residents to beds
        
        mgr1.addResidentToBed(r1, b1);
        mgr2.addResidentToBed(r2, b2);
        mgr1.addResidentToBed(r3, b3);

        // Doctors create prescriptions
        Prescription p1 = doc1.createNewPrescription("Paracetamol", "500mg", "18:00");
        Prescription p2 = doc2.createNewPrescription("Alloset", "250mg", "02:00");
        Prescription p3 = doc1.createNewPrescription("Angionoin", "200mg", "15:00");

        // Assign prescriptions to residents
        doc1.assignPrescriptionToResident(r1, p1);
        doc2.assignPrescriptionToResident(r2, p2);
        doc1.assignPrescriptionToResident(r3, p3);

        // Nurses administer prescriptions
        nurse1.administerPrescription(r1, p1);
        nurse2.administerPrescription(r2, p2);
        nurse1.administerPrescription(r3, p3);


        nurse2.moveResident(b3, b1); 

        // Print all logged actions
        System.out.println("\n--- ACTION LOGS ---");
        for (String log : ActionLog.getLogs()) {
            System.out.println(log);
        }
    }
}

