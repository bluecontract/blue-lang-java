package blue.lang.utils;

import blue.lang.Node;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static blue.lang.utils.NodeToObject.Strategy.SINGLE_VALUE;
import static blue.lang.utils.NodeToObject.Strategy.STANDARD;
import static blue.lang.utils.Properties.*;

public class NodeToObject {

    public enum Strategy {
        STANDARD,
        SINGLE_VALUE
    }

    public static Object get(Node node) {
        return get(node, STANDARD);
    }

    public static Object get(Node node, Strategy strategy) {

        if (node.getValue() != null && strategy == SINGLE_VALUE)
            return node.getValue();

        if (node.getItems() != null)
            return node.getItems().stream()
                    .map(item -> get(item, strategy))
                    .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        if (node.getName() != null)
            result.put(OBJECT_NAME, node.getName());
        if (node.getDescription() != null)
            result.put(OBJECT_DESCRIPTION, node.getDescription());
        if (node.getType() != null)
            result.put(OBJECT_TYPE, get(node.getType()));
        if (node.getValue() != null)
            result.put(OBJECT_VALUE, node.getValue());
        if (node.getRef() != null)
            result.put(OBJECT_REF, node.getRef());
        if (node.getBlueId() != null)
            result.put(OBJECT_BLUE_ID, node.getBlueId());
        if (node.getProperties() != null)
            node.getProperties().forEach((key, value) -> result.put(key, get(value, strategy)));
        return result;

    }

}
