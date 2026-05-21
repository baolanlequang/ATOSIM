/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.StubbornMiningAttack;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Stubborn Mining Attack</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.StubbornMiningAttackImpl#getLeadThreshold <em>Lead Threshold</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.StubbornMiningAttackImpl#getAbandonThreshold <em>Abandon Threshold</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class StubbornMiningAttackImpl extends AttackImpl implements StubbornMiningAttack {
	/**
	 * The default value of the '{@link #getLeadThreshold() <em>Lead Threshold</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLeadThreshold()
	 * @generated
	 * @ordered
	 */
	protected static final int LEAD_THRESHOLD_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getLeadThreshold() <em>Lead Threshold</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLeadThreshold()
	 * @generated
	 * @ordered
	 */
	protected int leadThreshold = LEAD_THRESHOLD_EDEFAULT;

	/**
	 * The default value of the '{@link #getAbandonThreshold() <em>Abandon Threshold</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAbandonThreshold()
	 * @generated
	 * @ordered
	 */
	protected static final int ABANDON_THRESHOLD_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getAbandonThreshold() <em>Abandon Threshold</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAbandonThreshold()
	 * @generated
	 * @ordered
	 */
	protected int abandonThreshold = ABANDON_THRESHOLD_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected StubbornMiningAttackImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AttackmodelPackage.Literals.STUBBORN_MINING_ATTACK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getLeadThreshold() {
		return leadThreshold;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLeadThreshold(int newLeadThreshold) {
		int oldLeadThreshold = leadThreshold;
		leadThreshold = newLeadThreshold;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.STUBBORN_MINING_ATTACK__LEAD_THRESHOLD, oldLeadThreshold, leadThreshold));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getAbandonThreshold() {
		return abandonThreshold;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAbandonThreshold(int newAbandonThreshold) {
		int oldAbandonThreshold = abandonThreshold;
		abandonThreshold = newAbandonThreshold;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.STUBBORN_MINING_ATTACK__ABANDON_THRESHOLD, oldAbandonThreshold,
					abandonThreshold));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case AttackmodelPackage.STUBBORN_MINING_ATTACK__LEAD_THRESHOLD:
			return getLeadThreshold();
		case AttackmodelPackage.STUBBORN_MINING_ATTACK__ABANDON_THRESHOLD:
			return getAbandonThreshold();
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
		case AttackmodelPackage.STUBBORN_MINING_ATTACK__LEAD_THRESHOLD:
			setLeadThreshold((Integer) newValue);
			return;
		case AttackmodelPackage.STUBBORN_MINING_ATTACK__ABANDON_THRESHOLD:
			setAbandonThreshold((Integer) newValue);
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
		case AttackmodelPackage.STUBBORN_MINING_ATTACK__LEAD_THRESHOLD:
			setLeadThreshold(LEAD_THRESHOLD_EDEFAULT);
			return;
		case AttackmodelPackage.STUBBORN_MINING_ATTACK__ABANDON_THRESHOLD:
			setAbandonThreshold(ABANDON_THRESHOLD_EDEFAULT);
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
		case AttackmodelPackage.STUBBORN_MINING_ATTACK__LEAD_THRESHOLD:
			return leadThreshold != LEAD_THRESHOLD_EDEFAULT;
		case AttackmodelPackage.STUBBORN_MINING_ATTACK__ABANDON_THRESHOLD:
			return abandonThreshold != ABANDON_THRESHOLD_EDEFAULT;
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
		result.append(" (leadThreshold: ");
		result.append(leadThreshold);
		result.append(", abandonThreshold: ");
		result.append(abandonThreshold);
		result.append(')');
		return result.toString();
	}

} //StubbornMiningAttackImpl
