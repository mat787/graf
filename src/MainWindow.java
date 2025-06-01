import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MainWindow extends JFrame {
    private GraphView graphView;
    private Graph graph;
    private JTextField marginField;
    private JComboBox<Integer> partitionsCombo;
    private JTextArea statusArea;
    private int[] przypisania; // przypisania węzłów do partycji
    private int liczbaPodzialow = 2;

    public MainWindow() {
        setTitle("Podział grafu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(255, 240, 245));

        // Obszar rysowania grafu
        graphView = new GraphView(null);
        graphView.setBorder(BorderFactory.createTitledBorder("Miejsce na rysunek grafu"));
        graphView.setBackground(new Color(255, 245, 250));
        add(graphView, BorderLayout.CENTER);

        // Panel boczny
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(252, 232, 239));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel wyboru grafu do podziału
        JPanel graphSelectPanel = new JPanel();
        graphSelectPanel.setLayout(new BoxLayout(graphSelectPanel, BoxLayout.Y_AXIS));
        graphSelectPanel.setBackground(new Color(252, 232, 239));
        graphSelectPanel.setBorder(BorderFactory.createTitledBorder("Wybierz graf do podziału"));

        JButton openGraphBtn = new JButton("Wczytaj graf...");
        openGraphBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        openGraphBtn.addActionListener(e -> loadGraph());

        graphSelectPanel.add(openGraphBtn);
        graphSelectPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Lista przykładowych grafów (możesz dodać własne pliki)
        JRadioButton part1 = new JRadioButton("graf 1");
        JRadioButton part2 = new JRadioButton("graf 2");
        JRadioButton part3 = new JRadioButton("graf 3");
        part1.setBackground(new Color(252, 232, 239));
        part2.setBackground(new Color(252, 232, 239));
        part3.setBackground(new Color(252, 232, 239));
        ButtonGroup group = new ButtonGroup();
        group.add(part1);
        group.add(part2);
        group.add(part3);
        part1.setSelected(true);

        graphSelectPanel.add(part1);
        graphSelectPanel.add(part2);
        graphSelectPanel.add(part3);

        sidePanel.add(graphSelectPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Pole do ustawiania marginesu
        JPanel marginPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        marginPanel.setBackground(new Color(252, 232, 239));
        JLabel marginLabel = new JLabel("Margines:");
        marginField = new JTextField("10", 4);
        marginPanel.add(marginLabel);
        marginPanel.add(marginField);

        sidePanel.add(marginPanel);

        // Pole do ustawiania liczby części podziału
        JPanel partitionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        partitionsPanel.setBackground(new Color(252, 232, 239));
        JLabel partitionsLabel = new JLabel("Liczba części:");
        partitionsCombo = new JComboBox<>(new Integer[]{2, 3, 4, 5, 6});
        partitionsPanel.add(partitionsLabel);
        partitionsPanel.add(partitionsCombo);

        sidePanel.add(partitionsPanel);

        // Przycisk "Rozpocznij podział!"
        JButton startBtn = new JButton("Rozpocznij podział!");
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setBackground(new Color(234, 183, 202));
        startBtn.setFocusPainted(false);
        startBtn.addActionListener(e -> startPartitioning());

        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(startBtn);

        // Przycisk zapisu do pliku tekstowego
        JButton saveTxtBtn = new JButton("Zapisz do pliku tekstowego");
        saveTxtBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveTxtBtn.setBackground(new Color(200, 210, 244));
        saveTxtBtn.setFocusPainted(false);
        saveTxtBtn.addActionListener(e -> saveGraphToText());
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(saveTxtBtn);

        // Przycisk zapisu do pliku binarnego
        JButton saveBinBtn = new JButton("Zapisz do pliku binarnego");
        saveBinBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBinBtn.setBackground(new Color(180, 200, 210));
        saveBinBtn.setFocusPainted(false);
        saveBinBtn.addActionListener(e -> saveGraphToBinary());
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(saveBinBtn);

        // Pole na komunikaty o błędach/sukcesie
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        statusArea = new JTextArea(3, 18);
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setBackground(new Color(255, 245, 250));
        statusArea.setBorder(BorderFactory.createTitledBorder("Status"));
        sidePanel.add(statusArea);

        add(sidePanel, BorderLayout.EAST);

        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void loadGraph() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            graph = FileHandler.wczytajGraf(path);
            przypisania = null;
            if (graph != null) {
                graphView.setGraph(graph);
                statusArea.setText("Pomyślnie wczytano graf.");
            } else {
                statusArea.setText("Błąd podczas wczytywania grafu.");
            }
        }
    }

    private void startPartitioning() {
        if (graph == null) {
            statusArea.setText("Najpierw wczytaj graf.");
            return;
        }
        int margin = 10;
        try {
            margin = Integer.parseInt(marginField.getText().trim());
        } catch (NumberFormatException ex) {
            statusArea.setText("Błędny margines!");
            return;
        }
        liczbaPodzialow = (Integer) partitionsCombo.getSelectedItem();

        // Przykład: partycjonujemy "na okrągło"
        przypisania = new int[graph.liczbaWezlow];
        for (int i = 0; i < graph.liczbaWezlow; i++) {
            przypisania[i] = i % liczbaPodzialow;
        }

        // Jeśli masz swój algorytm partycjonowania, ustaw przypisania tutaj!

        for (int i = 0; i < graph.liczbaWezlow; i++) {
            graph.wezly[i].numer = przypisania[i];
        }

        statusArea.setText("Podział wykonany.");
        graphView.setGraph(graph); // odśwież rysunek
    }

    private void saveGraphToText() {
        if (graph == null) {
            statusArea.setText("Najpierw wczytaj graf i wykonaj podział.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Zapisz graf do pliku tekstowego");
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        String path = chooser.getSelectedFile().getAbsolutePath();
        FileHandler.zapiszGraf(path, graph, liczbaPodzialow, przypisania);
        statusArea.setText("Zapisano graf do pliku tekstowego.");
    }

    private void saveGraphToBinary() {
        if (graph == null) {
            statusArea.setText("Najpierw wczytaj graf i wykonaj podział.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Zapisz graf do pliku binarnego");
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        String path = chooser.getSelectedFile().getAbsolutePath();
        FileHandler.zapiszGrafBinarnie(path, graph, liczbaPodzialow, przypisania);
        statusArea.setText("Zapisano graf do pliku binarnego.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}