package FinalProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DoctorTest {
    private Doctor doc;
    private resident r1;

    @BeforeEach
    void setUp() {
        doc = new Doctor("D01", "Dr. Luther", "docuser", "pass123");
        r1 = new resident("Adam John", "65", "Male");
    }

    @Test
    void testCreatePrescription() {
        Prescription p = doc.createNewPrescription("Paracetamol", "500mg", "08:00");
        assertNotNull(p);
        assertEquals("Paracetamol", p.Name);
    }

    @Test
    void testAssignPrescriptionToResident() {
        Prescription p = doc.createNewPrescription("Alloset", "200mg", "18:00");
        doc.assignPrescriptionToResident(r1, p);
        assertTrue(r1.prescription.contains(p));
    }
}
