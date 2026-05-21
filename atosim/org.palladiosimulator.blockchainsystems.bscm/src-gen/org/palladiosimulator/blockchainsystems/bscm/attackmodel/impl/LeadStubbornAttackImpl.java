/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.LeadStubbornAttack;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Lead Stubborn Attack</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.LeadStubbornAttackImpl#getMinLeadToPublish <em>Min Lead To Publish</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LeadStubbornAttackImpl extends StubbornMiningAttackImpl implements LeadStubbornAttack {
	/**
	 * The default value of the '{@link #getMinLeadToPublish() <em>Min Lead To Publish</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinLeadToPublish()
	 * @generated
	 * @ordered
	 */
	protected static final int MIN_LEAD_TO_PUBLISH_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getMinLeadToPublish() <em>Min Lead To Publish</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMinLeadToPublish()
	 * @generated
	 * @ordered
	 */
	protected int minLeadToPublish = MIN_LEAD_TO_PUBLISH_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LeadStubbornAttackImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AttackmodelPackage.Literals.LEAD_STUBBORN_ATTACK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getMinLeadToPublish() {
		return minLeadToPublish;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMinLeadToPublish(int newMinLeadToPublish) {
		int oldMinLeadToPublish = minLeadToPublish;
		minLeadToPublish = newMinLeadToPublish;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.LEAD_STUBBORN_ATTACK__MIN_LEAD_TO_PUBLISH, oldMinLeadToPublish,
					minLeadToPublish));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case AttackmodelPackage.LEAD_STUBBORN_ATTACK__MIN_LEAD_TO_PUBLISH:
			return getMinLeadToPublish();
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
		case AttackmodelPackage.LEAD_STUBBORN_ATTACK__MIN_LEAD_TO_PUBLISH:
			setMinLeadToPublish((Integer) newValue);
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
		case AttackmodelPackage.LEAD_STUBBORN_ATTACK__MIN_LEAD_TO_PUBLISH:
			setMinLeadToPublish(MIN_LEAD_TO_PUBLISH_EDEFAULT);
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
		case AttackmodelPackage.LEAD_STUBBORN_ATTACK__MIN_LEAD_TO_PUBLISH:
			return minLeadToPublish != MIN_LEAD_TO_PUBLISH_EDEFAULT;
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
		result.append(" (minLeadToPublish: ");
		result.append(minLeadToPublish);
		result.append(')');
		return result.toString();
	}

} //LeadStubbornAttackImpl
