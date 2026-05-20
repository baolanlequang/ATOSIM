package org.palladiosimulator.blockchainsystems.threesim_plugin

import org.eclipse.debug.core.ILaunchConfiguration
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab
import org.eclipse.jface.layout.GridLayoutFactory
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Group
import org.eclipse.swt.widgets.Combo
import org.eclipse.swt.widgets.Label
import org.palladiosimulator.blockchainsystems.plugin.ui.abstractions.TextField
import org.palladiosimulator.blockchainsystems.plugin.utils.DoubleVerifier
import org.palladiosimulator.blockchainsystems.plugin.utils.LongVerifier
import org.palladiosimulator.blockchainsystems.threesim_plugin.ThreesimAttributes

/**
 * Tab for configuring Threesim-specific simulation parameters.
 *
 * @author Davis Riedel
 */
class ThreesimTab : AbstractLaunchConfigurationTab() {
  companion object {
    private const val MIN_RELIABILITY_OBSERVATION_TIMESPAN = 0.0
    private const val MIN_NAKAMOTO_COEFFICIENT_THRESHOLD = 0.0
    private const val MAX_NAKAMOTO_COEFFICIENT_THRESHOLD = 100.0
    private const val MIN_SHANNON_ENTROPY_K = 0.0
    private const val MIN_FAILURE_THROUGHPUT_THRESHOLD = 0.0
  }

  private var isInitialized = false

  private lateinit var failureThroughputThresholdField: TextField
  private lateinit var shannonEntropyKField: TextField
  private lateinit var nakamotoCoefficientThresholdField: TextField
  private lateinit var reliabilityObservationTimespanField: TextField
  private lateinit var blockIntervalField: TextField
  private lateinit var propagationDelayField: TextField
  private lateinit var nodeDegreeField: TextField
  private lateinit var maxBlockSizeField: TextField
  private lateinit var networkBandwidthField: TextField
  private lateinit var transactionADelayField: TextField
  private lateinit var transactionBAccelerationField: TextField
  private lateinit var combinedAttackModeCombo: Combo

  override fun createControl(parent: Composite) {
    val root = Composite(parent, SWT.BORDER)
    GridLayoutFactory.swtDefaults().numColumns(1).applyTo(root)

    val group = Group(root, SWT.NONE)
    group.text = "3SIM Parameters"
    GridLayoutFactory.swtDefaults().numColumns(3).spacing(0, 10).applyTo(group)
    group.layoutData = GridData(SWT.FILL, SWT.BEGINNING, true, false)

    failureThroughputThresholdField = TextField(
      group,
      "Failure Throughput Threshold:",
      " transactions/min",
      DoubleVerifier,
      ThreesimAttributes.FAILURE_THROUGHPUT_THRESHOLD,
      ThreesimAttributes.FAILURE_THROUGHPUT_THRESHOLD_DEFAULT,
      isValueValid = { it.toDoubleOrNull()?.let { it >= MIN_FAILURE_THROUGHPUT_THRESHOLD } ?: false }
    )

    shannonEntropyKField = TextField(
      group,
      "Shannon Entropy K:",
      "",
      DoubleVerifier,
      ThreesimAttributes.SHANNON_ENTROPY_K,
      ThreesimAttributes.SHANNON_ENTROPY_K_DEFAULT,
      isValueValid = { it.toDoubleOrNull()?.let { it > MIN_SHANNON_ENTROPY_K } ?: false }
    )

    nakamotoCoefficientThresholdField = TextField(
      group,
      "Nakamoto Coefficient Threshold:",
      " %",
      DoubleVerifier,
      ThreesimAttributes.NAKAMOTO_COEFFICIENT_THRESHOLD,
      ThreesimAttributes.NAKAMOTO_COEFFICIENT_THRESHOLD_DEFAULT,
      isValueValid = {
        it.toDoubleOrNull()?.let {
          it in MIN_NAKAMOTO_COEFFICIENT_THRESHOLD..MAX_NAKAMOTO_COEFFICIENT_THRESHOLD
        } ?: false
      }
    )

    reliabilityObservationTimespanField = TextField(
      group,
      "Reliability Observation Timespan:",
      " hours",
      DoubleVerifier,
      ThreesimAttributes.RELIABILITY_OBSERVATION_TIMESPAN,
      ThreesimAttributes.RELIABILITY_OBSERVATION_TIMESPAN_DEFAULT,
      isValueValid = { it.toDoubleOrNull()?.let { it > MIN_RELIABILITY_OBSERVATION_TIMESPAN } ?: false }
    )

    blockIntervalField = TextField(
      group,
      "Block Interval:",
      " ms",
      DoubleVerifier,
      ThreesimAttributes.BLOCK_INTERVAL,
      ThreesimAttributes.BLOCK_INTERVAL_DEFAULT,
      isValueValid = { it.toDoubleOrNull()?.let { it > 0 } ?: false }
    )

    propagationDelayField = TextField(
      group,
      "Propagation Delay:",
      " ms",
      DoubleVerifier,
      ThreesimAttributes.PROPAGATION_DELAY,
      ThreesimAttributes.PROPAGATION_DELAY_DEFAULT,
      isValueValid = { it.toDoubleOrNull()?.let { it >= 0 } ?: false }
    )

    nodeDegreeField = TextField(
      group,
      "Node Degree:",
      "",
      LongVerifier,
      ThreesimAttributes.NODE_DEGREE,
      ThreesimAttributes.NODE_DEGREE_DEFAULT,
      isValueValid = { it.toLongOrNull()?.let { it > 0 } ?: false }
    )

    maxBlockSizeField = TextField(
      group,
      "Max Block Size:",
      " bytes",
      LongVerifier,
      ThreesimAttributes.MAX_BLOCK_SIZE,
      ThreesimAttributes.MAX_BLOCK_SIZE_DEFAULT,
      isValueValid = { it.toLongOrNull()?.let { it > 0 } ?: false }
    )

    networkBandwidthField = TextField(
      group,
      "Network Bandwidth:",
      " bytes/s",
      DoubleVerifier,
      ThreesimAttributes.NETWORK_BANDWIDTH,
      ThreesimAttributes.NETWORK_BANDWIDTH_DEFAULT,
      isValueValid = { it.toDoubleOrNull()?.let { it > 0 } ?: false }
    )
    transactionADelayField = TextField(
      group,
      "Transaction A Delay:",
      " ms",
      LongVerifier,
      ThreesimAttributes.TRANSACTION_A_DELAY,
      ThreesimAttributes.TRANSACTION_A_DELAY_DEFAULT,
      isValueValid = { it.toLongOrNull()?.let { it >= 0 } ?: false }
    )

    transactionBAccelerationField = TextField(
      group,
      "Transaction B Acceleration:",
      " ms",
      LongVerifier,
      ThreesimAttributes.TRANSACTION_B_ACCELERATION,
      ThreesimAttributes.TRANSACTION_B_ACCELERATION_DEFAULT,
      isValueValid = { it.toLongOrNull()?.let { it >= 0 } ?: false }
    )

    val combinedAttackLabel = Label(group, SWT.NONE)
    combinedAttackLabel.text = "Combined Attack Mode:"

    combinedAttackModeCombo = Combo(group, SWT.DROP_DOWN or SWT.READ_ONLY)
    combinedAttackModeCombo.setItems(
      "None",
      "Selfish + Race",
      "Selfish + Finney"
    )
    combinedAttackModeCombo.select(0)
    combinedAttackModeCombo.layoutData = GridData(SWT.FILL, SWT.CENTER, true, false)

    val combinedAttackUnitLabel = Label(group, SWT.NONE)
    combinedAttackUnitLabel.text = ""

    combinedAttackModeCombo.addModifyListener {
      updateLaunchConfigurationDialog()
    }

    control = root
    isInitialized = true
  }

  override fun getName(): String {
    return "ATO-SIM Parameters"
  }

  override fun initializeFrom(configuration: ILaunchConfiguration) {
    if (!isInitialized) return

    failureThroughputThresholdField.initializeFrom(configuration)
    shannonEntropyKField.initializeFrom(configuration)
    nakamotoCoefficientThresholdField.initializeFrom(configuration)
    reliabilityObservationTimespanField.initializeFrom(configuration)
    blockIntervalField.initializeFrom(configuration)
    propagationDelayField.initializeFrom(configuration)
    nodeDegreeField.initializeFrom(configuration)
    maxBlockSizeField.initializeFrom(configuration)
    networkBandwidthField.initializeFrom(configuration)
    transactionADelayField.initializeFrom(configuration)
    transactionBAccelerationField.initializeFrom(configuration)
    when (
      configuration.getAttribute(
        ThreesimAttributes.COMBINED_ATTACK_MODE,
        ThreesimAttributes.COMBINED_ATTACK_MODE_DEFAULT
      )
    ) {
      ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_RACE -> combinedAttackModeCombo.select(1)
      ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_FINNEY -> combinedAttackModeCombo.select(2)
      else -> combinedAttackModeCombo.select(0)
    }
  }

  override fun performApply(configuration: ILaunchConfigurationWorkingCopy) {
    if (!isInitialized) return

    failureThroughputThresholdField.performApply(configuration)
    shannonEntropyKField.performApply(configuration)
    nakamotoCoefficientThresholdField.performApply(configuration)
    reliabilityObservationTimespanField.performApply(configuration)
    blockIntervalField.performApply(configuration)
    propagationDelayField.performApply(configuration)
    nodeDegreeField.performApply(configuration)
    maxBlockSizeField.performApply(configuration)
    networkBandwidthField.performApply(configuration)
    transactionADelayField.performApply(configuration)
    transactionBAccelerationField.performApply(configuration)

    val combinedMode = when (combinedAttackModeCombo.selectionIndex) {
      1 -> ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_RACE
      2 -> ThreesimAttributes.COMBINED_ATTACK_MODE_SELFISH_FINNEY
      else -> ThreesimAttributes.COMBINED_ATTACK_MODE_NONE
    }

    configuration.setAttribute(ThreesimAttributes.COMBINED_ATTACK_MODE, combinedMode)
  }

  override fun setDefaults(configuration: ILaunchConfigurationWorkingCopy) {
    if (!isInitialized) return

    failureThroughputThresholdField.setDefaults(configuration)
    shannonEntropyKField.setDefaults(configuration)
    nakamotoCoefficientThresholdField.setDefaults(configuration)
    reliabilityObservationTimespanField.setDefaults(configuration)
    blockIntervalField.setDefaults(configuration)
    propagationDelayField.setDefaults(configuration)
    nodeDegreeField.setDefaults(configuration)
    maxBlockSizeField.setDefaults(configuration)
    networkBandwidthField.setDefaults(configuration)
    transactionADelayField.setDefaults(configuration)
    transactionBAccelerationField.setDefaults(configuration)
    configuration.setAttribute(
      ThreesimAttributes.COMBINED_ATTACK_MODE,
      ThreesimAttributes.COMBINED_ATTACK_MODE_DEFAULT
    )
  }

  override fun activated(workingCopy: ILaunchConfigurationWorkingCopy) {
    super.activated(workingCopy)
    updateLaunchConfigurationDialog()
  }

  override fun isValid(launchConfig: ILaunchConfiguration): Boolean {
    if (!isInitialized) return false

    return failureThroughputThresholdField.isValid() &&
      shannonEntropyKField.isValid() &&
      nakamotoCoefficientThresholdField.isValid() &&
      reliabilityObservationTimespanField.isValid() &&
            blockIntervalField.isValid() &&
            propagationDelayField.isValid() &&
            nodeDegreeField.isValid() &&
            maxBlockSizeField.isValid() &&
            networkBandwidthField.isValid() && transactionADelayField.isValid() &&
            transactionBAccelerationField.isValid()
  }
}