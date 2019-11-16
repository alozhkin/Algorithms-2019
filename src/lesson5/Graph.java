package lesson5;

import lesson7.ants.Choosable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public interface Graph {

    interface Vertex {
        @NotNull
        String getName();
    }

    interface Edge extends Choosable {
        @NotNull
        Vertex getBegin();

        @NotNull
        Vertex getEnd();

        default int getWeight() {
            return 1;
        }

        @NotNull
        Vertex getOtherEnd(Vertex v);
    }

    @NotNull
    Set<Vertex> getVertices();

    @NotNull
    Set<Edge> getEdges();

    @Nullable
    Vertex get(String name);

    @NotNull
    default Set<Vertex> getNeighbors(@NotNull  Vertex v) {
        return getConnections(v).keySet();
    }

    @NotNull
    Map<Vertex, Edge> getConnections(@NotNull Vertex v);

    @Nullable
    default Edge getConnection(@NotNull Vertex v1, @NotNull Vertex v2) {
        return getConnections(v1).get(v2);
    }

    @NotNull
    Integer getVertexDegree(@NotNull Vertex v);

    @NotNull
    Set<Graph> splitOnConnectedComponents();

    @NotNull
    Graph connectedComponentOf(@NotNull Vertex v);

    boolean checkIfAcyclic();
}