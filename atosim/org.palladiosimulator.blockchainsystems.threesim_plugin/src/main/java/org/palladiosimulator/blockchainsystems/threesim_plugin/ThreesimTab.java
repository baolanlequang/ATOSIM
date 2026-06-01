package org.palladiosimulator.blockchainsystems.threesim_plugin;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.palladiosimulator.blockchainsystems.plugin.ui.abstractions.TextField;
import org.palladiosimulator.blockchainsystems.plugin.utils.DoubleVerifier;
import org.palladiosimulator.blockchainsystems.plugin.utils.LongVerifier;

public class ThreesimTab extends AbstractLaunchConfigurationTab {

    private static final double MIN_FAILURE_THROUGHPUT_THRESHOLD = 0.0;
    private static final double MIN_SHANNON_ENTROPY_K = 0.0;
    private static final double MIN_NAKAMOTO_COEFFICIENT_THRESHOLD = 0.0;
    private static final double MAX_NAKAMOTO_COEFFICIENT_THRESHOLD = 100.0;
    private static final double MIN_RELIABILITY_OBSERVATION_TIMESPAN = 0.0;

    private boolean isInitialized = false;

    private TextField failureThroughputThresholdField;
    private TextField shannonEntropyKField;
    private TextField nakamotoCoefficientThresholdField;
    private TextField reliabilityObservationTimespanField;
    private TextField blockIntervalField;
    private TextField propagationDelayField;
    private TextField nodeDegreeField;
    private TextField maxBlockSizeField;
    private TextField networkBandwidthField;
    private TextField transactionADelayField;
    private TextField transactionBAccelerationField;
    private Combo combinedAttackModeCombo;

    @Override
    public void createControl(Composite parent) {
        Composite root = new Composite(parent, SWT.BORDER);
        GridLayoutFactory.swtDefaults().numColumns(1).applyTo(root);

        Group group = new Group(root, SWT.NONE);
        group.setText("3SIM Parameters");
        GridLayoutFactory.swtDefaults().numColumns(3).spacing(0, 10).applyTo(group);
        group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        failureThroughputThresholdField = new TextField(group,
                "Failure Throughput Threshold:", " transactions/min",
                DoubleVerifier.INSTANCE,
                ThreesimAttributes.FAILURE_THROUGHPUT_THRESHOLD,
                ThreesimAttributes.FAILURE_THROUGHPUT_THRESHOLD_DEFAULT,
                s -> parseDouble(s, v -> v >= MIN_FAILURE_THROUGHPUT_THRESHOLD));

        shannonEntropyKField = new TextField(group,
                "Shannon Entropy K:", "",
                DoubleVerifier.INSTANCE,
                ThreesimAttributes.SHANNON_ENTROPY_K,
                ThreesimAttributes.SHANNON_ENTROPY_K_DEFAULT,
                s -> parseDouble(s, v -> v > MIN_SHANNON_ENTROPY_K));

        nakamotoCoefficientThresholdField = new TextField(group,
                "Nakamoto Coefficient Threshold:", " %",
                DoubleVerifier.INSTANCE,
                ThreesimAttributes.NAKAMOTO_COEFFICIENT_THRESHOLD,
                ThreesimAttributes.NAKAMOTO_COEFFICIENT_THRESHOLD_DEFAULT,
                s -> parseDouble(s, v -> v >= MIN_NAKAMOTO_COEFFICIENT_THRESHOLD
                        && v <= MAX_NAKAMOTO_COEFFICIENT_THRESHOLD));

        reliabilityObservationTimespanField = new TextField(group,
                "Reliability Observation Timespan:", " hours",
                DoubleVerifier.INSTANCE,
                ThreesimAttributes.RELIABILITY_OBSERVATION_TIMESPAN,
                ThreesimAttributes.RELIABILITY_OBSERVATION_TIMESPAN_DEFAULT,
                s -> parseDouble(s, v -> v > MIN_RELIABILITY_OBSERVATION_TIMESPAN));

        blockIntervalField = new TextField(group,
                "Block Interval:", " ms",
                DoubleVerifier.INSTANCE,
                ThreesimAttributes.BLOCK_INTERVAL,
                ThreesimAttributes.BLOCK_INTERVAL_DEFAULT,
                s -> parseDouble(s, v -> v > 0));

        propagationDelayField = new TextField(group,
                "Propagation Delay:", " ms",
                DoubleVerifier.INSTANCE,
                ThreesimAttributes.PROPAGATION_DELAY,
                ThreesimAttributes.PROPAGATION_DELAY_DEFAULT,
                s -> parseDouble(s, v -> v >= 0));

        nodeDegreeField = new TextField(group,
                "Node Degree:", "",
                LongVerifier.INSTANCE,
                ThreesimAttributes.NODE_DEGREE,
                ThreesimAttributes.NODE_DEGREE_DEFAULT,
                s -> parseLong(s, v -> v > 0));

        maxBlockSizeField = new TextField(group,
                "Max Block Size:", " bytes",
                LongVerifier.INSTANCE,
                ThreesimAttributes.MAX_BLOCK_SIZE,
                ThreesimAttributes.MAX_BLOCK_SIZE_DEFAULT,
                s -> parseLong(s, v -> v > 0));

        networkBandwidthField = new TextField(group,
                "Network Bandwidth:", " bytes/s",
                DoubleVerifier.INSTANCE,
                ThreesimAttributes.NETWORK_BANDWIDTH,
                ThreesimAttributes.NETWORK_BANDWIDTH_DEFAULT,
                s -> parseDouble(s, v -> v > 0));

        transactionADelayField = new TextField(group,
                "Transaction A Delay:", " ms",
                LongVerifier.INSTANCE,
                ThreesimAttributes.TRANSACTION_A_DELAY,
                ThreesimAttributes.TRANSACTION_A_DELAY_DEFAULT,
                s -> parseLong(s, v -> v >= 0));

        transactionBAccelerationField = new TextField(group,
                "Transaction B Acceleration:", " ms",
                LongVerifier.INSTANCE,
                ThreesimAttributes.TRANSACTION_B_ACCELERATION,
                ThreesimAttributes.TRANSACTION_B_ACCELERATION_DEFAULT,
                s -> parseLong(s, v -> v >= 0));

        Label combinedAttackLabel = new Label(group, SWT.NONE);
        combinedAttackLabel.setText("Combined Attack Mode:");

        combinedAttackModeCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
        combinedAttackModeCombo.setItems("None", "Selfish + Race", "Selfish + Finney", "Selfish + Lead Stubborn", "Selfish + Trail Stubborn");
        combinedAttackModeCombo.select(0);
        combinedAttackModeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(group, SWT.NONE).setText("");

        combinedAttackModeCombo.addModifyListener(e -> updateLaunchConfigurationDialog());

        setControl(root);
        isInitialized = true;
    }

    @Override
    public String getName() {
        return "ATO-SIM Parameters";
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        if (!isInitialized) return;
        try {
            failureThroughputThresholdField.initializeFrom(configuration);
            shannonEntropyKField.initializeFrom(configuration);
            nakamotoCoefficientThresholdField.initializeFrom(configuration);
            reliabilityObservationTimespanField.initializeFrom(configuration);
            blockIntervalField.initializeFrom(configuration);
            propagationDelayField.initializeFrom(configuration);
            nodeDegreeField.initializeFrom(configuration);
            maxBlockSizeField.initializeFrom(configuration);
            networkBandwidthField.initializeFrom(configuration);
            transactionADelayField.initializeFrom(configuration);
            transactionBAccelerationField.initializeFrom(configuration);

            String mode = configuration.getAttribute(
                    ThreesimAttributes.COMBINED_ATTACK_MODE,
                    ThreesimAttributes.COMBINED_ATTACK_MODE_DEFAULT);
            if (ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_RACE.equals(mode)) {
                combinedAttackModeCombo.select(1);
            } else if (ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_FINNEY.equals(mode)) {
                combinedAttackModeCombo.select(2);
            } else if (ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_LEAD_STUBBORN.equals(mode)) {
                combinedAttackModeCombo.select(3);
            } else if (ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_TRAIL_STUBBORN.equals(mode)) {
                combinedAttackModeCombo.select(4);
            } else {
                combinedAttackModeCombo.select(0);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        if (!isInitialized) return;
        failureThroughputThresholdField.performApply(configuration);
        shannonEntropyKField.performApply(configuration);
        nakamotoCoefficientThresholdField.performApply(configuration);
        reliabilityObservationTimespanField.performApply(configuration);
        blockIntervalField.performApply(configuration);
        propagationDelayField.performApply(configuration);
        nodeDegreeField.performApply(configuration);
        maxBlockSizeField.performApply(configuration);
        networkBandwidthField.performApply(configuration);
        transactionADelayField.performApply(configuration);
        transactionBAccelerationField.performApply(configuration);

        String combinedMode = switch (combinedAttackModeCombo.getSelectionIndex()) {
            case 1 -> ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_RACE;
            case 2 -> ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_FINNEY;
            case 3 -> ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_LEAD_STUBBORN;
            case 4 -> ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_TRAIL_STUBBORN;
            default -> ThreesimAttributes.COMBINED_ATTACK_MODE_NONE;
        };
        configuration.setAttribute(ThreesimAttributes.COMBINED_ATTACK_MODE, combinedMode);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        if (!isInitialized) return;
        failureThroughputThresholdField.setDefaults(configuration);
        shannonEntropyKField.setDefaults(configuration);
        nakamotoCoefficientThresholdField.setDefaults(configuration);
        reliabilityObservationTimespanField.setDefaults(configuration);
        blockIntervalField.setDefaults(configuration);
        propagationDelayField.setDefaults(configuration);
        nodeDegreeField.setDefaults(configuration);
        maxBlockSizeField.setDefaults(configuration);
        networkBandwidthField.setDefaults(configuration);
        transactionADelayField.setDefaults(configuration);
        transactionBAccelerationField.setDefaults(configuration);
        configuration.setAttribute(ThreesimAttributes.COMBINED_ATTACK_MODE,
                ThreesimAttributes.COMBINED_ATTACK_MODE_DEFAULT);
    }

    @Override
    public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
        super.activated(workingCopy);
        updateLaunchConfigurationDialog();
    }

    @Override
    public boolean isValid(ILaunchConfiguration launchConfig) {
        if (!isInitialized) return false;
        return failureThroughputThresholdField.isValid()
                && shannonEntropyKField.isValid()
                && nakamotoCoefficientThresholdField.isValid()
                && reliabilityObservationTimespanField.isValid()
                && blockIntervalField.isValid()
                && propagationDelayField.isValid()
                && nodeDegreeField.isValid()
                && maxBlockSizeField.isValid()
                && networkBandwidthField.isValid()
                && transactionADelayField.isValid()
                && transactionBAccelerationField.isValid();
    }

    private interface DoubleCheck {
        boolean test(double v);
    }

    private interface LongCheck {
        boolean test(long v);
    }

    private static boolean parseDouble(String s, DoubleCheck check) {
        try {
            return check.test(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean parseLong(String s, LongCheck check) {
        try {
            return check.test(Long.parseLong(s));
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
