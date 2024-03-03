import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MainFrame extends JFrame {

    JButton loginButton = new JButton("Login");
    JButton registerButton = new JButton("Register");

    public MainFrame() {
        super("Main Menu");
        setLayout(null);
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginButton.setBounds(50, 50, 200, 30);
        registerButton.setBounds(50, 100, 200, 30);

        add(loginButton);
        add(registerButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setTitle("Login Form");
                loginFrame.setVisible(true);
                loginFrame.setBounds(10, 10, 370, 600);
                loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                loginFrame.setResizable(false);
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterFrame registerFrame = new RegisterFrame();
                registerFrame.setTitle("Registration Form");
                registerFrame.setVisible(true);
                registerFrame.setBounds(10, 10, 370, 600);
                registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                registerFrame.setResizable(false);
            }
        });
    }

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }
}
