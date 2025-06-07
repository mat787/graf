public class GraphPartitioner {

    void podzielGrafBFSZaawansowany(Graph graf, int liczbaCzesci, float marginesProcentowy, int[] przypisania) {
        boolean[] odwiedzone = new boolean[graf.liczbaWezlow];
        int[] rozmiaryCzesci = new int[liczbaCzesci];
        int maksNaCzesc = (int)Math.ceil((1.0 + marginesProcentowy / 100.0) * graf.liczbaWezlow / (double)liczbaCzesci);
        int minNaCzesc = (int)Math.floor((1.0 - marginesProcentowy / 100.0) * graf.liczbaWezlow / (double)liczbaCzesci);

        for (int start = 0; start < graf.liczbaWezlow; start++) {
            if (!odwiedzone[start]) {

                int[] kolejka = new int[graf.liczbaWezlow];
                int przod = 0, tyl = 0;
                kolejka[tyl++] = start;

                while (przod < tyl) {
                    int u = kolejka[przod++];
                    if (odwiedzone[u]) continue;


                    int najlepszaCzesc = -1;
                    int maxSasiadowWTymSamym = -1;

                    for (int cz = 0; cz < liczbaCzesci; cz++) {
                        if (rozmiaryCzesci[cz] >= maksNaCzesc) continue;

                        int sasiadow = 0;
                        for (int i = 0; i < graf.wezly[u].liczbaWezlowPowiazanych; i++) {
                            int v = graf.wezly[u].listaPowiazan[i];
                            if (odwiedzone[v] && przypisania[v] == cz) {
                                sasiadow++;
                            }
                        }
                        if (sasiadow > maxSasiadowWTymSamym || (sasiadow == maxSasiadowWTymSamym && najlepszaCzesc == -1)) {
                            najlepszaCzesc = cz;
                            maxSasiadowWTymSamym = sasiadow;
                        }
                    }

                    if (najlepszaCzesc == -1) {

                        for (int cz = 0; cz < liczbaCzesci; cz++) {
                            if (rozmiaryCzesci[cz] < maksNaCzesc) {
                                najlepszaCzesc = cz;
                                break;
                            }
                        }
                    }

                    if (najlepszaCzesc == -1) najlepszaCzesc = 0;

                    przypisania[u] = najlepszaCzesc;
                    odwiedzone[u] = true;
                    rozmiaryCzesci[najlepszaCzesc]++;


                    for (int i = 0; i < graf.wezly[u].liczbaWezlowPowiazanych; i++) {
                        int v = graf.wezly[u].listaPowiazan[i];
                        if (!odwiedzone[v]) {
                            kolejka[tyl++] = v;
                        }
                    }
                }

            }
        }

    }
    }

