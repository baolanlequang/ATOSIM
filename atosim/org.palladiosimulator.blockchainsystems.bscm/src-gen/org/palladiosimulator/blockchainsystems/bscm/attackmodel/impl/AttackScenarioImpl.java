/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;

import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainSystem;

import org.palladiosimulator.pcm.core.entity.impl.EntityImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attack Scenario</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackScenarioImpl#getAttack <em>Attack</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackScenarioImpl#getAttackers <em>Attackers</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackScenarioImpl#getBlockchainSystem <em>Blockchain System</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackScenarioImpl#getResult <em>Result</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AttackScenarioImpl extends EntityImpl implements AttackScenario {
	/**
	 * The cached value of the '{@link #getAttack() <em>Attack</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttack()
	 * @generated
	 * @ordered
	 */
	protected Attack attack;

	/**
	 * The cached value of the '{@link #getAttackers() <em>Attackers</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttackers()
	 * @generated
	 * @ordered
	 */
	protected EList<AttackerNode> attackers;

	/**
	 * The cached value of the '{@link #getBlockchainSystem() <em>Blockchain System</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBlockchainSystem()
	 * @generated
	 * @ordered
	 */
	protected BlockchainSystem blockchainSystem;

	/**
	 * The cached value of the '{@link #getResult() <em>Result</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResult()
	 * @generated
	 * @ordered
	 */
	protected AttackResult result;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AttackScenarioImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AttackmodelPackage.Literals.ATTACK_SCENARIO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Attack getAttack() {
		return attack;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetAttack(Attack newAttack, NotificationChain msgs) {
		Attack oldAttack = attack;
		attack = newAttack;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.ATTACK_SCENARIO__ATTACK, oldAttack, newAttack);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAttack(Attack newAttack) {
		if (newAttack != attack) {
			NotificationChain msgs = null;
			if (attack != null)
				msgs = ((InternalEObject) attack).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - AttackmodelPackage.ATTACK_SCENARIO__ATTACK, null, msgs);
			if (newAttack != null)
				msgs = ((InternalEObject) newAttack).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - AttackmodelPackage.ATTACK_SCENARIO__ATTACK, null, msgs);
			msgs = basicSetAttack(newAttack, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK_SCENARIO__ATTACK, newAttack,
					newAttack));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<AttackerNode> getAttackers() {
		if (attackers == null) {
			attackers = new EObjectContainmentEList<AttackerNode>(AttackerNode.class, this,
					AttackmodelPackage.ATTACK_SCENARIO__ATTACKERS);
		}
		return attackers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public BlockchainSystem getBlockchainSystem() {
		if (blockchainSystem != null && blockchainSystem.eIsProxy()) {
			InternalEObject oldBlockchainSystem = (InternalEObject) blockchainSystem;
			blockchainSystem = (BlockchainSystem) eResolveProxy(oldBlockchainSystem);
			if (blockchainSystem != oldBlockchainSystem) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							AttackmodelPackage.ATTACK_SCENARIO__BLOCKCHAIN_SYSTEM, oldBlockchainSystem,
							blockchainSystem));
			}
		}
		return blockchainSystem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BlockchainSystem basicGetBlockchainSystem() {
		return blockchainSystem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBlockchainSystem(BlockchainSystem newBlockchainSystem) {
		BlockchainSystem oldBlockchainSystem = blockchainSystem;
		blockchainSystem = newBlockchainSystem;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK_SCENARIO__BLOCKCHAIN_SYSTEM,
					oldBlockchainSystem, blockchainSystem));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttackResult getResult() {
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetResult(AttackResult newResult, NotificationChain msgs) {
		AttackResult oldResult = result;
		result = newResult;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.ATTACK_SCENARIO__RESULT, oldResult, newResult);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setResult(AttackResult newResult) {
		if (newResult != result) {
			NotificationChain msgs = null;
			if (result != null)
				msgs = ((InternalEObject) result).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - AttackmodelPackage.ATTACK_SCENARIO__RESULT, null, msgs);
			if (newResult != null)
				msgs = ((InternalEObject) newResult).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - AttackmodelPackage.ATTACK_SCENARIO__RESULT, null, msgs);
			msgs = basicSetResult(newResult, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK_SCENARIO__RESULT, newResult,
					newResult));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case AttackmodelPackage.ATTACK_SCENARIO__ATTACK:
			return basicSetAttack(null, msgs);
		case AttackmodelPackage.ATTACK_SCENARIO__ATTACKERS:
			return ((InternalEList<?>) getAttackers()).basicRemove(otherEnd, msgs);
		case AttackmodelPackage.ATTACK_SCENARIO__RESULT:
			return basicSetResult(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case AttackmodelPackage.ATTACK_SCENARIO__ATTACK:
			return getAttack();
		case AttackmodelPackage.ATTACK_SCENARIO__ATTACKERS:
			return getAttackers();
		case AttackmodelPackage.ATTACK_SCENARIO__BLOCKCHAIN_SYSTEM:
			if (resolve)
				return getBlockchainSystem();
			return basicGetBlockchainSystem();
		case AttackmodelPackage.ATTACK_SCENARIO__RESULT:
			return getResult();
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
		case AttackmodelPackage.ATTACK_SCENARIO__ATTACK:
			setAttack((Attack) newValue);
			return;
		case AttackmodelPackage.ATTACK_SCENARIO__ATTACKERS:
			getAttackers().clear();
			getAttackers().addAll((Collection<? extends AttackerNode>) newValue);
			return;
		case AttackmodelPackage.ATTACK_SCENARIO__BLOCKCHAIN_SYSTEM:
			setBlockchainSystem((BlockchainSystem) newValue);
			return;
		case AttackmodelPackage.ATTACK_SCENARIO__RESULT:
			setResult((AttackResult) newValue);
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
		case AttackmodelPackage.ATTACK_SCENARIO__ATTACK:
			setAttack((Attack) null);
			return;
		case AttackmodelPackage.ATTACK_SCENARIO__ATTACKERS:
			getAttackers().clear();
			return;
		case AttackmodelPackage.ATTACK_SCENARIO__BLOCKCHAIN_SYSTEM:
			setBlockchainSystem((BlockchainSystem) null);
			return;
		case AttackmodelPackage.ATTACK_SCENARIO__RESULT:
			setResult((AttackResult) null);
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
		case AttackmodelPackage.ATTACK_SCENARIO__ATTACK:
			return attack != null;
		case AttackmodelPackage.ATTACK_SCENARIO__ATTACKERS:
			return attackers != null && !attackers.isEmpty();
		case AttackmodelPackage.ATTACK_SCENARIO__BLOCKCHAIN_SYSTEM:
			return blockchainSystem != null;
		case AttackmodelPackage.ATTACK_SCENARIO__RESULT:
			return result != null;
		}
		return super.eIsSet(featureID);
	}

} //AttackScenarioImpl
