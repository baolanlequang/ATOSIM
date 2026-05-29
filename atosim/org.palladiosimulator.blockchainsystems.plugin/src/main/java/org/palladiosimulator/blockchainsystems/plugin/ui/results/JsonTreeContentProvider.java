package org.palladiosimulator.blockchainsystems.plugin.ui.results;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.palladiosimulator.blockchainsystems.plugin.json.JsonArray;
import org.palladiosimulator.blockchainsystems.plugin.json.JsonObject;
import org.palladiosimulator.blockchainsystems.plugin.json.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonTreeContentProvider implements ITreeContentProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof String s) {
            try {
                return new Object[]{new JsonTreeNode("root", JsonValue.parse(s))};
            } catch (Exception e) {
                e.printStackTrace();
                return new Object[]{"ERROR"};
            }
        }
        if (inputElement instanceof JsonValue v) {
            return new Object[]{new JsonTreeNode("root", v)};
        }
        return new Object[0];
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof JsonTreeNode node) {
            return getJsonElementChildren(node);
        }
        return new Object[0];
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof JsonTreeNode node) return node.parent;
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof JsonTreeNode node) {
            if (node.value instanceof JsonObject o) return !o.isEmpty();
            if (node.value instanceof JsonArray a) return !a.isEmpty();
        }
        return false;
    }

    private Object[] getJsonElementChildren(JsonTreeNode node) {
        if (node.value instanceof JsonObject o) {
            List<JsonTreeNode> children = new ArrayList<>();
            for (Map.Entry<String, JsonValue> entry : o.entrySet()) {
                children.add(new JsonTreeNode(entry.getKey(), entry.getValue(), node));
            }
            return children.toArray();
        }
        if (node.value instanceof JsonArray a) {
            List<JsonTreeNode> children = new ArrayList<>();
            List<JsonValue> elements = a.getElements();
            for (int i = 0; i < elements.size(); i++) {
                children.add(new JsonTreeNode("[" + i + "]", elements.get(i), node));
            }
            return children.toArray();
        }
        return new Object[0];
    }
}
