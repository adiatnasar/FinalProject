package FinalProject;
import java.util.List;
import java.util.ArrayList;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;

//Main class
public class CareHomeManagementSystem extends JFrame
{
 Map<String,Bed> beds = new HashMap<>();
 Map<String,JButton> bedButtons = new HashMap<>();
 JTextArea logArea = new JTextArea(8,40);
 Staff currentUser;
 List<Staff> staffList = new ArrayList<>();
 Map<String,Shift> shifts = new HashMap<>();

 static String STAFF_FILE = "staff.csv";
 static String RESIDENT_FILE = "residents.csv";
 static String PRESCRIPTION_FILE = "prescriptions.csv";
 static String LOG_FILE = "system_log.txt";
 static String SHIFT_FILE = "shifts.csv";

 // constructor
 public CareHomeManagementSystem()
 {
     super("RMIT Care Home System");
     setDefaultCloseOperation(EXIT_ON_CLOSE);
     setSize(1050,750);
     setLayout(new BorderLayout());

     staffList = loadStaffFromCSV(STAFF_FILE);
     loadShiftsFromCSV();

     if(!login())
     {
         JOptionPane.showMessageDialog(this,"Login failed! Exiting.");
         System.exit(0);
     }

     JPanel wardsPanel = createWardsPanel();
     add(wardsPanel,BorderLayout.CENTER);

     loadResidentsFromCSV(RESIDENT_FILE);
     loadPrescriptionsFromCSV(PRESCRIPTION_FILE);
     updateBedButtons();

     logArea.setEditable(false);
     add(new JScrollPane(logArea),BorderLayout.SOUTH);

     JPanel topControls = new JPanel();

     if(currentUser.getRole().equals("Manager"))
     {
         JButton addStaffBtn = new JButton("Add Staff");
         JButton modifyStaffBtn = new JButton("Modify Staff");
         JButton addResidentBtn = new JButton("Add Resident");
         JButton addShiftBtn = new JButton("Add Shift");
         JButton modifyShiftBtn = new JButton("Modify Shift");

         addStaffBtn.addActionListener(e->addStaff());
         modifyStaffBtn.addActionListener(e->modifyStaff());
         addResidentBtn.addActionListener(e->addResidentAction());
         addShiftBtn.addActionListener(e->addShift());
         modifyShiftBtn.addActionListener(e->modifyShift());

         topControls.add(addStaffBtn);
         topControls.add(modifyStaffBtn);
         topControls.add(addResidentBtn);
         topControls.add(addShiftBtn);
         topControls.add(modifyShiftBtn);
     }

     if(currentUser.getRole().equals("Doctor") || currentUser.getRole().equals("Nurse"))
     {
         JButton viewShiftBtn = new JButton("View My Shift");
         viewShiftBtn.addActionListener(e->viewMyShift());
         topControls.add(viewShiftBtn);
     }

     JButton logoutBtn = new JButton("Log Out");
     logoutBtn.addActionListener(e->logoutAction());
     topControls.add(logoutBtn);

     add(topControls,BorderLayout.NORTH);
     log("System started by "+currentUser.getRole()+" ("+currentUser.getUserID()+")");
 }

 // logout
 private void logoutAction()
 {
     int confirm = JOptionPane.showConfirmDialog(this,"Are you sure you want to log out?","Log Out",JOptionPane.YES_NO_OPTION);
     if(confirm==JOptionPane.YES_OPTION)
     {
         saveAllResidentsToCSV();
         saveAllPrescriptionsToCSV();
         saveShiftsToCSV();
         log("User "+currentUser.getUserID()+" logged out.");
         JOptionPane.showMessageDialog(this,"Logged out successfully!");
         dispose();
         new CareHomeManagementSystem().setVisible(true);
     }
 }

 // add shift (manager)
 private void addShift()
 {
     String staffID = JOptionPane.showInputDialog("Enter Staff ID:");
     if(staffID==null) return;

     String date = JOptionPane.showInputDialog("Enter Date (YYYY-MM-DD):");
     String start = JOptionPane.showInputDialog("Start Time (HH:MM):");
     String end = JOptionPane.showInputDialog("End Time (HH:MM):");

     if(date==null||start==null||end==null) return;

     Shift s = new Shift(staffID,date,start,end);
     shifts.put(staffID,s);
     saveShiftsToCSV();
     log("Manager added shift for "+staffID);
     JOptionPane.showMessageDialog(this,"Shift added for "+staffID);
 }

 // modify shift (manager)
 private void modifyShift()
 {
     String staffID = JOptionPane.showInputDialog("Enter Staff ID to modify:");
     if(staffID==null) return;
     Shift s = shifts.get(staffID);
     if(s==null){ JOptionPane.showMessageDialog(this,"No shift found!"); return; }

     String date = JOptionPane.showInputDialog("Date:",s.date);
     String start = JOptionPane.showInputDialog("Start:",s.startTime);
     String end = JOptionPane.showInputDialog("End:",s.endTime);

     if(date!=null) s.date=date;
     if(start!=null) s.startTime=start;
     if(end!=null) s.endTime=end;

     shifts.put(staffID,s);
     saveShiftsToCSV();
     log("Manager modified shift for "+staffID);
     JOptionPane.showMessageDialog(this,"Shift updated.");
 }

 // view shift (doctor/nurse)
 private void viewMyShift()
 {
     Shift s = shifts.get(currentUser.getID());
     if(s==null){ JOptionPane.showMessageDialog(this,"No shift assigned."); return; }

     String msg = "Shift Info:\nDate: "+s.date+"\nStart: "+s.startTime+"\nEnd: "+s.endTime;
     JOptionPane.showMessageDialog(this,msg,"My Shift",JOptionPane.INFORMATION_MESSAGE);
 }

 // load shifts
 private void loadShiftsFromCSV()
 {
     shifts.clear();
     try(BufferedReader br = new BufferedReader(new FileReader(SHIFT_FILE)))
     {
         String line;
         while((line=br.readLine())!=null)
         {
             String[] p = line.split(",");
             if(p.length>=4)
             {
                 Shift s = new Shift(p[0],p[1],p[2],p[3]);
                 shifts.put(p[0],s);
             }
         }
     }
     catch(IOException e){ }
 }

 // save shifts
 private void saveShiftsToCSV()
 {
     try(PrintWriter pw = new PrintWriter(new FileWriter(SHIFT_FILE)))
     {
         for(Shift s:shifts.values())
         {
             pw.println(s.toCSV());
         }
     }
     catch(IOException e){ }
 }

 // login
 private boolean login()
 {
     JPanel panel = new JPanel(new GridLayout(2,2));
     JTextField userField = new JTextField();
     JPasswordField passField = new JPasswordField();
     panel.add(new JLabel("Username:"));
     panel.add(userField);
     panel.add(new JLabel("Password:"));
     panel.add(passField);

     int res = JOptionPane.showConfirmDialog(this,panel,"Login",JOptionPane.OK_CANCEL_OPTION);
     if(res!=JOptionPane.OK_OPTION) return false;

     String username = userField.getText().trim();
     String password = new String(passField.getPassword());

     for(Staff s:staffList)
     {
         if(s.login(username,password))
         {
             currentUser = s;
             return true;
         }
     }

     JOptionPane.showMessageDialog(this,"Invalid username or password.");
     return login();
 }

    // create wards
    private JPanel createWardsPanel()
    {
        JPanel wardsPanel = new JPanel(new GridLayout(1,2,10,10));
        wardsPanel.add(createWardPanel("Ward 1"));
        wardsPanel.add(createWardPanel("Ward 2"));
        return wardsPanel;
    }

    private JPanel createWardPanel(String wardName)
    {
        JPanel wardPanel = new JPanel(new GridLayout(3,2,8,8));
        wardPanel.setBorder(BorderFactory.createTitledBorder(wardName));
        int[] bedCounts = {1,2,4,4, 3,2};

        for(int room=1;room<=6;room++)
        {
            JPanel roomPanel = new JPanel(new GridLayout(2,2,4,4));
            roomPanel.setBorder(BorderFactory.createTitledBorder("Room "+room));
            int bedsInRoom = bedCounts[room-1];

            for(int b=1;b<=bedsInRoom;b++)
            {
                String bedID = wardName.replace(" ","")+"_Room"+room+"_Bed"+b;
                Bed bed = beds.getOrDefault(bedID,new Bed(bedID,null));
                beds.put(bedID,bed);

                JButton bedButton = new JButton("Empty");
                bedButton.setBackground(Color.LIGHT_GRAY);
                bedButton.setToolTipText(bedID);

                Bed bedRef = bed;
                JButton btnRef = bedButton;

                bedButton.addActionListener(e->handleBedAction(bedRef,btnRef));

                roomPanel.add(bedButton);
                bedButtons.put(bedID,bedButton);
            }
            wardPanel.add(roomPanel);
        }
        return wardPanel;
    }

    // update buttons
    private void updateBedButtons()
    {
        for(String bedID:beds.keySet())
        {
            Bed bed = beds.get(bedID);
            JButton btn = bedButtons.get(bedID);
            if(btn==null) continue;

            if(bed.getResident()!=null)
            {
                String g = bed.getResident().Gender;
                btn.setText("Occupied");
                btn.setBackground(g.equalsIgnoreCase("M")||g.equalsIgnoreCase("Male")?Color.BLUE:Color.RED);
                btn.setForeground(Color.WHITE);
            }
            else
            {
                btn.setText("Empty");
                btn.setBackground(Color.LIGHT_GRAY);
                btn.setForeground(Color.BLACK);
            }
        }
    }

    // handle bed action
   private void handleBedAction(Bed bed,JButton button)
    {
        List<String> actions = new ArrayList<>();
        switch(currentUser.getRole())
        {
            case "Manager": actions.add("Add Resident"); break;
            case "Doctor": actions.add("Add Prescription"); actions.add("Modify Prescription"); break;
            case "Nurse": actions.add("Move Resident"); actions.add("Administer"); break;
        }
        actions.add("View Resident");

        String choice = (String)JOptionPane.showInputDialog(this,"Select Action for "+bed.ID,
                "Bed Actions",JOptionPane.PLAIN_MESSAGE,null,actions.toArray(),actions.get(0));
        if(choice==null) return;

        switch(choice)
        {
            case "Add Resident": addResidentToBed(bed,button); break;
            case "View Resident": viewResident(bed); break;
            case "Move Resident": moveResident(bed); break;
            case "Add Prescription": addPrescription(bed); break;
            case "Modify Prescription": modifyPrescription(bed); break;
            case "Administer": administerPrescription(bed); break;
            default: showError("Unknown action: "+choice);
        }
    }

    // Staff 
    private void addStaff()
    {
        String id = JOptionPane.showInputDialog("Staff ID:");
        String name = JOptionPane.showInputDialog("Name:");
        String role = JOptionPane.showInputDialog("Role:");
        String user = JOptionPane.showInputDialog("Username:");
        String pass = JOptionPane.showInputDialog("Password:");
        if(id==null||name==null||role==null||user==null||pass==null) return;

        Staff newStaff=null;
        switch(role)
        {
            case "Manager": newStaff=new Manager(id,name,user,pass); break;
            case "Doctor": newStaff=new Doctor(id,name,user,pass); break;
            case "Nurse": newStaff=new Nurse(id,name,user,pass); break;
        }
        if(newStaff!=null)
        {
            staffList.add(newStaff);
            saveStaffListToCSV();
            log("Added staff: "+role+" ("+id+")");
            JOptionPane.showMessageDialog(this,"Staff added!");
        }
        else{ showError("Invalid role"); }
    }

    private void modifyStaff()
    {
        String userID=JOptionPane.showInputDialog("UserID:");
        if(userID==null) return;

        Staff s = staffList.stream().filter(st->st.getUserID().equals(userID)).findFirst().orElse(null);
        if(s==null){ showError("Not found"); return; }

        String name = JOptionPane.showInputDialog("New Name:",s.getName());
        String role = JOptionPane.showInputDialog("New Role:",s.getRole());
        String pass = JOptionPane.showInputDialog("New Password:",s.getPassword());

        s.Name=name;
        s.Role=role;
        s.setPassword(pass);

        saveStaffListToCSV();
        log("Modified staff "+userID);
        JOptionPane.showMessageDialog(this,"Staff updated!");
    }

    private void saveStaffListToCSV()
    {
        try(PrintWriter pw=new PrintWriter(new FileWriter(STAFF_FILE)))
        {
            for(Staff s:staffList)
            {
                pw.println(s.getID()+","+s.getName()+","+s.getRole()+","+s.getUserID()+","+s.getPassword());
            }
        }
        catch(IOException e){ e.printStackTrace();}
    }

    // Residents / Prescriptions
    private void addResidentAction()
    {
        String bedID = JOptionPane.showInputDialog("Bed ID:");
        if(bedID==null) return;
        Bed bed = beds.get(bedID);
        if(bed==null){ showError("Invalid Bed"); return; }
        if(!bed.isVacant()){ showError("Occupied"); return; }

        String name = JOptionPane.showInputDialog("Resident Name:");
        String age = JOptionPane.showInputDialog("Age:");
        String gender = JOptionPane.showInputDialog("Gender (M/F):");
        if(name==null||age==null||gender==null) return;

        resident r = new resident(name,age,gender);
        bed.assignResident(r);
        saveResidentToCSV(r,bedID);
        updateBedButtons();
        log("Added resident "+name+" to "+bedID);
    }

    private void addResidentToBed(Bed bed,JButton button)
    {
        if(bed==null){ showError("Invalid Bed"); return; }
        if(!bed.isVacant()){ showError("Occupied"); return; }

        String name = JOptionPane.showInputDialog("Name:");
        if(name==null) return;
        String age = JOptionPane.showInputDialog("Age:");
        if(age==null) return;
        String gender = JOptionPane.showInputDialog("Gender:");
        if(gender==null) return;

        resident r = new resident(name,age,gender);
        bed.assignResident(r);
        saveResidentToCSV(r,bed.ID);
        updateBedButtons();
        log("Manager added resident "+name+" to "+bed.ID);
    }

    private void viewResident(Bed bed)
    {
        if(bed==null||bed.getResident()==null){ showError("No resident"); return; }
        resident r = bed.getResident();
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(r.Name).append("\n");
        sb.append("Age: ").append(r.Age).append("\n");
        sb.append("Gender: ").append(r.Gender).append("\n");
        sb.append("Prescriptions:\n");
        for(Prescription p:r.prescription){ sb.append(" - ").append(p.Name).append(" ").append(p.Dose).append(" @ ").append(p.Time).append("\n"); }
        JOptionPane.showMessageDialog(this,sb.toString(),"Resident "+bed.ID,JOptionPane.INFORMATION_MESSAGE);
    }

    private void moveResident(Bed fromBed)
    {
        if(fromBed==null||fromBed.getResident()==null){ showError("Empty"); return; }
        String toBedID=JOptionPane.showInputDialog("Dest Bed:");
        if(toBedID==null) return;
        Bed toBed=beds.get(toBedID);
        if(toBed==null||!toBed.isVacant()){ showError("Invalid/Occupied"); return; }

        toBed.assignResident(fromBed.getResident());
        fromBed.removeResident();
        saveAllResidentsToCSV();
        updateBedButtons();
        log("Moved resident from "+fromBed.ID+" to "+toBed.ID);
    }

    private void addPrescription(Bed bed)
    {
        if(bed.getResident()==null){ showError("No resident"); return; }
        String med = JOptionPane.showInputDialog("Medicine:");
        String dose = JOptionPane.showInputDialog("Dose:");
        String time = JOptionPane.showInputDialog("Time:");
        if(med==null||dose==null||time==null) return;

        Prescription p = new Prescription(med,dose,time);
        bed.getResident().addPresription(p);
        savePrescriptionToCSV(bed.ID,p);
        log(currentUser.getRole()+" prescribed "+med+" for "+bed.getResident().Name);
        updateBedButtons();
    }

    private void modifyPrescription(Bed bed)
    {
        if(bed.getResident()==null||bed.getResident().prescription.isEmpty()){ showError("None"); return; }
        resident r = bed.getResident();
        String[] meds = r.prescription.stream().map(p->p.Name).toArray(String[]::new);
        String selected = (String)JOptionPane.showInputDialog(this,"Select Prescription","Modify",
                JOptionPane.PLAIN_MESSAGE,null,meds,meds[0]);
        if(selected==null) return;

        Prescription target = r.prescription.stream().filter(p->p.Name.equals(selected)).findFirst().orElse(null);
        if(target==null) return;

        String newMed = JOptionPane.showInputDialog("Medicine:",target.Name);
        String newDose = JOptionPane.showInputDialog("Dose:",target.Dose);
        String newTime = JOptionPane.showInputDialog("Time:",target.Time);

        if(newMed!=null){ target.Name=newMed; }
        if(newDose!=null){ target.Dose=newDose; }
        if(newTime!=null){ target.Time=newTime; }

        saveAllPrescriptionsToCSV();
        log(currentUser.getRole()+" modified prescription for "+r.Name);
        JOptionPane.showMessageDialog(this,"Updated");
    }

    private void administerPrescription(Bed bed)
    {
        if(bed.getResident()==null){ showError("No resident"); return; }
        resident r = bed.getResident();
        if(r.prescription.isEmpty()){ showError("None"); return; }

        String[] meds = r.prescription.stream().map(p->p.Name+" ("+p.Dose+")").toArray(String[]::new);
        String selected = (String)JOptionPane.showInputDialog(this,"Select to Administer","Administer",
                JOptionPane.PLAIN_MESSAGE,null,meds,meds[0]);
        if(selected==null) return;

        Prescription target = null;
        for(Prescription p:r.prescription){ if(selected.startsWith(p.Name)){ target=p; break; } }
        if(target==null){ showError("Not found"); return; }

        log("Administered "+target.Name+" to "+r.Name+" by "+currentUser.getRole()+" "+currentUser.getUserID());
        saveAllPrescriptionsToCSV();
        JOptionPane.showMessageDialog(this,"Administered "+target.Name);
    }

    
    
    // CSV save/load 
    private void saveResidentToCSV(resident r,String bedID)
    {
        try(PrintWriter pw=new PrintWriter(new FileWriter(RESIDENT_FILE,true)))
        {
            pw.println(bedID+","+escapeCSV(r.Name)+","+escapeCSV(r.Age)+","+escapeCSV(r.Gender));
        }
        catch(IOException e){ e.printStackTrace(); }
    }

    private void saveAllResidentsToCSV()
    {
        try(PrintWriter pw=new PrintWriter(new FileWriter(RESIDENT_FILE)))
        {
            for(Bed bed:beds.values())
            {
                if(bed.getResident()!=null)
                {
                    resident r = bed.getResident();
                    pw.println(bed.ID+","+escapeCSV(r.Name)+","+escapeCSV(r.Age)+","+escapeCSV(r.Gender));
                }
            }
        }
        catch(IOException e){ e.printStackTrace();}
   }

    private void savePrescriptionToCSV(String bedID,Prescription p)
    {
        try(PrintWriter pw=new PrintWriter(new FileWriter(PRESCRIPTION_FILE,true)))
        {
            pw.println(bedID+","+escapeCSV(p.Name)+","+escapeCSV(p.Dose)+","+escapeCSV(p.Time));
        }
        catch(IOException e){ e.printStackTrace(); }
    }

    private void saveAllPrescriptionsToCSV()
    {
        try(PrintWriter pw=new PrintWriter(new FileWriter(PRESCRIPTION_FILE)))
        {
            for(Bed bed:beds.values())
            {
                if(bed.getResident()!=null)
                {
                    for(Prescription p:bed.getResident().prescription)
                    {
                        pw.println(bed.ID+","+escapeCSV(p.Name)+","+escapeCSV(p.Dose)+","+escapeCSV(p.Time));
                    }
                }
            }
        }
        catch(IOException e){ e.printStackTrace();}
    }

    private List<Staff> loadStaffFromCSV(String filename)
    {
        List<Staff> list = new ArrayList<>();
        try
        {
            File file = new File(filename);
            if(!file.exists()||file.length()==0)
            {
                Staff def = new Manager("M01","Default Manager","manager","1234");
                list.add(def);
                saveStaffListToCSV();
                System.out.println("Default Manager created");
                return list;
            }
            List<String> lines = Files.readAllLines(Paths.get(filename));
            for(String line:lines)
            {
                String[] p = line.split(",");
                if(p.length<5) continue;
                String id=p[0],name=p[1],role=p[2],user=p[3],pass=p[4];
                Staff s=null;
                switch(role){ case "Manager": s=new Manager(id,name,user,pass); break; case "Doctor": s=new Doctor(id,name,user,pass); break; case "Nurse": s=new Nurse(id,name,user,pass); break; }
                if(s!=null) list.add(s);
            }
        }
        catch(IOException e){ e.printStackTrace(); }
        return list;
    }

    private void loadResidentsFromCSV(String filename)
    {
        try
        {
            if(!Files.exists(Paths.get(filename))) return;
            for(String line:Files.readAllLines(Paths.get(filename)))
            {
                String[] p=line.split(",",4);
                if(p.length<4) continue;
                String bedID=p[0];
                resident r = new resident(p[1],p[2],p[3]);
                Bed bed = beds.get(bedID);
                if(bed!=null) bed.assignResident(r);
            }
        }
        catch(IOException e){ e.printStackTrace();}
    }

    private void loadPrescriptionsFromCSV(String filename)
    {
        try
        {
            if(!Files.exists(Paths.get(filename))) return;
            for(String line:Files.readAllLines(Paths.get(filename)))
            {
                String[] p=line.split(",",4);
                if(p.length<4) continue;
                String bedID=p[0];
                Bed bed = beds.get(bedID);
                if(bed!=null&&bed.getResident()!=null) bed.getResident().addPresription(new Prescription(p[1],p[2],p[3]));
            }
        }
        catch(IOException e){ e.printStackTrace();}
    }

    // Utilites
    private void log(String msg)
    {
        String entry = LocalDateTime.now()+" | "+msg;
        logArea.append(entry+"\n");
        try(PrintWriter pw=new PrintWriter(new FileWriter(LOG_FILE,true))){ pw.println(entry); }
        catch(IOException ignored){}
    }

    private void showError(String msg){ JOptionPane.showMessageDialog(this,msg,"Error",JOptionPane.ERROR_MESSAGE); }
    private String escapeCSV(String s){ if(s==null) return ""; return s.replace(",",";"); }

    // Main
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(()->new CareHomeManagementSystem().setVisible(true));
    }
}
