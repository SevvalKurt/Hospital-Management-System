import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminFrame extends JFrame implements ActionListener {

    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JComboBox<String> userTypeComboBox;
    private JComboBox<String> specializationComboBox;
    private JButton addButton = new JButton("Add Medical Staff");
    private JButton patientStatisticsButton = new JButton("Patient Statistics");
    private JButton roomAppointmentRatioButton = new JButton("Room Appointment Ratio");
    private JButton nurseToRoomRatioButton = new JButton("Nurse to Room Ratio");
    private JButton mostBookedRoomButton = new JButton("Most Booked Room");

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hms";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "sevvalK3.";

    private String selectedSpecialization = "";

    public AdminFrame(String adminUsername) {
        setLayout(new FlowLayout());
        setTitle("Admin Dashboard - " + adminUsername);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        addComponentsToContainer();
        addActionEvent();
        pack();
        setSize(400, 250);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addComponentsToContainer() {
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel userTypeLabel = new JLabel("User Type:");
        JLabel specializationLabel = new JLabel("Specialization:");

        usernameLabel.setPreferredSize(new Dimension(100, 30));
        passwordLabel.setPreferredSize(new Dimension(100, 30));
        userTypeLabel.setPreferredSize(new Dimension(100, 30));
        specializationLabel.setPreferredSize(new Dimension(100, 30));

        usernameField.setPreferredSize(new Dimension(150, 30));
        passwordField.setPreferredSize(new Dimension(150, 30));

        String[] userTypes = {"Doctor", "Nurse"};
        userTypeComboBox = new JComboBox<>(userTypes);
        userTypeComboBox.setPreferredSize(new Dimension(150, 30));
        userTypeComboBox.addActionListener(this);


        String[] specializations = {
                "Gastroenterology", "Dermatopathology", "Allergy", "Hematology",
                "Pediatric cardiology", "Cardiovascular Radiology",
                "Mental retardation psychiatry", "Pain medicine",
                "Female urology", "Plastic Surgery"
        };

        specializationComboBox = new JComboBox<>(specializations);
        specializationComboBox.setPreferredSize(new Dimension(150, 30));
        specializationComboBox.setEnabled(false);

        addButton.setPreferredSize(new Dimension(150, 30));

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(userTypeLabel);
        add(userTypeComboBox);
        add(specializationLabel);
        add(specializationComboBox);
        add(addButton);
        add(patientStatisticsButton);
        add(roomAppointmentRatioButton);
        add(nurseToRoomRatioButton);
        add(mostBookedRoomButton);
    }

    private void addActionEvent() {

        addButton.addActionListener(this);
        patientStatisticsButton.addActionListener(this);
        roomAppointmentRatioButton.addActionListener(this);
        nurseToRoomRatioButton.addActionListener(this);
        mostBookedRoomButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String userType = (String) userTypeComboBox.getSelectedItem();
            selectedSpecialization = (String) specializationComboBox.getSelectedItem();

            if (isValidInput(username, password)) {
                if (addMedicalStaff(username, password, userType, selectedSpecialization)) {
                    JOptionPane.showMessageDialog(this, "Medical Staff added successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error adding Medical Staff.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == userTypeComboBox) {

            String selectedUserType = (String) userTypeComboBox.getSelectedItem();


            if ("Doctor".equals(selectedUserType)) {
                specializationComboBox.setEnabled(true);
            } else {
                specializationComboBox.setEnabled(false);
                selectedSpecialization = "";
            }
        }
        if (e.getSource() == patientStatisticsButton) {
            retrievePatientStatistics();
        } else if (e.getSource() == roomAppointmentRatioButton) {
            retrieveRoomAppointmentRatio();
        } else if (e.getSource() == nurseToRoomRatioButton) {
            retrieveNurseToRoomRatio();
        } else if (e.getSource() == mostBookedRoomButton) {
            retrieveMostBookedRoom();
        }

    }
    private void retrievePatientStatistics() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT COUNT(*) AS totalPatients FROM Patient";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int totalPatients = resultSet.getInt("totalPatients");
                    JOptionPane.showMessageDialog(this, "Total Patients: " + totalPatients);
                } else {
                    JOptionPane.showMessageDialog(this, "No data available", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void retrieveRoomAppointmentRatio() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT D.depName, COUNT(A.appointmentID) AS totalAppointments, " +
                    "COUNT(A.roomID) AS appointmentsInRoom " +
                    "FROM Department D " +
                    "LEFT JOIN Doctor Doc ON D.departmentID = Doc.departmentID " +
                    "LEFT JOIN Appointment A ON Doc.doctorID = A.doctorID " +
                    "GROUP BY D.departmentID";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                StringBuilder result = new StringBuilder("Room Appointment Ratios:\n");
                while (resultSet.next()) {
                    String department = resultSet.getString("depName");
                    int totalAppointments = resultSet.getInt("totalAppointments");
                    int appointmentsInRoom = resultSet.getInt("appointmentsInRoom");
                    double ratio = (totalAppointments > 0) ? ((double) appointmentsInRoom / totalAppointments) * 100 : 0;

                    result.append(department).append(": ").append(String.format("%.2f%%", ratio)).append("\n");
                }
                JOptionPane.showMessageDialog(this, result.toString());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void retrieveNurseToRoomRatio() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT COUNT(DISTINCT N.nurseID) AS totalNurses, " +
                    "COUNT(DISTINCT R.roomID) AS totalRooms " +
                    "FROM Nurse N " +
                    "JOIN Appointment A ON N.nurseID = A.nurseID " +
                    "JOIN Room R ON A.roomID = R.roomID";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int totalNurses = resultSet.getInt("totalNurses");
                    int totalRooms = resultSet.getInt("totalRooms");
                    double ratio = (totalRooms > 0) ? (double) totalNurses / totalRooms : 0;
                    JOptionPane.showMessageDialog(this, "Nurse to Room Ratio: " + String.format("%.2f", ratio));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private void retrieveMostBookedRoom() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT D.depName, R.roomType, COUNT(A.appointmentID) AS totalAppointments " +
                    "FROM Department D " +
                    "JOIN Room R ON D.departmentID = R.departmentID " +
                    "JOIN Appointment A ON R.roomID = A.roomID " +
                    "GROUP BY D.departmentID, R.roomID " +
                    "ORDER BY totalAppointments DESC " +
                    "LIMIT 1";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String department = resultSet.getString("depName");
                    String roomType = resultSet.getString("roomType");
                    int totalAppointments = resultSet.getInt("totalAppointments");

                    JOptionPane.showMessageDialog(this, "Most Booked Room:\n" +
                            "Department: " + department + "\n" +
                            "Room Type: " + roomType + "\n" +
                            "Total Appointments: " + totalAppointments);
                } else {
                    JOptionPane.showMessageDialog(this, "No data available", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private boolean isValidInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    private boolean addMedicalStaff(String username, String password, String userType, String specialization) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            connection.setAutoCommit(false);

            String userInsertQuery = "INSERT INTO User (username, hashedPassword, userType) VALUES (?, ?, ?)";
            String doctorInsertQuery = "INSERT INTO Doctor (userID, doctorName, fieldOfExpertise) VALUES (?, ?, ?)";
            String nurseInsertQuery = "INSERT INTO Nurse (userID, nurseName) VALUES (?, ?)";

            try (PreparedStatement userPreparedStatement = connection.prepareStatement(userInsertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement doctorPreparedStatement = connection.prepareStatement(doctorInsertQuery);
                 PreparedStatement nursePreparedStatement = connection.prepareStatement(nurseInsertQuery)) {

                userPreparedStatement.setString(1, username);
                userPreparedStatement.setString(2, password);
                userPreparedStatement.setString(3, userType);

                if ("Doctor".equals(userType)) {
                    int affectedRows = userPreparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        connection.rollback();
                        return false;
                    }

                    try (ResultSet generatedKeys = userPreparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int userID = generatedKeys.getInt(1);
                            doctorPreparedStatement.setInt(1, userID);
                            doctorPreparedStatement.setString(2, username);
                            doctorPreparedStatement.setString(3, specialization);
                            int doctorAffectedRows = doctorPreparedStatement.executeUpdate();

                            if (doctorAffectedRows > 0) {
                                connection.commit();
                                return true;
                            } else {
                                connection.rollback();
                                return false;
                            }
                        } else {
                            connection.rollback();
                            return false;
                        }
                    }
                } else if ("Nurse".equals(userType)) {
                    int affectedRows = userPreparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        connection.rollback();
                        return false;
                    }

                    try (ResultSet generatedKeys = userPreparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int userID = generatedKeys.getInt(1);
                            nursePreparedStatement.setInt(1, userID);
                            nursePreparedStatement.setString(2, username);
                            int nurseAffectedRows = nursePreparedStatement.executeUpdate();

                            if (nurseAffectedRows > 0) {
                                connection.commit();
                                return true;
                            } else {
                                connection.rollback();
                                return false;
                            }
                        } else {
                            connection.rollback();
                            return false;
                        }
                    }
                } else {
                    connection.rollback();
                    return false;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminFrame("Admin");
        });
    }
}