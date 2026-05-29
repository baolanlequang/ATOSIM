package org.palladiosimulator.blockchainsystems.plugin.utils;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

public abstract class NumberVerifier implements VerifyListener {

    protected String getNewText(VerifyEvent e) {
        String oldText = ((Text) e.widget).getText();
        return oldText.substring(0, e.start) + e.text + oldText.substring(e.end);
    }
}
