package FinalProject;

import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import javax.swing.*;
import java.awt.*;

public class CareHomeManagementSystem extends JFrame {

    // beds and buttons
    private final Map<String, Bed> beds = new HashMap<>();
    private final Map<String, JButton> bedButtons = new HashMap<>();
    private final JTextArea logArea = new JTextArea(8, 40);

    // current user and staff list
    private Staff currentUser;
    private List<Staff> staffList = new ArrayList<>();

    // file names
    private static final String STAFF_FILE = "staff.csv";
    private static final String RESIDENT_FILE = "residents.csv";

    // constructor
    public CareHomeManagementSystem() {
        super("RMIT Care Home Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 750);
        setLayout(new BorderLayout());

        // load staff
        staffList = loadStaffFromCSV(STAFF_FILE);

        // login
        if (!login()) {
            JOptionPane.showMessageDialog(this, "Login failed! Exiting.");
            System.exit(0);
        }

        // wards panel
        JPanel wardsPanel = createWardsPanel();
        add(wardsPanel, BorderLayout.CENTER);

        // load residents
        loadResidentsFromCSV(RESIDENT_FILE);
        updateBedButtons();

        // log area
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        // manager buttons
        JPanel topControls = new JPanel();
        if (currentUser.getRole().equals("Manager")) {
            JButton addStaffBtn = new JButton("Add Staff");
            JButton addResidentBtn = new JButton("Add Resident");

            addStaffBtn.addActionListener(e -> addStaff());
            addResidentBtn.addActionListener(e -> addResidentAction());

            topControls.add(addStaffBtn);
            topControls.add(addResidentBtn);
        }

        add(topControls, BorderLayout.NORTH);

        log("System started by " + currentUser.getRole() + " (" + currentUser.getUserID() + ")");
    }

    // login stuff
    private boolean login() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return false;

        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        for (Staff s : staffList) {
            if (s.getUserID().equals(username) && s.getPassword().equals(password)) {
                currentUser = s;
                return true;
            }
        }

        JOptionPane.showMessageDialog(this, "Invalid username or password.");
        return login(); // retry
    }

    // wards and beds
    private JPanel createWardsPanel() {
        JPanel wardsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        wardsPanel.add(createWardPanel("Ward 1"));
        wardsPanel.add(createWardPanel("Ward 2"));
        return wardsPanel;
    }

    private JPanel createWardPanel(String wardName) {
        JPanel wardPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        wardPanel.setBorder(BorderFactory.createTitledBorder(wardName));

        int[] bedCounts = {1, 2, 4, 4, 3, 2};

        for (int room = 1; room <= 6; room++) {
            JPanel roomPanel = new JPanel(new GridLayout(2, 2, 4, 4));
            roomPanel.setBorder(BorderFactory.createTitledBorder("Room " + room));
            int bedsInRoom = bedCounts[room - 1];

            for (int b = 1; b <= bedsInRoom; b++) {
                String bedID = wardName.replace(" ", "") + "_Room" + room + "_Bed" + b;
                Bed bed = beds.getOrDefault(bedID, new Bed(bedID, null));
                beds.put(bedID, bed);

                JButton bedButton = new JButton("Empty");
                bedButton.setBackground(Color.LIGHT_GRAY);
                bedButton.setToolTipText(bedID);

                bedButton.addActionListener(e -> handleBedAction(bed, bedButton));

                roomPanel.add(bedButton);
                bedButtons.put(bedID, bedButton);
            }
            wardPanel.add(roomPanel);
        }

        return wardPanel;
    }

    private void updateBedButtons() {
        for (String bedID : beds.keySet()) {
            Bed bed = beds.get(bedID);
            JButton button = bedButtons.get(bedID);

            if (bed.getResident() != null) {
                String gender = bed.getResident().Gender;
                button.setText("Occupied");
                button.setBackground(gender.equalsIgnoreCase("M") ? Color.BLUE : Color.RED);
                button.setForeground(Color.WHITE);
            } else {
                button.setText("Empty");
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.BLACK);
            }
        }
    }

    // bed actions
    private void handleBedAction(Bed bed, JButton button) {
        List<String> actions = new ArrayList<>();

        switch (currentUser.getRole()) {
            case "Manager" -> actions.add("Add Resident");
            case "Doctor" -> actions.add("Add Prescription");
            case "Nurse" -> {
                actions.add("Move Resident");
                actions.add("Administer");
            }
        }

        actions.add("View Resident");

        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Select Action for " + bed.ID,
                "Bed Actions",
                JOptionPane.PLAIN_MESSAGE,
                null,
                actions.toArray(),
                actions.get(0)
        );

        if (choice == null) return;

        switch (choice) {
            case "Add Resident" -> addResidentToBed(bed, button);
            case "View Resident" -> viewResident(bed);
            case "Move Resident" -> moveResident(bed);
            case "Add Prescription" -> addPrescription(bed);
            case "Administer" -> administer(bed);
        }
    }

    // manager stuff
    private void addStaff() {
        String id = JOptionPane.showInputDialog("Staff ID:");
        String name = JOptionPane.showInputDialog("Name:");
        String role = JOptionPane.showInputDialog("Role (Doctor/Nurse/Manager):");
        String user = JOptionPane.showInputDialog("Username:");
        String pass = JOptionPane.showInputDialog("Password:");

        if (id == null || name == null || role == null || user == null || pass == null) return;

        Staff newStaff = switch (role) {
            case "Manager" -> new Manager(id, name, user, pass);
            case "Doctor" -> new Doctor(id, name, user, pass);
            case "Nurse" -> new Nurse(id, name, user, pass);
            default -> null;
        };

        if (newStaff != null) {
            staffList.add(newStaff);
            saveStaffToCSV(newStaff);
            log("Added staff: " + role + " (" + id + ")");
            JOptionPane.showMessageDialog(this, "Staff added!");
        }
    }

    private void addResidentAction() {
        String bedID = JOptionPane.showInputDialog("Bed ID:");
        Bed bed = beds.get(bedID);

        if (bed == null) {
            showError("Invalid Bed ID");
            return;
        }

        if (!bed.isVacant()) {
            showError("Bed is occupied");
            return;
        }

        String name = JOptionPane.showInputDialog("Resident Name:");
        String age = JOptionPane.showInputDialog("Age:");
        String gender = JOptionPane.showInputDialog("Gender (M/F):");

        if (name == null || age == null || gender == null) return;

        resident r = new resident(name, age, gender);
        bed.assignResident(r);
        saveResidentToCSV(r, bedID);

        updateBedButtons();
        log("Added resident " + name + " to " + bedID);
    }

    private void addResidentToBed(Bed bed, JButton button) {
        addResidentAction();
    }

    // resident and meds
    private void viewResident(Bed bed) {
        resident r = bed.getResident();
        if (r == null) {
            showError("Bed is empty");
            return;
        }

        StringBuilder info = new StringBuilder("Name: " + r.Name + "\nAge: " + r.Age + "\nGender: " + r.Gender);

        if (!r.prescription.isEmpty()) {
            info.append("\nPrescriptions:");
            for (Prescription p : r.prescription) {
                info.append("\n- ").append(p.Name).append(" ").append(p.Dose).append(" @ ").append(p.Time);
            }
        }

        JOptionPane.showMessageDialog(this, info.toString(), "Resident Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addPrescription(Bed bed) {
        if (bed.getResident() == null) {
            showError("No resident here!");
            return;
        }

        String med = JOptionPane.showInputDialog("Medicine:");
        String dose = JOptionPane.showInputDialog("Dose:");
        String time = JOptionPane.showInputDialog("Time:");

        if (med == null || dose == null || time == null) return;

        Prescription p = new Prescription(med, dose, time);
        bed.getResident().addPresription(p);

        saveResidentsCSV();
        log(currentUser.getRole() + " gave " + med + " to " + bed.getResident().Name);
    }

    private void moveResident(Bed bed) {
        if (bed.getResident() == null) {
            showError("No resident here!");
            return;
        }

        String targetID = JOptionPane.showInputDialog("Move to Bed ID:");
        Bed target = beds.get(targetID);

        if (target == null || !target.isVacant()) {
            showError("Bad target bed!");
            return;
        }

        target.assignResident(bed.getResident());
        bed.assignResident(null);

        updateBedButtons();
        saveResidentsCSV();

        log(currentUser.getRole() + " moved resident to " + targetID);
    }

    private void administer(Bed bed) {
        if (bed.getResident() == null) {
            showError("No resident here!");
            return;
        }

        String med = JOptionPane.showInputDialog("Medicine given:");
        if (med == null) return;

        log(currentUser.getRole() + " gave " + med + " to " + bed.getResident().Name);
    }

    // csv save/load
    private void saveStaffToCSV(Staff s) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(STAFF_FILE, true))) {
            pw.println(s.getID() + "," + s.getName() + "," + s.getRole() + "," + s.getUserID() + "," + s.getPassword());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void saveResidentToCSV(resident r, String bedID) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RESIDENT_FILE, true))) {
            pw.println(bedID + "," + r.Name + "," + r.Age + "," + r.Gender);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void saveResidentsCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RESIDENT_FILE))) {
            for (Bed bed : beds.values()) {
                if (bed.getResident() != null) {
                    resident r = bed.getResident();
                    pw.println(bed.ID + "," + r.Name + "," + r.Age + "," + r.Gender);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private List<Staff> loadStaffFromCSV(String filename) {
        List<Staff> list = new ArrayList<>();
        File file = new File(filename);

        try {
            if (!file.exists() || file.length() == 0) {
                Staff defaultManager = new Manager("M01", "Default Manager", "manager", "1234");
                list.add(defaultManager);
                saveStaffToCSV(defaultManager);
                System.out.println("No staff. Default manager created.");
                return list;
            }

            List<String> lines = Files.readAllLines(Paths.get(filename));

            for (String line : lines) {
                String[] parts = line.split(",");
                String id = parts[0];
                String name = parts[1];
                String role = parts[2];
                String user = parts[3];
                String pass = parts[4];

                Staff s = switch (role) {
                    case "Manager" -> new Manager(id, name, user, pass);
                    case "Doctor" -> new Doctor(id, name, user, pass);
                    case "Nurse" -> new Nurse(id, name, user, pass);
                    default -> null;
                };

                if (s != null) list.add(s);
            }

        } catch (IOException e) { e.printStackTrace(); }

        return list;
    }

    private void loadResidentsFromCSV(String filename) {
        try {
            if (!Files.exists(Paths.get(filename))) return;

            List<String> lines = Files.readAllLines(Paths.get(filename));
            for (String line : lines) {
                String[] parts = line.split(",", 4);
                String bedID = parts[0];
                String name = parts[1];
                String age = parts[2];
                String gender = parts[3];

                resident r = new resident(name, age, gender);
                Bed bed = beds.get(bedID);
                if (bed != null) bed.assignResident(r);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // helper stuff
    private void log(String message) {
        logArea.append(LocalDateTime.now() + " | " + message + "\n");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CareHomeManagementSystem().setVisible(true));
    }
}
