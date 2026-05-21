/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;

import org.palladiosimulator.blockchainsystems.bscm.nodesystem.BlockchainSystemNodeSystem;

import org.palladiosimulator.pcm.core.entity.impl.EntityImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attacker Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackerNodeImpl#getPowerShare <em>Power Share</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackerNodeImpl#getLinkedNodeSystem <em>Linked Node System</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AttackerNodeImpl extends EntityImpl implements AttackerNode {
	/**
	 * The default value of the '{@link #getPowerShare() <em>Power Share</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPowerShare()
	 * @generated
	 * @ordered
	 */
	protected static final double POWER_SHARE_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getPowerShare() <em>Power Share</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPowerShare()
	 * @generated
	 * @ordered
	 */
	protected double powerShare = POWER_SHARE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getLinkedNodeSystem() <em>Linked Node System</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLinkedNodeSystem()
	 * @generated
	 * @ordered
	 */
	protected BlockchainSystemNodeSystem linkedNodeSystem;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AttackerNodeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AttackmodelPackage.Literals.ATTACKER_NODE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getPowerShare() {
		return powerShare;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPowerShare(double newPowerShare) {
		double oldPowerShare = powerShare;
		powerShare = newPowerShare;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACKER_NODE__POWER_SHARE,
					oldPowerShare, powerShare));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BlockchainSystemNodeSystem getLinkedNodeSystem() {
		if (linkedNodeSystem != null && linkedNodeSystem.eIsProxy()) {
			InternalEObject oldLinkedNodeSystem = (InternalEObject) linkedNodeSystem;
			linkedNodeSystem = (BlockchainSystemNodeSystem) eResolveProxy(oldLinkedNodeSystem);
			if (linkedNodeSystem != oldLinkedNodeSystem) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							AttackmodelPackage.ATTACKER_NODE__LINKED_NODE_SYSTEM, oldLinkedNodeSystem,
							linkedNodeSystem));
			}
		}
		return linkedNodeSystem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BlockchainSystemNodeSystem basicGetLinkedNodeSystem() {
		return linkedNodeSystem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLinkedNodeSystem(BlockchainSystemNodeSystem newLinkedNodeSystem) {
		BlockchainSystemNodeSystem oldLinkedNodeSystem = linkedNodeSystem;
		linkedNodeSystem = newLinkedNodeSystem;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACKER_NODE__LINKED_NODE_SYSTEM,
					oldLinkedNodeSystem, linkedNodeSystem));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case AttackmodelPackage.ATTACKER_NODE__POWER_SHARE:
			return getPowerShare();
		case AttackmodelPackage.ATTACKER_NODE__LINKED_NODE_SYSTEM:
			if (resolve)
				return getLinkedNodeSystem();
			return basicGetLinkedNodeSystem();
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
		case AttackmodelPackage.ATTACKER_NODE__POWER_SHARE:
			setPowerShare((Double) newValue);
			return;
		case AttackmodelPackage.ATTACKER_NODE__LINKED_NODE_SYSTEM:
			setLinkedNodeSystem((BlockchainSystemNodeSystem) newValue);
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
		case AttackmodelPackage.ATTACKER_NODE__POWER_SHARE:
			setPowerShare(POWER_SHARE_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACKER_NODE__LINKED_NODE_SYSTEM:
			setLinkedNodeSystem((BlockchainSystemNodeSystem) null);
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
		case AttackmodelPackage.ATTACKER_NODE__POWER_SHARE:
			return powerShare != POWER_SHARE_EDEFAULT;
		case AttackmodelPackage.ATTACKER_NODE__LINKED_NODE_SYSTEM:
			return linkedNodeSystem != null;
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
		result.append(" (powerShare: ");
		result.append(powerShare);
		result.append(')');
		return result.toString();
	}

} //AttackerNodeImpl
