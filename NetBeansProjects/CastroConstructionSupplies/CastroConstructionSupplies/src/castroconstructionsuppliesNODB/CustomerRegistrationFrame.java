package castroconstructionsuppliesNODB;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Customer self-registration. First name and last name are separate fields (no full name). */
public class CustomerRegistrationFrame extends JFrame {

    private final JTextField txtFirstName = Theme.style(new JTextField());
    private final JTextField txtLastName = Theme.style(new JTextField());
    private final JTextField txtUsername = Theme.style(new JTextField());
    private final JTextField txtEmail = Theme.style(new JTextField());
    private final JTextField txtContact = Theme.style(new JTextField());
    private final JPasswordField txtPassword = Theme.style(new JPasswordField());
    private final JPasswordField txtConfirmPassword = Theme.style(new JPasswordField());
    private final JLabel lblError = Theme.errorLabel();

    public CustomerRegistrationFrame() {
        setTitle("Castro Construction Supplies - Customer Registration");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.NAVY);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.NAVY);
        card.setBorder(BorderFactory.createEmptyBorder(28, 40, 28, 40));

        JLabel brand = new JLabel("CASTRO");
        brand.setFont(Theme.TITLE);
        brand.setForeground(Theme.ORANGE);
        brand.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("CONSTRUCTION SUPPLIES  |  CREATE YOUR ACCOUNT");
        sub.setFont(Theme.LABEL);
        sub.setForeground(Theme.MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        card.add(brand);
        card.add(sub);
        card.add(Box.createVerticalStrut(18));
        Theme.limitToPhone(txtContact);

        JPanel names = new JPanel(new GridLayout(1, 2, 12, 0));
        names.setOpaque(false);
        txtFirstName.setPreferredSize(new Dimension(140, 36));
        txtLastName.setPreferredSize(new Dimension(140, 36));
        names.add(Theme.fieldBlock("First name", txtFirstName));
        names.add(Theme.fieldBlock("Last name", txtLastName));
        names.setAlignmentX(LEFT_ALIGNMENT);
        names.setMaximumSize(new Dimension(Integer.MAX_VALUE, names.getPreferredSize().height));
        card.add(names);

        addRow(card, "Username", txtUsername);
        addRow(card, "Email address", txtEmail);
        addRow(card, "Contact number", txtContact);
        addRow(card, "Password (min. 6 characters)", txtPassword);
        addRow(card, "Confirm password", txtConfirmPassword);
        card.add(Box.createVerticalStrut(8));
        card.add(lblError);
        card.add(Box.createVerticalStrut(8));

        JButton btnRegister = Theme.primaryButton("REGISTER");
        JButton btnBack = Theme.secondaryButton("BACK TO LOGIN");
        btnRegister.addActionListener(e -> doRegister());
        btnBack.addActionListener(e -> backToLogin());
        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);
        buttons.add(btnRegister);
        buttons.add(btnBack);
        buttons.setAlignmentX(LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(buttons);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        getRootPane().setDefaultButton(btnRegister);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void addRow(JPanel card, String label, javax.swing.JComponent field) {
        card.add(Box.createVerticalStrut(10));
        card.add(Theme.fieldBlock(label, field));
    }

    private void doRegister() {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String contact = txtContact.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty()
                || contact.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            lblError.setText("Please complete all fields.");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            lblError.setText("Please enter a valid email address.");
            return;
        }
        if (!Theme.isValidPhone(contact)) {
            lblError.setText(Theme.PHONE_RULE);
            return;
        }
        if (password.length() < 6) {
            lblError.setText("Password must be at least 6 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            lblError.setText("Passwords do not match.");
            return;
        }
        if (CustomerDAO.usernameExists(username)) {
            lblError.setText("That username is already taken.");
            return;
        }
        Customer newCustomer = new Customer(firstName, lastName, email, username, password, contact);
        if (!CustomerDAO.insert(newCustomer)) {
            lblError.setText("Could not create the account. Please try again.");
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Registration successful!\nYou can now log in as a customer.",
                "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
        backToLogin();
    }

    private void backToLogin() {
        new LoginFrame().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new CustomerRegistrationFrame().setVisible(true));
    }
}
