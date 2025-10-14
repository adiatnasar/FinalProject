package FinalProject;

import java.time.LocalDateTime;

public class Shift {
    private Staff staff;
    private LocalDateTime start;
    private LocalDateTime end;
    
    
    // Creates a Shift ensuring end time is after start and duration ≤ 12 hours

    public Shift(Staff staff, LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Shift end time cannot be before start time");
        }
        if (start.plusHours(12).isBefore(end)) {
            throw new ShiftViolationException("Shift exceeds maximum allowed hours (12h).");
        }
        this.staff = staff;
        this.start = start;
        this.end = end;
    }

    public Staff getStaff() {
        return staff;
    }
        // Register the shift info.
    public boolean isRosteredAt(LocalDateTime time) {
        return (time.isAfter(start) || time.isEqual(start)) &&
               (time.isBefore(end) || time.isEqual(end));
    }
}
