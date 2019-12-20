import model.*;
import org.javatuples.Pair;
import org.junit.Test;
import org.miv.pherd.geom.Point3;
import transformation.Transformation;
import transformation.TransformationP4;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class TransformationP4Test extends AbstractTransformationTest {
    private Transformation transformation = new TransformationP4();

    @Test
    public void simpleGraphHangingNode() {
        Pair<ModelGraph, InteriorNode> simplestGraph = this.createSimplestGraph();
        ModelGraph graph = simplestGraph.getValue0();
        InteriorNode interiorNode = simplestGraph.getValue1();

        assertTrue(this.transformation.isConditionCompleted(graph, interiorNode));
        assertEquals(2, getHangingNodeSize(graph));
        this.transformation.transformGraph(graph, simplestGraph.getValue1());
        assertEquals(1, getHangingNodeSize(graph));
    }

    private static int getHangingNodeSize(ModelGraph graph) {
        return (int) graph.getVertices()
                .stream()
                .filter(vertex -> vertex.getVertexType() == VertexType.HANGING_NODE)
                .count();
    }

    @Test
    public void simpleGraphInteriorNumber() {
        Pair<ModelGraph, InteriorNode> simplestGraph = this.createSimplestGraph();
        ModelGraph graph = simplestGraph.getValue0();
        InteriorNode interiorNode = simplestGraph.getValue1();

        assertTrue(this.transformation.isConditionCompleted(graph, interiorNode));
        assertEquals(1, graph.getInteriors().size());
        this.transformation.transformGraph(graph, simplestGraph.getValue1());
        assertEquals(2, graph.getInteriors().size());
    }

    private Pair<ModelGraph, InteriorNode> createSimplestGraph() {
        ModelGraph graph = new ModelGraph("simplestGraphTest");

        Vertex v0 = graph.insertVertex("v0", VertexType.SIMPLE_NODE, new Point3d(0., 0., 0.));
        Vertex v1 = graph.insertVertex("v1", VertexType.HANGING_NODE, new Point3d(50., 0., 0.));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(100., 0., 0.));
        Vertex v3 = graph.insertVertex("v3", VertexType.HANGING_NODE, new Point3d(75., 43., 0.));
        Vertex v4 = graph.insertVertex("v4", VertexType.SIMPLE_NODE, new Point3d(50., 86., 0.));

        graph.insertEdge("e0", v0, v1);
        graph.insertEdge("e1", v1, v2);
        graph.insertEdge("e2", v2, v3);
        graph.insertEdge("e3", v3, v4);
        graph.insertEdge("e4", v4, v0);


        InteriorNode interiorNode = graph.insertInterior("i1", v0, v2, v4);
        return Pair.with(graph, interiorNode);
    }

    @Test
    public void envelopeGraphHangingNodesCount() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
        ModelGraph graph = graphPair.getValue0();

        assertEquals(4, getHangingNodeSize(graph));
        for (Map.Entry<InteriorNode, Boolean> entry : graphPair.getValue1().entrySet()) {
            InteriorNode iNode = entry.getKey();

            if (transformation.isConditionCompleted(graph, iNode)) {
                transformation.transformGraph(graph, iNode);
            }
        }

        assertEquals(3, getHangingNodeSize(graph));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Test
    public void envelopeGraphConditionCompletion() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();

        for (Map.Entry<InteriorNode, Boolean> entry : graphPair.getValue1().entrySet()) {
            assertEquals(entry.getValue(), transformation.isConditionCompleted(graphPair.getValue0(), entry.getKey()));
        }
    }


    @Test
    public void envelopeGraphInternalNodesCount() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
        ModelGraph graph = graphPair.getValue0();

        assertEquals(10, graph.getInteriors().size());
        for (Map.Entry<InteriorNode, Boolean> entry : graphPair.getValue1().entrySet()) {
            InteriorNode iNode = entry.getKey();

            if (transformation.isConditionCompleted(graph, iNode)) {
                transformation.transformGraph(graph, iNode);
            }
        }
        assertEquals(11, graph.getInteriors().size());
    }

    @Test
    public void envelopeGraphEdgesCount() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
        ModelGraph graph = graphPair.getValue0();

        assertEquals(52, graph.getEdges().size());
        for (Map.Entry<InteriorNode, Boolean> entry : graphPair.getValue1().entrySet()) {
            InteriorNode iNode = entry.getKey();

            if (transformation.isConditionCompleted(graph, iNode)) {

                transformation.transformGraph(graph, iNode);
            }
        }
        assertEquals(56, graph.getEdges().size());
    }

    @Test
    public void envelopeGraphNewEdgeProperties() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createEnvelopeGraph();
        ModelGraph graph = graphPair.getValue0();
        InteriorNode iNode = graph.getInterior("i5").orElseThrow(IllegalStateException::new);

        Vertex v1 = getVertexIfExists(graph, "v2");
        Vertex v3 = getVertexIfExists(graph, "v5");

        Double expectedEdgeLength = Math.sqrt(Math.pow(v1.getXCoordinate() - v3.getXCoordinate(), 2.0) +
                Math.pow(v1.getYCoordinate() - v3.getYCoordinate(), 2.0) +
                Math.pow(v1.getZCoordinate() - v3.getZCoordinate(), 2.0));

        assertFalse(graph.getEdgeBetweenNodes(v1, v3).isPresent());
        transformation.transformGraph(graph, iNode);

        assertTrue(graph.getEdgeBetweenNodes(v1, v3).isPresent());
        assertEquals(expectedEdgeLength, graph.getEdgeBetweenNodes(v1, v3).get().getL());
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void newGraphConditionCompletion() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createNewGraph();

        for (Map.Entry<InteriorNode, Boolean> entry : graphPair.getValue1().entrySet()) {
            assertEquals(entry.getValue(), transformation.isConditionCompleted(graphPair.getValue0(), entry.getKey()));
        }
    }

    @Test
    public void newGraphEdgesCount() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createNewGraph();
        ModelGraph graph = graphPair.getValue0();

        assertEquals(26, graph.getEdges().size());
        for (Map.Entry<InteriorNode, Boolean> entry : graphPair.getValue1().entrySet()) {
            InteriorNode iNode = entry.getKey();

            if (transformation.isConditionCompleted(graph, iNode)) {

                transformation.transformGraph(graph, iNode);
            }
        }
        assertEquals(30, graph.getEdges().size());
    }

    @Test
    public void newGraphNewEdgeProperties() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createNewGraph();
        ModelGraph graph = graphPair.getValue0();
        InteriorNode iNode = graph.getInterior("i1").orElseThrow(IllegalStateException::new);

        Vertex v3 = getVertexIfExists(graph, "v3");
        Vertex v5 = getVertexIfExists(graph, "v5");

        Double expectedEdgeLength = Math.sqrt(Math.pow(v3.getXCoordinate() - v5.getXCoordinate(), 2.0) +
                Math.pow(v3.getYCoordinate() - v5.getYCoordinate(), 2.0) +
                Math.pow(v3.getZCoordinate() - v5.getZCoordinate(), 2.0));

        assertFalse(graph.getEdgeBetweenNodes(v3, v5).isPresent());
        transformation.transformGraph(graph, iNode);

        assertTrue(graph.getEdgeBetweenNodes(v3, v5).isPresent());
        assertEquals(expectedEdgeLength, graph.getEdgeBetweenNodes(v3, v5).get().getL());
    }


    @Test
    public void newGraphHangingNodesCount() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createNewGraph();
        ModelGraph graph = graphPair.getValue0();

        assertEquals(2, getHangingNodeSize(graph));
        for (Map.Entry<InteriorNode, Boolean> entry : graphPair.getValue1().entrySet()) {
            InteriorNode iNode = entry.getKey();

            if (transformation.isConditionCompleted(graph, iNode)) {
                transformation.transformGraph(graph, iNode);
            }
        }

        assertEquals(1, getHangingNodeSize(graph));
    }


    @Test
    public void newGraphInternalNodesCount() {
        Pair<ModelGraph, Map<InteriorNode, Boolean>> graphPair = createNewGraph();
        ModelGraph graph = graphPair.getValue0();

        assertEquals(5, graph.getInteriors().size());
        for (Map.Entry<InteriorNode, Boolean> entry : graphPair.getValue1().entrySet()) {
            InteriorNode iNode = entry.getKey();

            if (transformation.isConditionCompleted(graph, iNode)) {
                transformation.transformGraph(graph, iNode);
            }
        }
        assertEquals(6, graph.getInteriors().size());
    }


    private Pair<ModelGraph, Map<InteriorNode, Boolean>> createEnvelopeGraph() {
        ModelGraph graph = new ModelGraph("envelopeGraphTest");

        // vertices top -> down; in the same level: left -> right
        Vertex v0 = graph.insertVertex("v0", VertexType.SIMPLE_NODE, new Point3d(0., 100., 0.));
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(100., 100., 0.));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(250., 100., 0.));

        Vertex v3 = graph.insertVertex("v3", VertexType.HANGING_NODE, new Point3d(50., 50., 0.));
        Vertex v4 = graph.insertVertex("v4", VertexType.HANGING_NODE, new Point3d(100., 50., 0.));
        Vertex v5 = graph.insertVertex("v5", VertexType.HANGING_NODE, new Point3d(150., 50., 0.));
        Vertex v6 = graph.insertVertex("v6", VertexType.HANGING_NODE, new Point3d(225., 50., 0.));

        Vertex v7 = graph.insertVertex("v7", VertexType.SIMPLE_NODE, new Point3d(0., 0., 0.));
        Vertex v8 = graph.insertVertex("v8", VertexType.SIMPLE_NODE, new Point3d(100., 0., 0.));
        Vertex v9 = graph.insertVertex("v9", VertexType.SIMPLE_NODE, new Point3d(200., 0., 0.));
        Vertex v10 = graph.insertVertex("v10", VertexType.SIMPLE_NODE, new Point3d(250., 0., 0.));

        Vertex v11 = graph.insertVertex("v11", VertexType.SIMPLE_NODE, new Point3d(50., 150., 0));
        Vertex v12 = graph.insertVertex("v12", VertexType.SIMPLE_NODE, new Point3d(50., 100., 0));

        //edges
       // graph.insertEdge("e0", v0, v1);
        graph.insertEdge("e0a", v0, v12);
        graph.insertEdge("e0b", v12, v1);
        graph.insertEdge("e2", v0, v3);
        graph.insertEdge("e1", v0, v7);


        graph.insertEdge("e3", v1, v4);
        graph.insertEdge("e4", v1, v5);
        graph.insertEdge("e5", v1, v2);


        graph.insertEdge("e6", v2, v6);
        graph.insertEdge("e7", v2, v10);

      //  graph.insertEdge("e8", v4, v5);
        graph.insertEdge("e9", v4, v8);

        graph.insertEdge("e10", v5, v8);
        graph.insertEdge("e11", v5, v9);

        graph.insertEdge("e12", v6, v9);
        graph.insertEdge("e13", v6, v10);

        graph.insertEdge("e14", v9, v8);
        graph.insertEdge("e15", v9, v10);

        graph.insertEdge("e16", v3, v7);
        graph.insertEdge("e17", v3, v8);

        graph.insertEdge("e18", v7, v8);

        graph.insertEdge("e19", v0, v11);
        graph.insertEdge("e20", v1, v11);
        graph.insertEdge("e21", v11, v12);




        // i-nodes
        Map<InteriorNode, Boolean> nodesToFlags = new HashMap<>();
        nodesToFlags.put(graph.insertInterior("i0", v0, v3, v7), false);
        nodesToFlags.put(graph.insertInterior("i1", v0, v1, v8), false); // changed to false
//        nodesToFlags.put(graph.insertInterior("i2", v1, v4, v5), false);
        nodesToFlags.put(graph.insertInterior("i5", v1, v2, v9), true);
        nodesToFlags.put(graph.insertInterior("i6", v2, v6, v10), false);
        nodesToFlags.put(graph.insertInterior("i8", v3, v7, v8), false);
//        nodesToFlags.put(graph.insertInterior("i3", v4, v5, v8), false);
        nodesToFlags.put(graph.insertInterior("i4", v5, v8, v9), false);
        nodesToFlags.put(graph.insertInterior("i7", v6, v9, v10), false);

        nodesToFlags.put(graph.insertInterior("i9", v0, v11, v12), false);
        nodesToFlags.put(graph.insertInterior("i10", v11, v12, v1), false);
        nodesToFlags.put(graph.insertInterior("i11", v1, v5, v8), false);




        return Pair.with(graph, nodesToFlags);
    }

    private Pair<ModelGraph, Map<InteriorNode, Boolean>> createNewGraph() {
        ModelGraph graph = new ModelGraph("newGraphTest");

        Vertex v0 = graph.insertVertex("v0", VertexType.SIMPLE_NODE, new Point3d(50., 200., 0.));
        Vertex v1= graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(100., 200., 0.));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(100., 0., 0.));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(0., 0., 0.));
        Vertex v4 = graph.insertVertex("v4", VertexType.SIMPLE_NODE, new Point3d(50., -50., 0.));

        Vertex v5 = graph.insertVertex("v5", VertexType.HANGING_NODE, new Point3d(75., 100., 0));
        Vertex v6 = graph.insertVertex("v6", VertexType.HANGING_NODE, new Point3d(50., 0., 0.));

        graph.insertEdge("e1",v0, v1);
        graph.insertEdge("e2", v1, v2);
        graph.insertEdge("e3", v1, v5);
        graph.insertEdge("e4",v0, v3);
        graph.insertEdge("e5", v3, v6);
        graph.insertEdge("e6", v6, v2);
        graph.insertEdge("e7", v4, v6);
        graph.insertEdge("e8", v3, v4);
        graph.insertEdge("e9", v2, v4);
        graph.insertEdge("e10", v0, v5);
        graph.insertEdge("e11", v2, v5);


        Map<InteriorNode, Boolean> nodesToFlags = new HashMap<>();
        nodesToFlags.put(graph.insertInterior("i0", v0, v1, v5), false);
        nodesToFlags.put(graph.insertInterior("i1", v0, v2, v3), true);
        nodesToFlags.put(graph.insertInterior("i2", v0, v1, v2), false);
        nodesToFlags.put(graph.insertInterior("i3", v3, v4, v6), false);
        nodesToFlags.put(graph.insertInterior("i4", v2, v4, v6), false);

        return Pair.with(graph, nodesToFlags);


    }






    private Vertex getVertexIfExists(ModelGraph graph, String vertexId) {
        return graph.getVertex(vertexId)
                .orElseThrow(() -> new IllegalStateException("Cannot find vertex with id: " + vertexId));
    }


}