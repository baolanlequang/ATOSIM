/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

import org.palladiosimulator.blockchainsystems.bscm.nodesystem.BlockchainSystemNodeSystem;

import org.palladiosimulator.pcm.core.entity.Entity;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attacker Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode#getPowerShare <em>Power Share</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode#getLinkedNodeSystem <em>Linked Node System</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackerNode()
 * @model
 * @generated
 */
public interface AttackerNode extends Entity {
	/**
	 * Returns the value of the '<em><b>Power Share</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Power Share</em>' attribute.
	 * @see #setPowerShare(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackerNode_PowerShare()
	 * @model
	 * @generated
	 */
	double getPowerShare();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode#getPowerShare <em>Power Share</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Power Share</em>' attribute.
	 * @see #getPowerShare()
	 * @generated
	 */
	void setPowerShare(double value);

	/**
	 * Returns the value of the '<em><b>Linked Node System</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Linked Node System</em>' reference.
	 * @see #setLinkedNodeSystem(BlockchainSystemNodeSystem)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackerNode_LinkedNodeSystem()
	 * @model
	 * @generated
	 */
	BlockchainSystemNodeSystem getLinkedNodeSystem();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode#getLinkedNodeSystem <em>Linked Node System</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Linked Node System</em>' reference.
	 * @see #getLinkedNodeSystem()
	 * @generated
	 */
	void setLinkedNodeSystem(BlockchainSystemNodeSystem value);

} // AttackerNode
