package castroconstructionsuppliesNODB;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/** A small horizontal bar showing how full the stock is: red = out, amber = low, green = healthy. */
public class StockBar extends JComponent {

    private int stock;
    private int reorder;

    public StockBar() {
        setPreferredSize(new Dimension(80, 10));
        setOpaque(false);
    }

    public void setLevels(int stock, int reorder) {
        this.stock = stock;
        this.reorder = reorder;
        repaint();
    }

    public static Color colorFor(int stock, int reorder) {
        if (stock <= 0) {
            return Theme.DANGER;
        }
        return stock <= reorder ? Theme.WARNING : Theme.SUCCESS;
    }

    /** Draws the bar inside the given rectangle (shared with the table cell renderer). */
    public static void paintBar(Graphics2D g, int x, int y, int w, int h, int stock, int reorder) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int max = Math.max(Math.max(reorder * 3, stock), 1);
        g.setColor(Theme.BORDER);
        g.fillRoundRect(x, y, w, h, h, h);
        int fill = (int) Math.round(w * Math.min(1.0, (double) Math.max(stock, 0) / max));
        if (stock > 0) {
            g.setColor(colorFor(stock, reorder));
            g.fillRoundRect(x, y, Math.max(fill, h), h, h, h);
        }
        // tick = the reorder level
        int tick = x + (int) Math.round(w * Math.min(1.0, (double) reorder / max));
        g.setColor(Theme.NAVY);
        g.drawLine(tick, y - 1, tick, y + h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        int h = Math.min(getHeight(), 10);
        paintBar((Graphics2D) g.create(), 0, (getHeight() - h) / 2, getWidth() - 1, h, stock, reorder);
    }
}
