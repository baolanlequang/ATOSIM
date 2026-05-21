/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Stubborn Mining Attack</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.StubbornMiningAttack#getLeadThreshold <em>Lead Threshold</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.StubbornMiningAttack#getAbandonThreshold <em>Abandon Threshold</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getStubbornMiningAttack()
 * @model abstract="true"
 * @generated
 */
public interface StubbornMiningAttack extends Attack {
	/**
	 * Returns the value of the '<em><b>Lead Threshold</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lead Threshold</em>' attribute.
	 * @see #setLeadThreshold(int)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getStubbornMiningAttack_LeadThreshold()
	 * @model
	 * @generated
	 */
	int getLeadThreshold();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.StubbornMiningAttack#getLeadThreshold <em>Lead Threshold</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Lead Threshold</em>' attribute.
	 * @see #getLeadThreshold()
	 * @generated
	 */
	void setLeadThreshold(int value);

	/**
	 * Returns the value of the '<em><b>Abandon Threshold</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Abandon Threshold</em>' attribute.
	 * @see #setAbandonThreshold(int)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getStubbornMiningAttack_AbandonThreshold()
	 * @model
	 * @generated
	 */
	int getAbandonThreshold();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.StubbornMiningAttack#getAbandonThreshold <em>Abandon Threshold</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Abandon Threshold</em>' attribute.
	 * @see #getAbandonThreshold()
	 * @generated
	 */
	void setAbandonThreshold(int value);

} // StubbornMiningAttack
