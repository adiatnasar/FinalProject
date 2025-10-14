package FinalProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NurseTest {
    private Nurse nurse;
    private resident r1, r2;
    private Bed b1, b2;

    @BeforeEach
    void setUp() {
        nurse = new Nurse("N01", "Nurse Cane", "nuser1", "npass1234");
        r1 = new resident("Adam John", "65", "Male");
        r2 = new resident("Mary Lee", "72", "Female");
        b1 = new Bed("B1", null);
        b2 = new Bed("B2", null);
    }

    @Test
    void testMoveResident() {
        b1.assignResident(r1);
        nurse.moveResident(b1, b2);
        assertEquals(r1, b2.getResident());
        assertNull(b1.getResident());
    }
}

