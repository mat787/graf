import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class MainWindow extends JFrame {
    private GraphView graphView;
    private Graph graph;
    private JTextField marginField;
    private JTextField partsField; // zamiast ComboBox
    private JTextArea statusArea;
    private int[] przypisania;
    private int liczbaPodzialow = 2;
    private JPanel subgraphButtonsPanel;
    private ArrayList<JButton> subgraphButtons = new ArrayList<>();
    private JButton showAllButton, showColoredButton;

    public MainWindow() {
        setTitle("Podział grafu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(255, 240, 245));


        graphView = new GraphView(null);
        graphView.setBorder(BorderFactory.createTitledBorder("Miejsce na rysunek grafu"));
        graphView.setBackground(new Color(255, 245, 250));
        add(graphView, BorderLayout.CENTER);


        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(252, 232, 239));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));


        JPanel graphSelectPanel = new JPanel();
        graphSelectPanel.setLayout(new BoxLayout(graphSelectPanel, BoxLayout.Y_AXIS));
        graphSelectPanel.setBackground(new Color(252, 232, 239));
        graphSelectPanel.setBorder(BorderFactory.createTitledBorder("Wybierz graf do podziału"));

        JButton openGraphBtn = new JButton("Wczytaj graf...");
        openGraphBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        openGraphBtn.addActionListener(e -> loadGraph());
        graphSelectPanel.add(openGraphBtn);
        graphSelectPanel.add(Box.createRigidArea(new Dimension(0, 10)));



        sidePanel.add(graphSelectPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 15)));


        JPanel marginPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        marginPanel.setBackground(new Color(252, 232, 239));
        JLabel marginLabel = new JLabel("Margines:");
        marginField = new JTextField("10", 4);
        marginPanel.add(marginLabel);
        marginPanel.add(marginField);

        sidePanel.add(marginPanel);


        JPanel partitionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        partitionsPanel.setBackground(new Color(252, 232, 239));
        JLabel partitionsLabel = new JLabel("Liczba części:");
        partsField = new JTextField("2", 4);
        partitionsPanel.add(partitionsLabel);
        partitionsPanel.add(partsField);

        sidePanel.add(partitionsPanel);


        JButton startBtn = new JButton("Rozpocznij podział!");
        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        startBtn.setBackground(new Color(234, 183, 202));
        startBtn.setFocusPainted(false);
        startBtn.addActionListener(e -> startPartitioning());

        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(startBtn);


        subgraphButtonsPanel = new JPanel();
        subgraphButtonsPanel.setLayout(new BoxLayout(subgraphButtonsPanel, BoxLayout.Y_AXIS));
        subgraphButtonsPanel.setBackground(new Color(252, 232, 239));
        subgraphButtonsPanel.setBorder(BorderFactory.createTitledBorder("Podgrafy"));
        sidePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        sidePanel.add(subgraphButtonsPanel);


        JButton saveTxtBtn = new JButton("Zapisz do pliku tekstowego");
        saveTxtBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveTxtBtn.setBackground(new Color(200, 210, 244));
        saveTxtBtn.setFocusPainted(false);
        saveTxtBtn.addActionListener(e -> saveGraphToText());
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(saveTxtBtn);


        JButton saveBinBtn = new JButton("Zapisz do pliku binarnego");
        saveBinBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBinBtn.setBackground(new Color(180, 200, 210));
        saveBinBtn.setFocusPainted(false);
        saveBinBtn.addActionListener(e -> saveGraphToBinary());
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(saveBinBtn);


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
            clearSubgraphButtons();
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
        try {
            liczbaPodzialow = Integer.parseInt(partsField.getText().trim());
            if (liczbaPodzialow < 1 || liczbaPodzialow > graph.liczbaWezlow) {
                statusArea.setText("Nieprawidłowa liczba części.");
                return;
            }
        } catch (NumberFormatException ex) {
            statusArea.setText("Błędna liczba części!");
            return;
        }

        przypisania = new int[graph.liczbaWezlow];
        for (int i = 0; i < graph.liczbaWezlow; i++) {
         przypisania[i] = i % liczbaPodzialow;
        }


        //for (int i = 0; i < graph.liczbaWezlow; i++) {
        //    graph.wezly[i].numer = przypisania[i];
       // }

        statusArea.setText("Podział wykonany.");
        graphView.setGraphColored(graph, przypisania, liczbaPodzialow);
        updateSubgraphButtons();
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
        if (!path.toLowerCase().endsWith(".txt")) {
            path += ".txt";
        }
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
        if (!path.toLowerCase().endsWith(".bin")) {
            path += ".bin";
        }
        FileHandler.zapiszGrafBinarnie(path, graph, liczbaPodzialow, przypisania);
        statusArea.setText("Zapisano graf do pliku binarnego.");
    }


    private void clearSubgraphButtons() {
        subgraphButtonsPanel.removeAll();
        subgraphButtons.clear();
        if (showAllButton != null) showAllButton.setEnabled(false);
        if (showColoredButton != null) showColoredButton.setEnabled(false);
        subgraphButtonsPanel.revalidate();
        subgraphButtonsPanel.repaint();
    }

    private void updateSubgraphButtons() {
        clearSubgraphButtons();
        if (graph == null || przypisania == null) return;


        showColoredButton = new JButton("Pokaż kolorowy graf główny");
        showColoredButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        showColoredButton.setBackground(new Color(200, 230, 200));
        showColoredButton.setFocusPainted(false);
        showColoredButton.addActionListener(e -> {
            graphView.setGraphColored(graph, przypisania, liczbaPodzialow);
            statusArea.setText("Wyświetlono kolorowy graf główny.");
        });
        showColoredButton.setEnabled(true);
        subgraphButtonsPanel.add(showColoredButton);
        subgraphButtonsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        showAllButton = new JButton("Pokaż cały graf");
        showAllButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        showAllButton.setBackground(new Color(220, 220, 220));
        showAllButton.setFocusPainted(false);
        showAllButton.addActionListener(e -> {
            graphView.setGraph(graph);
            statusArea.setText("Wyświetlono cały graf.");
        });
        showAllButton.setEnabled(true);
        subgraphButtonsPanel.add(showAllButton);
        subgraphButtonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));


        for (int cz = 0; cz < liczbaPodzialow; cz++) {
            final int part = cz;
            JButton btn = new JButton("Pokaż część " + (cz + 1));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setBackground(new Color(234, 183, 202));
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                Graph subgraph = SubgraphBuilder.buildSubgraph(graph, przypisania, part);
                graphView.setGraph(subgraph);
                statusArea.setText("Wyświetlono część " + (part + 1) + ".");
            });
            subgraphButtons.add(btn);
            subgraphButtonsPanel.add(btn);
            subgraphButtonsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        subgraphButtonsPanel.revalidate();
        subgraphButtonsPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}