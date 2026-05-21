/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Finney Attack</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getNumberOfPreminedBlocks <em>Number Of Premined Blocks</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getWithholdTime <em>Withhold Time</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#isMerchantAcceptsZeroConf <em>Merchant Accepts Zero Conf</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getDoubleSpendSuccessCount <em>Double Spend Success Count</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getObservedTimeToReleaseMs <em>Observed Time To Release Ms</em>}</li>
 * </ul>
 *
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getFinneyAttack()
 * @model
 * @generated
 */
public interface FinneyAttack extends Attack {
	/**
	 * Returns the value of the '<em><b>Number Of Premined Blocks</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Number Of Premined Blocks</em>' attribute.
	 * @see #setNumberOfPreminedBlocks(int)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getFinneyAttack_NumberOfPreminedBlocks()
	 * @model
	 * @generated
	 */
	int getNumberOfPreminedBlocks();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getNumberOfPreminedBlocks <em>Number Of Premined Blocks</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Number Of Premined Blocks</em>' attribute.
	 * @see #getNumberOfPreminedBlocks()
	 * @generated
	 */
	void setNumberOfPreminedBlocks(int value);

	/**
	 * Returns the value of the '<em><b>Withhold Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Withhold Time</em>' attribute.
	 * @see #setWithholdTime(long)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getFinneyAttack_WithholdTime()
	 * @model
	 * @generated
	 */
	long getWithholdTime();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getWithholdTime <em>Withhold Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Withhold Time</em>' attribute.
	 * @see #getWithholdTime()
	 * @generated
	 */
	void setWithholdTime(long value);

	/**
	 * Returns the value of the '<em><b>Merchant Accepts Zero Conf</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Merchant Accepts Zero Conf</em>' attribute.
	 * @see #setMerchantAcceptsZeroConf(boolean)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getFinneyAttack_MerchantAcceptsZeroConf()
	 * @model
	 * @generated
	 */
	boolean isMerchantAcceptsZeroConf();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#isMerchantAcceptsZeroConf <em>Merchant Accepts Zero Conf</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Merchant Accepts Zero Conf</em>' attribute.
	 * @see #isMerchantAcceptsZeroConf()
	 * @generated
	 */
	void setMerchantAcceptsZeroConf(boolean value);

	/**
	 * Returns the value of the '<em><b>Double Spend Success Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Double Spend Success Count</em>' attribute.
	 * @see #setDoubleSpendSuccessCount(int)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getFinneyAttack_DoubleSpendSuccessCount()
	 * @model derived="true"
	 * @generated
	 */
	int getDoubleSpendSuccessCount();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getDoubleSpendSuccessCount <em>Double Spend Success Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Double Spend Success Count</em>' attribute.
	 * @see #getDoubleSpendSuccessCount()
	 * @generated
	 */
	void setDoubleSpendSuccessCount(int value);

	/**
	 * Returns the value of the '<em><b>Observed Time To Release Ms</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Observed Time To Release Ms</em>' attribute.
	 * @see #setObservedTimeToReleaseMs(long)
	 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage#getFinneyAttack_ObservedTimeToReleaseMs()
	 * @model derived="true"
	 * @generated
	 */
	long getObservedTimeToReleaseMs();

	/**
	 * Sets the value of the '{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack#getObservedTimeToReleaseMs <em>Observed Time To Release Ms</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Observed Time To Release Ms</em>' attribute.
	 * @see #getObservedTimeToReleaseMs()
	 * @generated
	 */
	void setObservedTimeToReleaseMs(long value);

} // FinneyAttack
