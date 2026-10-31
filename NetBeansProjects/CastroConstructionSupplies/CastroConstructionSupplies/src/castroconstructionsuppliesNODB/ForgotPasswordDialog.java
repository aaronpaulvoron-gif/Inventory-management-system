package castroconstructionsuppliesNODB;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Customer "Forgot password" screen. The customer proves who they are with the username,
 * email and contact number they registered with, then picks a new password.
 * (If they cannot do that, a staff member can reset it from Customers > Reset Password.)
 */
public class ForgotPasswordDialog extends JDialog {

    private final JTextField txtUsername = Theme.style(new JTextField());
    private final JTextField txtEmail = Theme.style(new JTextField());
    private final JTextField txtContact = Theme.style(new JTextField());
    private final JPasswordField txtNew = Theme.style(new JPasswordField());
    private final JPasswordField txtConfirm = Theme.style(new JPasswordField());
    private final JLabel lblError = Theme.errorLabel();

    public ForgotPasswordDialog(Window owner) {
        super(owner instanceof Frame ? (Frame) owner : null, "Forgot Password", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Theme.limitToPhone(txtContact);
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Theme.NAVY);
        root.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel title = new JLabel("RESET YOUR PASSWORD");
        title.setFont(Theme.HEADING);
        title.setForeground(Theme.ORANGE);
        title.setAlignmentX(LEFT_ALIGNMENT);
        JLabel hint = new JLabel("<html>Enter the username, email and contact number you registered with.<br>"
                + "Can't remember them? Ask the store admin to reset it for you.</html>");
        hint.setForeground(Theme.MUTED);
        hint.setFont(Theme.BODY);
        hint.setAlignmentX(LEFT_ALIGNMENT);

        root.add(title);
        root.add(javax.swing.Box.createVerticalStrut(6));
        root.add(hint);
        root.add(javax.swing.Box.createVerticalStrut(16));
        root.add(Theme.fieldBlock("Username", txtUsername));
        root.add(javax.swing.Box.createVerticalStrut(10));
        root.add(Theme.fieldBlock("Email address", txtEmail));
        root.add(javax.swing.Box.createVerticalStrut(10));
        root.add(Theme.fieldBlock("Contact number", txtContact));
        root.add(javax.swing.Box.createVerticalStrut(10));
        root.add(Theme.fieldBlock("New password (min. 6 characters)", txtNew));
        root.add(javax.swing.Box.createVerticalStrut(10));
        root.add(Theme.fieldBlock("Confirm new password", txtConfirm));
        root.add(javax.swing.Box.createVerticalStrut(8));
        root.add(lblError);
        root.add(javax.swing.Box.createVerticalStrut(8));

        JButton btnReset = Theme.primaryButton("RESET PASSWORD");
        JButton btnCancel = Theme.secondaryButton("CANCEL");
        btnReset.addActionListener(e -> doReset());
        btnCancel.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);
        buttons.add(btnReset);
        buttons.add(btnCancel);
        buttons.setAlignmentX(LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        root.add(buttons);

        setContentPane(root);
        getRootPane().setDefaultButton(btnReset);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void doReset() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String contact = txtContact.getText().trim();
        String pass = new String(txtNew.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (username.isEmpty() || email.isEmpty() || contact.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            lblError.setText("Please complete all fields.");
            return;
        }
        if (!Theme.isValidPhone(contact)) {
            lblError.setText(Theme.PHONE_RULE);
            return;
        }
        if (pass.length() < 6) {
            lblError.setText("Password must be at least 6 characters.");
            return;
        }
        if (!pass.equals(confirm)) {
            lblError.setText("Passwords do not match.");
            return;
        }
        Customer c = CustomerDAO.findForRecovery(username, email, contact);
        if (c == null) {
            lblError.setText("Those details don't match a customer account.");
            return;
        }
        if (!CustomerDAO.resetPassword(c.getId(), pass)) {
            lblError.setText("Could not reset the password. Please try again.");
            return;
        }
        JOptionPane.showMessageDialog(this, "Your password has been reset.\nYou can now log in with the new password.",
                "Password Reset", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
