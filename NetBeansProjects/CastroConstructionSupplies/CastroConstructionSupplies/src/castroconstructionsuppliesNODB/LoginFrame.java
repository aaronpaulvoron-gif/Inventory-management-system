package castroconstructionsuppliesNODB;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Login screen for the admin and customers, with Forgot Password and Register links. */
public class LoginFrame extends JFrame {

    private final JTextField txtUsername = Theme.style(new JTextField());
    private final JPasswordField txtPassword = Theme.style(new JPasswordField());
    private final JComboBox<String> cmbAccountType = Theme.style(new JComboBox<String>(new String[]{"Admin", "Customer"}));
    private final JLabel lblError = Theme.errorLabel();

    public LoginFrame() {
        setTitle("Castro Construction Supplies - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.NAVY);
        card.setBorder(BorderFactory.createEmptyBorder(32, 44, 32, 44));

        JLabel brand = new JLabel("CASTRO");
        brand.setFont(Theme.TITLE.deriveFont(30f));
        brand.setForeground(Theme.ORANGE);
        brand.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = new JLabel("CONSTRUCTION SUPPLIES  |  SYSTEM MANAGEMENT");
        sub.setFont(Theme.LABEL);
        sub.setForeground(Theme.MUTED);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        card.add(brand);
        card.add(sub);
        card.add(Box.createVerticalStrut(24));

        card.add(Theme.fieldBlock("Login as", cmbAccountType));
        card.add(Box.createVerticalStrut(12));
        card.add(Theme.fieldBlock("Username", txtUsername));
        card.add(Box.createVerticalStrut(12));
        card.add(Theme.fieldBlock("Password", txtPassword));
        card.add(Box.createVerticalStrut(6));

        JButton btnForgot = Theme.linkButton("Forgot password?");
        btnForgot.setAlignmentX(LEFT_ALIGNMENT);
        btnForgot.addActionListener(e -> forgotPassword());
        card.add(btnForgot);
        card.add(Box.createVerticalStrut(4));
        card.add(lblError);
        card.add(Box.createVerticalStrut(8));

        JButton btnLogin = Theme.primaryButton("LOGIN");
        btnLogin.addActionListener(e -> doLogin());
        btnLogin.setAlignmentX(LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));

        JButton btnRegister = Theme.secondaryButton("REGISTER AS CUSTOMER");
        JButton btnExit = Theme.dangerButton("EXIT");
        btnRegister.addActionListener(e -> {
            new CustomerRegistrationFrame().setVisible(true);
            dispose();
        });
        btnExit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit System",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        JPanel row = new JPanel(new GridLayout(1, 2, 10, 0));
        row.setOpaque(false);
        row.add(btnRegister);
        row.add(btnExit);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(row);

        setContentPane(card);
        getRootPane().setDefaultButton(btnLogin);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void forgotPassword() {
        if ("Customer".equals(cmbAccountType.getSelectedItem())) {
            new ForgotPasswordDialog(this).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "The admin password is set in the program (Session.java).\nCustomers can reset theirs here, or the admin can reset it\nfrom the Customers screen.",
                    "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String accountType = cmbAccountType.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter your username and password.");
            return;
        }

        if (accountType.equals("Admin")) {
            boolean builtInAdmin = username.equals(Session.ADMIN_USERNAME)
                    && password.equals(Session.ADMIN_PASSWORD);
            if (builtInAdmin) {
                Session.currentUserRole = Session.ROLE_STAFF;
                Session.currentUserName = "Admin";
                Session.currentStaffId = null;
                Session.currentCustomerId = null;
                openMain();
            } else {
                lblError.setText("Invalid admin username or password.");
            }
        } else {
            Customer customer = CustomerDAO.authenticate(username, password);
            if (customer != null) {
                Session.currentUserRole = Session.ROLE_CUSTOMER;
                Session.currentUserName = customer.getFullName();
                Session.currentCustomerId = customer.getId();
                Session.currentStaffId = null;
                openMain();
            } else {
                lblError.setText("Invalid customer username or password.");
            }
        }
    }

    private void openMain() {
        new MainFrame().setVisible(true);
        dispose();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
