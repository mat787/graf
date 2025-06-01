import java.io.*;
import java.util.*;

public class FileHandler {

    public static int policzElementy(String bufor) {
        if (bufor == null || bufor.isEmpty()) return 0;
        bufor = bufor.trim();
        String[] parts = bufor.split(";");
        int liczba = parts.length;
        if (parts.length > 0 && parts[parts.length-1].trim().isEmpty()) liczba--;
        return liczba;
    }


    public static boolean konwertujElement(String bufor, int[] element) {
        try {
            element[0] = Integer.parseInt(bufor.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void dumpTable(Graph graf) {
        for (int y = 0; y < graf.liczbaWierszy; y++) {
            for (int x = 0; x < graf.liczbaKolumn; x++) {
                if (graf.tablicaWezlow[y * graf.liczbaKolumn + x] == -1)
                    System.out.print("0");
                else
                    System.out.print("1");
            }
            System.out.println();
        }
    }


    public static Graph wczytajGraf(String nazwaPliku) {
        try (BufferedReader reader = new BufferedReader(new FileReader(nazwaPliku))) {
            Graph graf = new Graph();

            graf.liczbaKolumn = Integer.parseInt(reader.readLine().trim());

            String bufor = reader.readLine();

            String bufor2 = reader.readLine();

            String[] wezlyParts = bufor.split(";");
            graf.liczbaWezlow = wezlyParts.length;
            graf.wezly = new Node[graf.liczbaWezlow];
            for (int i = 0; i < wezlyParts.length; i++) {
                int[] val = new int[1];
                if (konwertujElement(wezlyParts[i], val)) {
                    graf.wezly[i] = new Node();
                    graf.wezly[i].kolumna = val[0];
                }
            }

            String[] rowBounds = bufor2.split(";");
            graf.liczbaWierszy = rowBounds.length - 1;
            graf.tablicaWezlow = new int[graf.liczbaKolumn * graf.liczbaWierszy];
            Arrays.fill(graf.tablicaWezlow, -1);

            for (int row = 0; row < graf.liczbaWierszy; row++) {
                int a = Integer.parseInt(rowBounds[row].trim());
                int b = Integer.parseInt(rowBounds[row + 1].trim());
                for (int index = a; index < b; index++) {
                    graf.wezly[index].wiersz = row;
                    graf.tablicaWezlow[graf.wezly[index].wiersz * graf.liczbaKolumn + graf.wezly[index].kolumna] = index;
                }
            }

            dumpTable(graf);

            String buforE = reader.readLine();
            String buforE2 = reader.readLine();

            if (buforE != null && buforE2 != null)
            {
                String[] edgesParts = buforE.split(";");
                int edgesCount = edgesParts.length;
                int[] edges = new int[edgesCount];
                for (int i = 0; i < edgesParts.length; i++) {
                    int[] val = new int[1];
                    if (konwertujElement(edgesParts[i], val)) {
                        edges[i] = val[0];
                    }
                }


                String[] edgeBounds = buforE2.split(";");
                int liczbaKrawedzi = 0;
                int ileWezlow = 0;
                int a = 0, b = 0;
                for (int i = 0; i < edgeBounds.length - 1; i++) {
                    a = Integer.parseInt(edgeBounds[i].trim());
                    b = Integer.parseInt(edgeBounds[i + 1].trim());
                    if (a >= b) continue;
                    Node node = graf.wezly[edges[a]];
                    node.liczbaWezlowPowiazanych = b - a - 1;
                    node.listaPowiazan = new int[node.liczbaWezlowPowiazanych];
                    int edgeIndex = 0;
                    for (int idx2 = a + 1; idx2 < b; idx2++) {
                        node.listaPowiazan[edgeIndex++] = edges[idx2];
                        liczbaKrawedzi++;
                    }
                    ileWezlow++;
                }
                System.out.println("Wczytano krawędzi: " + liczbaKrawedzi);
                System.out.println("Wczytano węzłów z krawędziami: " + ileWezlow);
            } else {
                System.err.println("Błąd: Brak danych o krawędziach.");
            }
            return graf;
        } catch (IOException e) {
            System.err.println("Błąd: Nie można otworzyć pliku " + nazwaPliku);
        } catch (Exception e) {
            System.err.println("Błąd: " + e.getMessage());
        }
        return null;
    }

    public static void zapiszGraf(String nazwaPliku, Graph graf, int liczbaPodzialow, int[] przypisania) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nazwaPliku))) {
            System.out.printf("Zapisuje graf w formacie tekstowym do pliku %s.\n", nazwaPliku);

            writer.println(graf.liczbaKolumn);

            for (int i = 0; i < graf.liczbaWezlow; i++) {
                writer.print(graf.wezly[i].kolumna);
                if (i < graf.liczbaWezlow - 1) writer.print(";");
            }
            writer.println();

            int previousIndex = 0;
            int currentIndex = 0;
            for (int i = 0; i < graf.liczbaWierszy; i++) {
                for (int j = 0; j < graf.liczbaKolumn; j++) {
                    int index = i * graf.liczbaKolumn + j;
                    if (graf.tablicaWezlow[index] != -1) {
                        currentIndex = graf.tablicaWezlow[index] + 1;
                    }
                }
                writer.print(previousIndex + ";");
                previousIndex = currentIndex;
            }
            writer.println(previousIndex);

            for (int i = 0; i < graf.liczbaWezlow; i++) {
                if (graf.wezly[i].liczbaWezlowPowiazanych > 0) {
                    writer.print(i + ";");
                }
                for (int j = 0; j < graf.wezly[i].liczbaWezlowPowiazanych; j++) {
                    writer.print(graf.wezly[i].listaPowiazan[j]);
                    writer.print(";");
                }
            }
            writer.println();

            if (przypisania != null) {
                for (int a = 0; a < liczbaPodzialow; a++) {
                    int edgeIndex = 0;
                    for (int i = 0; i < graf.liczbaWezlow; i++) {
                        if (przypisania[i] == a) {
                            if (graf.wezly[i].listaPowiazan != null) {
                                writer.print(edgeIndex + ";");
                                edgeIndex += graf.wezly[i].liczbaWezlowPowiazanych + 1;
                            }
                        }
                    }
                    writer.print(edgeIndex);
                    if (a < liczbaPodzialow - 1) writer.println();
                }
            } else {
                int edgeIndex = 0;
                for (int i = 0; i < graf.liczbaWezlow; i++) {
                    if (graf.wezly[i].listaPowiazan != null) {
                        writer.print(edgeIndex + ";");
                        edgeIndex += graf.wezly[i].liczbaWezlowPowiazanych + 1;
                    }
                }
                writer.print(edgeIndex); // <--- najważniejsze!
                // NIE dodawaj writer.println() na końcu!
            }
            System.out.printf("Graf został zapisany do pliku %s.\n", nazwaPliku);
        } catch (IOException e) {
            System.err.printf("Błąd: Nie można otworzyć pliku %s do zapisu.\n", nazwaPliku);
        }
    }

    // Zapis grafu w formacie binarnym (każda liczba jako int, wartości po kolei)
    public static void zapiszGrafBinarnie(String nazwaPliku, Graph graf, int liczbaPodzialow, int[] przypisania) {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(nazwaPliku))) {
            System.out.printf("Zapisuje graf w formacie binarnym do pliku %s.\n", nazwaPliku);

            // 1. Liczba kolumn
            out.writeInt(graf.liczbaKolumn);

            // 2. Indeksy kolumn węzłów
            for (int i = 0; i < graf.liczbaWezlow; i++) {
                out.writeInt(graf.wezly[i].kolumna);
            }

            // 3. Wskaźniki na pierwsze indeksy wierszy w liście (bufor2)
            int previousIndex = 0;
            int currentIndex = 0;
            for (int i = 0; i < graf.liczbaWierszy; i++) {
                for (int j = 0; j < graf.liczbaKolumn; j++) {
                    int index = i * graf.liczbaKolumn + j;
                    if (graf.tablicaWezlow[index] != -1) {
                        currentIndex = graf.tablicaWezlow[index] + 1;
                    }
                }
                out.writeInt(previousIndex);
                previousIndex = currentIndex;
            }
            out.writeInt(previousIndex);

            // 4. Grupy węzłów/krawędzi
            for (int i = 0; i < graf.liczbaWezlow; i++) {
                if (graf.wezly[i].liczbaWezlowPowiazanych > 0) {
                    out.writeInt(i);
                }
                for (int j = 0; j < graf.wezly[i].liczbaWezlowPowiazanych; j++) {
                    out.writeInt(graf.wezly[i].listaPowiazan[j]);
                }
            }

            // 5. Wskaźniki na pierwsze węzły w grupach (buforE2)
            if (przypisania != null) {
                for (int a = 0; a < liczbaPodzialow; a++) {
                    int edgeIndex = 0;
                    for (int i = 0; i < graf.liczbaWezlow; i++) {
                        if (przypisania[i] == a) {
                            if (graf.wezly[i].listaPowiazan != null) {
                                out.writeInt(edgeIndex);
                                edgeIndex += graf.wezly[i].liczbaWezlowPowiazanych + 1;
                            }
                        }
                    }
                    out.writeInt(edgeIndex);
                }
            } else {
                int edgeIndex = 0;
                for (int i = 0; i < graf.liczbaWezlow; i++) {
                    if (graf.wezly[i].listaPowiazan != null) {
                        out.writeInt(edgeIndex);
                        edgeIndex += graf.wezly[i].liczbaWezlowPowiazanych + 1;
                    }
                }
                out.writeInt(edgeIndex);
            }
            System.out.printf("Graf został zapisany binarnie do pliku %s.\n", nazwaPliku);
        } catch (IOException e) {
            System.err.printf("Błąd: Nie można otworzyć pliku %s do zapisu.\n", nazwaPliku);
        }
    }
}