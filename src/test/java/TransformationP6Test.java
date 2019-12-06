import model.*;
import org.javatuples.Pair;
import org.junit.Test;
import transformation.TransformationP6a;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TransformationP6Test extends AbstractTransformationTest {
    private TransformationP6a transformation = new TransformationP6a();

    @Test
    public void conditionPassesWithCorrectGraph() {
		Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
		ModelGraph graph = graphPair.getValue0();
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionPassesForRotatedGraph() {
		Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
		ModelGraph graph = graphPair.getValue0();
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void transformationProducesTwoInteriorNodes() {
		Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
		ModelGraph graph = graphPair.getValue0();
        assertEquals(2, transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)).getInteriors().size());
    }

    @Test
    public void transformationProducesCorrectVertexTypes() {
		Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
		ModelGraph graph = graphPair.getValue0();
        transformation.transformGraph(graph, graph.getInterior("i1").orElseThrow(AssertionError::new));
        int hanging_nodes = 0;
        int simple_nodes = 0;
        for (Vertex v : graph.getVertices()) {
            if (v.getVertexType() == VertexType.SIMPLE_NODE) {
                simple_nodes++;
            } else {
                hanging_nodes++;
            }
        }
        assertEquals(4, simple_nodes);
        assertEquals(2, hanging_nodes);
    }

    @Test
    public void transformationCreatesCorrectNumberOfEdges() {
		Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
		ModelGraph graph = graphPair.getValue0();
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        ModelGraph transformedGraph = transformation.transformGraph(graph, interior);
        assertEquals(13, transformedGraph.getEdges().size());
    }

    @Test
    public void conditionFailsForGraphWithNewInterior() {
		Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
		ModelGraph graph = graphPair.getValue0();
        InteriorNode interior = graph.getInterior("i1").orElseThrow(AssertionError::new);
        ModelGraph transformedGraph = transformation.transformGraph(graph, interior);
        assertFalse(transformation.isConditionCompleted(transformedGraph, graph.getInterior("v1h2v5").orElseThrow(AssertionError::new)));
    }

//    private ModelGraph createGraphWithTooManyNodes() {
//        ModelGraph graph = new ModelGraph("testGraph");
//        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
//        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
//        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
//        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 0.5, 0.0));
//        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
//        Vertex h6 = graph.insertVertex("h6", VertexType.HANGING_NODE, new Point3d(0.5, 0.5, 0.0));
//        Vertex h7 = graph.insertVertex("h7", VertexType.HANGING_NODE, new Point3d(0.75, 0.75, 0.0));
//
//
//        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, true);
//        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, true);
//        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
//        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, true);
//        GraphEdge v5_h7 = graph.insertEdge("e5", v5, h7, true);
//        GraphEdge h6_h7 = graph.insertEdge("e6", h6, h7, true);
//        GraphEdge h6_v1 = graph.insertEdge("e7", h6, v1, true);
//
//        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4, h6, h7);
//        return graph;
//    }
//
//    private ModelGraph createRotatedGraph() {
//        ModelGraph graph = new ModelGraph("testGraph");
//        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
//        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
//        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
//        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(2.0, 1.0, 0.0));
//        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(2.0, 2.0, 0.0));
//        Vertex h6 = graph.insertVertex("h6", VertexType.HANGING_NODE, new Point3d(1.41, 1.41, 0.0));
//
//        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, true);
//        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, true);
//        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
//        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, true);
//        GraphEdge v5_h6 = graph.insertEdge("e5", v5, h6, true);
//        GraphEdge h6_v1 = graph.insertEdge("e6", h6, v1, true);
//
//        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4, h6);
//        return graph;
//    }
//
//    private ModelGraph createCorrectGraph() {
//        ModelGraph graph = new ModelGraph("testGraph");
//        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
//        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
//        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
//        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 0.5, 0.0));
//        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
//        Vertex h6 = graph.insertVertex("h6", VertexType.HANGING_NODE, new Point3d(0.5, 0.5, 0.0));
//
//        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, true);
//        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, true);
//        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
//        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, true);
//        GraphEdge v5_h6 = graph.insertEdge("e5", v5, h6, true);
//        GraphEdge h6_v1 = graph.insertEdge("e6", h6, v1, true);
//
//        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4, h6);
//        return graph;
//    }
//
//    private ModelGraph createGraphWithTooFewHangingNodes() {
//        ModelGraph graph = new ModelGraph("testGraph");
//        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
//        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
//        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 0.5, 0.0));
//        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
//        Vertex h6 = graph.insertVertex("h6", VertexType.HANGING_NODE, new Point3d(0.5, 0.5, 0.0));
//
//        GraphEdge v1_h2 = graph.insertEdge("e1", v1, v3, true);
//        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
//        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, true);
//        GraphEdge v5_h6 = graph.insertEdge("e5", v5, h6, true);
//        GraphEdge h6_v1 = graph.insertEdge("e6", h6, v1, true);
//
//        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h4, h6);
//        return graph;
//    }
//
//    private ModelGraph createGraphWithOnlyHangingNodes() {
//        ModelGraph graph = new ModelGraph("testGraph");
//        Vertex v1 = graph.insertVertex("v1", VertexType.HANGING_NODE, new Point3d(0.0, 0.0, 0.0));
//        Vertex h2 = graph.insertVertex("h2", VertexType.HANGING_NODE, new Point3d(1.0, 0.0, 0.0));
//        Vertex v3 = graph.insertVertex("v3", VertexType.HANGING_NODE, new Point3d(2.0, 0.0, 0.0));
//        Vertex h4 = graph.insertVertex("h4", VertexType.HANGING_NODE, new Point3d(1.5, 0.5, 0.0));
//        Vertex v5 = graph.insertVertex("v5", VertexType.HANGING_NODE, new Point3d(1.0, 1.0, 0.0));
//        Vertex h6 = graph.insertVertex("h6", VertexType.HANGING_NODE, new Point3d(0.5, 0.5, 0.0));
//
//        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, true);
//        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, true);
//        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
//        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, true);
//        GraphEdge v5_h6 = graph.insertEdge("e5", v5, h6, true);
//        GraphEdge h6_v1 = graph.insertEdge("e6", h6, v1, true);
//
//        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4, h6);
//        return graph;
//    }
//
//    private ModelGraph createGraphWithOnlySimpleNodes() {
//        ModelGraph graph = new ModelGraph("testGraph");
//        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
//        Vertex h2 = graph.insertVertex("h2", VertexType.SIMPLE_NODE, new Point3d(1.0, 0.0, 0.0));
//        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(2.0, 0.0, 0.0));
//        Vertex h4 = graph.insertVertex("h4", VertexType.SIMPLE_NODE, new Point3d(1.5, 0.5, 0.0));
//        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(1.0, 1.0, 0.0));
//        Vertex h6 = graph.insertVertex("h6", VertexType.SIMPLE_NODE, new Point3d(0.5, 0.5, 0.0));
//
//        GraphEdge v1_h2 = graph.insertEdge("e1", v1, h2, true);
//        GraphEdge h2_v3 = graph.insertEdge("e2", h2, v3, true);
//        GraphEdge v3_h4 = graph.insertEdge("e3", v3, h4, true);
//        GraphEdge h4_v5 = graph.insertEdge("e4", h4, v5, true);
//        GraphEdge v5_h6 = graph.insertEdge("e5", v5, h6, true);
//        GraphEdge h6_v1 = graph.insertEdge("e6", h6, v1, true);
//
//        InteriorNode in1 = graph.insertInterior("i1", v1, v3, v5, h2, h4, h6);
//        return graph;
//    }

    private Pair<ModelGraph, Map<InteriorNode, Boolean>> createEnvelopeGraph() {
        ModelGraph graph = new ModelGraph("envelopeGraphTest");

        Vertex v0 = graph.insertVertex("v0", VertexType.SIMPLE_NODE, new Point3d(100., 0., 0.));
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(200., 0., 0.));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(250., 0., 0.));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(100., 50., 0.));
        Vertex v4 = graph.insertVertex("v4", VertexType.SIMPLE_NODE, new Point3d(100., 100., 0.));
        Vertex v5 = graph.insertVertex("v5", VertexType.SIMPLE_NODE, new Point3d(250., 100., 0.));
        Vertex v6 = graph.insertVertex("v6", VertexType.SIMPLE_NODE, new Point3d(150., 150., 0.));

        Vertex v7 = graph.insertVertex("v7", VertexType.HANGING_NODE, new Point3d(150., 50., 0.));
        Vertex v8 = graph.insertVertex("v8", VertexType.HANGING_NODE, new Point3d(250., 50., 0.));
        Vertex v9 = graph.insertVertex("v9", VertexType.HANGING_NODE, new Point3d(150., 100., 0.));


        graph.insertEdge("e0", v0, v1);
		graph.insertEdge("e2", v0, v3);
		graph.insertEdge("e1", v0, v7);

		graph.insertEdge("e5", v1, v2);
		graph.insertEdge("e3", v1, v7);
		graph.insertEdge("e4", v1, v8);

		graph.insertEdge("e7", v2, v5);
		graph.insertEdge("e6", v2, v8);

        graph.insertEdge("e8", v3, v4);
        graph.insertEdge("e9", v3, v7);

		graph.insertEdge("e12", v4, v6);
		graph.insertEdge("e10", v4, v7);
		graph.insertEdge("e11", v4, v9);

		graph.insertEdge("e14", v5, v6);
		graph.insertEdge("e15", v5, v8);
		graph.insertEdge("e13", v5, v9);

        graph.insertEdge("e16", v6, v9);

        // i-nodes
        Map<InteriorNode, Boolean> nodesToFlags = new HashMap<>();
        nodesToFlags.put(graph.insertInterior("i0", v0, v3, v7), false);
        nodesToFlags.put(graph.insertInterior("i1", v3, v4, v7), false);
        nodesToFlags.put(graph.insertInterior("i2", v0, v1, v7), false);
        nodesToFlags.put(graph.insertInterior("i5", v1, v4, v5), true);
        nodesToFlags.put(graph.insertInterior("i6", v1, v2, v8), false);
        nodesToFlags.put(graph.insertInterior("i8", v2, v5, v8), false);
        nodesToFlags.put(graph.insertInterior("i3", v4, v6, v9), true);
        nodesToFlags.put(graph.insertInterior("i4", v5, v6, v9), true);

        return Pair.with(graph, nodesToFlags);
    }

}
