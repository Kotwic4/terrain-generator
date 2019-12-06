package transformation;

import model.InteriorNode;
import model.ModelGraph;
import model.Vertex;
import model.VertexType;
import org.javatuples.Triplet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("Duplicates")
public class TransformationP6 implements Transformation {

    // original
    private static final String SIMPLE_NODE_1 = "v1";
    private static final String SIMPLE_NODE_3 = "v3";
    private static final String SIMPLE_NODE_5 = "v5";
    private static final String HANGING_NODE_2 = "h2";
    private static final String HANGING_NODE_4 = "h4";
    private static final String HANGING_NODE_6 = "h6";

    // added
    private static final String SIMPLE_NODE_2 = "v2";
    private static final double COMPARISION_THRESHOLD = 0.00000000001d;

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        if (isConditionCompleted(graph, interiorNode)) {
            graph.removeInterior(interiorNode.getId());

            Map<String, Vertex> vertexMap = detectTriangleAndReturnItAsAMap(graph, interiorNode);

            Vertex node2 = vertexMap.get(HANGING_NODE_2);
            node2.setVertexType(VertexType.SIMPLE_NODE);
            vertexMap.remove(HANGING_NODE_2);
            vertexMap.put(SIMPLE_NODE_2, node2);

            graph.insertEdge("e7", vertexMap.get(SIMPLE_NODE_2), vertexMap.get(SIMPLE_NODE_5), false);

            String leftInteriorId = vertexMap.get(SIMPLE_NODE_1).getId()
                    .concat(vertexMap.get(SIMPLE_NODE_2).getId())
                    .concat(vertexMap.get(SIMPLE_NODE_5).getId());
            String rightInteriorId = vertexMap.get(SIMPLE_NODE_2).getId()
                    .concat(vertexMap.get(SIMPLE_NODE_3).getId())
                    .concat(vertexMap.get(SIMPLE_NODE_5).getId());


            graph.insertInterior(
                    leftInteriorId,
                    vertexMap.get(SIMPLE_NODE_1),
                    vertexMap.get(SIMPLE_NODE_2),
                    vertexMap.get(SIMPLE_NODE_5),
                    vertexMap.get(HANGING_NODE_6)
            );
            graph.insertInterior(
                    rightInteriorId,
                    vertexMap.get(SIMPLE_NODE_2),
                    vertexMap.get(SIMPLE_NODE_3),
                    vertexMap.get(SIMPLE_NODE_5),
                    vertexMap.get(HANGING_NODE_4)
            );
        }

        return graph;
    }

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangleVertexes = interiorNode.getTriangleVertexes();

        Vertex vertexA = triangleVertexes.getValue0();
        Vertex vertexB = triangleVertexes.getValue1();
        Vertex vertexC = triangleVertexes.getValue2();

        if (!(isSimpleNode(vertexA) && isSimpleNode(vertexB) && isSimpleNode(vertexC))) return false;

        Optional<Vertex> hangingA = getHangingVertexBetween(graph, vertexB, vertexC);
        Optional<Vertex> hangingB = getHangingVertexBetween(graph, vertexC, vertexA);
        Optional<Vertex> hangingC = getHangingVertexBetween(graph, vertexA, vertexB);

        return hangingA.isPresent() && hangingB.isPresent() && hangingC.isPresent();
    }

    private Optional<Vertex> getHangingVertexBetween(ModelGraph graph, Vertex v1, Vertex v2) {
        return graph.getVertexBetween(v1, v2).filter(this::isHangingNode);
    }

    private boolean isSimpleNode(Vertex v) {
        return v.getVertexType() == VertexType.SIMPLE_NODE;
    }

    private boolean isHangingNode(Vertex v) {
        return v.getVertexType() == VertexType.HANGING_NODE;
    }

    private Map<String, Vertex> detectTriangleAndReturnItAsAMap(ModelGraph graph, InteriorNode interiorNode) {
        // initialize necessary variables
        Triplet<Vertex, Vertex, Vertex> triangleVertexes = interiorNode.getTriangleVertexes();

        Vertex simpleA = triangleVertexes.getValue0();
        Vertex simpleB = triangleVertexes.getValue1();
        Vertex simpleC = triangleVertexes.getValue2();

        Vertex hangingBetweenBC = getHangingVertexBetween(graph, simpleB, simpleC).get();
        Vertex hangingBetweenAC = getHangingVertexBetween(graph, simpleA, simpleC).get();
        Vertex hangingBetweenAB = getHangingVertexBetween(graph, simpleA, simpleB).get();

        double lBC = getLengthOfEdgeBetweenSimpleNodes(graph, simpleB, simpleC, hangingBetweenBC);
        double lAC = getLengthOfEdgeBetweenSimpleNodes(graph, simpleA, simpleC, hangingBetweenAC);
        double lAB = getLengthOfEdgeBetweenSimpleNodes(graph, simpleA, simpleB, hangingBetweenAB);

        // detect triangle
        double longestEdge = Stream.of(lBC, lAC, lAB).max(Double::compare).get();

        // assign labels according to http://home.agh.edu.pl/~paszynsk/GG/ProjektGG2019.pdf
        Map<String, Vertex> vertexMap = new HashMap<>();
        if (areEqualWithinThreshold(longestEdge, lBC)) {
            vertexMap.put(SIMPLE_NODE_1, simpleB);
            vertexMap.put(SIMPLE_NODE_3, simpleC);
            vertexMap.put(SIMPLE_NODE_5, simpleA);
            vertexMap.put(HANGING_NODE_2, hangingBetweenBC);
            vertexMap.put(HANGING_NODE_4, hangingBetweenAC);
            vertexMap.put(HANGING_NODE_6, hangingBetweenAB);
        } else if (areEqualWithinThreshold(longestEdge, lAC)) {
            vertexMap.put(SIMPLE_NODE_1, simpleC);
            vertexMap.put(SIMPLE_NODE_3, simpleA);
            vertexMap.put(SIMPLE_NODE_5, simpleB);
            vertexMap.put(HANGING_NODE_2, hangingBetweenAC);
            vertexMap.put(HANGING_NODE_4, hangingBetweenAB);
            vertexMap.put(HANGING_NODE_6, hangingBetweenBC);
        } else if (areEqualWithinThreshold(longestEdge, lAB)) {
            vertexMap.put(SIMPLE_NODE_1, simpleA);
            vertexMap.put(SIMPLE_NODE_3, simpleB);
            vertexMap.put(SIMPLE_NODE_5, simpleC);
            vertexMap.put(HANGING_NODE_2, hangingBetweenAB);
            vertexMap.put(HANGING_NODE_4, hangingBetweenBC);
            vertexMap.put(HANGING_NODE_6, hangingBetweenAC);
        }

        return vertexMap;
    }

    private double getLengthOfEdgeBetweenSimpleNodes(ModelGraph graph, Vertex simpleNode1, Vertex simpleNode2, Vertex hangingNodeBetween1And2) {
        double lBetweenSimple1AndHanging = graph.getEdgeBetweenNodes(simpleNode1, hangingNodeBetween1And2).get().getL();
        double lBetweenSimple2AndHanging = graph.getEdgeBetweenNodes(simpleNode2, hangingNodeBetween1And2).get().getL();
        return lBetweenSimple1AndHanging + lBetweenSimple2AndHanging;
    }

    private boolean areEqualWithinThreshold(double actual, double expected) {
        return Math.abs(expected - actual) < TransformationP6.COMPARISION_THRESHOLD;
    }
}