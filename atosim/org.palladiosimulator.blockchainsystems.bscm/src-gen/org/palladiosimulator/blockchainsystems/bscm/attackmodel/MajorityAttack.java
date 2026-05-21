/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Majority Attack</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getHashPowerShare <em>Hash Power Share</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getExpectedTakeoverTime <em>Expected Takeover Time</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getTargetConfirmationsToBreak <em>Target Confirmations To Break</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getColludingPools <em>Colluding Pools</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getMajorityAttack()
 * @model
 * @generated
 */
public interface MajorityAttack extends Attack {
	/**
	 * Returns the value of the '<em><b>Hash Power Share</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hash Power Share</em>' attribute.
	 * @see #setHashPowerShare(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getMajorityAttack_HashPowerShare()
	 * @model
	 * @generated
	 */
	double getHashPowerShare();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getHashPowerShare <em>Hash Power Share</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Hash Power Share</em>' attribute.
	 * @see #getHashPowerShare()
	 * @generated
	 */
	void setHashPowerShare(double value);

	/**
	 * Returns the value of the '<em><b>Expected Takeover Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Expected Takeover Time</em>' attribute.
	 * @see #setExpectedTakeoverTime(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getMajorityAttack_ExpectedTakeoverTime()
	 * @model
	 * @generated
	 */
	double getExpectedTakeoverTime();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getExpectedTakeoverTime <em>Expected Takeover Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expected Takeover Time</em>' attribute.
	 * @see #getExpectedTakeoverTime()
	 * @generated
	 */
	void setExpectedTakeoverTime(double value);

	/**
	 * Returns the value of the '<em><b>Target Confirmations To Break</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target Confirmations To Break</em>' attribute.
	 * @see #setTargetConfirmationsToBreak(int)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getMajorityAttack_TargetConfirmationsToBreak()
	 * @model
	 * @generated
	 */
	int getTargetConfirmationsToBreak();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.MajorityAttack#getTargetConfirmationsToBreak <em>Target Confirmations To Break</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target Confirmations To Break</em>' attribute.
	 * @see #getTargetConfirmationsToBreak()
	 * @generated
	 */
	void setTargetConfirmationsToBreak(int value);

	/**
	 * Returns the value of the '<em><b>Colluding Pools</b></em>' reference list.
	 * The list contents are of type {@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Colluding Pools</em>' reference list.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getMajorityAttack_ColludingPools()
	 * @model
	 * @generated
	 */
	EList<AttackerNode> getColludingPools();

} // MajorityAttack
