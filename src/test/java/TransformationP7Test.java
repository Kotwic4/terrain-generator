import model.*;
import org.junit.Test;
import transformation.Transformation;
import transformation.TransformationP7;

import static org.junit.jupiter.api.Assertions.*;

public class TransformationP7Test {
    private static final double EPS = 5.0f;
    private Transformation transformation = new TransformationP7(EPS);

    @Test
    public void conditionPassesWhenPartitionRequired() {
        ModelGraph graph = createGraphWithPartitionRequired();
        assertTrue(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    @Test
    public void conditionPassesWhenPartitionNotRequired() {
        ModelGraph graph = createGraphWithNoPartitionRequired();
        assertFalse(transformation.isConditionCompleted(graph, graph.getInterior("i1").orElseThrow(AssertionError::new)));
    }

    private ModelGraph createGraphWithPartitionRequired() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(4.0, 0.0, 10.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(8.0, 0.0, 20.0));

        graph.insertEdge("e1", v1, v2, false);
        graph.insertEdge("e2", v2, v3, false);
        graph.insertEdge("e3", v3, v1, false);
        InteriorNode i1 = graph.insertInterior("i1", v1, v2, v3);
        i1.setPartitionRequired(false);
        return graph;
    }

    private ModelGraph createGraphWithNoPartitionRequired() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(4.0, 0.0, 4.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(8.0, 0.0, 3.0));

        graph.insertEdge("e1", v1, v2, false);
        graph.insertEdge("e2", v2, v3, false);
        graph.insertEdge("e3", v3, v1, false);
        InteriorNode i1 = graph.insertInterior("i1", v1, v2, v3);
        i1.setPartitionRequired(false);
        return graph;
    }


}
