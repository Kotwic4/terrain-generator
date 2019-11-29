package transformation;

import model.*;
import org.javatuples.Triplet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformationP6a implements Transformation {

    private static final String SIMPLE_VERTEX_1 = "vertex1";
    private static final String SIMPLE_VERTEX_3 = "vertex3";
    private static final String SIMPLE_VERTEX_5 = "vertex5";

    private static final String HANGING_NODE_2 = "node2";
    private static final String HANGING_NODE_4 = "node4";
    private static final String HANGING_NODE_6 = "node6";

    private static final String VERTEX_MAP_SIMPLE_VERTEX_OPPOSITE_LONGEST_EDGE_KEY = "oppositeLongestEdgeVertex3";

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        if(!interiorNode.isPartitionRequired()){
            return false;
        }
        if(getSimpleVertexCount(triangle) != 3 || getHangingVertexCount(triangle) != 0){
            return false;
        }

        Map<String, Vertex> model = mapTriangleVertexesToModel(graph, interiorNode);

        Vertex simpleVertex1 = model.get(SIMPLE_VERTEX_1);
        Vertex simpleVertex2 = model.get(SIMPLE_VERTEX_3);
        Vertex oppositeLongestEdgeVertex = model.get(VERTEX_MAP_SIMPLE_VERTEX_OPPOSITE_LONGEST_EDGE_KEY);

        if(simpleVertex1 == null || simpleVertex2 == null || oppositeLongestEdgeVertex == null){
            throw new RuntimeException("Transformation error");
        }

        GraphEdge longestEdge = graph.getEdgeBetweenNodes(simpleVertex1, simpleVertex2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge shortEdge1 = graph.getEdgeBetweenNodes(oppositeLongestEdgeVertex, simpleVertex1)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));
        GraphEdge shortEdge2 = graph.getEdgeBetweenNodes(oppositeLongestEdgeVertex, simpleVertex2)
                .orElseThrow(() -> new RuntimeException("Unknown edge id"));

        if(!longestEdge.getB()){
            return false;
        }

        if(longestEdge.getL() < shortEdge1.getL() || longestEdge.getL() < shortEdge2.getL()){
            return false;
        }
        return true;
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        Map<String, Vertex> model = mapTriangleVertexesToModel(graph, interiorNode);
        Vertex simpleVertex1 = model.get(SIMPLE_VERTEX_1);
        Vertex simpleVertex2 = model.get(SIMPLE_VERTEX_3);
        Vertex oppositeLongestEdgeVertex = model.get(SIMPLE_VERTEX_5); //VERTEX_MAP_SIMPLE_VERTEX_OPPOSITE_LONGEST_EDGE_KEY

        if(simpleVertex1 == null || simpleVertex2 == null || oppositeLongestEdgeVertex == null){
            throw new RuntimeException("Transformation error");
        }

        graph.removeInterior(interiorNode.getId());

        Vertex insertedVertex = graph.insertVertex(interiorNode.getId(),
                VertexType.SIMPLE_NODE,
                Point3d.middlePoint(simpleVertex1.getCoordinates(), simpleVertex2.getCoordinates()));

        String newEdge1Id = simpleVertex1.getId().concat(insertedVertex.getId());
        String newEdge2Id = simpleVertex2.getId().concat(insertedVertex.getId());
        String newEdge3Id = oppositeLongestEdgeVertex.getId().concat(insertedVertex.getId());

        GraphEdge insertedEdge1 = graph.insertEdge(newEdge1Id, simpleVertex1, insertedVertex);
        insertedEdge1.setB(false);
        // insertedEdge1.setB(longestEdge.getB());

        GraphEdge insertedEdge2 = graph.insertEdge(newEdge2Id, simpleVertex2, insertedVertex);
        insertedEdge2.setB(false);
       // insertedEdge2.setB(longestEdge.getB());

        GraphEdge insertedEdge3 = graph.insertEdge(newEdge3Id, oppositeLongestEdgeVertex, insertedVertex);
        insertedEdge3.setB(false);

        String insertedInterior1Id = oppositeLongestEdgeVertex.getId().concat(simpleVertex1.getId()).concat(insertedVertex.getId());
        String insertedInterior2Id = oppositeLongestEdgeVertex.getId().concat(simpleVertex2.getId()).concat(insertedVertex.getId());
        graph.insertInterior(insertedInterior1Id, oppositeLongestEdgeVertex, simpleVertex1,  insertedVertex);
        graph.insertInterior(insertedInterior2Id, oppositeLongestEdgeVertex, simpleVertex2,  insertedVertex);

        graph.deleteEdge(simpleVertex1, model.get(HANGING_NODE_2));
        graph.deleteEdge(model.get(HANGING_NODE_2), simpleVertex2);

        graph.removeVertex(model.get(HANGING_NODE_2).getSymbol());

        return graph;
    }

    private static Map<String, Vertex> mapTriangleVertexesToModel(ModelGraph graph, InteriorNode interiorNode){
        Map<String, Vertex> verticesMap = new HashMap<>();

        Triplet<Vertex, Vertex, Vertex> triangleSimple = interiorNode.getTriangleVertexes();
        Vertex vertexA = triangleSimple.getValue0();
        Vertex vertexB = triangleSimple.getValue1();
        Vertex vertexC = triangleSimple.getValue2();

        List<Vertex> hangingNodes = interiorNode.getAssociatedNodes();

        Vertex hanging2 = hangingNodes.get(0);
        Vertex hanging4 = hangingNodes.get(1);
        Vertex hanging6 = hangingNodes.get(2);

        verticesMap.put(HANGING_NODE_2, hanging2);
        verticesMap.put(HANGING_NODE_4, hanging4);
        verticesMap.put(HANGING_NODE_6, hanging6);

        verticesMap.put(SIMPLE_VERTEX_1, vertexA);
        verticesMap.put(SIMPLE_VERTEX_3, vertexB);
        verticesMap.put(SIMPLE_VERTEX_5, vertexC);

        return verticesMap;
    }

    private static int getSimpleVertexCount(Triplet<Vertex, Vertex, Vertex> triangle) {
        int count = 0;
        for (Object o : triangle) {
            Vertex v = (Vertex)o;
            if(v.getVertexType() == VertexType.SIMPLE_NODE){
                count++;
            }
        }
        return count;
    }

    private static int getHangingVertexCount(Triplet<Vertex, Vertex, Vertex> triangle) {
        int count = 0;
        for (Object o : triangle) {
            Vertex v = (Vertex)o;
            if(v.getVertexType() == VertexType.HANGING_NODE){
                count++;
            }
        }
        return count;
    }
}
