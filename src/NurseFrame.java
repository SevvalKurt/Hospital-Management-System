import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class NurseFrame extends JFrame {

    private String nurseUsername;
    private JButton viewAssignedRoomsButton = new JButton("View Assigned Rooms");
    private JTextArea assignedRoomsTextArea = new JTextArea(15, 30);

    private JButton viewAvailableRoomsButton = new JButton("View Available Rooms");
    private JTextArea availableRoomsTextArea = new JTextArea(15, 30);

    private static final String DB_URL = "jdbc:mysql://localhost:3306/HMS";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "sevvalK3.";

    public NurseFrame(String nurseUsername) {
        this.nurseUsername = nurseUsername;
        setTitle("Nurse Dashboard - " + nurseUsername);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());
        addComponentsToContainer();
        addActionEvent();
        setVisible(true);
    }
    private void addComponentsToContainer() {
        add(viewAssignedRoomsButton);
        add(assignedRoomsTextArea);
        availableRoomsTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(availableRoomsTextArea);
        add(viewAvailableRoomsButton);
        add(scrollPane);
    }
    private void addActionEvent() {
        viewAssignedRoomsButton.addActionListener(e -> {
            List<String> assignedRooms = getAssignedRooms(nurseUsername);
            displayAssignedRooms(assignedRooms);
        });
        viewAvailableRoomsButton.addActionListener(e -> {
            List<String> availableRooms = getAvailableRooms();
            displayAvailableRooms(availableRooms);
        });
    }

    private List<String> getAssignedRooms(String nurseUsername) {
        List<String> assignedRooms = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String getNurseIdQuery = "SELECT n.nurseID " +
                    "FROM Nurse n " +
                    "JOIN User u ON n.userID = u.userID " +
                    "WHERE u.username = ?";
            int nurseId;
            try (PreparedStatement getNurseIdStatement = connection.prepareStatement(getNurseIdQuery)) {
                getNurseIdStatement.setString(1, nurseUsername);
                try (ResultSet nurseIdResultSet = getNurseIdStatement.executeQuery()) {
                    if (nurseIdResultSet.next()) {
                        nurseId = nurseIdResultSet.getInt("nurseID");

                        String getAppointmentsQuery = "SELECT a.appointmentID, r.roomID, r.roomType, a.date, a.time " +
                                "FROM Appointment a " +
                                "JOIN Room r ON a.roomID = r.roomID " +
                                "WHERE a.nurseID = ? " +
                                "ORDER BY a.date, a.time";
                        try (PreparedStatement getAppointmentsStatement = connection.prepareStatement(getAppointmentsQuery)) {
                            getAppointmentsStatement.setInt(1, nurseId);
                            try (ResultSet resultSet = getAppointmentsStatement.executeQuery()) {
                                while (resultSet.next()) {
                                    int appointmentID = resultSet.getInt("appointmentID");
                                    int roomID = resultSet.getInt("roomID");
                                    String roomType = resultSet.getString("roomType");
                                    String date = resultSet.getString("date");
                                    String time = resultSet.getString("time");
                                    assignedRooms.add("Appointment ID: " + appointmentID +
                                            ", Room ID: " + roomID +
                                            ", Room Type: " + roomType +
                                            ", Date: " + date +
                                            ", Time: " + time);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching assigned rooms", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return assignedRooms;
    }


    private void displayAssignedRooms(List<String> assignedRooms) {
        assignedRoomsTextArea.setText("");
        if (assignedRooms.isEmpty()) {
            assignedRoomsTextArea.append("No assigned rooms.");
        } else {
            for (String room : assignedRooms) {
                assignedRoomsTextArea.append(room + "\n");
            }
        }
    }
    private boolean validateLogin(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM User WHERE username = ? AND hashedPassword = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashPassword(password));
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error validating login", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    private List<String> getAvailableRooms() {
        List<String> availableRooms = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT roomID, roomType, status FROM Room WHERE status = 'Available'";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int roomID = resultSet.getInt("roomID");
                        String roomType = resultSet.getString("roomType");
                        String status = resultSet.getString("status");
                        availableRooms.add("Room ID: " + roomID + ", Room Type: " + roomType + ", Status: " + status);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching available rooms", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return availableRooms;
    }

    private void displayAvailableRooms(List<String> availableRooms) {
        availableRoomsTextArea.setText("");
        if (availableRooms.isEmpty()) {
            availableRoomsTextArea.append("No available rooms.");
        } else {
            for (String room : availableRooms) {
                availableRoomsTextArea.append(room + "\n");
            }
        }
    }
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                hexStringBuilder.append(String.format("%02x", b));
            }

            return hexStringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


}