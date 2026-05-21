/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

import org.palladiosimulator.pcm.core.entity.Entity;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attack Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackParameter#getKey <em>Key</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackParameter#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackParameter()
 * @model
 * @generated
 */
public interface AttackParameter extends Entity {
	/**
	 * Returns the value of the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Key</em>' attribute.
	 * @see #setKey(String)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackParameter_Key()
	 * @model
	 * @generated
	 */
	String getKey();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackParameter#getKey <em>Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key</em>' attribute.
	 * @see #getKey()
	 * @generated
	 */
	void setKey(String value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(String)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackParameter_Value()
	 * @model
	 * @generated
	 */
	String getValue();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackParameter#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(String value);

} // AttackParameter
