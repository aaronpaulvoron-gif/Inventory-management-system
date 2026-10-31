package castroconstructionsuppliesNODB;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Point of sale: build a cart, complete the sale (stock is deducted from
 * the shared inventory), and review sales history. Customers logged in as
 * "Customer" use the same screen to place their own orders.
 */
public class SalesPanel extends JPanel {

    private static final Color NAVY = new Color(18, 32, 47);
    private static final Color ORANGE = new Color(0xF9, 0x73, 0x16);
    private static final Color BG = new Color(245, 247, 250);
    private static final Color RED = new Color(190, 50, 45);

    private final ArrayList<SaleItem> cart = new ArrayList<>();
    private final ArrayList<Product> comboProducts = new ArrayList<>();
    private final ArrayList<Customer> comboCustomers = new ArrayList<>();
    private final ArrayList<Sale> historyShown = new ArrayList<>();
    private final ArrayList<Sale> queueShown = new ArrayList<>();

    private JComboBox<String> cmbCustomer;
    private JComboBox<String> cmbProduct;
    private JComboBox<String> cmbCategory;
    private JSpinner spnQty;
    private JTable tblCart;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    private JTable tblHistory;
    private DefaultTableModel historyModel;
    private JTable tblQueue;
    private DefaultTableModel queueModel;
    private JLabel lblQueueCount;

    public SalesPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(20, 25, 10, 25));
        JLabel title = new JLabel(Session.isCustomer() ? "PLACE AN ORDER" : "SALES");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(20, 35, 60));
        JLabel sub = new JLabel(Session.isCustomer()
                ? "Choose products, review your cart and place your order"
                : "Record sales and review transaction history");
        sub.setFont(new Font("Arial", Font.PLAIN, 14));
        sub.setForeground(new Color(100, 100, 100));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(Session.isCustomer() ? "NEW ORDER" : "NEW SALE", buildNewSaleTab());
        tabs.addTab(Session.isCustomer() ? "MY ORDERS" : "SALES HISTORY", buildHistoryTab());
        if (Session.isStaff()) {
            tabs.addTab("ORDER QUEUE", buildQueueTab());
        }
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG);
        wrap.setBorder(new EmptyBorder(0, 25, 20, 25));
        wrap.add(tabs, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);

        reloadProducts();
        reloadHistory();
        if (Session.isStaff()) {
            reloadQueue();
        }
    }

    // ------------------------------------------------------------ new sale
    private JPanel buildNewSaleTab() {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Two rows so nothing is squeezed or cut off:
        //   row 1: Customer | Category        row 2: Product (wide) | Qty | ADD TO CART
        JPanel top = new JPanel(new java.awt.GridBagLayout());
        top.setBackground(Color.WHITE);
        java.awt.GridBagConstraints gc = new java.awt.GridBagConstraints();
        gc.insets = new java.awt.Insets(5, 5, 5, 5);
        gc.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gc.gridy = 0;
        gc.gridx = 0;
        top.add(new JLabel("Customer:"), gc);
        if (Session.isCustomer()) {
            cmbCustomer = new JComboBox<>(new String[]{Session.currentUserName});
            cmbCustomer.setEnabled(false);
        } else {
            cmbCustomer = new JComboBox<>();
            cmbCustomer.addItem("Walk-in Customer");
            for (Customer c : CustomerDAO.search("")) {
                comboCustomers.add(c);
                cmbCustomer.addItem(c.getFullName());
            }
        }
        cmbCustomer.setPreferredSize(new java.awt.Dimension(180, 34));
        gc.gridx = 1;
        gc.weightx = 1.0;
        top.add(cmbCustomer, gc);

        gc.gridx = 2;
        gc.weightx = 0;
        top.add(new JLabel("Category:"), gc);
        cmbCategory = new JComboBox<>();
        cmbCategory.addItem("All Categories");
        for (String c : CategoryDAO.findAllNames()) {
            cmbCategory.addItem(c);
        }
        cmbCategory.setPreferredSize(new java.awt.Dimension(180, 34));
        gc.gridx = 3;
        gc.weightx = 1.0;
        top.add(cmbCategory, gc);

        gc.gridy = 1;
        gc.gridx = 0;
        gc.weightx = 0;
        top.add(new JLabel("Product:"), gc);
        cmbProduct = new JComboBox<>();
        cmbProduct.setPreferredSize(new java.awt.Dimension(180, 34));
        gc.gridx = 1;
        gc.gridwidth = 3;
        gc.weightx = 1.0; // the product list takes all spare width
        top.add(cmbProduct, gc);

        gc.gridwidth = 1;
        gc.weightx = 0;
        JPanel qtyBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        qtyBox.setBackground(Color.WHITE);
        spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));
        spnQty.setPreferredSize(new java.awt.Dimension(80, 34));
        qtyBox.add(new JLabel("Qty:"));
        qtyBox.add(spnQty);
        gc.gridx = 4;
        top.add(qtyBox, gc);

        JButton btnAdd = button("ADD TO CART", ORANGE);
        btnAdd.setPreferredSize(new java.awt.Dimension(140, 36));
        btnAdd.setOpaque(true);
        btnAdd.setBorderPainted(false);
        btnAdd.addActionListener(e -> addToCart());
        gc.gridx = 5;
        top.add(btnAdd, gc);

        // choosing a category narrows the product list
        cmbCategory.addActionListener(e -> reloadProducts());
        root.add(top, BorderLayout.NORTH);

        cartModel = new DefaultTableModel(new Object[][]{},
                new String[]{"Product", "Unit Price", "Quantity", "Subtotal"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblCart = new JTable(cartModel);
        tblCart.setRowHeight(28);
        tblCart.setAutoCreateRowSorter(true);
        tblCart.getTableHeader().setReorderingAllowed(false);
        tblCart.getColumnModel().getColumn(1).setCellRenderer(new CurrencyRenderer());
        tblCart.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer());
        root.add(new JScrollPane(tblCart), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        lblTotal = new JLabel("TOTAL: ₱0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotal.setForeground(new Color(20, 35, 60));
        bottom.add(lblTotal, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Color.WHITE);
        JButton btnRemove = button("REMOVE SELECTED", NAVY);
        btnRemove.addActionListener(e -> removeSelected());
        JButton btnClear = button(Session.isCustomer() ? "CANCEL ORDER" : "CLEAR CART", RED);
        btnClear.addActionListener(e -> clearCart());
        JButton btnComplete = button(Session.isCustomer() ? "PLACE ORDER" : "COMPLETE SALE", new Color(30, 130, 76));
        btnComplete.addActionListener(e -> completeSale());
        buttons.add(btnRemove);
        buttons.add(btnClear);
        buttons.add(btnComplete);
        bottom.add(buttons, BorderLayout.EAST);
        root.add(bottom, BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildHistoryTab() {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        historyModel = new DefaultTableModel(new Object[][]{},
                new String[]{"Transaction ID", "Date", "Customer", "Handled By", "Items", "Total", "Status", "Cancel Reason"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblHistory = new JTable(historyModel);
        tblHistory.setRowHeight(28);
        tblHistory.getTableHeader().setReorderingAllowed(false);
        tblHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblHistory.setAutoCreateRowSorter(true);
        tblHistory.getColumnModel().getColumn(5).setCellRenderer(new CurrencyRenderer());
        tblHistory.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());
        tblHistory.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] hw = {95, 80, 120, 90, 45, 90, 80, 220};
        for (int i = 0; i < hw.length; i++) {
            tblHistory.getColumnModel().getColumn(i).setPreferredWidth(hw[i]);
        }
        root.add(new JScrollPane(tblHistory), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Color.WHITE);
        JButton btnDetails = button("VIEW DETAILS", NAVY);
        btnDetails.addActionListener(e -> showDetails());
        if (Session.isCustomer()) {
            JButton btnCancelMine = button("CANCEL ORDER", RED);
            btnCancelMine.addActionListener(e -> cancelMyOrder());
            buttons.add(btnCancelMine);
        }
        buttons.add(btnDetails);
        root.add(buttons, BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildQueueTab() {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        lblQueueCount = new JLabel();
        lblQueueCount.setFont(new Font("Arial", Font.BOLD, 13));
        lblQueueCount.setForeground(new Color(20, 35, 60));
        root.add(lblQueueCount, BorderLayout.NORTH);

        queueModel = new DefaultTableModel(new Object[][]{},
                new String[]{"Order ID", "Placed", "Customer", "Items", "Total"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblQueue = new JTable(queueModel);
        tblQueue.setRowHeight(28);
        tblQueue.getTableHeader().setReorderingAllowed(false);
        tblQueue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblQueue.setAutoCreateRowSorter(true);
        tblQueue.getColumnModel().getColumn(4).setCellRenderer(new CurrencyRenderer());
        // default view: oldest order first, exactly the order they should be handled in
        tblQueue.getRowSorter().toggleSortOrder(1);
        root.add(new JScrollPane(tblQueue), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Color.WHITE);
        JButton btnViewQ = button("VIEW DETAILS", NAVY);
        btnViewQ.addActionListener(e -> showQueueDetails());
        JButton btnCancel = button("CANCEL ORDER", RED);
        btnCancel.addActionListener(e -> cancelQueuedOrder());
        JButton btnConfirm = button("CONFIRM & DEDUCT STOCK", new Color(30, 130, 76));
        btnConfirm.addActionListener(e -> confirmQueuedOrder());
        buttons.add(btnViewQ);
        buttons.add(btnCancel);
        buttons.add(btnConfirm);
        root.add(buttons, BorderLayout.SOUTH);
        return root;
    }

    private JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 16, 8, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ---------------------------------------------------------------- cart
    private void reloadProducts() {
        cmbProduct.removeAllItems();
        comboProducts.clear();
        String category = (cmbCategory == null || cmbCategory.getSelectedIndex() <= 0)
                ? null : String.valueOf(cmbCategory.getSelectedItem());
        for (Product p : ProductDAO.findInStock()) {
            if (category != null && !category.equalsIgnoreCase(p.getCategory())) {
                continue;
            }
            comboProducts.add(p);
            cmbProduct.addItem(String.format("%s - ₱%,.2f (%d in stock)",
                    p.getName(), p.getPrice(), p.getStockQuantity()));
        }
    }

    private int quantityInCart(int productId) {
        for (SaleItem item : cart) {
            if (item.getProductId() == productId) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    private void addToCart() {
        int index = cmbProduct.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this,
                    cmbCategory != null && cmbCategory.getSelectedIndex() > 0
                    ? "No products are in stock for this category."
                    : "No products are available in stock.",
                    "Sales", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Product p = comboProducts.get(index);
        int qty = (Integer) spnQty.getValue();
        int alreadyInCart = quantityInCart(p.getId());

        if (alreadyInCart + qty > p.getStockQuantity()) {
            JOptionPane.showMessageDialog(this,
                    "Not enough stock for \"" + p.getName() + "\".\nAvailable: " + p.getStockQuantity()
                    + " (already in cart: " + alreadyInCart + ")",
                    "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // merge with an existing cart line
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getProductId() == p.getId()) {
                cart.set(i, new SaleItem(p.getId(), p.getName(), alreadyInCart + qty, p.getPrice()));
                refreshCart();
                return;
            }
        }
        cart.add(new SaleItem(p.getId(), p.getName(), qty, p.getPrice()));
        spnQty.setValue(1);
        refreshCart();
    }

    private void removeSelected() {
        int row = tblCart.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a cart item first.");
            return;
        }
        cart.remove(tblCart.convertRowIndexToModel(row));
        refreshCart();
    }

    private void clearCart() {
        if (Session.isCustomer()) {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is already empty.",
                        "Cancel Order", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            double total = 0;
            for (SaleItem item : cart) {
                total += item.getSubtotal();
            }
            String answer = CancelOrderDialog.ask(this, "CANCEL THIS ORDER?",
                    String.format("Your cart has %d item(s) worth \u20B1%,.2f. Cancelling removes everything from "
                            + "your cart. Nothing has been ordered yet, so no one is notified.", cart.size(), total),
                    false, "KEEP SHOPPING", "YES, CANCEL ORDER");
            if (answer == null) {
                return;
            }
        }
        cart.clear();
        refreshCart();
    }

    private void refreshCart() {
        cartModel.setRowCount(0);
        double total = 0;
        for (SaleItem item : cart) {
            cartModel.addRow(new Object[]{item.getProductName(),
                item.getUnitPrice(), item.getQuantity(), item.getSubtotal()});
            total += item.getSubtotal();
        }
        lblTotal.setText(String.format("TOTAL: ₱%,.2f", total));
    }

    private void completeSale() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The cart is empty.", "Sales", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = 0;
        for (SaleItem item : cart) {
            total += item.getSubtotal();
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Confirm %s of %d item(s) for ₱%,.2f?",
                        Session.isCustomer() ? "order" : "sale", cart.size(), total),
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String customer = String.valueOf(cmbCustomer.getSelectedItem());
        String staff = Session.isCustomer() ? "Online Order" : Session.currentUserName;
        Sale sale = new Sale(null,
                LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                customer, staff, new ArrayList<>(cart));
        if (Session.isCustomer()) {
            sale.setCustomerId(Session.currentCustomerId);
        } else {
            sale.setStaffId(Session.currentStaffId);
            int pick = cmbCustomer.getSelectedIndex();
            if (pick > 0) {
                sale.setCustomerId(comboCustomers.get(pick - 1).getId());
            }
        }

        // Staff sales deduct stock immediately. Customer orders join the
        // FIFO queue as Pending and wait for a staff member to confirm them.
        boolean immediate = Session.isStaff();
        String error = SaleDAO.create(sale, immediate);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error + "\nPlease adjust your cart.",
                    "Sale Not Saved", JOptionPane.ERROR_MESSAGE);
            reloadProducts();
            return;
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append(sale.getId()).append("  -  ").append(sale.getDate()).append("\n");
        receipt.append("Customer: ").append(customer).append("\n\n");
        for (SaleItem item : sale.getItems()) {
            receipt.append(String.format("%s  x%d  =  ₱%,.2f%n",
                    item.getProductName(), item.getQuantity(), item.getSubtotal()));
        }
        receipt.append(String.format("%nTOTAL: ₱%,.2f", sale.getTotal()));
        if (!immediate) {
            receipt.append("\n\nYour order is waiting in the queue for the store to confirm it.");
        }
        JOptionPane.showMessageDialog(this, receipt.toString(),
                immediate ? "Sale Completed" : "Order Placed", JOptionPane.INFORMATION_MESSAGE);

        cart.clear();
        refreshCart();
        reloadProducts();
        reloadHistory();
        if (Session.isStaff()) {
            reloadQueue();
        }
    }

    // ------------------------------------------------------------- history
    private void reloadHistory() {
        historyModel.setRowCount(0);
        historyShown.clear();
        Integer onlyCustomer = Session.isCustomer() ? Session.currentCustomerId : null;
        for (Sale s : SaleDAO.findAll(onlyCustomer)) {
            historyShown.add(s);
            int units = 0;
            for (SaleItem item : s.getItems()) {
                units += item.getQuantity();
            }
            historyModel.addRow(new Object[]{s.getId(), s.getDate(), s.getCustomerName(),
                s.getStaffName(), units, s.getTotal(), s.getStatus(), s.getCancelSummary()});
        }
    }

    // --------------------------------------------------------------- queue
    private void reloadQueue() {
        queueModel.setRowCount(0);
        queueShown.clear();
        List<Sale> queue = SaleDAO.findQueue();
        for (Sale s : queue) {
            queueShown.add(s);
            int units = 0;
            for (SaleItem item : s.getItems()) {
                units += item.getQuantity();
            }
            queueModel.addRow(new Object[]{s.getId(), s.getDate(), s.getCustomerName(), units, s.getTotal()});
        }
        lblQueueCount.setText(queue.isEmpty()
                ? "No orders waiting."
                : queue.size() + " order(s) waiting -- oldest first is next to process.");
    }

    private Sale selectedQueued() {
        int row = tblQueue.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order first.");
            return null;
        }
        return queueShown.get(tblQueue.convertRowIndexToModel(row));
    }

    private void confirmQueuedOrder() {
        Sale s = selectedQueued();
        if (s == null) {
            return;
        }
        String error = SaleDAO.confirmOrder(s.getId());
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Cannot Confirm", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, s.getId() + " confirmed. Stock has been deducted.",
                    "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
        }
        reloadQueue();
        reloadProducts();
        reloadHistory();
    }

    private void cancelQueuedOrder() {
        Sale s = selectedQueued();
        if (s == null) {
            return;
        }
        boolean hasAccount = s.getCustomerId() != null;
        String reason = CancelOrderDialog.ask(this, "CANCEL ORDER " + s.getId() + "?",
                "Cancel the order of <b>" + s.getCustomerName() + "</b> (\u20B1" + String.format("%,.2f", s.getTotal())
                + ")? " + (hasAccount ? "The customer will be notified with the reason you pick."
                        : "This order has no customer account, so no one is notified."),
                true, CancelOrderDialog.STAFF_REASONS, "KEEP ORDER", "YES, CANCEL ORDER");
        if (reason == null) {
            return;
        }
        if (SaleDAO.cancelOrder(s.getId(), reason)) {
            reloadQueue();
            reloadHistory();
        } else {
            JOptionPane.showMessageDialog(this, "This order is no longer pending.", "Cancel Order",
                    JOptionPane.WARNING_MESSAGE);
            reloadQueue();
            reloadHistory();
        }
    }

    private void showQueueDetails() {
        Sale s = selectedQueued();
        if (s != null) {
            JOptionPane.showMessageDialog(this, formatReceipt(s), "Order Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String formatReceipt(Sale s) {
        StringBuilder sb = new StringBuilder();
        sb.append(s.getId()).append("  -  ").append(s.getDate()).append("  [").append(s.getStatus()).append("]\n");
        sb.append("Customer: ").append(s.getCustomerName()).append("\n");
        sb.append("Handled by: ").append(s.getStaffName()).append("\n");
        if ("Cancelled".equals(s.getStatus())) {
            if (!s.getCancelledBy().isEmpty()) {
                sb.append("Cancelled by: ").append(s.getCancelledBy()).append("\n");
            }
            if (!s.getCancelReason().isEmpty()) {
                sb.append("Cancel reason: ").append(s.getCancelReason()).append("\n");
            }
        }
        sb.append("\n");
        for (SaleItem item : s.getItems()) {
            sb.append(String.format("%s  x%d @ ₱%,.2f  =  ₱%,.2f%n", item.getProductName(),
                    item.getQuantity(), item.getUnitPrice(), item.getSubtotal()));
        }
        sb.append(String.format("%nTOTAL: ₱%,.2f", s.getTotal()));
        return sb.toString();
    }

    /** A customer cancels one of their own orders while it is still waiting to be confirmed. */
    private void cancelMyOrder() {
        int row = tblHistory.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select the order you want to cancel.",
                    "Cancel Order", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Sale s = historyShown.get(tblHistory.convertRowIndexToModel(row));
        if (!"Pending".equals(s.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    s.getId() + " is already " + s.getStatus().toLowerCase()
                    + ".\nOnly orders that are still waiting to be confirmed can be cancelled.",
                    "Cancel Order", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int units = 0;
        for (SaleItem item : s.getItems()) {
            units += item.getQuantity();
        }
        String reason = CancelOrderDialog.ask(this, "CANCEL ORDER " + s.getId() + "?",
                String.format("This order has %d unit(s) totalling \u20B1%,.2f. Once cancelled it cannot be "
                        + "restored; you would need to place a new order.", units, s.getTotal()),
                true, "KEEP ORDER", "YES, CANCEL ORDER");
        if (reason == null) {
            return;
        }
        String error = SaleDAO.cancelOwnOrder(s.getId(), Session.currentCustomerId, reason);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Cancel Order", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Order " + s.getId() + " has been cancelled.",
                    "Order Cancelled", JOptionPane.INFORMATION_MESSAGE);
        }
        reloadHistory();
    }

    private void showDetails() {
        int row = tblHistory.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a transaction first.");
            return;
        }
        Sale s = historyShown.get(tblHistory.convertRowIndexToModel(row));
        JOptionPane.showMessageDialog(this, formatReceipt(s), "Transaction Details", JOptionPane.INFORMATION_MESSAGE);
    }

}
