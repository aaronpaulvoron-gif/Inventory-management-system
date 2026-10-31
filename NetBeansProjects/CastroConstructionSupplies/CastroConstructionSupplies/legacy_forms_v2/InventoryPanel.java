package castroconstructionsuppliesNODB;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Stock overview: live stock levels, low/out-of-stock highlighting,
 * restocking and reorder-level changes. Reads the same products table the
 * Products, Sales and Dashboard screens use.
 */
public class InventoryPanel extends JPanel {

    private static final Color NAVY = new Color(18, 32, 47);
    private static final Color ORANGE = new Color(255, 153, 0);
    private static final Color BG = new Color(245, 247, 250);

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JCheckBox chkLowOnly;
    private JLabel lblTotalProducts, lblTotalUnits, lblLow, lblOut;
    private final java.util.ArrayList<Product> shown = new java.util.ArrayList<>();

    public InventoryPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(20, 25, 10, 25));

        JLabel title = new JLabel("INVENTORY");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(20, 35, 60));
        JLabel sub = new JLabel("Monitor stock levels and restock products");
        sub.setFont(new Font("Arial", Font.PLAIN, 14));
        sub.setForeground(new Color(100, 100, 100));
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        header.add(Box.createVerticalStrut(15));

        JPanel cards = new JPanel(new GridLayout(1, 4, 15, 0));
        cards.setBackground(BG);
        lblTotalProducts = new JLabel("0");
        lblTotalUnits = new JLabel("0");
        lblLow = new JLabel("0");
        lblOut = new JLabel("0");
        cards.add(card("TOTAL PRODUCTS", lblTotalProducts));
        cards.add(card("TOTAL UNITS IN STOCK", lblTotalUnits));
        cards.add(card("LOW STOCK", lblLow));
        cards.add(card("OUT OF STOCK", lblOut));
        header.add(cards);
        return header;
    }

    private JPanel card(String caption, JLabel value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(12, 15, 12, 15));
        JLabel c = new JLabel(caption);
        c.setForeground(new Color(230, 126, 34));
        c.setFont(new Font("Arial", Font.BOLD, 12));
        value.setFont(new Font("Arial", Font.BOLD, 26));
        value.setForeground(new Color(20, 35, 60));
        p.add(c);
        p.add(Box.createVerticalStrut(4));
        p.add(value);
        return p;
    }

    private JPanel buildCenter() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(new EmptyBorder(5, 25, 20, 25));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setBackground(Color.WHITE);
        txtSearch = new JTextField(18);
        txtSearch.addActionListener(e -> refresh());
        JButton btnSearch = button("SEARCH", new Color(52, 152, 219));
        btnSearch.addActionListener(e -> refresh());
        chkLowOnly = new JCheckBox("Show low / out of stock only");
        chkLowOnly.setBackground(Color.WHITE);
        chkLowOnly.addActionListener(e -> refresh());
        top.add(new JLabel("Search Product:"));
        top.add(txtSearch);
        top.add(btnSearch);
        top.add(chkLowOnly);
        card.add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[][]{},
                new String[]{"ID", "Product", "Category", "Unit", "Stock", "Reorder Level", "Status"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(Color.WHITE);
        if (Session.isStaff()) {
            JButton btnRestock = button("RESTOCK SELECTED", ORANGE);
            btnRestock.addActionListener(e -> restock());
            JButton btnReorder = button("SET REORDER LEVEL", NAVY);
            btnReorder.addActionListener(e -> setReorderLevel());
            bottom.add(btnReorder);
            bottom.add(btnRestock);
        }
        card.add(bottom, BorderLayout.SOUTH);

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
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

    private void refresh() {
        model.setRowCount(0);
        shown.clear();
        String filter = txtSearch.getText().trim();
        int low = 0, out = 0;

        for (Product p : ProductDAO.search(filter)) {
            if (p.isOutOfStock()) {
                out++;
            } else if (p.isLowStock()) {
                low++;
            }
            if (chkLowOnly.isSelected() && !(p.isLowStock() || p.isOutOfStock())) {
                continue;
            }
            shown.add(p);
            model.addRow(new Object[]{p.getCode(), p.getName(), p.getCategory(), p.getUnit(),
                p.getStockQuantity(), p.getReorderLevel(), p.getStockStatus()});
        }

        lblTotalProducts.setText(String.valueOf(ProductDAO.count()));
        lblTotalUnits.setText(String.format("%,d", ProductDAO.totalStockUnits()));
        lblLow.setText(String.valueOf(low));
        lblOut.setText(String.valueOf(out));
    }

    private Product selected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product first.",
                    "Inventory", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return shown.get(table.convertRowIndexToModel(row));
    }

    private Integer askNumber(String message, String title, int initial, int min) {
        String input = JOptionPane.showInputDialog(this, message, initial);
        if (input == null) {
            return null;
        }
        try {
            int value = Integer.parseInt(input.trim());
            if (value < min) {
                throw new NumberFormatException();
            }
            return value;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a whole number of at least " + min + ".",
                    title, JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void restock() {
        Product p = selected();
        if (p == null) {
            return;
        }
        Integer qty = askNumber("Units to add to \"" + p.getName() + "\" (current: "
                + p.getStockQuantity() + "):", "Restock", 10, 1);
        if (qty != null) {
            if (ProductDAO.addStock(p.getId(), qty)) {
                refresh();
            }
        }
    }

    private void setReorderLevel() {
        Product p = selected();
        if (p == null) {
            return;
        }
        Integer level = askNumber("New reorder level for \"" + p.getName() + "\":",
                "Reorder Level", p.getReorderLevel(), 0);
        if (level != null) {
            if (ProductDAO.setReorderLevel(p.getId(), level)) {
                refresh();
            }
        }
    }
}
