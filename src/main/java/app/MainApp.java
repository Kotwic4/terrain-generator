package app;

import javafx.geometry.Point3D;
import model.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import processor.MapProcessingUtil;
import transformation.*;

import java.util.*;

public class MainApp {

    private static Logger log = Logger.getLogger(MainApp.class.getName());

    public static void main(String[] args) throws InterruptedException {
        BasicConfigurator.configure();

        Transformation t1 = new TransformationP1();
        Transformation t2 = new TransformationP2();
        Transformation t3 = new TransformationP3();
        Transformation t4 = new TransformationP4();

        ModelGraph graph = new ModelGraph("test");
        Vertex v1 = new Vertex(graph, "v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, -42.0));
        Vertex v2 = new Vertex(graph, "v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 100.0, -42.0));
        Vertex v3 = new Vertex(graph, "v3", VertexType.SIMPLE_NODE, new Point3d(100.0, 0.0, -42.0));
        Vertex v4 = new Vertex(graph, "v4", VertexType.SIMPLE_NODE, new Point3d(100.0, 100.0, -42.0));
        GraphEdge e1 = new GraphEdge("e1", "E", new Pair<>(v1, v2), false);
        GraphEdge e2 = new GraphEdge("e2", "E", new Pair<>(v4, v3), false);
        GraphEdge e3 = new GraphEdge("e3", "E", new Pair<>(v3, v1), false);
        GraphEdge e4 = new GraphEdge("e4", "E", new Pair<>(v2, v4), false);
        GraphEdge e5 = new GraphEdge("e5", "E", new Pair<>(v4, v1), false);

        graph.insertVertex(v1);
        graph.insertVertex(v2);
        graph.insertVertex(v3);
        graph.insertVertex(v4);
        graph.insertEdge(e1);
        graph.insertEdge(e2);
        graph.insertEdge(e3);
        graph.insertEdge(e4);
        graph.insertEdge(e5);
        graph.insertInterior("i1", v1, v2, v4);
        graph.insertInterior("i2", v1, v3, v4);

        graph.display();
        Point3d optimizedPoint = new Point3d(27.5, 52.5, -42.0);


        System.out.println("Start breaking");
        while (!isGraphOptimized(optimizedPoint, graph)) {
            Thread.sleep(5000);
            setPartitionRequired(graph.getInteriors(), optimizedPoint);
            for (InteriorNode interiorNode : new ArrayList<>(graph.getInteriors())) {
                if (t1.isConditionCompleted(graph, interiorNode))
                    t1.transformGraph(graph, interiorNode);
            }
            boolean dryRun = false;
            Thread.sleep(5000);
            while (!dryRun) {
                dryRun = true;
                for (InteriorNode interiorNode : new ArrayList<>(graph.getInteriors())) {
                    if (t2.isConditionCompleted(graph, interiorNode)) {
                        t2.transformGraph(graph, interiorNode);
                        dryRun = false;
                    }
                }
                for (InteriorNode interiorNode : new ArrayList<>(graph.getInteriors())) {
                    if (t3.isConditionCompleted(graph, interiorNode)) {
                        t3.transformGraph(graph, interiorNode);
                        dryRun = false;
                    }
                }
                for (InteriorNode interiorNode : new ArrayList<>(graph.getInteriors())) {
                    if (t4.isConditionCompleted(graph, interiorNode)) {
                        t4.transformGraph(graph, interiorNode);
                        dryRun = false;
                    }
                }
            }
        }

        graph.display();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        t1.transformGraph(graph, interiorNode);

//        TerrainMap map = new TerrainMap();
//        map.fillMapWithExampleData();
//
//        ModelGraph graph = new ModelGraph("testGraph");
//        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 2.0));
//        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(5.0, 0.0, 2.0));
//        Vertex v3 = graph.insertVertex("v3", VertexType.HANGING_NODE, new Point3d(0.0, 3.0, 2.0));
//        graph.insertEdge("e1", v1, v2, true);
//        graph.insertEdge("e2", v2, v3, true);
//        graph.insertEdge("e3", v3, v1, true);
//        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v3);
//
//        System.out.println(map.getAllPointsInTriangleArea(in1).size());
//        System.out.println(MapProcessingUtil.calculateTerrainApproximationError(in1, map));
    }

    private static void setPartitionRequired(Collection<InteriorNode> interiors, Point3d optimizedPoint) {
        for (InteriorNode i : interiors) {
            if (isInsideTriangle(optimizedPoint, i.getTriangle())) {
                i.setPartitionRequired(true);
            }
        }
    }

    private static boolean isGraphOptimized(Point3d optimizedPoint, ModelGraph graph) {
        for (InteriorNode interiorNode : graph.getInteriors()) {
            Point3d p1 = interiorNode.getTriangle().getValue0().getCoordinates();
            Point3d p2 = interiorNode.getTriangle().getValue1().getCoordinates();
            Point3d p3 = interiorNode.getTriangle().getValue2().getCoordinates();
            List<Point3d> soughtTriangle = Arrays.asList(
                    new Point3d(25, 50, -42),
                    new Point3d(50, 50, -42),
                    new Point3d(25, 75, -42));
            List<Point3d> givenTriangle = Arrays.asList(p1, p2, p3);
            if (new HashSet<>(givenTriangle).equals(new HashSet<>(soughtTriangle)))
                return true;
        }
        return false;
    }

    private static double sign(Point3d p1, Point3d p2, Point3d p3) {
        return (p1.getX() - p3.getX()) * (p2.getY() - p3.getY()) - (p2.getX() - p3.getX()) * (p1.getY() - p3.getY());
    }


    private static boolean isInsideTriangle(Point3d point, Triplet<Vertex, Vertex, Vertex> triangle) {
        double d1 = sign(point, triangle.getValue0().getCoordinates(), triangle.getValue1().getCoordinates());
        double d2 = sign(point, triangle.getValue1().getCoordinates(), triangle.getValue2().getCoordinates());
        double d3 = sign(point, triangle.getValue2().getCoordinates(), triangle.getValue0().getCoordinates());

        boolean has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        boolean has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

        return !(has_neg && has_pos);
    }
}
