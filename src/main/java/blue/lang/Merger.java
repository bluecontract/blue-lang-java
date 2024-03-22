package blue.lang;

import blue.lang.model.limits.Limits;
import blue.lang.model.limits.LimitsInterface;
import blue.lang.utils.BlueIdCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Merger implements NodeResolver {

    private MergingProcessor mergingProcessor;
    private NodeProvider nodeProvider;

    public Merger(MergingProcessor mergingProcessor, NodeProvider nodeProvider) {
        this.mergingProcessor = mergingProcessor;
        this.nodeProvider = nodeProvider;
    }

    public void merge(Node target, Node source) {
        merge(target, source, Limits.NO_LIMITS);
    }

    public void merge(Node target, Node source, LimitsInterface limits) {
        if (limits.canReadNext()) {
            if (source.getType() != null) {
                Node resolvedType = resolve(source.getType(), limits);
                source.type(resolvedType);
                merge(target, source.getType(), limits.next(true));
            }
        }
        mergeObject(target, source, limits);
    }

    private void mergeObject(Node target, Node source, LimitsInterface limits) {
        mergingProcessor.process(target, source, nodeProvider, this);

            List<Node> children = source.getItems();
            if (children != null)
                mergeChildren(target, children, limits);
            Map<String, Node> properties = source.getProperties();
            if (properties != null)
                properties.forEach((key, value) -> mergeProperty(target, key, value, limits));
    }

    private void mergeChildren(Node target, List<Node> sourceChildren, LimitsInterface limits) {
        List<Node> targetChildren = target.getItems();
        if (targetChildren == null) {
            targetChildren = new ArrayList<>();
            for (int i = 0; i < sourceChildren.size(); i++)
                targetChildren.add(new Node());
            target.items(targetChildren);
        } else if (sourceChildren.size() != targetChildren.size())
            throw new IllegalArgumentException("Cannot merge two lists with different items size.");
        for (int i = 0; i < sourceChildren.size(); i++)
            if (!limits.canReadNext()) {
                targetChildren.get(i).blueId(BlueIdCalculator.calculateBlueId(sourceChildren.get(i)));
            } else {
                merge(targetChildren.get(i), sourceChildren.get(i), limits);
            }
    }

    private void mergeProperty(Node target, String sourceKey, Node sourceValue, LimitsInterface limits) {
        if (!limits.filter(sourceKey)) {
            return;
        }
        if (!limits.canReadNext()) {
            Node node = resolve(sourceValue, Limits.depth(0));
            target.blueId(BlueIdCalculator.calculateBlueId(node));
            return;
        }

        Node node = resolve(sourceValue, limits.next(false));


        if (target.getProperties() == null)
            target.properties(new HashMap<>());
        Node targetValue = target.getProperties().get(sourceKey);
        if (targetValue == null)
            target.getProperties().put(sourceKey, node);
        else
            mergeObject(targetValue, node, limits.next(false));
    }

    @Override
    public Node resolve(Node node, LimitsInterface limits) {
        Node resultNode = new Node();
        merge(resultNode, node, limits);
        return resultNode;
    }
}