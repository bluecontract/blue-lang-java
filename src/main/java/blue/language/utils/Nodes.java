package blue.language.utils;

import blue.language.model.Node;
import blue.language.model.Feature;

import java.util.ArrayList;

public class Nodes {

    public static boolean isEmptyNode(Node node) {
        return node.getName() == null && node.getType() == null && node.getValue() == null && node.getDescription() == null &&
                node.getItems() == null && node.getProperties() == null && node.getRef() == null && node.getFeatures() == null;
    }

    public static void addFeature(Node node, Feature feature) {
        if (node.getFeatures() == null)
            node.features(new ArrayList<>());
        node.getFeatures().add(feature);
    }

}
