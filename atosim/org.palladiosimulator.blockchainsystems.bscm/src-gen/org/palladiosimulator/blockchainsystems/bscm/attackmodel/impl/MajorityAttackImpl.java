/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Majority Attack</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.MajorityAttackImpl#getHashPowerShare <em>Hash Power Share</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.MajorityAttackImpl#getExpectedTakeoverTime <em>Expected Takeover Time</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.MajorityAttackImpl#getTargetConfirmationsToBreak <em>Target Confirmations To Break</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.MajorityAttackImpl#getColludingPools <em>Colluding Pools</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MajorityAttackImpl extends AttackImpl implements MajorityAttack {
	/**
	 * The default value of the '{@link #getHashPowerShare() <em>Hash Power Share</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHashPowerShare()
	 * @generated
	 * @ordered
	 */
	protected static final double HASH_POWER_SHARE_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getHashPowerShare() <em>Hash Power Share</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHashPowerShare()
	 * @generated
	 * @ordered
	 */
	protected double hashPowerShare = HASH_POWER_SHARE_EDEFAULT;

	/**
	 * The default value of the '{@link #getExpectedTakeoverTime() <em>Expected Takeover Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpectedTakeoverTime()
	 * @generated
	 * @ordered
	 */
	protected static final double EXPECTED_TAKEOVER_TIME_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getExpectedTakeoverTime() <em>Expected Takeover Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpectedTakeoverTime()
	 * @generated
	 * @ordered
	 */
	protected double expectedTakeoverTime = EXPECTED_TAKEOVER_TIME_EDEFAULT;

	/**
	 * The default value of the '{@link #getTargetConfirmationsToBreak() <em>Target Confirmations To Break</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetConfirmationsToBreak()
	 * @generated
	 * @ordered
	 */
	protected static final int TARGET_CONFIRMATIONS_TO_BREAK_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getTargetConfirmationsToBreak() <em>Target Confirmations To Break</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTargetConfirmationsToBreak()
	 * @generated
	 * @ordered
	 */
	protected int targetConfirmationsToBreak = TARGET_CONFIRMATIONS_TO_BREAK_EDEFAULT;

	/**
	 * The cached value of the '{@link #getColludingPools() <em>Colluding Pools</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColludingPools()
	 * @generated
	 * @ordered
	 */
	protected EList<AttackerNode> colludingPools;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MajorityAttackImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AttackmodelPackage.Literals.MAJORITY_ATTACK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getHashPowerShare() {
		return hashPowerShare;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setHashPowerShare(double newHashPowerShare) {
		double oldHashPowerShare = hashPowerShare;
		hashPowerShare = newHashPowerShare;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.MAJORITY_ATTACK__HASH_POWER_SHARE,
					oldHashPowerShare, hashPowerShare));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getExpectedTakeoverTime() {
		return expectedTakeoverTime;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExpectedTakeoverTime(double newExpectedTakeoverTime) {
		double oldExpectedTakeoverTime = expectedTakeoverTime;
		expectedTakeoverTime = newExpectedTakeoverTime;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.MAJORITY_ATTACK__EXPECTED_TAKEOVER_TIME, oldExpectedTakeoverTime,
					expectedTakeoverTime));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getTargetConfirmationsToBreak() {
		return targetConfirmationsToBreak;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTargetConfirmationsToBreak(int newTargetConfirmationsToBreak) {
		int oldTargetConfirmationsToBreak = targetConfirmationsToBreak;
		targetConfirmationsToBreak = newTargetConfirmationsToBreak;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.MAJORITY_ATTACK__TARGET_CONFIRMATIONS_TO_BREAK, oldTargetConfirmationsToBreak,
					targetConfirmationsToBreak));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<AttackerNode> getColludingPools() {
		if (colludingPools == null) {
			colludingPools = new EObjectResolvingEList<AttackerNode>(AttackerNode.class, this,
					AttackmodelPackage.MAJORITY_ATTACK__COLLUDING_POOLS);
		}
		return colludingPools;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case AttackmodelPackage.MAJORITY_ATTACK__HASH_POWER_SHARE:
			return getHashPowerShare();
		case AttackmodelPackage.MAJORITY_ATTACK__EXPECTED_TAKEOVER_TIME:
			return getExpectedTakeoverTime();
		case AttackmodelPackage.MAJORITY_ATTACK__TARGET_CONFIRMATIONS_TO_BREAK:
			return getTargetConfirmationsToBreak();
		case AttackmodelPackage.MAJORITY_ATTACK__COLLUDING_POOLS:
			return getColludingPools();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case AttackmodelPackage.MAJORITY_ATTACK__HASH_POWER_SHARE:
			setHashPowerShare((Double) newValue);
			return;
		case AttackmodelPackage.MAJORITY_ATTACK__EXPECTED_TAKEOVER_TIME:
			setExpectedTakeoverTime((Double) newValue);
			return;
		case AttackmodelPackage.MAJORITY_ATTACK__TARGET_CONFIRMATIONS_TO_BREAK:
			setTargetConfirmationsToBreak((Integer) newValue);
			return;
		case AttackmodelPackage.MAJORITY_ATTACK__COLLUDING_POOLS:
			getColludingPools().clear();
			getColludingPools().addAll((Collection<? extends AttackerNode>) newValue);
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
		case AttackmodelPackage.MAJORITY_ATTACK__HASH_POWER_SHARE:
			setHashPowerShare(HASH_POWER_SHARE_EDEFAULT);
			return;
		case AttackmodelPackage.MAJORITY_ATTACK__EXPECTED_TAKEOVER_TIME:
			setExpectedTakeoverTime(EXPECTED_TAKEOVER_TIME_EDEFAULT);
			return;
		case AttackmodelPackage.MAJORITY_ATTACK__TARGET_CONFIRMATIONS_TO_BREAK:
			setTargetConfirmationsToBreak(TARGET_CONFIRMATIONS_TO_BREAK_EDEFAULT);
			return;
		case AttackmodelPackage.MAJORITY_ATTACK__COLLUDING_POOLS:
			getColludingPools().clear();
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
		case AttackmodelPackage.MAJORITY_ATTACK__HASH_POWER_SHARE:
			return hashPowerShare != HASH_POWER_SHARE_EDEFAULT;
		case AttackmodelPackage.MAJORITY_ATTACK__EXPECTED_TAKEOVER_TIME:
			return expectedTakeoverTime != EXPECTED_TAKEOVER_TIME_EDEFAULT;
		case AttackmodelPackage.MAJORITY_ATTACK__TARGET_CONFIRMATIONS_TO_BREAK:
			return targetConfirmationsToBreak != TARGET_CONFIRMATIONS_TO_BREAK_EDEFAULT;
		case AttackmodelPackage.MAJORITY_ATTACK__COLLUDING_POOLS:
			return colludingPools != null && !colludingPools.isEmpty();
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
		result.append(" (hashPowerShare: ");
		result.append(hashPowerShare);
		result.append(", expectedTakeoverTime: ");
		result.append(expectedTakeoverTime);
		result.append(", targetConfirmationsToBreak: ");
		result.append(targetConfirmationsToBreak);
		result.append(')');
		return result.toString();
	}

} //MajorityAttackImpl
