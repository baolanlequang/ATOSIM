package org.palladiosimulator.blockchainsystems.plugin.ui.results;

import org.eclipse.jface.viewers.LabelProvider;

public class JsonTreeLabelProvider extends LabelProvider {

    @Override
    public String getText(Object element) {
        if (element instanceof JsonTreeNode node) return node.getDisplayText();
        if (element instanceof String s) return s;
        return element != null ? element.toString() : "";
    }
}
