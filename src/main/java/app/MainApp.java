package app;

import model.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import transformation.*;

import java.util.Collection;

public class MainApp {

    private static Logger log = Logger.getLogger(MainApp.class.getName());


    private static ModelGraph task() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(10.0, 0.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(10.0, 10.0, 0.0));
        Vertex v4 = graph.insertVertex("v4", VertexType.SIMPLE_NODE, new Point3d(0.0, 10.0, 0.0));

        GraphEdge v1_v2 = graph.insertEdge("e1", v1, v2, true);
        GraphEdge v2_v3 = graph.insertEdge("e2", v2, v3, true);
        GraphEdge v3_v4 = graph.insertEdge("e3", v3, v4, true);
        GraphEdge v4_v1 = graph.insertEdge("e4", v4, v1, true);
        GraphEdge v2_v4 = graph.insertEdge("e5", v2, v4, false);

        InteriorNode in1 = graph.insertInterior("i1", v1, v2, v4);
        InteriorNode in2 = graph.insertInterior("i2", v2, v3, v4);

        return graph;
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        ModelGraph graph = task();
        Transformation t1 = new TransformationP1();
        Transformation t2 = new TransformationP2();
        Transformation t3 = new TransformationP3();
//        Transformation t4 = new TransformationP4();
//        Transformation t5 = new TransformationP5();
//        Transformation t6 = new TransformationP6();
        Transformation t7 = new TransformationP7(5.0f);

//        log.info(String.format("Condition state for transformation P1: %b", t1.isConditionCompleted(graph, interiorNode)));

        graph.display();

        Transformation[] transformations = {t1, t2, t3};
        for (int i = 0; i < 5; i++) {
            Collection<InteriorNode> interiorNodes = graph.getInteriors();
            for (InteriorNode node : interiorNodes) {
                t7.transformGraph(graph, node);
            }
            boolean change = true;
            while (change) {
                change = false;
                for (Transformation t : transformations) {
                    interiorNodes = graph.getInteriors();
                    for (InteriorNode node : interiorNodes) {
                        if (t.isConditionCompleted(graph, node)) {
                            t.transformGraph(graph, node);
                            change = true;
                            break;
                        }

                    }
                }
            }
//            Thread.sleep(300l);

//            graph.display();
        }

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
}
