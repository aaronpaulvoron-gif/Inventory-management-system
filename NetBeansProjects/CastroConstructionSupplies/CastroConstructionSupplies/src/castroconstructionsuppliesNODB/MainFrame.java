package castroconstructionsuppliesNODB;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author USER
 */
public class MainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
    initComponents();

    // Navigation buttons that were not wired in the form designer
    btnCustomers.addActionListener(e -> showPanel(new CustomersPanel()));
    btnSales.addActionListener(e -> showPanel(new SalesPanel()));
    btnLogout.addActionListener(e -> logout());

    fixMainLayout();
    applyRoleAccess();

    setSize(1200, 750);
    setLocationRelativeTo(null);

    showPanel(createDashboard());
}

/** Staff get the management dashboard, customers get their own. */
private javax.swing.JPanel createDashboard() {
    return Session.isCustomer() ? new CustomerDashboardPanel() : new DashboardPanel();
}


/** Customers only see Dashboard, Products and their orders. */
private void applyRoleAccess() {
    jLabel1.setText("SYSTEM MANAGEMENT  |  Logged in as: " + Session.currentUserName
            + (Session.isCustomer() ? " (" + Session.currentUserRole + ")" : ""));

    if (Session.isCustomer()) {
        btnSales.setText("PLACE ORDER");
        // tell the customer about any order staff cancelled (and why) once the window is showing
        javax.swing.SwingUtilities.invokeLater(() -> CustomerNoticesDialog.showIfAny(this));

        // Rebuild the sidebar with only the customer's buttons so there
        // are no empty gaps where staff-only buttons used to be.
        pnlSidebar.removeAll();
        pnlSidebar.add(btnDashboard);
        pnlSidebar.add(javax.swing.Box.createVerticalStrut(35));
        pnlSidebar.add(btnProducts);
        pnlSidebar.add(javax.swing.Box.createVerticalStrut(35));
        pnlSidebar.add(btnSales);
        pnlSidebar.add(javax.swing.Box.createVerticalGlue());
        pnlSidebar.add(btnLogout);
        pnlSidebar.revalidate();
        pnlSidebar.repaint();
    }
}

private void logout() {
    int answer = javax.swing.JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out?",
            "Logout",
            javax.swing.JOptionPane.YES_NO_OPTION
    );

    if (answer == javax.swing.JOptionPane.YES_OPTION) {
        Session.currentUserName = "Guest";
        Session.currentUserRole = Session.ROLE_STAFF;
        Session.currentStaffId = null;
        Session.currentCustomerId = null;
        new LoginFrame().setVisible(true);
        this.dispose();
    }
}

public void showPanel(javax.swing.JPanel panel) {
    pnlContent.removeAll();
    pnlContent.setLayout(new java.awt.BorderLayout());
    pnlContent.add(panel, java.awt.BorderLayout.CENTER);
    pnlContent.revalidate();
    pnlContent.repaint();
    
}
private void fixMainLayout() {

    // =========================================
    // CLEAR THE GENERATED NETBEANS LAYOUT
    // =========================================
    getContentPane().removeAll();

    jPanel1.removeAll();
    jPanel1.setLayout(new java.awt.BorderLayout());
    jPanel1.setBackground(new java.awt.Color(18, 32, 47));


    // =========================================
    // SIDEBAR
    // =========================================

    pnlSidebar.removeAll();

    pnlSidebar.setLayout(new javax.swing.BoxLayout(
            pnlSidebar,
            javax.swing.BoxLayout.Y_AXIS
    ));

    pnlSidebar.setBackground(new java.awt.Color(18, 32, 47));

    pnlSidebar.setPreferredSize(
            new java.awt.Dimension(250, 0)
    );

    pnlSidebar.setBorder(
            javax.swing.BorderFactory.createEmptyBorder(
                    40, 15, 25, 15
            )
    );


    // Button sizes
    java.awt.Dimension buttonSize =
            new java.awt.Dimension(220, 50);

    btnDashboard.setMaximumSize(buttonSize);
    btnDashboard.setPreferredSize(buttonSize);

    btnInventory.setMaximumSize(buttonSize);
    btnInventory.setPreferredSize(buttonSize);

    btnProducts.setMaximumSize(buttonSize);
    btnProducts.setPreferredSize(buttonSize);

    btnCustomers.setMaximumSize(buttonSize);
    btnCustomers.setPreferredSize(buttonSize);

    btnStaff.setMaximumSize(buttonSize);
    btnStaff.setPreferredSize(buttonSize);

    btnSales.setMaximumSize(buttonSize);
    btnSales.setPreferredSize(buttonSize);

    btnLogout.setMaximumSize(buttonSize);
    btnLogout.setPreferredSize(buttonSize);


    // Center buttons
    btnDashboard.setAlignmentX(
            javax.swing.JComponent.CENTER_ALIGNMENT
    );

    btnInventory.setAlignmentX(
            javax.swing.JComponent.CENTER_ALIGNMENT
    );

    btnProducts.setAlignmentX(
            javax.swing.JComponent.CENTER_ALIGNMENT
    );

    btnCustomers.setAlignmentX(
            javax.swing.JComponent.CENTER_ALIGNMENT
    );

    btnStaff.setAlignmentX(
            javax.swing.JComponent.CENTER_ALIGNMENT
    );

    btnSales.setAlignmentX(
            javax.swing.JComponent.CENTER_ALIGNMENT
    );

    btnLogout.setAlignmentX(
            javax.swing.JComponent.CENTER_ALIGNMENT
    );


    // Add sidebar buttons
    pnlSidebar.add(btnDashboard);

    pnlSidebar.add(javax.swing.Box.createVerticalStrut(35));

    pnlSidebar.add(btnInventory);

    pnlSidebar.add(javax.swing.Box.createVerticalStrut(35));

    pnlSidebar.add(btnProducts);

    pnlSidebar.add(javax.swing.Box.createVerticalStrut(35));

    pnlSidebar.add(btnCustomers);

    pnlSidebar.add(javax.swing.Box.createVerticalStrut(35));

    // Staff management is hidden from the menu (StaffPanel and its code are kept).
    // To bring it back, add these two lines again:
    //   pnlSidebar.add(btnStaff);
    //   pnlSidebar.add(javax.swing.Box.createVerticalStrut(35));

    pnlSidebar.add(btnSales);

    // Push logout toward bottom
    pnlSidebar.add(javax.swing.Box.createVerticalGlue());

    pnlSidebar.add(btnLogout);


    // =========================================
    // HEADER
    // =========================================

    pnlHeader.removeAll();

    pnlHeader.setLayout(new java.awt.BorderLayout());

    pnlHeader.setBackground(
            new java.awt.Color(18, 32, 47)
    );

    pnlHeader.setBorder(
            javax.swing.BorderFactory.createEmptyBorder(
                    20, 30, 20, 30
            )
    );


    // Main title
    lblHeaderTitle.setText(
            "CASTRO'S CONSTRUCTION SUPPLIES"
    );

    lblHeaderTitle.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.BOLD,
                    26
            )
    );

    lblHeaderTitle.setForeground(
            java.awt.Color.WHITE
    );


    // Subtitle
    jLabel1.setText(
            "SYSTEM MANAGEMENT"
    );

    jLabel1.setFont(
            new java.awt.Font(
                    "Arial",
                    java.awt.Font.PLAIN,
                    16
            )
    );

    jLabel1.setForeground(
            new java.awt.Color(204, 204, 204)
    );


    // Title container
    javax.swing.JPanel titlePanel =
            new javax.swing.JPanel();

    titlePanel.setLayout(
            new javax.swing.BoxLayout(
                    titlePanel,
                    javax.swing.BoxLayout.Y_AXIS
            )
    );

    titlePanel.setBackground(
            new java.awt.Color(18, 32, 47)
    );

    titlePanel.add(lblHeaderTitle);

    titlePanel.add(
            javax.swing.Box.createVerticalStrut(5)
    );

    titlePanel.add(jLabel1);


    pnlHeader.add(
            titlePanel,
            java.awt.BorderLayout.WEST
    );


    // =========================================
    // CONTENT AREA
    // =========================================

    pnlContent.removeAll();

    pnlContent.setLayout(
            new java.awt.BorderLayout()
    );

    pnlContent.setBackground(
            new java.awt.Color(240, 243, 245)
    );


    // =========================================
    // RIGHT SIDE
    // =========================================

    javax.swing.JPanel rightPanel =
            new javax.swing.JPanel(
                    new java.awt.BorderLayout()
            );

    rightPanel.setBackground(
            new java.awt.Color(240, 243, 245)
    );

    rightPanel.add(
            pnlHeader,
            java.awt.BorderLayout.NORTH
    );

    rightPanel.add(
            pnlContent,
            java.awt.BorderLayout.CENTER
    );


    // =========================================
    // MAIN FRAME
    // =========================================

    jPanel1.add(
            pnlSidebar,
            java.awt.BorderLayout.WEST
    );

    jPanel1.add(
            rightPanel,
            java.awt.BorderLayout.CENTER
    );


    getContentPane().setLayout(
            new java.awt.BorderLayout()
    );

    getContentPane().add(
            jPanel1,
            java.awt.BorderLayout.CENTER
    );


    getContentPane().revalidate();
    getContentPane().repaint();
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jSpinner1 = new javax.swing.JSpinner();
        pnlSidebar = new javax.swing.JPanel();
        btnDashboard = new javax.swing.JButton();
        btnInventory = new javax.swing.JButton();
        btnProducts = new javax.swing.JButton();
        btnCustomers = new javax.swing.JButton();
        btnStaff = new javax.swing.JButton();
        btnSales = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblHeaderTitle = new javax.swing.JLabel();
        pnlContent = new javax.swing.JPanel();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(18, 32, 47));

        pnlSidebar.setBackground(new java.awt.Color(18, 32, 47));

        btnDashboard.setBackground(new java.awt.Color(0, 0, 102));
        btnDashboard.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnDashboard.setForeground(new java.awt.Color(255, 255, 255));
        btnDashboard.setText("DASHBOARD");
        btnDashboard.addActionListener(this::btnDashboardActionPerformed);

        btnInventory.setBackground(new java.awt.Color(0, 0, 102));
        btnInventory.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnInventory.setForeground(new java.awt.Color(255, 255, 255));
        btnInventory.setText("INVENTORY");
        btnInventory.addActionListener(this::btnInventoryActionPerformed);

        btnProducts.setBackground(new java.awt.Color(0, 0, 102));
        btnProducts.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnProducts.setForeground(new java.awt.Color(255, 255, 255));
        btnProducts.setText("PRODUCTS");
        btnProducts.addActionListener(this::btnProductsActionPerformed);

        btnCustomers.setBackground(new java.awt.Color(0, 0, 102));
        btnCustomers.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnCustomers.setForeground(new java.awt.Color(255, 255, 255));
        btnCustomers.setText("CUSTOMERS");

        btnStaff.setBackground(new java.awt.Color(0, 0, 102));
        btnStaff.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnStaff.setForeground(new java.awt.Color(255, 255, 255));
        btnStaff.setText("STAFF");
        btnStaff.addActionListener(this::btnStaffActionPerformed);

        btnSales.setBackground(new java.awt.Color(0, 0, 102));
        btnSales.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnSales.setForeground(new java.awt.Color(255, 255, 255));
        btnSales.setText("SALES");

        btnLogout.setBackground(new java.awt.Color(190, 50, 45));
        btnLogout.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText(" LOGOUT");

        javax.swing.GroupLayout pnlSidebarLayout = new javax.swing.GroupLayout(pnlSidebar);
        pnlSidebar.setLayout(pnlSidebarLayout);
        pnlSidebarLayout.setHorizontalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnLogout, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSales, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnStaff, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCustomers, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProducts, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnInventory, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDashboard, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(420, Short.MAX_VALUE))
        );
        pnlSidebarLayout.setVerticalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnCustomers, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnSales, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 88, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(18, 32, 47));

        pnlHeader.setBackground(new java.awt.Color(18, 32, 47));

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 39, Short.MAX_VALUE)
        );

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("   SYSTEM MANAGEMENT");

        lblHeaderTitle.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N
        lblHeaderTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblHeaderTitle.setText("CASTRO'S CONSTRUCTION SUPPLIES ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(405, 405, 405)
                .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(615, 615, 615)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 603, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHeaderTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 775, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblHeaderTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addGap(11, 11, 11))
        );

        pnlContent.setBackground(new java.awt.Color(255, 255, 255));
        pnlContent.setPreferredSize(new java.awt.Dimension(850, 600));

        javax.swing.GroupLayout pnlContentLayout = new javax.swing.GroupLayout(pnlContent);
        pnlContent.setLayout(pnlContentLayout);
        pnlContentLayout.setHorizontalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );
        pnlContentLayout.setVerticalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlSidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlContent, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlSidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE))
                .addGap(140, 140, 140))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStaffActionPerformed
       showPanel(new StaffPanel());
    }//GEN-LAST:event_btnStaffActionPerformed

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
       showPanel(createDashboard());
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventoryActionPerformed
         showPanel(new InventoryPanel());
    }//GEN-LAST:event_btnInventoryActionPerformed

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        showPanel(new ProductsPanel());
    }//GEN-LAST:event_btnProductsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCustomers;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnInventory;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnSales;
    private javax.swing.JButton btnStaff;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblHeaderTitle;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlSidebar;
    // End of variables declaration//GEN-END:variables
}
