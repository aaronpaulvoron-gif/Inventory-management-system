/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package castroconstructionsuppliesNODB;

import java.util.ArrayList;
import java.util.List;

public class StaffPanel extends javax.swing.JPanel {

    private final List<Staff> shown = new ArrayList<>();
    private boolean loading = false;

    private static final String[] POSITIONS = {
        "Owner", "Manager", "Cashier", "Sales Staff", "Warehouse Staff", "Admin"
    };

public StaffPanel() {
    initComponents();
    tblStaff.setFillsViewportHeight(true);
    tblStaff.setAutoCreateRowSorter(true);
    tblStaff.getTableHeader().setReorderingAllowed(false);

    btnSearchStaff.addActionListener(e -> refreshStaffTable());
    btnEditStaff.addActionListener(e -> editStaff());
    btnDeleteStaff.addActionListener(e -> deleteStaff());

    // POSITION DROPDOWN
    javax.swing.JComboBox<String> positionComboBox =
            new javax.swing.JComboBox<>(POSITIONS);
    tblStaff.getColumnModel()
            .getColumn(2)
            .setCellEditor(new javax.swing.DefaultCellEditor(positionComboBox));

    // STATUS DROPDOWN
    javax.swing.JComboBox<String> statusComboBox =
            new javax.swing.JComboBox<>(new String[]{"Active", "Inactive"});
    tblStaff.getColumnModel()
            .getColumn(5)
            .setCellEditor(new javax.swing.DefaultCellEditor(statusComboBox));

    // SAVE POSITION + STATUS CHANGES TO THE DATABASE
    tblStaff.getModel().addTableModelListener(e -> {
        if (loading || e.getFirstRow() < 0
                || (e.getColumn() != 2 && e.getColumn() != 5)) {
            return;
        }
        int row = e.getFirstRow();
        if (row >= shown.size()) {
            return;
        }
        Staff staff = shown.get(row);
        String position = tblStaff.getValueAt(row, 2).toString();
        String status = tblStaff.getValueAt(row, 5).toString();

        // never lock out the account that is currently logged in
        if (staff.getId() == (Session.currentStaffId == null ? -1 : Session.currentStaffId)
                && "Inactive".equals(status)) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "You cannot deactivate the account you are logged in with.");
            javax.swing.SwingUtilities.invokeLater(this::refreshStaffTable);
            return;
        }
        StaffDAO.updatePositionAndStatus(staff.getId(), position, status);
    });

    // RIGHT-CLICK > RESET PASSWORD
    javax.swing.JPopupMenu menu = new javax.swing.JPopupMenu();
    javax.swing.JMenuItem resetItem = new javax.swing.JMenuItem("Reset password...");
    resetItem.addActionListener(e -> resetStaffPassword());
    menu.add(resetItem);
    tblStaff.addMouseListener(new java.awt.event.MouseAdapter() {
        private void maybeShow(java.awt.event.MouseEvent e) {
            if (e.isPopupTrigger()) {
                int row = tblStaff.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    tblStaff.setRowSelectionInterval(row, row);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
        @Override public void mousePressed(java.awt.event.MouseEvent e) { maybeShow(e); }
        @Override public void mouseReleased(java.awt.event.MouseEvent e) { maybeShow(e); }
    });

    refreshStaffTable();
}

private void resetStaffPassword() {
    Staff staff = selectedStaff();
    if (staff == null) {
        return;
    }
    javax.swing.JPasswordField p1 = new javax.swing.JPasswordField();
    javax.swing.JPasswordField p2 = new javax.swing.JPasswordField();
    javax.swing.JPanel form = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 6, 6));
    form.add(new javax.swing.JLabel("New password for " + staff.getFullName() + " (min. 6 characters):"));
    form.add(p1);
    form.add(new javax.swing.JLabel("Confirm new password:"));
    form.add(p2);
    while (true) {
        if (javax.swing.JOptionPane.showConfirmDialog(this, form, "Reset Staff Password",
                javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE)
                != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }
        String a = new String(p1.getPassword());
        if (a.length() < 6) {
            javax.swing.JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.");
            continue;
        }
        if (!a.equals(new String(p2.getPassword()))) {
            javax.swing.JOptionPane.showMessageDialog(this, "Passwords do not match.");
            continue;
        }
        if (StaffDAO.resetPassword(staff.getId(), a)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Password reset for " + staff.getFullName() + ".");
        }
        return;
    }
}

private void addStaff() {

    javax.swing.JTextField txtFirst = new javax.swing.JTextField();
    javax.swing.JTextField txtLast = new javax.swing.JTextField();
    javax.swing.JComboBox<String> cmbPosition = new javax.swing.JComboBox<>(POSITIONS);
    javax.swing.JTextField txtContact = new javax.swing.JTextField();
    Theme.limitToPhone(txtContact);
    javax.swing.JTextField txtUser = new javax.swing.JTextField();
    javax.swing.JPasswordField txtPass = new javax.swing.JPasswordField();
    javax.swing.JPasswordField txtConfirm = new javax.swing.JPasswordField();

    javax.swing.JPanel form = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 8, 8));
    form.add(new javax.swing.JLabel("First name:"));
    form.add(txtFirst);
    form.add(new javax.swing.JLabel("Last name:"));
    form.add(txtLast);
    form.add(new javax.swing.JLabel("Position:"));
    form.add(cmbPosition);
    form.add(new javax.swing.JLabel("Contact number:"));
    form.add(txtContact);
    form.add(new javax.swing.JLabel("Username:"));
    form.add(txtUser);
    form.add(new javax.swing.JLabel("Password:"));
    form.add(txtPass);
    form.add(new javax.swing.JLabel("Confirm password:"));
    form.add(txtConfirm);

    while (true) {
        int option = javax.swing.JOptionPane.showConfirmDialog(this, form, "Add Staff",
                javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE);
        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        String first = txtFirst.getText().trim();
        String last = txtLast.getText().trim();
        String contact = txtContact.getText().trim();
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());

        String error = null;
        if (first.isEmpty() || last.isEmpty() || contact.isEmpty() || username.isEmpty() || password.isEmpty()) {
            error = "Please complete all fields.";
        } else if (!Theme.isValidPhone(contact)) {
            error = Theme.PHONE_RULE;
        } else if (password.length() < 6) {
            error = "Password must be at least 6 characters.";
        } else if (!password.equals(new String(txtConfirm.getPassword()))) {
            error = "Passwords do not match.";
        } else if (username.equalsIgnoreCase(Session.ADMIN_USERNAME)
                || StaffDAO.usernameExists(username, 0)) {
            error = "That username is already taken.";
        }

        if (error != null) {
            javax.swing.JOptionPane.showMessageDialog(this, error);
            continue;
        }

        if (StaffDAO.insert(first, last, cmbPosition.getSelectedItem().toString(), contact, username, password)) {
            refreshStaffTable();
        }
        return;
    }
}

private void refreshStaffTable() {
    javax.swing.table.DefaultTableModel model =
            (javax.swing.table.DefaultTableModel) tblStaff.getModel();

    loading = true;
    try {
        model.setRowCount(0);
        shown.clear();
        for (Staff s : StaffDAO.search(txtSearchStaff.getText().trim())) {
            shown.add(s);
            model.addRow(new Object[]{
                s.getCode(), s.getFullName(), s.getPosition(),
                s.getContact(), s.getUsername(), s.getStatus()
            });
        }
    } finally {
        loading = false;
    }
}

private Staff selectedStaff() {
    int row = tblStaff.getSelectedRow();
    if (row == -1) {
        javax.swing.JOptionPane.showMessageDialog(this, "Please select a staff member first.");
        return null;
    }
    return shown.get(tblStaff.convertRowIndexToModel(row));
}

private void editStaff() {
    Staff staff = selectedStaff();
    if (staff == null) {
        return;
    }

    String first = javax.swing.JOptionPane.showInputDialog(this, "Enter first name:", staff.getFirstName());
    if (first == null || first.trim().isEmpty()) {
        return;
    }
    String last = javax.swing.JOptionPane.showInputDialog(this, "Enter last name:", staff.getLastName());
    if (last == null || last.trim().isEmpty()) {
        return;
    }

    String contact;
    while (true) {
        contact = javax.swing.JOptionPane.showInputDialog(this, "Enter contact number:", staff.getContact());
        if (contact == null) {
            return;
        }
        contact = contact.trim();
        if (Theme.isValidPhone(contact)) {
            break;
        }
        javax.swing.JOptionPane.showMessageDialog(this,
                Theme.PHONE_RULE + "\nPlease try again.");
    }

    String username = javax.swing.JOptionPane.showInputDialog(this, "Enter username:", staff.getUsername());
    if (username == null || username.trim().isEmpty()) {
        return;
    }
    username = username.trim();
    if (username.equalsIgnoreCase(Session.ADMIN_USERNAME)
            || StaffDAO.usernameExists(username, staff.getId())) {
        javax.swing.JOptionPane.showMessageDialog(this, "That username is already taken.");
        return;
    }

    if (StaffDAO.update(staff.getId(), first.trim(), last.trim(), contact, username)) {
        refreshStaffTable();
        javax.swing.JOptionPane.showMessageDialog(this, "Staff information updated successfully.");
    }
}

private void deleteStaff() {
    Staff staff = selectedStaff();
    if (staff == null) {
        return;
    }

    if (Session.currentStaffId != null && staff.getId() == Session.currentStaffId) {
        javax.swing.JOptionPane.showMessageDialog(this,
                "You cannot delete the account you are logged in with.");
        return;
    }

    int confirm = javax.swing.JOptionPane.showConfirmDialog(
            this,
            "Delete " + staff.getFullName() + "?\nTheir past sales will keep their name.",
            "Confirm Delete",
            javax.swing.JOptionPane.YES_NO_OPTION
    );

    if (confirm == javax.swing.JOptionPane.YES_OPTION && StaffDAO.delete(staff.getId())) {
        refreshStaffTable();
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        lblStaffTitle = new javax.swing.JLabel();
        lblStaffSubtitle = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblSearchStaff = new javax.swing.JLabel();
        txtSearchStaff = new javax.swing.JTextField();
        btnSearchStaff = new javax.swing.JButton();
        btnAddStaff = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStaff = new javax.swing.JTable();
        btnDeleteStaff = new javax.swing.JButton();
        btnEditStaff = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBackground(new java.awt.Color(240, 243, 245));

        lblStaffTitle.setBackground(new java.awt.Color(204, 204, 204));
        lblStaffTitle.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        lblStaffTitle.setForeground(new java.awt.Color(18, 32, 47));
        lblStaffTitle.setText("STAFF MANAGEMENT");

        lblStaffSubtitle.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblStaffSubtitle.setForeground(new java.awt.Color(100, 100, 100));
        lblStaffSubtitle.setText("Manage employees and staff accounts");

        jPanel2.setBackground(new java.awt.Color(240, 243, 245));

        lblSearchStaff.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblSearchStaff.setForeground(new java.awt.Color(18, 32, 47));
        lblSearchStaff.setText("Search Staff:");

        txtSearchStaff.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txtSearchStaff.addActionListener(this::txtSearchStaffActionPerformed);

        btnSearchStaff.setBackground(new java.awt.Color(0, 0, 102));
        btnSearchStaff.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnSearchStaff.setForeground(new java.awt.Color(255, 255, 255));
        btnSearchStaff.setText("SEARCH");

        btnAddStaff.setBackground(new java.awt.Color(255, 153, 0));
        btnAddStaff.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnAddStaff.setForeground(new java.awt.Color(255, 255, 255));
        btnAddStaff.setText("+ ADD STAFF");
        btnAddStaff.addActionListener(this::btnAddStaffActionPerformed);

        tblStaff.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Staff ID", "Full Name ", "Position", "Contact Number ", "Username", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblStaff);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblSearchStaff)
                .addGap(49, 49, 49)
                .addComponent(txtSearchStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(btnSearchStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(229, 229, 229))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 695, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(btnAddStaff)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearchStaff)
                    .addComponent(txtSearchStaff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearchStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(btnAddStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnDeleteStaff.setBackground(new java.awt.Color(190, 50, 45));
        btnDeleteStaff.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnDeleteStaff.setForeground(new java.awt.Color(255, 255, 255));
        btnDeleteStaff.setText("DELETE");

        btnEditStaff.setBackground(new java.awt.Color(0, 0, 102));
        btnEditStaff.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        btnEditStaff.setForeground(new java.awt.Color(255, 255, 255));
        btnEditStaff.setText("EDIT");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblStaffTitle))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(lblStaffSubtitle))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(btnEditStaff)
                        .addGap(30, 30, 30)
                        .addComponent(btnDeleteStaff)))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblStaffTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStaffSubtitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDeleteStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchStaffActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchStaffActionPerformed

    private void btnAddStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddStaffActionPerformed
        addStaff();
    }//GEN-LAST:event_btnAddStaffActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddStaff;
    private javax.swing.JButton btnDeleteStaff;
    private javax.swing.JButton btnEditStaff;
    private javax.swing.JButton btnSearchStaff;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblSearchStaff;
    private javax.swing.JLabel lblStaffSubtitle;
    private javax.swing.JLabel lblStaffTitle;
    private javax.swing.JTable tblStaff;
    private javax.swing.JTextField txtSearchStaff;
    // End of variables declaration//GEN-END:variables
}
