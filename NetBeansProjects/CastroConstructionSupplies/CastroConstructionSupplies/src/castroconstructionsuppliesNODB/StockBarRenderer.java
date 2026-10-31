package castroconstructionsuppliesNODB;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.function.IntFunction;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/** Table cell that draws a stock-level bar for the product on that row. */
public class StockBarRenderer extends JPanel implements TableCellRenderer {

    private final IntFunction<Product> productAtModelRow;
    private int stock;
    private int reorder;

    /** @param productAtModelRow gives the product shown on a model row (not a view row) */
    public StockBarRenderer(IntFunction<Product> productAtModelRow) {
        this.productAtModelRow = productAtModelRow;
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Product p = productAtModelRow.apply(table.convertRowIndexToModel(row));
        stock = p == null ? 0 : p.getStockQuantity();
        reorder = p == null ? 0 : p.getReorderLevel();
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int h = 10;
        StockBar.paintBar((Graphics2D) g.create(), 8, (getHeight() - h) / 2, Math.max(getWidth() - 18, 10), h, stock, reorder);
    }
}
