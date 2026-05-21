/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Equal Fork Stubborn Attack</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.EqualForkStubbornAttack#getTieResolutionBias <em>Tie Resolution Bias</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getEqualForkStubbornAttack()
 * @model
 * @generated
 */
public interface EqualForkStubbornAttack extends StubbornMiningAttack {
	/**
	 * Returns the value of the '<em><b>Tie Resolution Bias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tie Resolution Bias</em>' attribute.
	 * @see #setTieResolutionBias(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getEqualForkStubbornAttack_TieResolutionBias()
	 * @model
	 * @generated
	 */
	double getTieResolutionBias();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.EqualForkStubbornAttack#getTieResolutionBias <em>Tie Resolution Bias</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Tie Resolution Bias</em>' attribute.
	 * @see #getTieResolutionBias()
	 * @generated
	 */
	void setTieResolutionBias(double value);

} // EqualForkStubbornAttack
