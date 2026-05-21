/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

import org.eclipse.emf.common.util.EList;

import org.palladiosimulator.pcm.core.entity.Entity;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attack</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getType <em>Type</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getRequiredHashPower <em>Required Hash Power</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#isDependsOnNetworkDelay <em>Depends On Network Delay</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getBaselineSuccessProbability <em>Baseline Success Probability</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getGamma <em>Gamma</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttack()
 * @model abstract="true"
 * @generated
 */
public interface Attack extends Entity {
	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(String)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttack_Type()
	 * @model
	 * @generated
	 */
	String getType();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

	/**
	 * Returns the value of the '<em><b>Required Hash Power</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Required Hash Power</em>' attribute.
	 * @see #setRequiredHashPower(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttack_RequiredHashPower()
	 * @model
	 * @generated
	 */
	double getRequiredHashPower();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getRequiredHashPower <em>Required Hash Power</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required Hash Power</em>' attribute.
	 * @see #getRequiredHashPower()
	 * @generated
	 */
	void setRequiredHashPower(double value);

	/**
	 * Returns the value of the '<em><b>Depends On Network Delay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Depends On Network Delay</em>' attribute.
	 * @see #setDependsOnNetworkDelay(boolean)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttack_DependsOnNetworkDelay()
	 * @model
	 * @generated
	 */
	boolean isDependsOnNetworkDelay();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#isDependsOnNetworkDelay <em>Depends On Network Delay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Depends On Network Delay</em>' attribute.
	 * @see #isDependsOnNetworkDelay()
	 * @generated
	 */
	void setDependsOnNetworkDelay(boolean value);

	/**
	 * Returns the value of the '<em><b>Baseline Success Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Baseline Success Probability</em>' attribute.
	 * @see #setBaselineSuccessProbability(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttack_BaselineSuccessProbability()
	 * @model
	 * @generated
	 */
	double getBaselineSuccessProbability();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getBaselineSuccessProbability <em>Baseline Success Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Baseline Success Probability</em>' attribute.
	 * @see #getBaselineSuccessProbability()
	 * @generated
	 */
	void setBaselineSuccessProbability(double value);

	/**
	 * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
	 * The list contents are of type {@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackParameter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parameters</em>' containment reference list.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttack_Parameters()
	 * @model containment="true"
	 * @generated
	 */
	EList<AttackParameter> getParameters();

	/**
	 * Returns the value of the '<em><b>Gamma</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gamma</em>' attribute.
	 * @see #setGamma(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttack_Gamma()
	 * @model default="0"
	 * @generated
	 */
	double getGamma();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack#getGamma <em>Gamma</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gamma</em>' attribute.
	 * @see #getGamma()
	 * @generated
	 */
	void setGamma(double value);

} // Attack
