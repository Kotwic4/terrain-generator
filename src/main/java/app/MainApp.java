package app;

import model.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import processor.MapProcessingUtil;
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

    public static void main(String[] args) throws InterruptedException {
        BasicConfigurator.configure();
        ModelGraph graph = task();
        Transformation t1 = new TransformationP1();
        Transformation t2 = new TransformationP2();
        Transformation t3 = new TransformationP1();
        Transformation t4 = new TransformationP4();
        Transformation t5 = new TransformationP5();
        Transformation t6 = new TransformationP6();
//        log.info(String.format("Condition state for transformation P1: %b", t1.isConditionCompleted(graph, interiorNode)));

        graph.display();

        for(int i = 0; i < 5; i++){
            Collection<InteriorNode> interiorNodes = graph.getInteriors();
            for(InteriorNode node: interiorNodes){
                node.setPartitionRequired(true);
            }
//            Collection<InteriorNode> interiorNodes = graph.getInteriors();
            boolean change = true;
            while(change){
                change = false;
                for(InteriorNode node: interiorNodes){
                    if(t1.isConditionCompleted(graph, node)){
                        t1.transformGraph(graph,node);
                        change = true;
                        break;
                    }
                    if(t2.isConditionCompleted(graph, node)){
                        t2.transformGraph(graph,node);
                        change = true;
                        break;
                    }
                    if(t3.isConditionCompleted(graph, node)){
                        t3.transformGraph(graph,node);
                        change = true;
                        break;
                    }
                    if(t4.isConditionCompleted(graph, node)){
                        t4.transformGraph(graph,node);
                        change = true;
                        break;
                    }
                    if(t5.isConditionCompleted(graph, node)){
                        t5.transformGraph(graph,node);
                        change = true;
                        break;
                    }
                    if(t6.isConditionCompleted(graph, node)){
                        t6.transformGraph(graph,node);
                        change = true;
                        break;
                    }
                }
            }
            Thread.sleep(3000l);

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
