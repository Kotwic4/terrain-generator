package transformation;

import model.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TransformationP7 implements Transformation {

    private final double eps;

    public TransformationP7(double eps) {
        this.eps = eps;
    }


    @Override
    public boolean isConditionCompleted(ModelGraph graph, InteriorNode interiorNode) {
        List<Double> vertexesZ = interiorNode.getAssociatedNodes().stream().map(e -> e.getCoordinates().getZ()).collect(Collectors.toList());

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
