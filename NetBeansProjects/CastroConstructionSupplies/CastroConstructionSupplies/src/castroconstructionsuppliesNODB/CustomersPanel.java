package castroconstructionsuppliesNODB;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.security.SecureRandom;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** Staff-side customer management: search, edit, delete and RESET PASSWORD. */
public class CustomersPanel extends JPanel {

    private final ArrayList<Customer> shown = new ArrayList<>();
    private final JTextField txtSearchCustomer = Theme.style(new JTextField());
    private final JTable tblCustomers = new JTable();

    public CustomersPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.SOFT);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // ----- header -----
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JLabel title = new JLabel("Customers");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.NAVY);
        JLabel subtitle = new JLabel("Manage customer accounts, update details and reset forgotten passwords.");
        subtitle.setFont(Theme.BODY);
        subtitle.setForeground(Theme.STEEL);
        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(subtitle);
        head.add(titles, BorderLayout.WEST);

        // ----- toolbar -----
        JPanel tools = new JPanel(new BorderLayout(12, 0));
        tools.setOpaque(false);
        JPanel left = new JPanel(new BorderLayout(8, 0));
        left.setOpaque(false);
        txtSearchCustomer.setPreferredSize(new java.awt.Dimension(280, 36));
        JButton btnSearch = Theme.secondaryButton("SEARCH");
        left.add(txtSearchCustomer, BorderLayout.CENTER);
        left.add(btnSearch, BorderLayout.EAST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JButton btnEdit = Theme.secondaryButton("EDIT");
        JButton btnReset = Theme.primaryButton("RESET PASSWORD");
        JButton btnDelete = Theme.dangerButton("DELETE");
        right.add(btnEdit);
        right.add(btnReset);
        right.add(btnDelete);
        tools.add(left, BorderLayout.WEST);
        tools.add(right, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout(0, 14));
        top.setOpaque(false);
        top.add(head, BorderLayout.NORTH);
        top.add(tools, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // ----- table -----
        tblCustomers.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{"Customer ID", "First Name", "Last Name", "Username", "Contact Number", "Email"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        Theme.styleTable(tblCustomers);
        tblCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCustomers.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(tblCustomers);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(java.awt.Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> refreshTable());
        txtSearchCustomer.addActionListener(e -> refreshTable());
        btnEdit.addActionListener(e -> editCustomer());
        btnReset.addActionListener(e -> resetPassword());
        btnDelete.addActionListener(e -> deleteCustomer());

        refreshTable();
    }

    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) tblCustomers.getModel();
        model.setRowCount(0);
        shown.clear();
        for (Customer c : CustomerDAO.search(txtSearchCustomer.getText().trim())) {
            shown.add(c);
            model.addRow(new Object[]{c.getCode(), c.getFirstName(), c.getLastName(),
                c.getUsername(), c.getContact(), c.getEmail()});
        }
    }

    private Customer selectedCustomer() {
        int row = tblCustomers.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.");
            return null;
        }
        return shown.get(tblCustomers.convertRowIndexToModel(row));
    }

    private void editCustomer() {
        Customer c = selectedCustomer();
        if (c == null) {
            return;
        }
        JTextField first = new JTextField(c.getFirstName());
        JTextField last = new JTextField(c.getLastName());
        JTextField contact = new JTextField(c.getContact());
        Theme.limitToPhone(contact);
        JTextField email = new JTextField(c.getEmail());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("First name:"));
        form.add(first);
        form.add(new JLabel("Last name:"));
        form.add(last);
        form.add(new JLabel("Contact number:"));
        form.add(contact);
        form.add(new JLabel("Email address:"));
        form.add(email);

        while (true) {
            if (JOptionPane.showConfirmDialog(this, form, "Edit Customer",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                return;
            }
            String f = first.getText().trim();
            String l = last.getText().trim();
            String ct = contact.getText().trim();
            String em = email.getText().trim();
            String error = null;
            if (f.isEmpty() || l.isEmpty() || ct.isEmpty() || em.isEmpty()) {
                error = "Please complete all fields.";
            } else if (!Theme.isValidPhone(ct)) {
                error = Theme.PHONE_RULE;
            } else if (!em.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                error = "Please enter a valid email address.";
            }
            if (error != null) {
                JOptionPane.showMessageDialog(this, error);
                continue;
            }
            if (CustomerDAO.update(c.getId(), f, l, ct, em)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Customer updated successfully.");
            }
            return;
        }
    }

    /** Staff resets a customer's password: type a new one, or generate a temporary one to hand over. */
    private void resetPassword() {
        Customer c = selectedCustomer();
        if (c == null) {
            return;
        }
        JPasswordField pass = new JPasswordField();
        JPasswordField confirm = new JPasswordField();
        JLabel generated = new JLabel(" ");
        generated.setForeground(Theme.ORANGE_DARK);
        JButton btnGenerate = Theme.secondaryButton("GENERATE TEMPORARY PASSWORD");
        btnGenerate.addActionListener(e -> {
            String temp = randomPassword();
            pass.setText(temp);
            confirm.setText(temp);
            generated.setText("Temporary password: " + temp + "  (tell the customer)");
        });

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Reset password for " + c.getFullName() + " (" + c.getUsername() + ")"));
        form.add(new JLabel("New password (min. 6 characters):"));
        form.add(pass);
        form.add(new JLabel("Confirm new password:"));
        form.add(confirm);
        form.add(btnGenerate);
        form.add(generated);

        while (true) {
            if (JOptionPane.showConfirmDialog(this, form, "Reset Customer Password",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                return;
            }
            String p1 = new String(pass.getPassword());
            String p2 = new String(confirm.getPassword());
            if (p1.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.");
                continue;
            }
            if (!p1.equals(p2)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.");
                continue;
            }
            if (CustomerDAO.resetPassword(c.getId(), p1)) {
                JOptionPane.showMessageDialog(this, "Password reset for " + c.getFullName() + ".");
            }
            return;
        }
    }

    private static String randomPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void deleteCustomer() {
        Customer c = selectedCustomer();
        if (c == null) {
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete customer \"" + c.getFullName() + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION
                && CustomerDAO.delete(c.getId())) {
            refreshTable();
        }
    }
}
