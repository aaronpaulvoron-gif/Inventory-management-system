package castroconstructionsuppliesNODB;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/** Lists every batch (delivery) of one product, newest first. */
public class BatchesDialog extends JDialog {

    public BatchesDialog(Component parent, Product product) {
        super(parent == null ? null : SwingUtilities.getWindowAncestor(parent),
                "Batches - " + product.getName(), ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(Theme.SOFT);
        root.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("BATCHES  |  " + product.getName());
        title.setFont(Theme.HEADING);
        title.setForeground(Theme.NAVY);
        root.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new Object[][]{},
                new String[]{"Batch No.", "Date Received", "Qty Received", "Qty Left", "Purchase Price", "Batch Cost", "Notes"}) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        int left = 0;
        for (StockBatch b : BatchDAO.findByProduct(product.getId())) {
            model.addRow(new Object[]{b.getBatchNo(), b.getReceivedDate(), b.getQuantityReceived(),
                b.getQuantityRemaining(), b.getPurchasePrice(), b.getTotalCost(), b.getNotes()});
            left += b.getQuantityRemaining();
        }
        JTable table = new JTable(model);
        Theme.styleTable(table);
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(4).setCellRenderer(new CurrencyRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new CurrencyRenderer());
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.setPreferredSize(new Dimension(700, 260));
        root.add(scroll, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        JLabel sum = new JLabel(model.getRowCount() == 0 ? "No batches recorded yet."
                : String.format("%d batch(es)   |   %d unit(s) left in batches   |   Current stock: %d",
                        model.getRowCount(), left, product.getStockQuantity()));
        sum.setFont(Theme.BODY);
        sum.setForeground(Theme.STEEL);
        JButton close = Theme.secondaryButton("CLOSE");
        close.addActionListener(e -> dispose());
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btn.setOpaque(false);
        btn.add(close);
        south.add(sum, BorderLayout.WEST);
        south.add(btn, BorderLayout.EAST);
        root.add(south, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }
}
