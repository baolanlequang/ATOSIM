/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Lead Stubborn Attack</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.LeadStubbornAttack#getMinLeadToPublish <em>Min Lead To Publish</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getLeadStubbornAttack()
 * @model
 * @generated
 */
public interface LeadStubbornAttack extends StubbornMiningAttack {
	/**
	 * Returns the value of the '<em><b>Min Lead To Publish</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Min Lead To Publish</em>' attribute.
	 * @see #setMinLeadToPublish(int)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getLeadStubbornAttack_MinLeadToPublish()
	 * @model
	 * @generated
	 */
	int getMinLeadToPublish();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.LeadStubbornAttack#getMinLeadToPublish <em>Min Lead To Publish</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min Lead To Publish</em>' attribute.
	 * @see #getMinLeadToPublish()
	 * @generated
	 */
	void setMinLeadToPublish(int value);

} // LeadStubbornAttack
