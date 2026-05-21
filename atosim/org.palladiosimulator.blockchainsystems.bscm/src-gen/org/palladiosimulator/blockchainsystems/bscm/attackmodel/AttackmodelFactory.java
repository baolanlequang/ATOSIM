/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage
 * @generated
 */
public interface AttackmodelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	AttackmodelFactory eINSTANCE = org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackmodelFactoryImpl
			.init();

	/**
	 * Returns a new object of class '<em>Attack Scenario</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attack Scenario</em>'.
	 * @generated
	 */
	AttackScenario createAttackScenario();

	/**
	 * Returns a new object of class '<em>Attacker Node</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attacker Node</em>'.
	 * @generated
	 */
	AttackerNode createAttackerNode();

	/**
	 * Returns a new object of class '<em>Attack Result</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attack Result</em>'.
	 * @generated
	 */
	AttackResult createAttackResult();

	/**
	 * Returns a new object of class '<em>Attack Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attack Parameter</em>'.
	 * @generated
	 */
	AttackParameter createAttackParameter();

	/**
	 * Returns a new object of class '<em>Fork Depth Entry</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Fork Depth Entry</em>'.
	 * @generated
	 */
	ForkDepthEntry createForkDepthEntry();

	/**
	 * Returns a new object of class '<em>Race Attack</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Race Attack</em>'.
	 * @generated
	 */
	RaceAttack createRaceAttack();

	/**
	 * Returns a new object of class '<em>Finney Attack</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Finney Attack</em>'.
	 * @generated
	 */
	FinneyAttack createFinneyAttack();

	/**
	 * Returns a new object of class '<em>Selfish Mining Attack</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Selfish Mining Attack</em>'.
	 * @generated
	 */
	SelfishMiningAttack createSelfishMiningAttack();

	/**
	 * Returns a new object of class '<em>Equal Fork Stubborn Attack</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Equal Fork Stubborn Attack</em>'.
	 * @generated
	 */
	EqualForkStubbornAttack createEqualForkStubbornAttack();

	/**
	 * Returns a new object of class '<em>Lead Stubborn Attack</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Lead Stubborn Attack</em>'.
	 * @generated
	 */
	LeadStubbornAttack createLeadStubbornAttack();

	/**
	 * Returns a new object of class '<em>Trail Stubborn Attack</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Trail Stubborn Attack</em>'.
	 * @generated
	 */
	TrailStubbornAttack createTrailStubbornAttack();

	/**
	 * Returns a new object of class '<em>Majority Attack</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Majority Attack</em>'.
	 * @generated
	 */
	MajorityAttack createMajorityAttack();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	AttackmodelPackage getAttackmodelPackage();

} //AttackmodelFactory
