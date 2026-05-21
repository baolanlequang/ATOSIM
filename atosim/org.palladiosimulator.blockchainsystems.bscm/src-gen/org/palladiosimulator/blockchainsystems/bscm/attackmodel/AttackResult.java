/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

import org.eclipse.emf.common.util.EList;

import org.palladiosimulator.pcm.core.entity.Entity;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attack Result</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#isSuccess <em>Success</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getDuration <em>Duration</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getAffectedBlocks <em>Affected Blocks</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getSuccessProbabilityObserved <em>Success Probability Observed</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getAvgForkRate <em>Avg Fork Rate</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getStaleBlockRate <em>Stale Block Rate</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getDoubleSpendCount <em>Double Spend Count</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getTimeToTakeover <em>Time To Takeover</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getForkDepthDistribution <em>Fork Depth Distribution</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getAttackerRevenueRatio <em>Attacker Revenue Ratio</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackResult()
 * @model
 * @generated
 */
public interface AttackResult extends Entity {
	/**
	 * Returns the value of the '<em><b>Success</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Success</em>' attribute.
	 * @see #setSuccess(boolean)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackResult_Success()
	 * @model
	 * @generated
	 */
	boolean isSuccess();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#isSuccess <em>Success</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Success</em>' attribute.
	 * @see #isSuccess()
	 * @generated
	 */
	void setSuccess(boolean value);

	/**
	 * Returns the value of the '<em><b>Duration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Duration</em>' attribute.
	 * @see #setDuration(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackResult_Duration()
	 * @model
	 * @generated
	 */
	double getDuration();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getDuration <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Duration</em>' attribute.
	 * @see #getDuration()
	 * @generated
	 */
	void setDuration(double value);

	/**
	 * Returns the value of the '<em><b>Affected Blocks</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Affected Blocks</em>' attribute.
	 * @see #setAffectedBlocks(int)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackResult_AffectedBlocks()
	 * @model
	 * @generated
	 */
	int getAffectedBlocks();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getAffectedBlocks <em>Affected Blocks</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Affected Blocks</em>' attribute.
	 * @see #getAffectedBlocks()
	 * @generated
	 */
	void setAffectedBlocks(int value);

	/**
	 * Returns the value of the '<em><b>Success Probability Observed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Success Probability Observed</em>' attribute.
	 * @see #setSuccessProbabilityObserved(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackResult_SuccessProbabilityObserved()
	 * @model derived="true"
	 * @generated
	 */
	double getSuccessProbabilityObserved();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getSuccessProbabilityObserved <em>Success Probability Observed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Success Probability Observed</em>' attribute.
	 * @see #getSuccessProbabilityObserved()
	 * @generated
	 */
	void setSuccessProbabilityObserved(double value);

	/**
	 * Returns the value of the '<em><b>Avg Fork Rate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Avg Fork Rate</em>' attribute.
	 * @see #setAvgForkRate(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackResult_AvgForkRate()
	 * @model derived="true"
	 * @generated
	 */
	double getAvgForkRate();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getAvgForkRate <em>Avg Fork Rate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Avg Fork Rate</em>' attribute.
	 * @see #getAvgForkRate()
	 * @generated
	 */
	void setAvgForkRate(double value);

	/**
	 * Returns the value of the '<em><b>Stale Block Rate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Stale Block Rate</em>' attribute.
	 * @see #setStaleBlockRate(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackResult_StaleBlockRate()
	 * @model derived="true"
	 * @generated
	 */
	double getStaleBlockRate();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getStaleBlockRate <em>Stale Block Rate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Stale Block Rate</em>' attribute.
	 * @see #getStaleBlockRate()
	 * @generated
	 */
	void setStaleBlockRate(double value);

	/**
	 * Returns the value of the '<em><b>Double Spend Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Double Spend Count</em>' attribute.
	 * @see #setDoubleSpendCount(int)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackResult_DoubleSpendCount()
	 * @model derived="true"
	 * @generated
	 */
	int getDoubleSpendCount();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getDoubleSpendCount <em>Double Spend Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Double Spend Count</em>' attribute.
	 * @see #getDoubleSpendCount()
	 * @generated
	 */
	void setDoubleSpendCount(int value);

	/**
	 * Returns the value of the '<em><b>Time To Takeover</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Time To Takeover</em>' attribute.
	 * @see #setTimeToTakeover(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackResult_TimeToTakeover()
	 * @model derived="true"
	 * @generated
	 */
	double getTimeToTakeover();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getTimeToTakeover <em>Time To Takeover</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Time To Takeover</em>' attribute.
	 * @see #getTimeToTakeover()
	 * @generated
	 */
	void setTimeToTakeover(double value);

	/**
	 * Returns the value of the '<em><b>Fork Depth Distribution</b></em>' containment reference list.
	 * The list contents are of type {@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.ForkDepthEntry}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fork Depth Distribution</em>' containment reference list.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackResult_ForkDepthDistribution()
	 * @model containment="true"
	 * @generated
	 */
	EList<ForkDepthEntry> getForkDepthDistribution();

	/**
	 * Returns the value of the '<em><b>Attacker Revenue Ratio</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attacker Revenue Ratio</em>' attribute.
	 * @see #setAttackerRevenueRatio(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getAttackResult_AttackerRevenueRatio()
	 * @model
	 * @generated
	 */
	double getAttackerRevenueRatio();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult#getAttackerRevenueRatio <em>Attacker Revenue Ratio</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Attacker Revenue Ratio</em>' attribute.
	 * @see #getAttackerRevenueRatio()
	 * @generated
	 */
	void setAttackerRevenueRatio(double value);

} // AttackResult
