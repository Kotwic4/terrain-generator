package app;

import model.*;
import transformation.Transformation;
import transformation.TransformationP3;

import java.util.Arrays;

public class Transformation3Example {
    public static void main(String[] args) {
        ModelGraph graph = new ModelGraph("Example P3 Graph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0., 0., 0.));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(100., 0., 0.));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(200., 0., 0.));
        Vertex v4 = graph.insertVertex("v4", VertexType.HANGING_NODE, new Point3d(150., 50., 0.));
        Vertex v5 = graph.insertVertex("v5", VertexType.HANGING_NODE, new Point3d(100., 50., 0.));
        Vertex v6 = graph.insertVertex("v6", VertexType.SIMPLE_NODE, new Point3d(250., 100., 0.));
        Vertex v7 = graph.insertVertex("v7", VertexType.SIMPLE_NODE, new Point3d(100., 100., 0.));
        Vertex v8 = graph.insertVertex("v8", VertexType.SIMPLE_NODE, new Point3d(0., 100., 0.));

        GraphEdge e1 = graph.insertEdge("e1", v1, v8, true);
        GraphEdge e2 = graph.insertEdge("e2", v1, v2, true);
        GraphEdge e3 = graph.insertEdge("e3", v8, v2, true);
        GraphEdge e4 = graph.insertEdge("e4", v8, v7, true);
        GraphEdge e5 = graph.insertEdge("e5", v2, v5, true);
        GraphEdge e6 = graph.insertEdge("e6", v5, v4, true);
        GraphEdge e7 = graph.insertEdge("e7", v7, v5, true);
        GraphEdge e8 = graph.insertEdge("e8", v2, v4, true);
        GraphEdge e9 = graph.insertEdge("e9", v7, v4, true);
        GraphEdge e10 = graph.insertEdge("e10", v2, v3, true);
        GraphEdge e11 = graph.insertEdge("e11", v4, v3, true);
        GraphEdge e12 = graph.insertEdge("e12", v6, v3, true);
        GraphEdge e13 = graph.insertEdge("e13", v6, v7, true);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v8);
        InteriorNode in2 = graph.insertInterior("i2", v2, v7, v8, v5);
        InteriorNode in3 = graph.insertInterior("i3", v2, v4, v5);
        InteriorNode in4 = graph.insertInterior("i4", v5, v4, v7);
        InteriorNode in5 = graph.insertInterior("i5", v2, v3, v4);
        InteriorNode in6 = graph.insertInterior("i6", v3, v7, v6, v4);
        in2.setPartitionRequired(true);
        in6.setPartitionRequired(true);
        InteriorNode[] nodes = {in1, in2, in3, in4, in5, in6};
        graph.display();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Transformation t = new TransformationP3();
        Arrays.stream(nodes).forEach(n -> tryTransformation(graph, t, n));
    }

    private static void tryTransformation(ModelGraph graph, Transformation t, InteriorNode i) {
        if (t.isConditionCompleted(graph, i)) {
            t.transformGraph(graph, i);
        }
    }
}
