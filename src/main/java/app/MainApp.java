package app;

import model.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import processor.MapProcessingUtil;
import transformation.*;

import java.util.ArrayList;
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
        ArrayList<Transformation> transformations = new ArrayList<Transformation>();
        transformations.add(new TransformationP1());
        transformations.add(new TransformationP2());
        transformations.add(new TransformationP3());
        transformations.add(new TransformationP4());
        transformations.add(new TransformationP5());
        transformations.add(new TransformationP6());
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
                    for(Transformation transformation: transformations){
                        try{
                            if(transformation.isConditionCompleted(graph, node)){
                                transformation.transformGraph(graph,node);
                                change = true;
                                break;
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    if(change){
                        break;
                    }
                }
            }
            Thread.sleep(3000l);

//            graph.display();
        }
    }
}
