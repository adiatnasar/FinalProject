package FinalProject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BedTest {
    private resident r1;
    private Bed b1;
    @BeforeEach
    void setUp() {
        r1 = new resident("Adam John", "59", "Male");
        b1 = new Bed("B1", null);
    }

    @Test
    void testAssignResident() {
        b1.assignResident(r1);
        assertEquals(r1, b1.getResident());
    }

    @Test
    void testRemoveResident() {
        b1.assignResident(r1);
        b1.removeResident();
        assertNull(b1.getResident());
    }

    @Test
    void testIsVacant() {
        assertTrue(b1.isVacant());
        b1.assignResident(r1);
        assertFalse(b1.isVacant());
    }
}
