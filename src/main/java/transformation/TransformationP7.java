package transformation;

import model.*;
import org.javatuples.Triplet;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TransformationP7 implements Transformation {

    private final double eps;

    public TransformationP7(double eps) {
        this.eps = eps;
    }


    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangleVertexes();
        List<Vertex> vertexList = new LinkedList<>();
        vertexList.add(triangle.getValue0());
        vertexList.add(triangle.getValue1());
        vertexList.add(triangle.getValue2());

        List<Double> vertexesZ = vertexList.stream().map(e -> e.getCoordinates().getZ()).collect(Collectors.toList());

        System.out.println(vertexesZ);
        if(vertexesZ.size() == 0){
            return false;
        }
        Double minZ = Collections.min(vertexesZ);
        Double maxZ = Collections.max(vertexesZ);

        return maxZ - minZ > eps;
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        interiorNode.setPartitionRequired(true);
        return graph;
    }
}
