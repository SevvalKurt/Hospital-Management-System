import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DoctorFrame extends JFrame implements ActionListener {

    private String doctorUsername;
    private JButton listAvailabilityButton = new JButton("List Available Rooms");
    private JTextArea availabilityTextArea = new JTextArea(15, 15);
    private JList<String> availableRoomsList;
    private DefaultListModel<String> availableRoomsListModel;
    private JScrollPane availableRoomsScrollPane;

    private JComboBox<String> appointmentComboBox;
    private JComboBox<String> roomComboBox;
    private JComboBox<String> nurseComboBox;
    private JButton assignRoomButton;

    private JSpinner dateSpinner;
    private JComboBox<String> timeComboBox;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/hms";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "sevvalK3.";

    public DoctorFrame(String doctorUsername) {
        this.doctorUsername = doctorUsername;
        setTitle("Doctor Dashboard - " + doctorUsername);
        setSize(1300, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel(new BorderLayout());
        availableRoomsListModel = new DefaultListModel<>();
        availableRoomsList = new JList<>(availableRoomsListModel);
        availableRoomsScrollPane = new JScrollPane(availableRoomsList);
        leftPanel.add(listAvailabilityButton, BorderLayout.NORTH);
        leftPanel.add(availableRoomsScrollPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridBagLayout());

        appointmentComboBox = new JComboBox<>();
        roomComboBox = new JComboBox<>();
        nurseComboBox = new JComboBox<>();
        assignRoomButton = new JButton("Assign Room");
        assignRoomButton.addActionListener(this);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        String[] timeOptions = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};
        timeComboBox = new JComboBox<>(timeOptions);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        rightPanel.add(new JLabel("Select Appointment (for assigning):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        rightPanel.add(appointmentComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        rightPanel.add(new JLabel("Select Room (for assigning):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        rightPanel.add(roomComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        rightPanel.add(new JLabel("Select Nurse (for assigning):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        rightPanel.add(nurseComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        rightPanel.add(new JLabel("Select Date (for listing availability):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        rightPanel.add(dateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        rightPanel.add(new JLabel("Select Time (for listing availability):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        rightPanel.add(timeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Make it span two columns
        rightPanel.add(assignRoomButton, gbc);

        add(leftPanel);
        add(rightPanel);

        listAvailabilityButton.addActionListener(this);
        populateAppointmentComboBox();
        populateRoomComboBox();
        populateNurseComboBox();

        setVisible(true);
    }
    private void setLayoutManagerForAssignment() {
        setLayout(new FlowLayout());
    }

    private void setLocationAndSizeForAssignment() {
        appointmentComboBox.setBounds(50, 400, 200, 30);
        roomComboBox.setBounds(50, 450, 200, 30);
        nurseComboBox.setBounds(50, 500, 200, 30);
        assignRoomButton.setBounds(50, 550, 150, 30);
    }

    private void addComponentsToContainer() {
        add(listAvailabilityButton);
        add(availabilityTextArea);
        add(appointmentComboBox);
        add(roomComboBox);
        add(nurseComboBox);
        add(assignRoomButton);
    }

    private void addActionEvent() {
        listAvailabilityButton.addActionListener(this);
        assignRoomButton.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == listAvailabilityButton) {
            List<String> availableRooms = getAvailableRooms();
            updateAvailableRoomsList(availableRooms);
        } else if (e.getSource() == assignRoomButton) {
            String selectedAppointment = (String) appointmentComboBox.getSelectedItem();
            String selectedRoom = (String) roomComboBox.getSelectedItem();
            String selectedNurse = (String) nurseComboBox.getSelectedItem();
            int appointmentID = extractAppointmentID(selectedAppointment);
            int roomID = extractRoomID(selectedRoom);
            int nurseID = extractNurseID(selectedNurse);
            assignRoomAndNurseToAppointment(appointmentID, roomID, nurseID);
        }
    }

    private void updateAvailableRoomsList(List<String> availableRooms) {
        availableRoomsListModel.clear();
        for (String room : availableRooms) {
            availableRoomsListModel.addElement(room);
        }
    }

    private List<String> getAvailableRooms() {
        List<String> availableRooms = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT DISTINCT r.roomID, r.roomType FROM Room r " +
                    "LEFT JOIN Availability a ON r.roomID = a.roomID AND a.date = ? AND a.availableHours = ? " +
                    "WHERE (a.status = true OR a.status IS NULL)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                Date selectedDate = (Date) dateSpinner.getValue();
                java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

                preparedStatement.setDate(1, sqlDate);
                preparedStatement.setString(2, (String) timeComboBox.getSelectedItem());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int roomID = resultSet.getInt("roomID");
                        String roomType = resultSet.getString("roomType");
                        availableRooms.add("Room ID: " + roomID + ", Type: " + roomType);
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
        availabilityTextArea.setText("");
        if (availableRooms.isEmpty()) {
            availabilityTextArea.append("No available rooms.");
        } else {
            for (String room : availableRooms) {
                availabilityTextArea.append("Room: " + room + "\n");
            }
        }
    }

    private void populateNurseComboBox() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT nurseID, nurseName FROM Nurse";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int nurseID = resultSet.getInt("nurseID");
                    String nurseName = resultSet.getString("nurseName");
                    nurseComboBox.addItem("Nurse " + nurseID + " - " + nurseName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void populateAppointmentComboBox() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT appointmentID, date, time FROM Appointment";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int appointmentID = resultSet.getInt("appointmentID");
                    Date date = resultSet.getDate("date");
                    String time = resultSet.getString("time");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String formattedDateTime = sdf.format(date) + " " + time;
                    appointmentComboBox.addItem("Appointment " + appointmentID + " - " + formattedDateTime);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void populateRoomComboBox() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT roomID, roomType FROM Room";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    roomComboBox.addItem("Room " + resultSet.getInt("roomID") + " - " + resultSet.getString("roomType"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int extractAppointmentID(String selectedAppointment) {
        return Integer.parseInt(selectedAppointment.split(" ")[1]);
    }

    private int extractRoomID(String selectedRoom) {
        return Integer.parseInt(selectedRoom.split(" ")[1]);
    }

    private int extractNurseID(String selectedNurse) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String nurseName = selectedNurse.substring(selectedNurse.indexOf("-") + 1).trim();
            String query = "SELECT nurseID FROM Nurse WHERE nurseName = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, nurseName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("nurseID");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void assignRoomAndNurseToAppointment(int appointmentID, int roomID, int nurseID) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (isDateTimeInPast(appointmentID, connection)) {
                JOptionPane.showMessageDialog(this, "Cannot assign room and nurse to a past appointment", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isNurseAvailable(appointmentID, nurseID, connection)) {
                JOptionPane.showMessageDialog(this, "Selected nurse is not available at the chosen date and time", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isRoomAvailable(appointmentID, roomID, connection)) {
                JOptionPane.showMessageDialog(this, "Selected room is not available at the chosen date and time", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String updateQuery = "UPDATE Appointment SET roomID = ?, nurseID = ? WHERE appointmentID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, roomID);
                preparedStatement.setInt(2, nurseID);
                preparedStatement.setInt(3, appointmentID);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Room and nurse assigned successfully");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to assign room and nurse", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error assigning room and nurse", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private boolean isDateTimeInPast(int appointmentID, Connection connection) {
        try {
            String query = "SELECT date, time FROM Appointment WHERE appointmentID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, appointmentID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Date appointmentDate = resultSet.getDate("date");
                        String appointmentTime = resultSet.getString("time");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date appointmentDateTime = sdf.parse(appointmentDate.toString() + " " + appointmentTime);
                        return new Date().after(appointmentDateTime);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isNurseAvailable(int appointmentID, int nurseID, Connection connection) {
        try {
            String query = "SELECT av.date, av.availableHours, av.status FROM Appointment a " +
                    "LEFT JOIN Availability av ON a.nurseID = av.nurseID AND a.date = av.date AND a.time = av.availableHours " +
                    "WHERE a.nurseID = ? AND a.appointmentID <> ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, nurseID);
                preparedStatement.setInt(2, appointmentID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        boolean isAvailable = resultSet.getBoolean("status");
                        return !isAvailable;
                    } else {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean isRoomAvailable(int appointmentID, int roomID, Connection connection) {
        try {
            String query = "SELECT av.date, av.availableHours, av.status FROM Appointment a " +
                    "LEFT JOIN Availability av ON a.roomID = av.roomID AND a.date = av.date AND a.time = av.availableHours " +
                    "WHERE a.roomID = ? AND a.appointmentID <> ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, roomID);
                preparedStatement.setInt(2, appointmentID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        boolean isAvailable = resultSet.getBoolean("status");
                        return !isAvailable;
                    } else {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isOverlapping(Date newDate, String newTime, Date existingDateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date newDateTime;
        try {
            newDateTime = sdf.parse(newDate.toString() + " " + newTime);
            return (newDateTime.equals(existingDateTime) || newDateTime.before(existingDateTime) && existingDateTime.before(newDateTime));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


}