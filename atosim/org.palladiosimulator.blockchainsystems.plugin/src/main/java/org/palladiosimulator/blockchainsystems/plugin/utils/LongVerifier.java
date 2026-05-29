package org.palladiosimulator.blockchainsystems.plugin.utils;

import org.eclipse.swt.events.VerifyEvent;

public class LongVerifier extends NumberVerifier {

    public static final LongVerifier INSTANCE = new LongVerifier();

    @Override
    public void verifyText(VerifyEvent e) {
        String text = getNewText(e);
        if (text.isEmpty()) return;
        try { Long.parseLong(text); } catch (NumberFormatException ex) { e.doit = false; }
    }
}
