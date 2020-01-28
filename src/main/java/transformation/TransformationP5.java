package transformation;

import model.GraphEdge;
import model.InteriorNode;
import model.ModelGraph;
import model.Point3d;
import model.Vertex;
import model.VertexType;
import org.apache.log4j.BasicConfigurator;
import org.javatuples.Triplet;

import java.util.LinkedList;
import java.util.List;

public class TransformationP5 implements Transformation {

    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        List<Vertex> hangingNodes = interiorNode.getAssociatedNodes();
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();

        if (getSimpleVertexCount(triangle) != 3 || hangingNodes.size() != 2) {
            return false;
        }

        Vertex hangingNode1 = graph.getVertexBetween(triangle.getValue0(), triangle.getValue1()).orElse(null);
        Vertex hangingNode2 = graph.getVertexBetween(triangle.getValue0(), triangle.getValue2()).orElse(null);
        Vertex hangingNode3 = graph.getVertexBetween(triangle.getValue1(), triangle.getValue2()).orElse(null);

        double noHangingNodeEdge = 0.0;
        double edge1Length = 0.0;
        double edge2Length = 0.0;
        double edge3Length = 0.0;

        if (hangingNode1 == null) {
            noHangingNodeEdge = getLengthOfEdgeBetween(graph, triangle.getValue0(), triangle.getValue1());
        } else {
            edge1Length = getLengthOfEdgeBetween(graph, hangingNode1, triangle.getValue0()) +
                          getLengthOfEdgeBetween(graph, hangingNode1, triangle.getValue1());
        }

        if (hangingNode2 == null) {
            noHangingNodeEdge = getLengthOfEdgeBetween(graph, triangle.getValue0(), triangle.getValue2());
        } else {
            edge2Length = getLengthOfEdgeBetween(graph, hangingNode2, triangle.getValue0()) +
                          getLengthOfEdgeBetween(graph, hangingNode2, triangle.getValue2());
        }

        if (hangingNode3 == null) {
            noHangingNodeEdge = getLengthOfEdgeBetween(graph, triangle.getValue1(), triangle.getValue2());
        } else {
            edge3Length = getLengthOfEdgeBetween(graph, hangingNode3, triangle.getValue1()) +
                          getLengthOfEdgeBetween(graph, hangingNode3, triangle.getValue2());
        }

        System.out.println("Edge length: " + "e1: " + edge1Length + ", e2: " + edge2Length + ", e3: " + edge3Length + ", noHanging: " + noHangingNodeEdge);

        return edge1Length < noHangingNodeEdge && edge2Length < noHangingNodeEdge && edge3Length < noHangingNodeEdge;
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        if (this.isConditionCompleted(graph, interiorNode)) {
            Vertex v2 = getNodeToSplit(graph, interiorNode);
            Vertex v4 = getNotSplittableNode(graph, interiorNode);

            Vertex v3 = graph.getVertexBetween(v2, v4).orElse(null);
            Vertex[] vertexCandidates = triangleToList(interiorNode.getTriangle());
            Vertex v1 = null;
            Vertex v5 = null;

            for (Vertex vertex : vertexCandidates) {
                if (vertex != v3 && graph.getEdgeBetweenNodes(v2, vertex).isPresent()) {
                    v1 = vertex;
                }
                System.out.println(vertex);
                if (vertex != v3 && v4.hasEdgeBetween(vertex)) {
                    v5 = vertex;
                }
            }

            System.out.println(v1 + ", " + v2 + ", " + v3 + ", " + v4 + ", " + v5);

            graph.removeInterior(interiorNode.getId());
            graph.removeEdge(v1, v5);

            Vertex newVertex = graph.insertVertex("new1",
                                                  VertexType.SIMPLE_NODE,
                                                  new Point3d((v1.getXCoordinate() + v5.getXCoordinate()) / 2, (v1.getYCoordinate() + v5.getYCoordinate()) / 2, 0.0));
            insertEdgeBetween(v1, graph, newVertex, interiorNode.getId() + "v1");
            insertEdgeBetween(v5, graph, newVertex, interiorNode.getId() + "v5");
            insertEdgeBetween(v3, graph, newVertex, interiorNode.getId() + "v3");

            InteriorNode i1 = graph.insertInterior(interiorNode.getId() + "i1", v1, v3, newVertex, v2);
            InteriorNode i2 = graph.insertInterior(interiorNode.getId() + "i2", v3, newVertex, v5, v4);

            i1.setPartitionRequired(true);
            i2.setPartitionRequired(true);
        }
        return graph;
    }

    private double getLengthOfEdgeBetween(ModelGraph graph, Vertex value0, Vertex value1) {
        return graph.getEdgeBetweenNodes(value0, value1).get().getL();
    }


    private double getEdgeLength(ModelGraph graph, InteriorNode interiorNode, Vertex vertex) {
        double sum = 0;
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangle();
        Vertex[] nodes = triangleToList(triangle);

        for (Vertex node : nodes) {
            if (node.hasEdgeBetween(vertex)) {
                GraphEdge edge = graph.getEdgeBetweenNodes(node, vertex).orElse(null);
                if (edge != null) {
                    sum += edge.getL();
                }
            }
        }
        System.out.println(sum);
        return sum;
    }

    private Vertex getNodeToSplit(ModelGraph graph, InteriorNode interiorNode) {
        Vertex bestFit = null;
        double bestFitSum = 0.0;

        for (Vertex candidate : interiorNode.getAssociatedNodes()) {
            double edge = getEdgeLength(graph, interiorNode, candidate);
            if (edge > bestFitSum) {
                bestFit = candidate;
                bestFitSum = edge;
            }
        }
        return bestFit;
    }

    private Vertex getNotSplittableNode(ModelGraph graph, InteriorNode interiorNode) {
        List<Vertex> candidates = interiorNode.getAssociatedNodes();
        Vertex bestFit = getNodeToSplit(graph, interiorNode);
        for (Vertex vertex : candidates) {
            if (vertex != bestFit) {
                return vertex;
            }
        }
        throw new RuntimeException();
    }

    private Vertex[] triangleToList(Triplet<Vertex, Vertex, Vertex> triangle) {
        return new Vertex[]{triangle.getValue0(), triangle.getValue1(), triangle.getValue2()};
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

    public static ModelGraph createGraph() {
        ModelGraph graph = new ModelGraph("test");
        int vertexId = 1;
        Vertex v1 = insertSimpleVertexWithId(vertexId++, graph, 0.0, 100.0, 0.0);
        Vertex v2 = insertSimpleVertexWithId(vertexId++, graph, 0.0, 0.0, 0.0);
        Vertex v3 = insertSimpleVertexWithId(vertexId++, graph, 50.0, 50.0, 0.0);
        Vertex v4 = insertSimpleVertexWithId(vertexId++, graph, 100.0, 100.0, 0.0);
        Vertex v5 = insertSimpleVertexWithId(vertexId++, graph, 100.0, 50.0, 0.0);
        Vertex v6 = insertSimpleVertexWithId(vertexId++, graph, 100.0, 0.0, 0.0);
        Vertex v7 = insertSimpleVertexWithId(vertexId++, graph, 150.0, 50.0, 0.0);
        Vertex v8 = insertSimpleVertexWithId(vertexId++, graph, 200.0, 0.0, 0.0);
        Vertex v9 = insertSimpleVertexWithId(vertexId++, graph, 250.0, 50.0, 0.0);
        Vertex v10 = insertSimpleVertexWithId(vertexId++, graph, 300.0, 100.0, 0.0);
        Vertex v11 = insertSimpleVertexWithId(vertexId, graph, 300.0, 0.0, 0.0);

        insertEdgeBetween(v1, graph, v4, "e1");
        insertEdgeBetween(v4, graph, v10, "e2");
        insertEdgeBetween(v10, graph, v11, "e3");
        insertEdgeBetween(v11, graph, v8, "e4");
        insertEdgeBetween(v8, graph, v6, "e5");
        insertEdgeBetween(v6, graph, v2, "e6");
        insertEdgeBetween(v2, graph, v1, "e7");
        insertEdgeBetween(v1, graph, v3, "e8");
        insertEdgeBetween(v3, graph, v6, "e9");
        insertEdgeBetween(v2, graph, v3, "e10");
        insertEdgeBetween(v4, graph, v7, "e11");
        insertEdgeBetween(v5, graph, v7, "e12");
        insertEdgeBetween(v6, graph, v7, "e13");
        insertEdgeBetween(v7, graph, v8, "e14");
        insertEdgeBetween(v8, graph, v9, "e15");
        insertEdgeBetween(v9, graph, v10, "e16");
        insertEdgeBetween(v9, graph, v11, "e17");
        insertEdgeBetween(v5, graph, v6, "e18");
        insertEdgeBetween(v4, graph, v5, "e19");

        graph.insertInterior("i1", v1, v2, v3);
        graph.insertInterior("i2", v1, v4, v6, v3, v5);
        graph.insertInterior("i3", v2, v3, v6);
        graph.insertInterior("i4", v4, v5, v7);
        graph.insertInterior("i5", v5, v6, v7);
        graph.insertInterior("i6", v6, v7, v8);
        graph.insertInterior("i7", v8, v9, v11);
        graph.insertInterior("i8", v9, v10, v11);
        graph.insertInterior("i9", v4, v8, v10, v7, v9);

        return graph;
    }

    private static void insertEdgeBetween(Vertex v1, ModelGraph graph, Vertex v4, String e1) {
        graph.insertEdge(e1, v1, v4, false);
    }

    private static Vertex insertSimpleVertexWithId(int id, ModelGraph graph, double x, double y, double z) {
        return graph.insertVertex("v" + id, VertexType.SIMPLE_NODE, new Point3d(x, y, z));
    }

    public static void main(String[] args) throws InterruptedException {
        BasicConfigurator.configure();
        Transformation transformationP5 = new TransformationP5();
        ModelGraph graph = createGraph();
        graph.display();

        System.out.println("Displaying graph before applying transformation.");
        Thread.sleep(5000);
        System.out.println("Applying transformation...");

        List<InteriorNode> interiors = new LinkedList<>(graph.getInteriors());

        interiors.forEach(node -> {
            if (transformationP5.isConditionCompleted(graph, node)) {
                System.out.println("Interior " + node.getId() + " can be split.");
                transformationP5.transformGraph(graph, node);
            }
        });

    }
}
