/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Finney Attack</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.FinneyAttackImpl#getNumberOfPreminedBlocks <em>Number Of Premined Blocks</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.FinneyAttackImpl#getWithholdTime <em>Withhold Time</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.FinneyAttackImpl#isMerchantAcceptsZeroConf <em>Merchant Accepts Zero Conf</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.FinneyAttackImpl#getDoubleSpendSuccessCount <em>Double Spend Success Count</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.FinneyAttackImpl#getObservedTimeToReleaseMs <em>Observed Time To Release Ms</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FinneyAttackImpl extends AttackImpl implements FinneyAttack {
	/**
	 * The default value of the '{@link #getNumberOfPreminedBlocks() <em>Number Of Premined Blocks</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNumberOfPreminedBlocks()
	 * @generated
	 * @ordered
	 */
	protected static final int NUMBER_OF_PREMINED_BLOCKS_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getNumberOfPreminedBlocks() <em>Number Of Premined Blocks</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNumberOfPreminedBlocks()
	 * @generated
	 * @ordered
	 */
	protected int numberOfPreminedBlocks = NUMBER_OF_PREMINED_BLOCKS_EDEFAULT;

	/**
	 * The default value of the '{@link #getWithholdTime() <em>Withhold Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWithholdTime()
	 * @generated
	 * @ordered
	 */
	protected static final long WITHHOLD_TIME_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getWithholdTime() <em>Withhold Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWithholdTime()
	 * @generated
	 * @ordered
	 */
	protected long withholdTime = WITHHOLD_TIME_EDEFAULT;

	/**
	 * The default value of the '{@link #isMerchantAcceptsZeroConf() <em>Merchant Accepts Zero Conf</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isMerchantAcceptsZeroConf()
	 * @generated
	 * @ordered
	 */
	protected static final boolean MERCHANT_ACCEPTS_ZERO_CONF_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isMerchantAcceptsZeroConf() <em>Merchant Accepts Zero Conf</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isMerchantAcceptsZeroConf()
	 * @generated
	 * @ordered
	 */
	protected boolean merchantAcceptsZeroConf = MERCHANT_ACCEPTS_ZERO_CONF_EDEFAULT;

	/**
	 * The default value of the '{@link #getDoubleSpendSuccessCount() <em>Double Spend Success Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDoubleSpendSuccessCount()
	 * @generated
	 * @ordered
	 */
	protected static final int DOUBLE_SPEND_SUCCESS_COUNT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getDoubleSpendSuccessCount() <em>Double Spend Success Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDoubleSpendSuccessCount()
	 * @generated
	 * @ordered
	 */
	protected int doubleSpendSuccessCount = DOUBLE_SPEND_SUCCESS_COUNT_EDEFAULT;

	/**
	 * The default value of the '{@link #getObservedTimeToReleaseMs() <em>Observed Time To Release Ms</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObservedTimeToReleaseMs()
	 * @generated
	 * @ordered
	 */
	protected static final long OBSERVED_TIME_TO_RELEASE_MS_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getObservedTimeToReleaseMs() <em>Observed Time To Release Ms</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObservedTimeToReleaseMs()
	 * @generated
	 * @ordered
	 */
	protected long observedTimeToReleaseMs = OBSERVED_TIME_TO_RELEASE_MS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FinneyAttackImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AttackmodelPackage.Literals.FINNEY_ATTACK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getNumberOfPreminedBlocks() {
		return numberOfPreminedBlocks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNumberOfPreminedBlocks(int newNumberOfPreminedBlocks) {
		int oldNumberOfPreminedBlocks = numberOfPreminedBlocks;
		numberOfPreminedBlocks = newNumberOfPreminedBlocks;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.FINNEY_ATTACK__NUMBER_OF_PREMINED_BLOCKS, oldNumberOfPreminedBlocks,
					numberOfPreminedBlocks));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getWithholdTime() {
		return withholdTime;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWithholdTime(long newWithholdTime) {
		long oldWithholdTime = withholdTime;
		withholdTime = newWithholdTime;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.FINNEY_ATTACK__WITHHOLD_TIME,
					oldWithholdTime, withholdTime));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isMerchantAcceptsZeroConf() {
		return merchantAcceptsZeroConf;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMerchantAcceptsZeroConf(boolean newMerchantAcceptsZeroConf) {
		boolean oldMerchantAcceptsZeroConf = merchantAcceptsZeroConf;
		merchantAcceptsZeroConf = newMerchantAcceptsZeroConf;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.FINNEY_ATTACK__MERCHANT_ACCEPTS_ZERO_CONF, oldMerchantAcceptsZeroConf,
					merchantAcceptsZeroConf));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getDoubleSpendSuccessCount() {
		return doubleSpendSuccessCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDoubleSpendSuccessCount(int newDoubleSpendSuccessCount) {
		int oldDoubleSpendSuccessCount = doubleSpendSuccessCount;
		doubleSpendSuccessCount = newDoubleSpendSuccessCount;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.FINNEY_ATTACK__DOUBLE_SPEND_SUCCESS_COUNT, oldDoubleSpendSuccessCount,
					doubleSpendSuccessCount));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getObservedTimeToReleaseMs() {
		return observedTimeToReleaseMs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setObservedTimeToReleaseMs(long newObservedTimeToReleaseMs) {
		long oldObservedTimeToReleaseMs = observedTimeToReleaseMs;
		observedTimeToReleaseMs = newObservedTimeToReleaseMs;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.FINNEY_ATTACK__OBSERVED_TIME_TO_RELEASE_MS, oldObservedTimeToReleaseMs,
					observedTimeToReleaseMs));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case AttackmodelPackage.FINNEY_ATTACK__NUMBER_OF_PREMINED_BLOCKS:
			return getNumberOfPreminedBlocks();
		case AttackmodelPackage.FINNEY_ATTACK__WITHHOLD_TIME:
			return getWithholdTime();
		case AttackmodelPackage.FINNEY_ATTACK__MERCHANT_ACCEPTS_ZERO_CONF:
			return isMerchantAcceptsZeroConf();
		case AttackmodelPackage.FINNEY_ATTACK__DOUBLE_SPEND_SUCCESS_COUNT:
			return getDoubleSpendSuccessCount();
		case AttackmodelPackage.FINNEY_ATTACK__OBSERVED_TIME_TO_RELEASE_MS:
			return getObservedTimeToReleaseMs();
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
		case AttackmodelPackage.FINNEY_ATTACK__NUMBER_OF_PREMINED_BLOCKS:
			setNumberOfPreminedBlocks((Integer) newValue);
			return;
		case AttackmodelPackage.FINNEY_ATTACK__WITHHOLD_TIME:
			setWithholdTime((Long) newValue);
			return;
		case AttackmodelPackage.FINNEY_ATTACK__MERCHANT_ACCEPTS_ZERO_CONF:
			setMerchantAcceptsZeroConf((Boolean) newValue);
			return;
		case AttackmodelPackage.FINNEY_ATTACK__DOUBLE_SPEND_SUCCESS_COUNT:
			setDoubleSpendSuccessCount((Integer) newValue);
			return;
		case AttackmodelPackage.FINNEY_ATTACK__OBSERVED_TIME_TO_RELEASE_MS:
			setObservedTimeToReleaseMs((Long) newValue);
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
		case AttackmodelPackage.FINNEY_ATTACK__NUMBER_OF_PREMINED_BLOCKS:
			setNumberOfPreminedBlocks(NUMBER_OF_PREMINED_BLOCKS_EDEFAULT);
			return;
		case AttackmodelPackage.FINNEY_ATTACK__WITHHOLD_TIME:
			setWithholdTime(WITHHOLD_TIME_EDEFAULT);
			return;
		case AttackmodelPackage.FINNEY_ATTACK__MERCHANT_ACCEPTS_ZERO_CONF:
			setMerchantAcceptsZeroConf(MERCHANT_ACCEPTS_ZERO_CONF_EDEFAULT);
			return;
		case AttackmodelPackage.FINNEY_ATTACK__DOUBLE_SPEND_SUCCESS_COUNT:
			setDoubleSpendSuccessCount(DOUBLE_SPEND_SUCCESS_COUNT_EDEFAULT);
			return;
		case AttackmodelPackage.FINNEY_ATTACK__OBSERVED_TIME_TO_RELEASE_MS:
			setObservedTimeToReleaseMs(OBSERVED_TIME_TO_RELEASE_MS_EDEFAULT);
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
		case AttackmodelPackage.FINNEY_ATTACK__NUMBER_OF_PREMINED_BLOCKS:
			return numberOfPreminedBlocks != NUMBER_OF_PREMINED_BLOCKS_EDEFAULT;
		case AttackmodelPackage.FINNEY_ATTACK__WITHHOLD_TIME:
			return withholdTime != WITHHOLD_TIME_EDEFAULT;
		case AttackmodelPackage.FINNEY_ATTACK__MERCHANT_ACCEPTS_ZERO_CONF:
			return merchantAcceptsZeroConf != MERCHANT_ACCEPTS_ZERO_CONF_EDEFAULT;
		case AttackmodelPackage.FINNEY_ATTACK__DOUBLE_SPEND_SUCCESS_COUNT:
			return doubleSpendSuccessCount != DOUBLE_SPEND_SUCCESS_COUNT_EDEFAULT;
		case AttackmodelPackage.FINNEY_ATTACK__OBSERVED_TIME_TO_RELEASE_MS:
			return observedTimeToReleaseMs != OBSERVED_TIME_TO_RELEASE_MS_EDEFAULT;
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
		result.append(" (numberOfPreminedBlocks: ");
		result.append(numberOfPreminedBlocks);
		result.append(", withholdTime: ");
		result.append(withholdTime);
		result.append(", merchantAcceptsZeroConf: ");
		result.append(merchantAcceptsZeroConf);
		result.append(", doubleSpendSuccessCount: ");
		result.append(doubleSpendSuccessCount);
		result.append(", observedTimeToReleaseMs: ");
		result.append(observedTimeToReleaseMs);
		result.append(')');
		return result.toString();
	}

} //FinneyAttackImpl
