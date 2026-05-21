/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

import org.eclipse.emf.common.util.EList;

import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainSystem;

import org.palladiosimulator.pcm.core.entity.Entity;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attack Scenario</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getAttack <em>Attack</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getAttackers <em>Attackers</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getBlockchainSystem <em>Blockchain System</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getResult <em>Result</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackScenario()
 * @model
 * @generated
 */
public interface AttackScenario extends Entity {
	/**
	 * Returns the value of the '<em><b>Attack</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attack</em>' containment reference.
	 * @see #setAttack(Attack)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackScenario_Attack()
	 * @model containment="true" required="true"
	 * @generated
	 */
	Attack getAttack();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getAttack <em>Attack</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Attack</em>' containment reference.
	 * @see #getAttack()
	 * @generated
	 */
	void setAttack(Attack value);

	/**
	 * Returns the value of the '<em><b>Attackers</b></em>' containment reference list.
	 * The list contents are of type {@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attackers</em>' containment reference list.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackScenario_Attackers()
	 * @model containment="true"
	 * @generated
	 */
	EList<AttackerNode> getAttackers();

	/**
	 * Returns the value of the '<em><b>Blockchain System</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Blockchain System</em>' reference.
	 * @see #setBlockchainSystem(BlockchainSystem)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackScenario_BlockchainSystem()
	 * @model required="true"
	 * @generated
	 */
	BlockchainSystem getBlockchainSystem();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getBlockchainSystem <em>Blockchain System</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Blockchain System</em>' reference.
	 * @see #getBlockchainSystem()
	 * @generated
	 */
	void setBlockchainSystem(BlockchainSystem value);

	/**
	 * Returns the value of the '<em><b>Result</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Result</em>' containment reference.
	 * @see #setResult(AttackResult)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackScenario_Result()
	 * @model containment="true"
	 * @generated
	 */
	AttackResult getResult();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario#getResult <em>Result</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Result</em>' containment reference.
	 * @see #getResult()
	 * @generated
	 */
	void setResult(AttackResult value);

} // AttackScenario
