package transformation;

import model.GraphNode;
import model.InteriorNode;
import model.ModelGraph;
import model.Vertex;
import org.javatuples.Triplet;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TransformationP7 implements Transformation {

    private double eps;

    public TransformationP7(double eps) {
        this.eps = eps;
    }


    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        Triplet<Vertex, Vertex, Vertex> triangle = interiorNode.getTriangle();
        List<Double> zCoordinates = triangle.toList().stream()
                .map(v -> (Vertex) v)
                .map(GraphNode::getZCoordinate)
                .collect(Collectors.toList());

        double minZ = Collections.min(zCoordinates);
        double maxZ = Collections.max(zCoordinates);
        return (maxZ - minZ) >= eps;
    }

    @Override
    public ModelGraph transformGraph(ModelGraph graph, InteriorNode interiorNode) {
        if (isConditionCompleted(graph, interiorNode)) {
            interiorNode.setPartitionRequired(true);
        }
        return graph;
    }
}
