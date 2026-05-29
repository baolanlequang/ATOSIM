package org.palladiosimulator.blockchainsystems.plugin.utils;

import org.eclipse.swt.events.VerifyEvent;

public class DoubleVerifier extends NumberVerifier {

    public static final DoubleVerifier INSTANCE = new DoubleVerifier();

    @Override
    public void verifyText(VerifyEvent e) {
        String text = getNewText(e);
        if (text.isEmpty()) return;
        try { Double.parseDouble(text); } catch (NumberFormatException ex) { e.doit = false; }
    }
}
