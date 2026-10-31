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
    private static final Color ORANGE = Theme.ORANGE;
    private static final Color BG = new Color(245, 247, 250);

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JCheckBox chkLowOnly;
    private JLabel lblTotalProducts, lblTotalUnits, lblLow, lblOut;
    private JLabel lblRestockTitle;
    private JPanel pnlRestock;
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
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        header.add(cards);
        header.add(Box.createVerticalStrut(14));

        lblRestockTitle = new JLabel("NEEDS RESTOCK");
        lblRestockTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblRestockTitle.setForeground(new Color(230, 126, 34));
        lblRestockTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(lblRestockTitle);
        header.add(Box.createVerticalStrut(6));
        pnlRestock = new JPanel(new GridLayout(1, 4, 15, 0));
        pnlRestock.setBackground(BG);
        pnlRestock.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlRestock.setPreferredSize(new Dimension(100, 84));
        pnlRestock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));
        header.add(pnlRestock);
        return header;
    }

    /** One small visual card for a product that is low or out of stock. */
    private JPanel restockCard(Product p) {
        JPanel c = new JPanel(new BorderLayout(0, 4));
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, StockBar.colorFor(p.getStockQuantity(), p.getReorderLevel())),
                new EmptyBorder(8, 10, 8, 10)));
        JLabel name = new JLabel(p.getName());
        name.setFont(new Font("Arial", Font.BOLD, 13));
        name.setForeground(new Color(20, 35, 60));
        JLabel info = new JLabel(p.isOutOfStock() ? "OUT OF STOCK" : p.getStockQuantity() + " left  (min " + p.getReorderLevel() + ")");
        info.setFont(new Font("Arial", Font.PLAIN, 12));
        info.setForeground(StockBar.colorFor(p.getStockQuantity(), p.getReorderLevel()).darker());
        StockBar bar = new StockBar();
        bar.setLevels(p.getStockQuantity(), p.getReorderLevel());
        JPanel text = new JPanel(new GridLayout(2, 1, 0, 0));
        text.setOpaque(false);
        text.add(name);
        text.add(info);
        c.add(text, BorderLayout.NORTH);
        c.add(bar, BorderLayout.CENTER);
        if (Session.isStaff()) {
            JButton in = new JButton("STOCK-IN");
            in.setUI(new javax.swing.plaf.basic.BasicButtonUI());
            in.setFont(new Font("Arial", Font.BOLD, 11));
            in.setForeground(ORANGE);
            in.setContentAreaFilled(false);
            in.setBorderPainted(false);
            in.setFocusPainted(false);
            in.setHorizontalAlignment(SwingConstants.RIGHT);
            in.setMargin(new Insets(0, 0, 0, 0));
            in.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            in.addActionListener(e -> openStockIn(p));
            c.add(in, BorderLayout.SOUTH);
        }
        return c;
    }

    private void refreshRestockCards() {
        pnlRestock.removeAll();
        java.util.List<Product> needs = ProductDAO.findLowStock(); // lowest stock first
        lblRestockTitle.setText(needs.isEmpty() ? "NEEDS RESTOCK" : "NEEDS RESTOCK (" + needs.size() + ")");
        if (needs.isEmpty()) {
            JLabel ok = new JLabel(ProductDAO.count() == 0
                    ? "No products yet. Add products first." : "All products are above their reorder level.");
            ok.setFont(new Font("Arial", Font.PLAIN, 13));
            ok.setForeground(new Color(30, 130, 76));
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(Color.WHITE);
            p.setBorder(new EmptyBorder(8, 12, 8, 12));
            p.add(ok, BorderLayout.CENTER);
            pnlRestock.add(p);
            for (int i = 1; i < 4; i++) {
                pnlRestock.add(new JPanel() {{ setOpaque(false); }});
            }
        } else {
            for (int i = 0; i < 4; i++) {
                if (i < needs.size()) {
                    pnlRestock.add(restockCard(needs.get(i)));
                } else {
                    pnlRestock.add(new JPanel() {{ setOpaque(false); }});
                }
            }
        }
        pnlRestock.revalidate();
        pnlRestock.repaint();
    }

    private void openStockIn(Product preselected) {
        if (ProductDAO.count() == 0) {
            JOptionPane.showMessageDialog(this, "There are no products yet. Add a product first.",
                    "Stock-In", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new StockInDialog(this, preselected, this::refresh).setVisible(true);
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
                new String[]{"ID", "Product", "Category", "Purchase", "Selling", "Stock", "Level", "Reorder", "Status"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new CurrencyRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new StockBarRenderer(i -> shown.get(i)));
        table.getColumnModel().getColumn(8).setCellRenderer(new StatusRenderer());
        int[] widths = {56, 165, 160, 88, 88, 46, 78, 58, 122};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(Color.WHITE);
        if (Session.isStaff()) {
            JButton btnBatches = button("VIEW BATCHES", NAVY);
            btnBatches.addActionListener(e -> viewBatches());
            JButton btnReorder = button("SET REORDER LEVEL", NAVY);
            btnReorder.addActionListener(e -> setReorderLevel());
            JButton btnRestock = button("STOCK-IN / ADD BATCH", ORANGE);
            btnRestock.addActionListener(e -> restock());
            bottom.add(btnBatches);
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
            model.addRow(new Object[]{p.getCode(), p.getName(), p.getCategory(),
                p.getPurchasePrice(), p.getPrice(), p.getStockQuantity(), p.getStockQuantity(),
                p.getReorderLevel(), p.getStockStatus()});
        }

        lblTotalProducts.setText(String.valueOf(ProductDAO.count()));
        lblTotalUnits.setText(String.format("%,d", ProductDAO.totalStockUnits()));
        lblLow.setText(String.valueOf(low));
        lblOut.setText(String.valueOf(out));
        refreshRestockCards();
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

    /** Restock = Stock-In: opens the Add Batch form (selected product pre-filled if one is selected). */
    private void restock() {
        int row = table.getSelectedRow();
        Product p = row == -1 ? null : shown.get(table.convertRowIndexToModel(row));
        openStockIn(p);
    }

    private void viewBatches() {
        Product p = selected();
        if (p != null) {
            new BatchesDialog(this, p).setVisible(true);
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
