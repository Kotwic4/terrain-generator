package transformation;

import model.*;
import org.javatuples.Triplet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransformationP1 implements Transformation {

    private static final String VERTEX_MAP_SIMPLE_VERTEX_1_KEY = "simpleVertex1";

    private static final String VERTEX_MAP_SIMPLE_VERTEX_2_KEY = "simpleVertex2";

    private static final String VERTEX_MAP_SIMPLE_VERTEX_OPPOSITE_LONGEST_EDGE_KEY = "oppositeLongestEdgeVertex3";

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        if (!interiorNode.isPartitionRequired()) {
            return false;
        }
        return getSimpleVertexCount(triangle) == 3 && checkIfTriangle(triangle);
    }

    private boolean checkIfTriangle(Triplet<Vertex, Vertex, Vertex> triangle) {
        Vertex value0 = triangle.getValue0();
        Vertex value1 = triangle.getValue1();
        Vertex value2 = triangle.getValue2();
        return value0.hasEdgeBetween(value1) && value1.hasEdgeBetween(value2) && value2.hasEdgeBetween(value0);
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        Map<String, Vertex> model = mapTriangleVertexesToModel(graph, interiorNode);
        Vertex simpleVertex1 = model.get(VERTEX_MAP_SIMPLE_VERTEX_1_KEY);
        Vertex simpleVertex2 = model.get(VERTEX_MAP_SIMPLE_VERTEX_2_KEY);
        Vertex oppositeLongestEdgeVertex = model.get(VERTEX_MAP_SIMPLE_VERTEX_OPPOSITE_LONGEST_EDGE_KEY);

        GraphEdge longestEdge = graph.getEdgeBetweenNodes(simpleVertex1, simpleVertex2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));

        //transformation process
        graph.removeInterior(interiorNode.getId());
        graph.deleteEdge(simpleVertex1, simpleVertex2);

        Vertex insertedVertex = graph.insertVertex(interiorNode.getId(),
                longestEdge.getB() ? VertexType.SIMPLE_NODE : VertexType.HANGING_NODE,
                Point3d.middlePoint(simpleVertex1.getCoordinates(), simpleVertex2.getCoordinates()));

        String newEdge1Id = simpleVertex1.getId().concat(insertedVertex.getId());
        String newEdge2Id = simpleVertex2.getId().concat(insertedVertex.getId());
        String newEdge3Id = oppositeLongestEdgeVertex.getId().concat(insertedVertex.getId());

        GraphEdge insertedEdge1 = graph.insertEdge(newEdge1Id, simpleVertex1, insertedVertex);
        insertedEdge1.setB(longestEdge.getB());

        GraphEdge insertedEdge2 = graph.insertEdge(newEdge2Id, simpleVertex2, insertedVertex);
        insertedEdge2.setB(longestEdge.getB());

        GraphEdge insertedEdge3 = graph.insertEdge(newEdge3Id, oppositeLongestEdgeVertex, insertedVertex);
        insertedEdge3.setB(false);

        String insertedInterior1Id = oppositeLongestEdgeVertex.getId().concat(simpleVertex1.getId()).concat(insertedVertex.getId());
        String insertedInterior2Id = oppositeLongestEdgeVertex.getId().concat(simpleVertex2.getId()).concat(insertedVertex.getId());
        graph.insertInterior(insertedInterior1Id, oppositeLongestEdgeVertex, simpleVertex1, insertedVertex);
        graph.insertInterior(insertedInterior2Id, oppositeLongestEdgeVertex, simpleVertex2, insertedVertex);
        return graph;
    }

    private static boolean checkEdge(GraphEdge edge, List<GraphEdge> edges, boolean onB) {
        return edge.getB() == onB && edges.stream().allMatch(e -> edge.getL() >= e.getL());
    }

    private static Optional<GraphEdge> getTraingleLongestEdge(ModelGraph modelGraph, InteriorNode interiorNode, boolean onB) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangle();
        Vertex v1 = triangle.getValue0();
        Vertex v2 = triangle.getValue1();
        Vertex v3 = triangle.getValue2();

        GraphEdge edge1 = modelGraph.getEdgeBetweenNodes(v1, v2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge edge2 = modelGraph.getEdgeBetweenNodes(v2, v3)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge edge3 = modelGraph.getEdgeBetweenNodes(v1, v3)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));

        List<GraphEdge> edges = Stream.of(edge1, edge2, edge3).collect(Collectors.toList());

        return edges.stream().filter(e -> checkEdge(e, edges, onB)).findFirst();
    }


    private static Map<String, Vertex> mapTriangleVertexesToModel(ModelGraph modelGraph, InteriorNode interiorNode) {
        Map<String, Vertex> triangleModel = new HashMap<>();

        GraphEdge triangleLongestEdge = getTraingleLongestEdge(modelGraph, interiorNode, true)
                .orElseGet(() ->
                        getTraingleLongestEdge(modelGraph, interiorNode, false)
                                .orElseThrow(() -> new RuntimeException("Unknown edge"))
                );

        triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_1_KEY, triangleLongestEdge.getNode0());
        triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_2_KEY, triangleLongestEdge.getNode1());

        Triplet<Vertex, Vertex, Vertex> triangleVertexes = interiorNode.getTriangleVertexes();

        for (Object o : triangleVertexes) {
            Vertex v = (Vertex) o;
            if (v != triangleLongestEdge.getNode0() && v != triangleLongestEdge.getNode1()) {
                triangleModel.put(VERTEX_MAP_SIMPLE_VERTEX_OPPOSITE_LONGEST_EDGE_KEY, v);
                break;
            }
        }
        return triangleModel;
    }

    private static int getSimpleVertexCount(Triplet<Vertex, Vertex, Vertex> triangle) {
        int count = 0;
        for (Object o : triangle) {
            Vertex v = (Vertex) o;
            if (v.getVertexType() == VertexType.SIMPLE_NODE) {
                count++;
            }
        }
        return count;
    }
}
