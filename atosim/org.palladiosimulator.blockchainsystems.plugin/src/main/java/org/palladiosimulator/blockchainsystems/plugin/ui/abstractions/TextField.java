package org.palladiosimulator.blockchainsystems.plugin.ui.abstractions;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.util.function.Predicate;

public class TextField {

    public final Label beforeLabel;
    public final Text textControl;
    public final Label afterLabel;

    private final String _attributeKey;
    private final String _defaultValue;
    private final Predicate<String> _isValueValid;

    public TextField(
            Composite parent,
            String labelBeforeText,
            String labelAfterText,
            VerifyListener verifier,
            String attributeKey,
            String defaultValue,
            Predicate<String> isValueValid
    ) {
        _attributeKey = attributeKey;
        _defaultValue = defaultValue;
        _isValueValid = isValueValid;

        beforeLabel = new Label(parent, SWT.NONE);
        beforeLabel.setText(labelBeforeText);
        beforeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

        textControl = new Text(parent, SWT.BORDER);
        textControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textControl.addVerifyListener(verifier);

        afterLabel = new Label(parent, SWT.NONE);
        afterLabel.setText(labelAfterText);
        afterLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
    }

    public boolean isValid() {
        return _isValueValid.test(textControl.getText());
    }

    public void initializeFrom(ILaunchConfiguration configuration) {
        try {
            textControl.setText(configuration.getAttribute(_attributeKey, _defaultValue));
        } catch (Exception e) {
            textControl.setText(_defaultValue);
        }
    }

    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        String text = textControl.getText();
        configuration.setAttribute(_attributeKey, text != null ? text : _defaultValue);
    }

    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(_attributeKey, _defaultValue);
    }
}
