import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegisterFrame extends JFrame implements ActionListener {

    private Map<String, String> userDatabase = new HashMap<>();


    Container container = getContentPane();
    JLabel userLabel = new JLabel("Username");
    JLabel passwordLabel = new JLabel("Password");
    JLabel dobLabel = new JLabel("Date of Birth");
    JLabel genderLabel = new JLabel("Gender");
    JLabel nameLabel = new JLabel("Name");

    JTextField userTextField = new JTextField();
    JPasswordField passwordField = new JPasswordField();

    JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
    {
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
    }

    JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"});
    JTextField nameTextField = new JTextField();

    JButton registerButton = new JButton("Register");


    RegisterFrame() {
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
    }

    public void setLayoutManager() {
        container.setLayout(null);
    }

    public void setLocationAndSize() {
        userLabel.setBounds(50, 150, 100, 30);
        passwordLabel.setBounds(50, 220, 100, 30);
        dobLabel.setBounds(50, 260, 100, 30);
        genderLabel.setBounds(50, 330, 100, 30);
        nameLabel.setBounds(50, 400, 100, 30);

        userTextField.setBounds(150, 150, 150, 30);
        passwordField.setBounds(150, 220, 150, 30);
        dateSpinner.setBounds(150, 260, 150, 30);
        genderComboBox.setBounds(150, 330, 150, 30);
        nameTextField.setBounds(150, 400, 150, 30);

        registerButton.setBounds(50, 500, 100, 30);
    }

    public void addComponentsToContainer() {
        container.add(userLabel);
        container.add(passwordLabel);
        container.add(dobLabel);
        container.add(genderLabel);
        container.add(nameLabel);
        container.add(userTextField);
        container.add(passwordField);
        container.add(dateSpinner);
        container.add(genderComboBox);
        container.add(nameTextField);
        container.add(registerButton);
    }

    public void addActionEvent() {
        registerButton.addActionListener(this);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            String username = userTextField.getText();
            String password = new String(passwordField.getPassword());

            String formattedDate = ((JSpinner.DefaultEditor) dateSpinner.getEditor()).getTextField().getText();

            java.sql.Date dob = java.sql.Date.valueOf(formattedDate);

            String gender = (String) genderComboBox.getSelectedItem();
            String name = nameTextField.getText();

            if (validateInput(username, password, dob, gender, name)) {
                addUserToDatabase(username, password, dob, gender, name);
            }
        }
    }


    private boolean validateInput(String username, String password, Date dob, String gender, String name) {
        if (username.isEmpty() || password.isEmpty() || dob == null || gender.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        if (userDatabase.containsKey(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        if (dob.toLocalDate().isAfter(java.time.LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Date of Birth cannot be later than the current date", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        return true;
    }


    private void addUserToDatabase(String username, String password, Date dob, String gender, String name) {

        String dbUrl = "jdbc:mysql://localhost:3306/hms";
        String dbUser = "root";
        String dbPassword = "sevvalK3.";

        String insertUserSql = "INSERT INTO User (username, hashedPassword, userType) VALUES (?, ?, 'Patient')";

        String insertPatientSql = "INSERT INTO Patient (userID, DOB, gender, patientName) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {

            String hashedPassword = hashPassword(password);
            if (hashedPassword == null) {
                throw new SQLException("Password hashing failed");
            }

            try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                insertUserStatement.setString(1, username);
                insertUserStatement.setString(2, hashedPassword);

                int rowsAffected = insertUserStatement.executeUpdate();

                if (rowsAffected > 0) {

                    ResultSet generatedKeys = insertUserStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int userID = generatedKeys.getInt(1);


                        try (PreparedStatement insertPatientStatement = connection.prepareStatement(insertPatientSql)) {
                            insertPatientStatement.setInt(1, userID);
                            insertPatientStatement.setDate(2, dob);
                            insertPatientStatement.setString(3, gender);
                            insertPatientStatement.setString(4, name);


                            int patientRowsAffected = insertPatientStatement.executeUpdate();


                            if (patientRowsAffected > 0) {
                                JOptionPane.showMessageDialog(this, "Registration Successful for " + username);
                            } else {
                                JOptionPane.showMessageDialog(this, "Registration failed for " + username, "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to retrieve userID for " + username, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed for " + username, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding user to database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] a) {
        RegisterFrame frame = new RegisterFrame();
        frame.setTitle("Registration Form");
        frame.setVisible(true);
        frame.setBounds(10, 10, 370, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }
}