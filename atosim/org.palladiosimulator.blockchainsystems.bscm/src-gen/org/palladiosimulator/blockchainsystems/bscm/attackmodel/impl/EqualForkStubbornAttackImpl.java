/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.EqualForkStubbornAttack;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Equal Fork Stubborn Attack</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.EqualForkStubbornAttackImpl#getTieResolutionBias <em>Tie Resolution Bias</em>}</li>
 * </ul>
 *
 * @generated
 */
public class EqualForkStubbornAttackImpl extends StubbornMiningAttackImpl implements EqualForkStubbornAttack {
	/**
	 * The default value of the '{@link #getTieResolutionBias() <em>Tie Resolution Bias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTieResolutionBias()
	 * @generated
	 * @ordered
	 */
	protected static final double TIE_RESOLUTION_BIAS_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getTieResolutionBias() <em>Tie Resolution Bias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTieResolutionBias()
	 * @generated
	 * @ordered
	 */
	protected double tieResolutionBias = TIE_RESOLUTION_BIAS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EqualForkStubbornAttackImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AttackmodelPackage.Literals.EQUAL_FORK_STUBBORN_ATTACK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getTieResolutionBias() {
		return tieResolutionBias;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTieResolutionBias(double newTieResolutionBias) {
		double oldTieResolutionBias = tieResolutionBias;
		tieResolutionBias = newTieResolutionBias;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.EQUAL_FORK_STUBBORN_ATTACK__TIE_RESOLUTION_BIAS, oldTieResolutionBias,
					tieResolutionBias));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case AttackmodelPackage.EQUAL_FORK_STUBBORN_ATTACK__TIE_RESOLUTION_BIAS:
			return getTieResolutionBias();
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
		case AttackmodelPackage.EQUAL_FORK_STUBBORN_ATTACK__TIE_RESOLUTION_BIAS:
			setTieResolutionBias((Double) newValue);
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
		case AttackmodelPackage.EQUAL_FORK_STUBBORN_ATTACK__TIE_RESOLUTION_BIAS:
			setTieResolutionBias(TIE_RESOLUTION_BIAS_EDEFAULT);
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
		case AttackmodelPackage.EQUAL_FORK_STUBBORN_ATTACK__TIE_RESOLUTION_BIAS:
			return tieResolutionBias != TIE_RESOLUTION_BIAS_EDEFAULT;
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
		result.append(" (tieResolutionBias: ");
		result.append(tieResolutionBias);
		result.append(')');
		return result.toString();
	}

} //EqualForkStubbornAttackImpl
