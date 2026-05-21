/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Trail Stubborn Attack</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.TrailStubbornAttack#isContinueOnTrail <em>Continue On Trail</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getTrailStubbornAttack()
 * @model
 * @generated
 */
public interface TrailStubbornAttack extends StubbornMiningAttack {
	/**
	 * Returns the value of the '<em><b>Continue On Trail</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Continue On Trail</em>' attribute.
	 * @see #setContinueOnTrail(boolean)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getTrailStubbornAttack_ContinueOnTrail()
	 * @model
	 * @generated
	 */
	boolean isContinueOnTrail();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.TrailStubbornAttack#isContinueOnTrail <em>Continue On Trail</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Continue On Trail</em>' attribute.
	 * @see #isContinueOnTrail()
	 * @generated
	 */
	void setContinueOnTrail(boolean value);

} // TrailStubbornAttack
