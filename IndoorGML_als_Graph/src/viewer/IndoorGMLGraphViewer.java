package viewer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple JavaFX application that visualizes IndoorGML cell spaces
 * as a node relation graph. CellSpace elements become nodes and
 * Transition elements become edges. Nodes are labelled with either the
 * gml:name or the gml:id of the cell space.
 */
public class IndoorGMLGraphViewer extends Application {

    /**
     * Graph node representing a CellSpace.
     */
    static class GraphNode {
        String id;
        String label;
        double x;
        double y;
    }

    /**
     * Graph edge representing a Transition between two CellSpaces.
     */
    static class GraphEdge {
        GraphNode from;
        GraphNode to;
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Determine input file
        String gmlFile = getParameters().getRaw().isEmpty()
                ? "sample.gml"
                : getParameters().getRaw().get(0);

        Map<String, GraphNode> nodes = new LinkedHashMap<>();
        List<GraphEdge> edges = new ArrayList<>();

        // Parse IndoorGML document
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new File(gmlFile));
        doc.getDocumentElement().normalize();

        // Collect CellSpace elements
        NodeList cellSpaces = doc.getElementsByTagName("CellSpace");
        for (int i = 0; i < cellSpaces.getLength(); i++) {
            Element cs = (Element) cellSpaces.item(i);
            String id = cs.getAttribute("gml:id");
            String name = "";
            NodeList nameList = cs.getElementsByTagName("gml:name");
            if (nameList.getLength() > 0) {
                name = nameList.item(0).getTextContent();
            }
            GraphNode n = new GraphNode();
            n.id = id;
            n.label = name.isEmpty() ? id : name;
            nodes.put("#" + id, n); // store with '#' to match xlink:href
        }

        // Collect Transition elements and build edges
        NodeList transitions = doc.getElementsByTagName("Transition");
        for (int i = 0; i < transitions.getLength(); i++) {
            Element tr = (Element) transitions.item(i);
            NodeList connects = tr.getElementsByTagName("connects");
            if (connects.getLength() >= 2) {
                String href1 = ((Element) connects.item(0)).getAttribute("xlink:href");
                String href2 = ((Element) connects.item(1)).getAttribute("xlink:href");
                GraphNode n1 = nodes.get(href1);
                GraphNode n2 = nodes.get(href2);
                if (n1 != null && n2 != null) {
                    GraphEdge e = new GraphEdge();
                    e.from = n1;
                    e.to = n2;
                    edges.add(e);
                }
            }
        }

        // Layout nodes on a circle
        int n = nodes.size();
        double radius = 200;
        double centerX = 250;
        double centerY = 250;
        int idx = 0;
        Group root = new Group();
        for (GraphNode node : nodes.values()) {
            double angle = 2 * Math.PI * idx / n;
            node.x = centerX + radius * Math.cos(angle);
            node.y = centerY + radius * Math.sin(angle);
            Circle c = new Circle(node.x, node.y, 20);
            Text t = new Text(node.x - 15, node.y + 5, node.label);
            root.getChildren().addAll(c, t);
            idx++;
        }

        // Draw edges
        for (GraphEdge e : edges) {
            Line l = new Line(e.from.x, e.from.y, e.to.x, e.to.y);
            root.getChildren().add(l);
        }

        stage.setTitle("IndoorGML Graph");
        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
