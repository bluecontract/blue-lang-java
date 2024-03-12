package blue.lang;

import blue.lang.graph.*;
import blue.lang.graph.processor.ListItemsTypeChecker;
import blue.lang.graph.processor.SequentialNodeProcessor;
import blue.lang.graph.processor.TypeAssigner;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListItemsTypeCheckerTest {

    @Test
    public void testSuccess() throws Exception {
        Node a = new Node().name("A");
        Node b = new Node().name("B").type("A");
        Node c = new Node().name("C").type("B");

        Node x = new Node().name("X").properties(
                "a", new Node().type("B")
        );
        Node y = new Node().name("Y").type("X").properties(
                "a", new Node().items(
                        new Node().type("B"),
                        new Node().type("C")
                )
        );

        List<Node> nodes = Arrays.asList(a, b, c, x, y);
        Types types = new Types(nodes);
        NodeProcessor nodeProcessor = new SequentialNodeProcessor(
                Arrays.asList(
                        new TypeAssigner(types),
                        new ListItemsTypeChecker(types)
                )
        );
        NodeManager manager = new NodeManager(nodes, nodeProcessor);

        Merger merger = new Merger(manager);
        Node node = new Node();
        merger.merge(node, manager.getNode("Y"));

        assertEquals(node.getProperties().get("a").getType(), "B");
    }


    @Test
    public void testFailure() throws Exception {
        Node a = new Node().name("A");
        Node b = new Node().name("B").type("A");
        Node c = new Node().name("C").type("B");

        Node x = new Node().name("X").properties(
                "a", new Node().type("B")
        );
        Node y = new Node().name("Y").type("X").properties(
                "a", new Node().items(
                        new Node().type("A"),
                        new Node().type("C")
                )
        );

        List<Node> nodes = Arrays.asList(a, b, c, x, y);
        Types types = new Types(nodes);
        NodeProcessor nodeProcessor = new SequentialNodeProcessor(
                Arrays.asList(
                        new TypeAssigner(types),
                        new ListItemsTypeChecker(types)
                )
        );
        NodeManager manager = new NodeManager(nodes, nodeProcessor);

        Merger merger = new Merger(manager);
        Node node = new Node();

        assertThrows(IllegalArgumentException.class, () -> {
            merger.merge(node, manager.getNode("Y"));
        });
    }

}