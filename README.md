File Structure:

FinalProject/
 ├── src/
 |    └── FinalProject/
 |         ├── ActionLog.java
 |         ├── Bed.java
 |         ├── CareHomeManagementSystem.java
 |         ├── Doctor.java
 |         ├── Main.java
 |         ├── Manager.java
 |         ├── NotRosteredException.java
 |         ├── Nurse.java
 |         ├── Prescription.java
 |         ├── resident.java   
 |         ├── Shift.java
 |         ├── Staff.java
 |         └── UnauthorizedActionException.java
 ├── test/
 |    └── FinalProject/
 |         ├── BedTest.java
 |         ├── DoctorTest.java
 |         └── NurseTest.java
 └── Libraries/
 |    ├── JavaSE-24
 |    └── JUnit 5
 └── Files
      ├── prescriptions.csv
      ├── residents.csv
      ├── staff.csv
      ├── shifts.csv
      ├── system_log.txt

RMIT Care Home Management System

This management system is built with a Java Swing-based GUI, which helps managers, doctors, and nurses perform their specific duties easily. All the information is saved in .csv and .txt files to store the data and allow for its reuse between different sessions. This program is built using Java SE 24. Java SE 24 is used to write all the classes (like Staff, resident, and Bed), handle file operations, and create a GUI. JUnit 5 is used for testing to make sure all the functions work properly.

Steps to Run the Program:

Open the program in any Java IDE
Run the CareHomeManagementSystem.java
Log in using userID: manager and password: 1234
Start adding staff and residents.

Login System:

The program starts with the login window. By default, there's one user (Username: manager and Password: 1234. The panel is shown according to the user's role after logging in. Each panel has a ward and activity log 

Manager Functions:

After login, a Manager can:
Add Staff – Create new staff accounts (Doctor, Nurse, or another Manager).
Modify Staff – Change staff details such as name, role, or password.
Add Resident (by Bed ID) – Assign a new resident to any bed.
Add Shift / Modify Shift – Assign or update work shifts for staff.
View Beds – See which beds are empty or occupied.

Doctor Functions:

Add Prescription – Add medicine for a resident.
Modify Prescription – Change existing medicine details.
View Resident – See patient information and prescriptions.
View My Shift – Check assigned shift information.

Nurse Functions:

Move Resident – Transfer a resident to another empty bed.
Administer – Record that a prescription was given.
View Resident – Check details and medicine list.
View My Shift – See their assigned working hours.

Bed & Ward System:

The system has 2 wards, each with 6 rooms and multiple beds. Every panel (Manager, Doctor, Nurse) shows bed buttons for all wards and rooms. Each bed button is clickable and performs different actions depending on the user's role.

Beds are color-coded:
Blue: Male resident
Red: Female resident
Gray: Empty bed

Logout System:

Every panel has the logout button. When clicked, it logs the action in system_log.txt file and closes the current window. Reopens the login window automatically, allowing another user to log in.

File Used:

staff.csv (Stores all staff info)
residents.csv (Stores bed assignments and resident data)
prescriptions.csv (Stores medicine information)
shifts.csv (Stores shift information)
system_log.txt (Logs all actions)

The system automatically creates default files if they do not exist. All the data is saved automatically.


References:

Oracle. (2024). How to Use Swing Components (The Java™ Tutorials).
https://docs.oracle.com/javase/tutorial/uiswing/components/

Oracle. (2024). Java I/O (Input and Output) Tutorial.
https://docs.oracle.com/javase/tutorial/essential/io/

Oracle. (2024). Object-Oriented Programming Concepts.
https://docs.oracle.com/javase/tutorial/java/concepts/

Baeldung. (2024). Introduction to Java Swing.
https://www.baeldung.com/java-swing

W3Schools. (2024). Java File Handling.
https://www.w3schools.com/java/java_files.asp

Oracle. (2025). How to Use Buttons, Check Boxes, and Radio Buttons. Oracle Swing Tutorial. https://docs.oracle.com/javase/tutorial/uiswing/components/button.html

ZetCode. (2024). Java Swing Basic Tutorial. Retrieved from https://zetcode.com/javaswing/

 
