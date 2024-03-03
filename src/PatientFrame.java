import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PatientFrame extends JFrame {
    private String patientUsername;
    private JComboBox<String> timeComboBox;
    private JButton createAppointmentButton;
    private JTextArea upcomingAppointmentsTextArea = new JTextArea(10, 30);
    private JScrollPane upcomingAppointmentsScrollPane;
    private JComboBox<String> expertiseComboBox;
    private JSpinner daysFilterSpinner;

    private JComboBox<String> expertiseFilterComboBox;
    private JList<String> appointmentsList;
    private DefaultListModel<String> appointmentsModel;
    private Map<String, Integer> appointmentIds;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/HMS";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "sevvalK3.";

    private JSpinner dateSpinner;

    public PatientFrame(String patientUsername) {
        this.patientUsername = patientUsername;
        setTitle("Patient Dashboard - " + patientUsername);
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel(new BorderLayout());
        upcomingAppointmentsTextArea.setEditable(false);
        upcomingAppointmentsScrollPane = new JScrollPane(upcomingAppointmentsTextArea);
        leftPanel.add(new JLabel("Upcoming Appointments:"), BorderLayout.NORTH);
        leftPanel.add(upcomingAppointmentsScrollPane, BorderLayout.CENTER);


        appointmentsModel = new DefaultListModel<>();
        appointmentsList = new JList<>(appointmentsModel);
        appointmentIds = new HashMap<>();
        JScrollPane appointmentsScrollPane = new JScrollPane(appointmentsList);

        JButton cancelAppointmentButton = new JButton("Cancel Appointment");
        cancelAppointmentButton.addActionListener(e -> cancelAppointment());


        leftPanel.add(appointmentsScrollPane, BorderLayout.CENTER);
        leftPanel.add(cancelAppointmentButton, BorderLayout.SOUTH);



        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));


        createSearchInterface();

        createAppointmentButton = new JButton("Create Appointment");


        createAppointmentButton.addActionListener(e -> {
            Date selectedDate = (Date) dateSpinner.getValue();
            if (selectedDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String formattedDate = sdf.format(selectedDate);

                createAppointment(formattedDate, (String) timeComboBox.getSelectedItem(), expertiseComboBox.getSelectedItem().toString());
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid date.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        daysFilterSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 365, 1));
        JButton filterButton = new JButton("Filter Appointments");
        filterButton.addActionListener(e -> showAppointments((int) daysFilterSpinner.getValue()));
        filterPanel.add(new JLabel("Show appointments for the next:"));
        filterPanel.add(daysFilterSpinner);
        filterPanel.add(new JLabel("days"));
        filterPanel.add(filterButton);

        JButton removeFilterButton = new JButton("Remove Filter");
        removeFilterButton.addActionListener(e -> showAllAppointments());


        filterPanel.add(removeFilterButton);

        JButton showPastAppointmentsButton = new JButton("Show Past Appointments");
        showPastAppointmentsButton.addActionListener(e -> showPastAppointments());


        filterPanel.add(showPastAppointmentsButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(createAppointmentButton);


        expertiseFilterComboBox = new JComboBox<>(new String[]{"Gastroenterology", "Dermatopathology", "Allergy", "Hematology", "Pediatric cardiology", "Cardiovascular Radiology", "Mental retardation psychiatry", "Pain medicine", "Female urology", "Plastic Surgery"});
        JButton filterByExpertiseButton = new JButton("Filter by Expertise");
        filterByExpertiseButton.addActionListener(e -> filterAppointmentsByExpertise());

        JPanel expertiseFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        expertiseFilterPanel.add(new JLabel("Filter by Expertise:"));
        expertiseFilterPanel.add(expertiseFilterComboBox);
        expertiseFilterPanel.add(filterByExpertiseButton);


        rightPanel.add(expertiseFilterPanel);

        rightPanel.add(filterPanel);
        rightPanel.add(buttonPanel);


        add(leftPanel);
        add(rightPanel);

        loadPatientDetails();
        showUpcomingAppointments();

        setVisible(true);
    }

    private void createSearchInterface() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridLayout(0, 2));

        expertiseComboBox = new JComboBox<>(new String[]{"Gastroenterology", "Dermatopathology", "Allergy", "Hematology", "Pediatric cardiology", "Cardiovascular Radiology", "Mental retardation psychiatry", "Pain medicine", "Female urology", "Plastic Surgery"});


        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);


        String[] timeOptions = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};
        timeComboBox = new JComboBox<>(timeOptions);

        searchPanel.add(new JLabel("Field of Expertise:"));
        searchPanel.add(expertiseComboBox);
        searchPanel.add(new JLabel("Date:"));
        searchPanel.add(dateSpinner);
        searchPanel.add(new JLabel("Time:"));
        searchPanel.add(timeComboBox);

        this.add(searchPanel, BorderLayout.NORTH);
    }

    private void loadDoctorsIntoComboBox(JComboBox<String> doctorComboBox) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM Doctor";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        doctorComboBox.addItem(resultSet.getString("doctorName"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading doctors", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPatientDetails() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM Patient P " +
                    "JOIN User U ON P.userID = U.userID " +
                    "WHERE P.patientName = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, patientUsername);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int patientID = resultSet.getInt("patientID");
                        int userID = resultSet.getInt("userID");
                        String username = resultSet.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showUpcomingAppointments() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT A.appointmentID, A.date, A.time, D.doctorName FROM Appointment A " +
                    "JOIN Doctor D ON A.doctorID = D.doctorID " +
                    "WHERE A.patientID = ? AND A.date >= CURDATE() " +
                    "ORDER BY A.date, A.time";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, getPatientID(patientUsername));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    appointmentsModel.clear();
                    appointmentIds.clear();
                    while (resultSet.next()) {
                        int appointmentId = resultSet.getInt("appointmentID");
                        String date = resultSet.getString("date");
                        String time = resultSet.getString("time");
                        String doctorName = resultSet.getString("doctorName");

                        String appointmentDetails = "ID: " + appointmentId + ", Date: " + date +
                                ", Time: " + time + ", Doctor: " + doctorName;

                        appointmentsModel.addElement(appointmentDetails);
                        appointmentIds.put(appointmentDetails, appointmentId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading upcoming appointments", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createAppointment(String date, String time, String expertise) {
        if (date == null || time == null || expertise == null) {
            JOptionPane.showMessageDialog(this, "Invalid input data", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date selectedDate = (Date) dateSpinner.getValue();
            String formattedDate = dateFormat.format(selectedDate);

            try {
                Date parsedDate = dateFormat.parse(formattedDate);
                java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

                if (parsedDate.before(new Date())) {
                    JOptionPane.showMessageDialog(this, "Cannot create an appointment for a past date.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int patientID = getPatientID(patientUsername);
                int doctorID = getDoctorID(getDoctorUsernameForAppointment(expertise));

                if (patientID == -1 || doctorID == -1) {
                    JOptionPane.showMessageDialog(this, "Invalid patient or doctor ID", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (isAppointmentSlotTaken(sqlDate, time, doctorID)) {
                    JOptionPane.showMessageDialog(this, "This time slot is already taken. Please choose another time.", "Time Slot Unavailable", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "INSERT INTO Appointment (date, time, patientID, doctorID) VALUES (?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setDate(1, sqlDate);
                    statement.setString(2, time);
                    statement.setInt(3, patientID);
                    statement.setInt(4, doctorID);
                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Appointment created successfully.");
                        showUpcomingAppointments();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to create appointment.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error parsing date", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating appointment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterAppointmentsByExpertise() {
        String selectedExpertise = (String) expertiseFilterComboBox.getSelectedItem();

        if (selectedExpertise == null || selectedExpertise.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an expertise.", "No Expertise Selected", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT A.appointmentID, A.date, A.time, D.doctorName FROM Appointment A " +
                    "JOIN Doctor D ON A.doctorID = D.doctorID " +
                    "WHERE A.patientID = ? AND D.fieldOfExpertise = ? " +
                    "ORDER BY A.date, A.time";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, getPatientID(patientUsername));
                preparedStatement.setString(2, selectedExpertise);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    appointmentsModel.clear();
                    appointmentIds.clear();

                    while (resultSet.next()) {
                        int appointmentId = resultSet.getInt("appointmentID");
                        String date = resultSet.getString("date");
                        String time = resultSet.getString("time");
                        String doctorName = resultSet.getString("doctorName");


                        String appointmentDetails = "ID: " + appointmentId + ", Date: " + date +
                                ", Time: " + time + ", Doctor: " + doctorName;

                        appointmentsModel.addElement(appointmentDetails);
                        appointmentIds.put(appointmentDetails, appointmentId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading appointments by expertise", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void cancelAppointment() {
        String selectedAppointment = appointmentsList.getSelectedValue();
        if (selectedAppointment == null) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.", "No Selection", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int appointmentId = appointmentIds.get(selectedAppointment);
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            String checkQuery = "SELECT date, time FROM Appointment WHERE appointmentID = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setInt(1, appointmentId);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        java.sql.Date appointmentDate = resultSet.getDate("date");
                        Time appointmentTime = resultSet.getTime("time");
                        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate.toLocalDate(), appointmentTime.toLocalTime());

                        if (appointmentDateTime.isBefore(LocalDateTime.now().plusDays(1)))
                        {
                            JOptionPane.showMessageDialog(this, "Cannot cancel appointments less than 24 hours in advance.", "Cancellation Not Allowed", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
            }

            String deleteQuery = "DELETE FROM Appointment WHERE appointmentID = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setInt(1, appointmentId);
                int rowsAffected = deleteStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Appointment cancelled successfully.");
                    showUpcomingAppointments();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel appointment.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cancelling appointment: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAllAppointments() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM Appointment A " +
                    "JOIN Doctor D ON A.doctorID = D.doctorID " +
                    "WHERE A.patientID = ? " +
                    "ORDER BY A.date, A.time";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, getPatientID(patientUsername));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    StringBuilder appointments = new StringBuilder();
                    while (resultSet.next()) {
                        appointments.append("Date: ").append(resultSet.getString("date"))
                                .append(", Time: ").append(resultSet.getString("time"))
                                .append(", Doctor: ").append(resultSet.getString("doctorName"))
                                .append("\n");
                    }
                    upcomingAppointmentsTextArea.setText(appointments.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading appointments", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isAppointmentSlotTaken(java.sql.Date date, String time, int doctorID) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT COUNT(*) FROM Appointment WHERE date = ? AND time = ? AND doctorID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDate(1, date);
                preparedStatement.setString(2, time);
                preparedStatement.setInt(3, doctorID);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int getPatientID(String username) {
        int patientID = -1;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT patientID FROM Patient WHERE userID = (SELECT userID FROM User WHERE username = ? AND userType = 'Patient')";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        patientID = resultSet.getInt("patientID");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patientID;
    }

    private int getDoctorID(String username) {
        int doctorID = -1;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT doctorID FROM Doctor WHERE userID = (SELECT userID FROM User WHERE username = ? AND userType = 'Doctor')";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        doctorID = resultSet.getInt("doctorID");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctorID;
    }

    private String getDoctorUsernameForAppointment(String expertise) {
        String username = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT userID FROM Doctor WHERE fieldOfExpertise = ? LIMIT 1";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, expertise);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int userID = resultSet.getInt("userID");
                        // Now fetch the username from the User table
                        query = "SELECT username FROM User WHERE userID = ? AND userType = 'Doctor'";
                        try (PreparedStatement psUser = connection.prepareStatement(query)) {
                            psUser.setInt(1, userID);
                            try (ResultSet rsUser = psUser.executeQuery()) {
                                if (rsUser.next()) {
                                    username = rsUser.getString("username");
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }
    private void showAppointmentsForDays(int days) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM Appointment A " +
                    "JOIN Doctor D ON A.doctorID = D.doctorID " +
                    "WHERE A.patientID = ? AND A.date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                    "ORDER BY A.date, A.time";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, getPatientID(patientUsername));
                preparedStatement.setInt(2, days);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    StringBuilder appointments = new StringBuilder();
                    while (resultSet.next()) {
                        appointments.append("Date: ").append(resultSet.getString("date"))
                                .append(", Time: ").append(resultSet.getString("time"))
                                .append(", Doctor: ").append(resultSet.getString("doctorName"))
                                .append("\n");
                    }
                    upcomingAppointmentsTextArea.setText(appointments.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading appointments", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showPastAppointments() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT A.appointmentID, A.date, A.time, D.doctorName FROM Appointment A " +
                    "JOIN Doctor D ON A.doctorID = D.doctorID " +
                    "WHERE A.patientID = ? AND A.date < CURDATE() " +
                    "ORDER BY A.date DESC, A.time DESC";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, getPatientID(patientUsername));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    appointmentsModel.clear();
                    appointmentIds.clear();

                    while (resultSet.next()) {
                        int appointmentId = resultSet.getInt("appointmentID");
                        String date = resultSet.getString("date");
                        String time = resultSet.getString("time");
                        String doctorName = resultSet.getString("doctorName");


                        String appointmentDetails = "ID: " + appointmentId + ", Date: " + date +
                                ", Time: " + time + ", Doctor: " + doctorName;

                        appointmentsModel.addElement(appointmentDetails);
                        appointmentIds.put(appointmentDetails, appointmentId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading past appointments", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAppointments(int days) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM Appointment A " +
                    "JOIN Doctor D ON A.doctorID = D.doctorID " +
                    "WHERE A.patientID = ? AND A.date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                    "ORDER BY A.date, A.time";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, getPatientID(patientUsername));
                preparedStatement.setInt(2, days);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    StringBuilder appointments = new StringBuilder();
                    while (resultSet.next()) {
                        appointments.append("Date: ").append(resultSet.getString("date"))
                                .append(", Time: ").append(resultSet.getString("time"))
                                .append(", Doctor: ").append(resultSet.getString("doctorName"))
                                .append("\n");
                    }
                    upcomingAppointmentsTextArea.setText(appointments.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading appointments", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}