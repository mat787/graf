public class GraphPartitioner {

    public void partition(Graph graph, int partitions, int margin) {
        if (graph == null || graph.wezly == null) return;

        for (int i = 0; i < graph.wezly.length; i++) {
            graph.wezly[i].numer = i % partitions;
        }
    }
}
