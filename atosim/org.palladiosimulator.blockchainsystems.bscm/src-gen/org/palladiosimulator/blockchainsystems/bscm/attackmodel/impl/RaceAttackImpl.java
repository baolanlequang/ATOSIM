/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Race Attack</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.RaceAttackImpl#getPropagationAdvantage <em>Propagation Advantage</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.RaceAttackImpl#getTransactionADelay <em>Transaction ADelay</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.RaceAttackImpl#getTransactionBAcceleration <em>Transaction BAcceleration</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.RaceAttackImpl#getFeeDifferenceThreshold <em>Fee Difference Threshold</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.RaceAttackImpl#getZeroConfAcceptanceRisk <em>Zero Conf Acceptance Risk</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.RaceAttackImpl#getObservedDoubleSpends <em>Observed Double Spends</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.RaceAttackImpl#getAvgPropagationAdvMs <em>Avg Propagation Adv Ms</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RaceAttackImpl extends AttackImpl implements RaceAttack {
	/**
	 * The default value of the '{@link #getPropagationAdvantage() <em>Propagation Advantage</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropagationAdvantage()
	 * @generated
	 * @ordered
	 */
	protected static final long PROPAGATION_ADVANTAGE_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getPropagationAdvantage() <em>Propagation Advantage</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropagationAdvantage()
	 * @generated
	 * @ordered
	 */
	protected long propagationAdvantage = PROPAGATION_ADVANTAGE_EDEFAULT;

	/**
	 * The default value of the '{@link #getTransactionADelay() <em>Transaction ADelay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransactionADelay()
	 * @generated
	 * @ordered
	 */
	protected static final long TRANSACTION_ADELAY_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getTransactionADelay() <em>Transaction ADelay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransactionADelay()
	 * @generated
	 * @ordered
	 */
	protected long transactionADelay = TRANSACTION_ADELAY_EDEFAULT;

	/**
	 * The default value of the '{@link #getTransactionBAcceleration() <em>Transaction BAcceleration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransactionBAcceleration()
	 * @generated
	 * @ordered
	 */
	protected static final long TRANSACTION_BACCELERATION_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getTransactionBAcceleration() <em>Transaction BAcceleration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransactionBAcceleration()
	 * @generated
	 * @ordered
	 */
	protected long transactionBAcceleration = TRANSACTION_BACCELERATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getFeeDifferenceThreshold() <em>Fee Difference Threshold</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeeDifferenceThreshold()
	 * @generated
	 * @ordered
	 */
	protected static final double FEE_DIFFERENCE_THRESHOLD_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getFeeDifferenceThreshold() <em>Fee Difference Threshold</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeeDifferenceThreshold()
	 * @generated
	 * @ordered
	 */
	protected double feeDifferenceThreshold = FEE_DIFFERENCE_THRESHOLD_EDEFAULT;

	/**
	 * The default value of the '{@link #getZeroConfAcceptanceRisk() <em>Zero Conf Acceptance Risk</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getZeroConfAcceptanceRisk()
	 * @generated
	 * @ordered
	 */
	protected static final double ZERO_CONF_ACCEPTANCE_RISK_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getZeroConfAcceptanceRisk() <em>Zero Conf Acceptance Risk</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getZeroConfAcceptanceRisk()
	 * @generated
	 * @ordered
	 */
	protected double zeroConfAcceptanceRisk = ZERO_CONF_ACCEPTANCE_RISK_EDEFAULT;

	/**
	 * The default value of the '{@link #getObservedDoubleSpends() <em>Observed Double Spends</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObservedDoubleSpends()
	 * @generated
	 * @ordered
	 */
	protected static final int OBSERVED_DOUBLE_SPENDS_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getObservedDoubleSpends() <em>Observed Double Spends</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObservedDoubleSpends()
	 * @generated
	 * @ordered
	 */
	protected int observedDoubleSpends = OBSERVED_DOUBLE_SPENDS_EDEFAULT;

	/**
	 * The default value of the '{@link #getAvgPropagationAdvMs() <em>Avg Propagation Adv Ms</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAvgPropagationAdvMs()
	 * @generated
	 * @ordered
	 */
	protected static final long AVG_PROPAGATION_ADV_MS_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getAvgPropagationAdvMs() <em>Avg Propagation Adv Ms</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAvgPropagationAdvMs()
	 * @generated
	 * @ordered
	 */
	protected long avgPropagationAdvMs = AVG_PROPAGATION_ADV_MS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RaceAttackImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AttackmodelPackage.Literals.RACE_ATTACK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getPropagationAdvantage() {
		return propagationAdvantage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPropagationAdvantage(long newPropagationAdvantage) {
		long oldPropagationAdvantage = propagationAdvantage;
		propagationAdvantage = newPropagationAdvantage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.RACE_ATTACK__PROPAGATION_ADVANTAGE,
					oldPropagationAdvantage, propagationAdvantage));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getTransactionADelay() {
		return transactionADelay;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTransactionADelay(long newTransactionADelay) {
		long oldTransactionADelay = transactionADelay;
		transactionADelay = newTransactionADelay;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.RACE_ATTACK__TRANSACTION_ADELAY,
					oldTransactionADelay, transactionADelay));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getTransactionBAcceleration() {
		return transactionBAcceleration;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTransactionBAcceleration(long newTransactionBAcceleration) {
		long oldTransactionBAcceleration = transactionBAcceleration;
		transactionBAcceleration = newTransactionBAcceleration;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.RACE_ATTACK__TRANSACTION_BACCELERATION, oldTransactionBAcceleration,
					transactionBAcceleration));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getFeeDifferenceThreshold() {
		return feeDifferenceThreshold;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFeeDifferenceThreshold(double newFeeDifferenceThreshold) {
		double oldFeeDifferenceThreshold = feeDifferenceThreshold;
		feeDifferenceThreshold = newFeeDifferenceThreshold;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.RACE_ATTACK__FEE_DIFFERENCE_THRESHOLD, oldFeeDifferenceThreshold,
					feeDifferenceThreshold));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getZeroConfAcceptanceRisk() {
		return zeroConfAcceptanceRisk;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setZeroConfAcceptanceRisk(double newZeroConfAcceptanceRisk) {
		double oldZeroConfAcceptanceRisk = zeroConfAcceptanceRisk;
		zeroConfAcceptanceRisk = newZeroConfAcceptanceRisk;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.RACE_ATTACK__ZERO_CONF_ACCEPTANCE_RISK, oldZeroConfAcceptanceRisk,
					zeroConfAcceptanceRisk));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getObservedDoubleSpends() {
		return observedDoubleSpends;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setObservedDoubleSpends(int newObservedDoubleSpends) {
		int oldObservedDoubleSpends = observedDoubleSpends;
		observedDoubleSpends = newObservedDoubleSpends;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.RACE_ATTACK__OBSERVED_DOUBLE_SPENDS, oldObservedDoubleSpends,
					observedDoubleSpends));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getAvgPropagationAdvMs() {
		return avgPropagationAdvMs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAvgPropagationAdvMs(long newAvgPropagationAdvMs) {
		long oldAvgPropagationAdvMs = avgPropagationAdvMs;
		avgPropagationAdvMs = newAvgPropagationAdvMs;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.RACE_ATTACK__AVG_PROPAGATION_ADV_MS, oldAvgPropagationAdvMs,
					avgPropagationAdvMs));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case AttackmodelPackage.RACE_ATTACK__PROPAGATION_ADVANTAGE:
			return getPropagationAdvantage();
		case AttackmodelPackage.RACE_ATTACK__TRANSACTION_ADELAY:
			return getTransactionADelay();
		case AttackmodelPackage.RACE_ATTACK__TRANSACTION_BACCELERATION:
			return getTransactionBAcceleration();
		case AttackmodelPackage.RACE_ATTACK__FEE_DIFFERENCE_THRESHOLD:
			return getFeeDifferenceThreshold();
		case AttackmodelPackage.RACE_ATTACK__ZERO_CONF_ACCEPTANCE_RISK:
			return getZeroConfAcceptanceRisk();
		case AttackmodelPackage.RACE_ATTACK__OBSERVED_DOUBLE_SPENDS:
			return getObservedDoubleSpends();
		case AttackmodelPackage.RACE_ATTACK__AVG_PROPAGATION_ADV_MS:
			return getAvgPropagationAdvMs();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case AttackmodelPackage.RACE_ATTACK__PROPAGATION_ADVANTAGE:
			setPropagationAdvantage((Long) newValue);
			return;
		case AttackmodelPackage.RACE_ATTACK__TRANSACTION_ADELAY:
			setTransactionADelay((Long) newValue);
			return;
		case AttackmodelPackage.RACE_ATTACK__TRANSACTION_BACCELERATION:
			setTransactionBAcceleration((Long) newValue);
			return;
		case AttackmodelPackage.RACE_ATTACK__FEE_DIFFERENCE_THRESHOLD:
			setFeeDifferenceThreshold((Double) newValue);
			return;
		case AttackmodelPackage.RACE_ATTACK__ZERO_CONF_ACCEPTANCE_RISK:
			setZeroConfAcceptanceRisk((Double) newValue);
			return;
		case AttackmodelPackage.RACE_ATTACK__OBSERVED_DOUBLE_SPENDS:
			setObservedDoubleSpends((Integer) newValue);
			return;
		case AttackmodelPackage.RACE_ATTACK__AVG_PROPAGATION_ADV_MS:
			setAvgPropagationAdvMs((Long) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case AttackmodelPackage.RACE_ATTACK__PROPAGATION_ADVANTAGE:
			setPropagationAdvantage(PROPAGATION_ADVANTAGE_EDEFAULT);
			return;
		case AttackmodelPackage.RACE_ATTACK__TRANSACTION_ADELAY:
			setTransactionADelay(TRANSACTION_ADELAY_EDEFAULT);
			return;
		case AttackmodelPackage.RACE_ATTACK__TRANSACTION_BACCELERATION:
			setTransactionBAcceleration(TRANSACTION_BACCELERATION_EDEFAULT);
			return;
		case AttackmodelPackage.RACE_ATTACK__FEE_DIFFERENCE_THRESHOLD:
			setFeeDifferenceThreshold(FEE_DIFFERENCE_THRESHOLD_EDEFAULT);
			return;
		case AttackmodelPackage.RACE_ATTACK__ZERO_CONF_ACCEPTANCE_RISK:
			setZeroConfAcceptanceRisk(ZERO_CONF_ACCEPTANCE_RISK_EDEFAULT);
			return;
		case AttackmodelPackage.RACE_ATTACK__OBSERVED_DOUBLE_SPENDS:
			setObservedDoubleSpends(OBSERVED_DOUBLE_SPENDS_EDEFAULT);
			return;
		case AttackmodelPackage.RACE_ATTACK__AVG_PROPAGATION_ADV_MS:
			setAvgPropagationAdvMs(AVG_PROPAGATION_ADV_MS_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case AttackmodelPackage.RACE_ATTACK__PROPAGATION_ADVANTAGE:
			return propagationAdvantage != PROPAGATION_ADVANTAGE_EDEFAULT;
		case AttackmodelPackage.RACE_ATTACK__TRANSACTION_ADELAY:
			return transactionADelay != TRANSACTION_ADELAY_EDEFAULT;
		case AttackmodelPackage.RACE_ATTACK__TRANSACTION_BACCELERATION:
			return transactionBAcceleration != TRANSACTION_BACCELERATION_EDEFAULT;
		case AttackmodelPackage.RACE_ATTACK__FEE_DIFFERENCE_THRESHOLD:
			return feeDifferenceThreshold != FEE_DIFFERENCE_THRESHOLD_EDEFAULT;
		case AttackmodelPackage.RACE_ATTACK__ZERO_CONF_ACCEPTANCE_RISK:
			return zeroConfAcceptanceRisk != ZERO_CONF_ACCEPTANCE_RISK_EDEFAULT;
		case AttackmodelPackage.RACE_ATTACK__OBSERVED_DOUBLE_SPENDS:
			return observedDoubleSpends != OBSERVED_DOUBLE_SPENDS_EDEFAULT;
		case AttackmodelPackage.RACE_ATTACK__AVG_PROPAGATION_ADV_MS:
			return avgPropagationAdvMs != AVG_PROPAGATION_ADV_MS_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (propagationAdvantage: ");
		result.append(propagationAdvantage);
		result.append(", transactionADelay: ");
		result.append(transactionADelay);
		result.append(", transactionBAcceleration: ");
		result.append(transactionBAcceleration);
		result.append(", feeDifferenceThreshold: ");
		result.append(feeDifferenceThreshold);
		result.append(", zeroConfAcceptanceRisk: ");
		result.append(zeroConfAcceptanceRisk);
		result.append(", observedDoubleSpends: ");
		result.append(observedDoubleSpends);
		result.append(", avgPropagationAdvMs: ");
		result.append(avgPropagationAdvMs);
		result.append(')');
		return result.toString();
	}

} //RaceAttackImpl
