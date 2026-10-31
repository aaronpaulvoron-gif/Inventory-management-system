package castroconstructionsuppliesNODB;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Dashboard shown to customers: a welcome header, three summary cards
 * (orders, total spent, last order), their recent orders, and the
 * products currently available. All figures come from the database.
 */
public class CustomerDashboardPanel extends JPanel {

    private static final Color BG = new Color(245, 247, 250);
    private static final Color NAVY_TEXT = new Color(20, 35, 60);
    private static final Color ORANGE = new Color(255, 153, 0);
    private static final Color CAPTION = new Color(230, 126, 34);

    public CustomerDashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 15));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(0, 25, 20, 25));

        List<Sale> orders = SaleDAO.findAll(Session.currentCustomerId);
        center.add(buildCards(orders), BorderLayout.NORTH);
        center.add(buildTables(orders), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    // ---------------------------------------------------------------- header
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(20, 25, 15, 25));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBackground(BG);

        JLabel title = new JLabel("WELCOME, " + Session.currentUserName.toUpperCase());
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(NAVY_TEXT);
        JLabel sub = new JLabel("Browse our products and place your order online");
        sub.setFont(new Font("Arial", Font.PLAIN, 14));
        sub.setForeground(new Color(100, 100, 100));
        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(sub);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(BG);
        JButton btnBrowse = button("BROWSE PRODUCTS", new Color(18, 32, 47));
        btnBrowse.addActionListener(e -> goTo(new ProductsPanel()));
        JButton btnOrder = button("PLACE AN ORDER", ORANGE);
        btnOrder.addActionListener(e -> goTo(new SalesPanel()));
        buttons.add(btnBrowse);
        buttons.add(btnOrder);

        header.add(text, BorderLayout.WEST);
        header.add(buttons, BorderLayout.EAST);
        return header;
    }

    // ----------------------------------------------------------------- cards
    private JPanel buildCards(List<Sale> orders) {
        double spent = 0;
        int pending = 0;
        for (Sale s : orders) {
            if ("Completed".equals(s.getStatus())) {
                spent += s.getTotal();
            } else if ("Pending".equals(s.getStatus())) {
                pending++;
            }
        }
        String last = orders.isEmpty() ? "—" : orders.get(0).getDate(); // newest first

        JPanel cards = new JPanel(new GridLayout(1, 4, 15, 0));
        cards.setBackground(BG);
        cards.add(card("MY ORDERS", String.valueOf(orders.size())));
        cards.add(card("WAITING TO BE CONFIRMED", String.valueOf(pending)));
        cards.add(card("TOTAL SPENT", String.format("₱%,.2f", spent)));
        cards.add(card("LAST ORDER", last));
        return cards;
    }

    private JPanel card(String caption, String value) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel c = new JLabel(caption);
        c.setFont(new Font("Arial", Font.BOLD, 12));
        c.setForeground(CAPTION);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Arial", Font.BOLD, 28));
        v.setForeground(NAVY_TEXT);

        p.add(c);
        p.add(Box.createVerticalStrut(6));
        p.add(v);
        return p;
    }

    // ---------------------------------------------------------------- tables
    private JPanel buildTables(List<Sale> orders) {
        JPanel row = new JPanel(new GridLayout(1, 2, 15, 0));
        row.setBackground(BG);
        row.add(buildOrdersCard(orders));
        row.add(buildProductsCard());
        return row;
    }

    private JPanel buildOrdersCard(List<Sale> orders) {
        DefaultTableModel model = readOnlyModel("Order ID", "Date", "Items", "Total", "Status");
        int shown = Math.min(orders.size(), 10);
        for (int i = 0; i < shown; i++) {
            Sale s = orders.get(i);
            int units = 0;
            for (SaleItem item : s.getItems()) {
                units += item.getQuantity();
            }
            model.addRow(new Object[]{s.getId(), s.getDate(), units, s.getTotal(), s.getStatus()});
        }
        String hint = orders.isEmpty() ? "You have no orders yet. Place your first order!" : "Your latest orders";
        return tableCard("MY RECENT ORDERS", hint, model, 3, 4);
    }

    private JPanel buildProductsCard() {
        DefaultTableModel model = readOnlyModel("Product", "Category", "Unit", "Price", "Availability");
        List<Product> products = ProductDAO.findInStock();
        for (Product p : products) {
            model.addRow(new Object[]{p.getName(), p.getCategory(), p.getUnit(),
                p.getPrice(), p.isLowStock() ? "LIMITED STOCK" : "AVAILABLE"});
        }
        String hint = products.isEmpty() ? "No products are available right now." : "Products you can order today";
        return tableCard("AVAILABLE PRODUCTS", hint, model, 3, 4);
    }

    private JPanel tableCard(String title, String hint, DefaultTableModel model,
            Integer currencyColumn, Integer statusColumn) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setBackground(Color.WHITE);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 15));
        t.setForeground(NAVY_TEXT);
        JLabel h = new JLabel(hint);
        h.setFont(new Font("Arial", Font.PLAIN, 12));
        h.setForeground(new Color(120, 120, 120));
        head.add(t);
        head.add(Box.createVerticalStrut(3));
        head.add(h);
        card.add(head, BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        if (currencyColumn != null) {
            table.getColumnModel().getColumn(currencyColumn).setCellRenderer(new CurrencyRenderer());
        }
        if (statusColumn != null) {
            table.getColumnModel().getColumn(statusColumn).setCellRenderer(new StatusRenderer());
        }
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(new Object[][]{}, columns) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void goTo(JPanel panel) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof MainFrame) {
            ((MainFrame) w).showPanel(panel);
        }
    }
}
