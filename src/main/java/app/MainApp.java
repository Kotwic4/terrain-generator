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


    private static ModelGraph task1Graph() {
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

    private static ModelGraph task2Graph() {
        ModelGraph graph = new ModelGraph("testGraph");
        Vertex v1 = graph.insertVertex("v1", VertexType.SIMPLE_NODE, new Point3d(0.0, 200.0, 0.0));
        Vertex v2 = graph.insertVertex("v2", VertexType.SIMPLE_NODE, new Point3d(0.0, 100.0, 0.0));
        Vertex v3 = graph.insertVertex("v3", VertexType.SIMPLE_NODE, new Point3d(0.0, 0.0, 0.0));

        Vertex v4 = graph.insertVertex("v4", VertexType.HANGING_NODE, new Point3d(50.0, 150.0, 5.0));
        Vertex v5 = graph.insertVertex("v5", VertexType.HANGING_NODE, new Point3d(50.0, 50.0, 5.0));

        Vertex v6 = graph.insertVertex("v6", VertexType.SIMPLE_NODE, new Point3d(100.0, 100.0, 10.0));
        Vertex v7 = graph.insertVertex("v7", VertexType.HANGING_NODE, new Point3d(100.0, 50.0, 10.0));
        Vertex v8 = graph.insertVertex("v8", VertexType.SIMPLE_NODE, new Point3d(100.0, 0.0, 10.0));

        Vertex v9 = graph.insertVertex("v9", VertexType.HANGING_NODE, new Point3d(150.0, 150.0, 15.0));
        Vertex v10 = graph.insertVertex("v10", VertexType.HANGING_NODE, new Point3d(150.0, 50.0, 15.0));

        Vertex v11 = graph.insertVertex("v11", VertexType.SIMPLE_NODE, new Point3d(200.0, 200.0, 20.0));
        Vertex v12 = graph.insertVertex("v12", VertexType.HANGING_NODE, new Point3d(200.0, 100.0, 20.0));
        Vertex v13 = graph.insertVertex("v13", VertexType.SIMPLE_NODE, new Point3d(200.0, 0.0, 20.0));

        Vertex v14 = graph.insertVertex("v14", VertexType.HANGING_NODE, new Point3d(250.0, 50.0, 45.0));

        Vertex v15 = graph.insertVertex("v15", VertexType.SIMPLE_NODE, new Point3d(300.0, 200.0, 60.0));
        Vertex v16 = graph.insertVertex("v16", VertexType.SIMPLE_NODE, new Point3d(300.0, 100.0, 50.0));
        Vertex v17 = graph.insertVertex("v17", VertexType.SIMPLE_NODE, new Point3d(300.0, 0.0, 45.0));

        graph.insertEdge("e1", v1, v2, true);
        graph.insertEdge("e2", v2, v3, true);
        graph.insertEdge("e3", v3, v8, true);
        graph.insertEdge("e4", v8, v13, true);
        graph.insertEdge("e5", v13, v17, true);
        graph.insertEdge("e6", v17, v16, true);
        graph.insertEdge("e7", v16, v15, true);
        graph.insertEdge("e8", v15, v11, true);
        graph.insertEdge("e9", v1, v11, true);

        graph.insertEdge("e10", v1, v4, false);
        graph.insertEdge("e11", v4, v2, false);
        graph.insertEdge("e13", v4, v6, false);
        graph.insertEdge("e14", v2, v6, false);
        graph.insertEdge("e15", v2, v5, false);
        graph.insertEdge("e16", v5, v3, false);
        graph.insertEdge("e17", v5, v8, false);
        graph.insertEdge("e19", v6, v7, false);
        graph.insertEdge("e20", v6, v9, false);
        graph.insertEdge("e21", v6, v10, false);
        graph.insertEdge("e22", v7, v8, false);
        graph.insertEdge("e23", v7, v10, false);
        graph.insertEdge("e24", v8, v10, false);
        graph.insertEdge("e25", v9, v11, false);
        graph.insertEdge("e26", v9, v12, false);
        graph.insertEdge("e27", v10, v13, false);
        graph.insertEdge("e28", v11, v12, false);
        graph.insertEdge("e29", v12, v16, false);
        graph.insertEdge("e30", v13, v14, false);
        graph.insertEdge("e31", v14, v16, false);
        graph.insertEdge("e32", v14, v17, false);
        graph.insertEdge("e33", v6, v12, false);
        graph.insertEdge("e34", v11, v16, false);



        graph.insertInterior("i1", v1, v2, v4);
        graph.insertInterior("i2", v2, v4, v6);
        graph.insertInterior("i3", v2, v3, v5);
        graph.insertInterior("i4", v3, v5, v8);
        graph.insertInterior("i5", v2, v6, v8);
        graph.insertInterior("i6", v1, v6, v11);
        graph.insertInterior("i7", v6, v7, v10);
        graph.insertInterior("i8", v7, v8, v10);
        graph.insertInterior("i9", v8, v10, v13);
        graph.insertInterior("i10", v6, v13, v16);
        graph.insertInterior("i11", v6, v9, v12);
        graph.insertInterior("i12", v9, v11, v12);
        graph.insertInterior("i13", v11, v12, v16);
        graph.insertInterior("i14", v11, v15, v16);
        graph.insertInterior("i15", v13, v14, v17);
        graph.insertInterior("i16", v14, v16, v17);

        return graph;
    }

    public static void task1() throws InterruptedException {
        BasicConfigurator.configure();
        ModelGraph graph = task1Graph();
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

    public static void task2() throws InterruptedException {
        BasicConfigurator.configure();
        ModelGraph graph = task2Graph();
        ArrayList<Transformation> transformations = new ArrayList<Transformation>();
        transformations.add(new TransformationP1());
        transformations.add(new TransformationP2());
        transformations.add(new TransformationP3());
        transformations.add(new TransformationP4());
        transformations.add(new TransformationP5());
        transformations.add(new TransformationP6());
        graph.display();

        TransformationP7 transformationP7 = new TransformationP7(5);

        for(int i = 0; i < 5; i++){
            Collection<InteriorNode> interiorNodes = graph.getInteriors();
            for(InteriorNode node: interiorNodes){
                if(transformationP7.isConditionCompleted(graph, node)){
                    transformationP7.transformGraph(graph,node);
                }
            }
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

    public static void main(String[] args) throws InterruptedException {
//        task1();
        task2();
    }
}
