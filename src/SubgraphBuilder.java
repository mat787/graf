import java.util.ArrayList;
import java.util.HashMap;

public class SubgraphBuilder {

    public static Graph buildSubgraph(Graph original, int[] przypisania, int czesc) {

        ArrayList<Integer> nodesInPart = new ArrayList<>();
        for (int i = 0; i < przypisania.length; i++) {
            if (przypisania[i] == czesc) nodesInPart.add(i);
        }
        int n = nodesInPart.size();
        if (n == 0) return null;


        HashMap<Integer, Integer> mapOldToNew = new HashMap<>();
        for (int i = 0; i < n; i++) mapOldToNew.put(nodesInPart.get(i), i);


        Node[] newNodes = new Node[n];
        for (int i = 0; i < n; i++) {
            int origIdx = nodesInPart.get(i);
            Node orig = original.wezly[origIdx];
            Node nowy = new Node();
            nowy.numer = origIdx;
            nowy.wiersz = orig.wiersz;
            nowy.kolumna = orig.kolumna;
            nowy.liczbaWezlowPowiazanych = 0;
            newNodes[i] = nowy;
        }


        for (int i = 0; i < n; i++) {
            int origIdx = nodesInPart.get(i);
            Node orig = original.wezly[origIdx];
            ArrayList<Integer> sasiedzi = new ArrayList<>();
            if (orig.listaPowiazan != null) {
                for (int v : orig.listaPowiazan) {

                    if (mapOldToNew.containsKey(v)) {
                        sasiedzi.add(mapOldToNew.get(v));
                    }
                }
            }
            newNodes[i].liczbaWezlowPowiazanych = sasiedzi.size();
            newNodes[i].listaPowiazan = sasiedzi.stream().mapToInt(x -> x).toArray();
        }


        Graph sub = new Graph();
        sub.liczbaWezlow = n;
        sub.wezly = newNodes;
        sub.liczbaWierszy = original.liczbaWierszy;
        sub.liczbaKolumn = original.liczbaKolumn;
        sub.tablicaWezlow = original.tablicaWezlow;

        return sub;
    }
}