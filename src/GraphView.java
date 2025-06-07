import javax.swing.*;
import java.awt.*;

public class GraphView extends JPanel {
    private Graph graph;
    private int[] kolorowanie = null;
    private int liczbaCzesci = 0;

    public GraphView(Graph graph) {
        this.graph = graph;
        setPreferredSize(new Dimension(600, 500));
        setBackground(new Color(255, 245, 250));
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
        this.kolorowanie = null;
        repaint();
    }


    public void setGraphColored(Graph graph, int[] przypisania, int liczbaCzesci) {
        this.graph = graph;
        this.kolorowanie = przypisania;
        this.liczbaCzesci = liczbaCzesci;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null || graph.wezly == null) {
            g.setColor(new Color(180, 140, 150));
            g.drawString("Miejsce na rysunek grafu.", getWidth() / 2 - 60, getHeight() / 2);
            return;
        }

        int margin = 40;
        int nodeSize = 24;

        for (Node node : graph.wezly) {
            int x = margin + node.kolumna * 50;
            int y = margin + node.wiersz * 50;
            if (node.listaPowiazan != null) {
                for (int idx : node.listaPowiazan) {
                    Node target = graph.wezly[idx];
                    int tx = margin + target.kolumna * 50;
                    int ty = margin + target.wiersz * 50;
                    g.setColor(new Color(180, 180, 200));
                    g.drawLine(x + nodeSize / 2, y + nodeSize / 2, tx + nodeSize / 2, ty + nodeSize / 2);
                }
            }
        }

        for (int i = 0; i < graph.wezly.length; i++) {
            Node node = graph.wezly[i];
            int x = margin + node.kolumna * 50;
            int y = margin + node.wiersz * 50;
            Color kolor = new Color(234, 183, 202);
            if (kolorowanie != null && i < kolorowanie.length) {
                kolor = getColorForPart(kolorowanie[i], liczbaCzesci);
            }
            g.setColor(kolor);
            g.fillOval(x, y, nodeSize, nodeSize);
            g.setColor(Color.DARK_GRAY);
            g.drawOval(x, y, nodeSize, nodeSize);
            g.drawString("" + node.numer, x + 8, y + 16);
        }
    }


    private Color getColorForPart(int part, int liczbaCzesci) {
        Color[] palette = {
                new Color(234,183,202), // pastel pink
                new Color(180,200,244), // pastel blue
                new Color(183,234,202), // pastel green
                new Color(244,234,180), // pastel yellow
                new Color(210,180,244), // pastel violet
                new Color(244,180,216), // pastel magenta
                new Color(180,244,244), // pastel cyan
                new Color(244,200,180)  // pastel orange
        };
        if (part < palette.length) return palette[part];

        float hue = (float)part/liczbaCzesci;
        return Color.getHSBColor(hue, 0.3f, 1.0f);
    }
}