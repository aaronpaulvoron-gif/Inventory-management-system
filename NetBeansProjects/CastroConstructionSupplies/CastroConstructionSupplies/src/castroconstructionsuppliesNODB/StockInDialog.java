package castroconstructionsuppliesNODB;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/** Stock-In: record a delivery as a new BATCH. Adds to stock and sets the purchase price. */
public class StockInDialog extends JDialog {

    private final List<Product> products = new ArrayList<>();
    private final JComboBox<String> cmbProduct = Theme.style(new JComboBox<String>());
    private final StockBar bar = new StockBar();
    private final JLabel lblLevel = new JLabel(" ");
    private final JTextField txtBatchNo = Theme.style(new JTextField());
    private final JTextField txtQty = Theme.style(new JTextField());
    private final JTextField txtCost = Theme.style(new JTextField());
    private final JTextField txtDate = Theme.style(new JTextField(LocalDate.now().toString()));
    private final JTextField txtNotes = Theme.style(new JTextField());
    private final JLabel lblSummary = new JLabel(" ");
    private final JLabel lblError = new JLabel(" ");
    private final Runnable onSaved;

    public StockInDialog(Component parent, Product preselected, Runnable onSaved) {
        super(parent == null ? null : SwingUtilities.getWindowAncestor(parent), "Stock-In (Add Batch)",
                ModalityType.APPLICATION_MODAL);
        this.onSaved = onSaved;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        for (Product p : ProductDAO.search("")) {
            products.add(p);
            cmbProduct.addItem(p.getName());
        }
        int pre = 0;
        for (int i = 0; i < products.size(); i++) {
            if (preselected != null && products.get(i).getId() == preselected.getId()) {
                pre = i;
            }
        }

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.SOFT);

        JPanel band = new JPanel(new BorderLayout());
        band.setBackground(Theme.NAVY);
        band.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));
        JLabel title = new JLabel("STOCK-IN  |  ADD BATCH");
        title.setFont(Theme.HEADING);
        title.setForeground(Theme.ORANGE);
        band.add(title, BorderLayout.WEST);
        root.add(band, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.SOFT);
        body.setBorder(BorderFactory.createEmptyBorder(18, 24, 6, 24));

        cmbProduct.setPreferredSize(new Dimension(380, 36));
        body.add(Theme.fieldBlock("Product", cmbProduct, Theme.STEEL));
        body.add(Box.createVerticalStrut(6));

        JPanel levelRow = new JPanel(new BorderLayout(10, 0));
        levelRow.setOpaque(false);
        lblLevel.setFont(Theme.BODY);
        lblLevel.setForeground(Theme.STEEL);
        levelRow.add(lblLevel, BorderLayout.NORTH);
        bar.setPreferredSize(new Dimension(380, 14));
        levelRow.add(bar, BorderLayout.CENTER);
        levelRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        levelRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        body.add(levelRow);
        body.add(Box.createVerticalStrut(12));

        txtBatchNo.setPreferredSize(new Dimension(180, 36));
        txtDate.setPreferredSize(new Dimension(180, 36));
        body.add(row(Theme.fieldBlock("Batch number", txtBatchNo, Theme.STEEL),
                Theme.fieldBlock("Date received (yyyy-mm-dd)", txtDate, Theme.STEEL)));
        body.add(Box.createVerticalStrut(10));
        txtQty.setPreferredSize(new Dimension(180, 36));
        txtCost.setPreferredSize(new Dimension(180, 36));
        body.add(row(Theme.fieldBlock("Quantity received", txtQty, Theme.STEEL),
                Theme.fieldBlock("Purchase price per unit (\u20B1)", txtCost, Theme.STEEL)));
        body.add(Box.createVerticalStrut(10));
        txtNotes.setPreferredSize(new Dimension(380, 36));
        body.add(Theme.fieldBlock("Notes (optional)", txtNotes, Theme.STEEL));
        body.add(Box.createVerticalStrut(10));

        lblSummary.setFont(Theme.LABEL);
        lblSummary.setForeground(Theme.STEEL);
        lblSummary.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblSummary);
        body.add(Box.createVerticalStrut(4));
        lblError.setFont(Theme.LABEL);
        lblError.setForeground(Theme.DANGER);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblError);
        root.add(body, BorderLayout.CENTER);

        JButton btnAdd = Theme.primaryButton("ADD BATCH");
        JButton btnCancel = Theme.secondaryButton("CANCEL");
        btnAdd.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Theme.SOFT);
        buttons.setBorder(BorderFactory.createEmptyBorder(6, 24, 18, 24));
        buttons.add(btnCancel);
        buttons.add(btnAdd);
        root.add(buttons, BorderLayout.SOUTH);

        cmbProduct.addActionListener(e -> productChanged());
        DocumentListener live = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateSummary(); }
            @Override public void removeUpdate(DocumentEvent e) { updateSummary(); }
            @Override public void changedUpdate(DocumentEvent e) { updateSummary(); }
        };
        txtQty.getDocument().addDocumentListener(live);
        txtCost.getDocument().addDocumentListener(live);

        setContentPane(root);
        getRootPane().setDefaultButton(btnAdd);
        if (!products.isEmpty()) {
            cmbProduct.setSelectedIndex(pre);
            productChanged();
        }
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private JPanel row(JPanel left, JPanel right) {
        JPanel r = new JPanel(new GridLayout(1, 2, 20, 0));
        r.setOpaque(false);
        r.add(left);
        r.add(right);
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, r.getPreferredSize().height));
        return r;
    }

    private Product current() {
        int i = cmbProduct.getSelectedIndex();
        return i < 0 ? null : products.get(i);
    }

    private void productChanged() {
        Product p = current();
        if (p == null) {
            return;
        }
        lblLevel.setText(String.format("Current stock: %d %s   |   Reorder level: %d   |   %s",
                p.getStockQuantity(), p.getUnit(), p.getReorderLevel(), p.getStockStatus()));
        bar.setLevels(p.getStockQuantity(), p.getReorderLevel());
        txtBatchNo.setText(BatchDAO.suggestBatchNo(p.getId()));
        txtCost.setText(p.getPurchasePrice() > 0 ? String.format("%.2f", p.getPurchasePrice()) : "");
        updateSummary();
    }

    private void updateSummary() {
        Product p = current();
        if (p == null) {
            return;
        }
        try {
            int qty = Integer.parseInt(txtQty.getText().trim());
            Double cost = Theme.parseMoney(txtCost.getText());
            String total = cost == null ? "" : String.format("   |   Batch cost: \u20B1%,.2f", qty * cost);
            lblSummary.setText("Stock after this batch: " + (p.getStockQuantity() + qty) + total);
        } catch (NumberFormatException e) {
            lblSummary.setText(" ");
        }
    }

    private void fail(String message, JTextField field) {
        lblError.setText(message);
        for (JTextField f : new JTextField[]{txtBatchNo, txtQty, txtCost, txtDate}) {
            Theme.setInvalid(f, f == field);
        }
        if (field != null) {
            field.requestFocusInWindow();
        }
    }

    private void save() {
        Product p = current();
        if (p == null) {
            fail("There are no products yet. Add a product first.", null);
            return;
        }
        String batchNo = txtBatchNo.getText().trim();
        if (batchNo.isEmpty() || batchNo.length() > 20) {
            fail("Enter a batch number (up to 20 characters).", txtBatchNo);
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(txtQty.getText().trim());
        } catch (NumberFormatException e) {
            fail("Quantity must be a whole number.", txtQty);
            return;
        }
        if (qty < 1 || qty > 1_000_000) {
            fail("Quantity must be between 1 and 1,000,000.", txtQty);
            return;
        }
        Double cost = Theme.parseMoney(txtCost.getText());
        if (cost == null || cost <= 0) {
            fail("Enter the purchase price per unit (more than zero).", txtCost);
            return;
        }
        LocalDate date;
        try {
            date = LocalDate.parse(txtDate.getText().trim());
        } catch (DateTimeParseException e) {
            fail("Date must look like 2026-10-09.", txtDate);
            return;
        }
        if (date.isAfter(LocalDate.now())) {
            fail("The received date cannot be in the future.", txtDate);
            return;
        }
        if (p.getPrice() < cost) {
            int ok = JOptionPane.showConfirmDialog(this,
                    String.format("The purchase price (\u20B1%,.2f) is higher than the selling price (\u20B1%,.2f).%n"
                            + "You would lose money on every unit sold. Add this batch anyway?", cost, p.getPrice()),
                    "Check the price", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok != JOptionPane.YES_OPTION) {
                return;
            }
        }
        String error = BatchDAO.addBatch(p.getId(), batchNo, qty, cost, date.toString(), txtNotes.getText().trim());
        if (error != null) {
            fail(error, txtBatchNo);
            return;
        }
        JOptionPane.showMessageDialog(this,
                String.format("Batch %s added: %d x %s.%nStock is now %d.", batchNo, qty, p.getName(),
                        p.getStockQuantity()),
                "Stock-In Saved", JOptionPane.INFORMATION_MESSAGE);
        if (onSaved != null) {
            onSaved.run();
        }
        dispose();
    }

    public boolean hasProducts() {
        return !products.isEmpty();
    }
}
