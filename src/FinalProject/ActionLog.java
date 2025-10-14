package FinalProject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

    /// Records all the activity 

public class ActionLog {
    private static List<String> logs = new ArrayList<>();

    public static void log(String staffID, String action) {
        String entry = LocalDateTime.now() + " | Staff: " + staffID + " | " + action;
        logs.add(entry);
        System.out.println(entry);
    }

    public static List<String> getLogs() {
        return logs;
    }
}

