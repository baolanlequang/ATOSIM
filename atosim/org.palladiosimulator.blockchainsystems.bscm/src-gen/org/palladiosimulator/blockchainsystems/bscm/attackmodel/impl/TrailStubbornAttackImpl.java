/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.TrailStubbornAttack;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Trail Stubborn Attack</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.TrailStubbornAttackImpl#isContinueOnTrail <em>Continue On Trail</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TrailStubbornAttackImpl extends StubbornMiningAttackImpl implements TrailStubbornAttack {
	/**
	 * The default value of the '{@link #isContinueOnTrail() <em>Continue On Trail</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isContinueOnTrail()
	 * @generated
	 * @ordered
	 */
	protected static final boolean CONTINUE_ON_TRAIL_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isContinueOnTrail() <em>Continue On Trail</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isContinueOnTrail()
	 * @generated
	 * @ordered
	 */
	protected boolean continueOnTrail = CONTINUE_ON_TRAIL_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TrailStubbornAttackImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AttackmodelPackage.Literals.TRAIL_STUBBORN_ATTACK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isContinueOnTrail() {
		return continueOnTrail;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setContinueOnTrail(boolean newContinueOnTrail) {
		boolean oldContinueOnTrail = continueOnTrail;
		continueOnTrail = newContinueOnTrail;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.TRAIL_STUBBORN_ATTACK__CONTINUE_ON_TRAIL, oldContinueOnTrail, continueOnTrail));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case AttackmodelPackage.TRAIL_STUBBORN_ATTACK__CONTINUE_ON_TRAIL:
			return isContinueOnTrail();
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
		case AttackmodelPackage.TRAIL_STUBBORN_ATTACK__CONTINUE_ON_TRAIL:
			setContinueOnTrail((Boolean) newValue);
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
		case AttackmodelPackage.TRAIL_STUBBORN_ATTACK__CONTINUE_ON_TRAIL:
			setContinueOnTrail(CONTINUE_ON_TRAIL_EDEFAULT);
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
		case AttackmodelPackage.TRAIL_STUBBORN_ATTACK__CONTINUE_ON_TRAIL:
			return continueOnTrail != CONTINUE_ON_TRAIL_EDEFAULT;
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
		result.append(" (continueOnTrail: ");
		result.append(continueOnTrail);
		result.append(')');
		return result.toString();
	}

} //TrailStubbornAttackImpl
