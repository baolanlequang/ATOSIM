/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Race Attack</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getPropagationAdvantage <em>Propagation Advantage</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getTransactionADelay <em>Transaction ADelay</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getTransactionBAcceleration <em>Transaction BAcceleration</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getFeeDifferenceThreshold <em>Fee Difference Threshold</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getZeroConfAcceptanceRisk <em>Zero Conf Acceptance Risk</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getObservedDoubleSpends <em>Observed Double Spends</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getAvgPropagationAdvMs <em>Avg Propagation Adv Ms</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getRaceAttack()
 * @model
 * @generated
 */
public interface RaceAttack extends Attack {
	/**
	 * Returns the value of the '<em><b>Propagation Advantage</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Propagation Advantage</em>' attribute.
	 * @see #setPropagationAdvantage(long)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getRaceAttack_PropagationAdvantage()
	 * @model
	 * @generated
	 */
	long getPropagationAdvantage();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getPropagationAdvantage <em>Propagation Advantage</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Propagation Advantage</em>' attribute.
	 * @see #getPropagationAdvantage()
	 * @generated
	 */
	void setPropagationAdvantage(long value);

	/**
	 * Returns the value of the '<em><b>Transaction ADelay</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transaction ADelay</em>' attribute.
	 * @see #setTransactionADelay(long)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getRaceAttack_TransactionADelay()
	 * @model
	 * @generated
	 */
	long getTransactionADelay();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getTransactionADelay <em>Transaction ADelay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Transaction ADelay</em>' attribute.
	 * @see #getTransactionADelay()
	 * @generated
	 */
	void setTransactionADelay(long value);

	/**
	 * Returns the value of the '<em><b>Transaction BAcceleration</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transaction BAcceleration</em>' attribute.
	 * @see #setTransactionBAcceleration(long)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getRaceAttack_TransactionBAcceleration()
	 * @model
	 * @generated
	 */
	long getTransactionBAcceleration();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getTransactionBAcceleration <em>Transaction BAcceleration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Transaction BAcceleration</em>' attribute.
	 * @see #getTransactionBAcceleration()
	 * @generated
	 */
	void setTransactionBAcceleration(long value);

	/**
	 * Returns the value of the '<em><b>Fee Difference Threshold</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fee Difference Threshold</em>' attribute.
	 * @see #setFeeDifferenceThreshold(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getRaceAttack_FeeDifferenceThreshold()
	 * @model
	 * @generated
	 */
	double getFeeDifferenceThreshold();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getFeeDifferenceThreshold <em>Fee Difference Threshold</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Fee Difference Threshold</em>' attribute.
	 * @see #getFeeDifferenceThreshold()
	 * @generated
	 */
	void setFeeDifferenceThreshold(double value);

	/**
	 * Returns the value of the '<em><b>Zero Conf Acceptance Risk</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Zero Conf Acceptance Risk</em>' attribute.
	 * @see #setZeroConfAcceptanceRisk(double)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getRaceAttack_ZeroConfAcceptanceRisk()
	 * @model
	 * @generated
	 */
	double getZeroConfAcceptanceRisk();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getZeroConfAcceptanceRisk <em>Zero Conf Acceptance Risk</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Zero Conf Acceptance Risk</em>' attribute.
	 * @see #getZeroConfAcceptanceRisk()
	 * @generated
	 */
	void setZeroConfAcceptanceRisk(double value);

	/**
	 * Returns the value of the '<em><b>Observed Double Spends</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Observed Double Spends</em>' attribute.
	 * @see #setObservedDoubleSpends(int)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getRaceAttack_ObservedDoubleSpends()
	 * @model derived="true"
	 * @generated
	 */
	int getObservedDoubleSpends();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getObservedDoubleSpends <em>Observed Double Spends</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Observed Double Spends</em>' attribute.
	 * @see #getObservedDoubleSpends()
	 * @generated
	 */
	void setObservedDoubleSpends(int value);

	/**
	 * Returns the value of the '<em><b>Avg Propagation Adv Ms</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Avg Propagation Adv Ms</em>' attribute.
	 * @see #setAvgPropagationAdvMs(long)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getRaceAttack_AvgPropagationAdvMs()
	 * @model derived="true"
	 * @generated
	 */
	long getAvgPropagationAdvMs();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack#getAvgPropagationAdvMs <em>Avg Propagation Adv Ms</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Avg Propagation Adv Ms</em>' attribute.
	 * @see #getAvgPropagationAdvMs()
	 * @generated
	 */
	void setAvgPropagationAdvMs(long value);

} // RaceAttack
