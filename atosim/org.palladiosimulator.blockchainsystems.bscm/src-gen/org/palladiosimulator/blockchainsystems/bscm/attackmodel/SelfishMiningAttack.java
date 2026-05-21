/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Selfish Mining Attack</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getPrivateChainLeadTarget <em>Private Chain Lead Target</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getWithholdProbability <em>Withhold Probability</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#isPublishOnCatchup <em>Publish On Catchup</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getExpectedForksPerHour <em>Expected Forks Per Hour</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getMonitoredNodes <em>Monitored Nodes</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getSelfishMiningAttack()
 * @model
 * @generated
 */
public interface SelfishMiningAttack extends Attack {
	/**
	 * Returns the value of the '<em><b>Private Chain Lead Target</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Private Chain Lead Target</em>' attribute.
	 * @see #setPrivateChainLeadTarget(int)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getSelfishMiningAttack_PrivateChainLeadTarget()
	 * @model
	 * @generated
	 */
	int getPrivateChainLeadTarget();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getPrivateChainLeadTarget <em>Private Chain Lead Target</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Private Chain Lead Target</em>' attribute.
	 * @see #getPrivateChainLeadTarget()
	 * @generated
	 */
	void setPrivateChainLeadTarget(int value);

	/**
	 * Returns the value of the '<em><b>Withhold Probability</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Withhold Probability</em>' attribute.
	 * @see #setWithholdProbability(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getSelfishMiningAttack_WithholdProbability()
	 * @model
	 * @generated
	 */
	double getWithholdProbability();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getWithholdProbability <em>Withhold Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Withhold Probability</em>' attribute.
	 * @see #getWithholdProbability()
	 * @generated
	 */
	void setWithholdProbability(double value);

	/**
	 * Returns the value of the '<em><b>Publish On Catchup</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Publish On Catchup</em>' attribute.
	 * @see #setPublishOnCatchup(boolean)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getSelfishMiningAttack_PublishOnCatchup()
	 * @model
	 * @generated
	 */
	boolean isPublishOnCatchup();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#isPublishOnCatchup <em>Publish On Catchup</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Publish On Catchup</em>' attribute.
	 * @see #isPublishOnCatchup()
	 * @generated
	 */
	void setPublishOnCatchup(boolean value);

	/**
	 * Returns the value of the '<em><b>Expected Forks Per Hour</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Expected Forks Per Hour</em>' attribute.
	 * @see #setExpectedForksPerHour(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getSelfishMiningAttack_ExpectedForksPerHour()
	 * @model derived="true"
	 * @generated
	 */
	double getExpectedForksPerHour();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack#getExpectedForksPerHour <em>Expected Forks Per Hour</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expected Forks Per Hour</em>' attribute.
	 * @see #getExpectedForksPerHour()
	 * @generated
	 */
	void setExpectedForksPerHour(double value);

	/**
	 * Returns the value of the '<em><b>Monitored Nodes</b></em>' reference list.
	 * The list contents are of type {@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Monitored Nodes</em>' reference list.
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getSelfishMiningAttack_MonitoredNodes()
	 * @model
	 * @generated
	 */
	EList<AttackerNode> getMonitoredNodes();

} // SelfishMiningAttack
