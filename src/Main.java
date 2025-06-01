

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
/*
class Main {
    public static void main(String[] args) {
        // podaj ścieżkę do pliku z grafem
        String filename = "graftestsigma.txt";
        Graph graph = FileHandler.wczytajGraf(filename);

        if (graph != null) {
            System.out.println("Graf poprawnie wczytany!");
            // np. wypisz liczbę węzłów
            System.out.println("Liczba węzłów: " + graph.liczbaWezlow);
        } else {
            System.out.println("Błąd podczas wczytywania grafu.");
        }
    }
}

 */