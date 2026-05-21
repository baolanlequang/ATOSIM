/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class AttackmodelFactoryImpl extends EFactoryImpl implements AttackmodelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static AttackmodelFactory init() {
		try {
			AttackmodelFactory theAttackmodelFactory = (AttackmodelFactory) EPackage.Registry.INSTANCE
					.getEFactory(AttackmodelPackage.eNS_URI);
			if (theAttackmodelFactory != null) {
				return theAttackmodelFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new AttackmodelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttackmodelFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case AttackmodelPackage.ATTACK_SCENARIO:
			return createAttackScenario();
		case AttackmodelPackage.ATTACKER_NODE:
			return createAttackerNode();
		case AttackmodelPackage.ATTACK_RESULT:
			return createAttackResult();
		case AttackmodelPackage.ATTACK_PARAMETER:
			return createAttackParameter();
		case AttackmodelPackage.FORK_DEPTH_ENTRY:
			return createForkDepthEntry();
		case AttackmodelPackage.RACE_ATTACK:
			return createRaceAttack();
		case AttackmodelPackage.FINNEY_ATTACK:
			return createFinneyAttack();
		case AttackmodelPackage.SELFISH_MINING_ATTACK:
			return createSelfishMiningAttack();
		case AttackmodelPackage.EQUAL_FORK_STUBBORN_ATTACK:
			return createEqualForkStubbornAttack();
		case AttackmodelPackage.LEAD_STUBBORN_ATTACK:
			return createLeadStubbornAttack();
		case AttackmodelPackage.TRAIL_STUBBORN_ATTACK:
			return createTrailStubbornAttack();
		case AttackmodelPackage.MAJORITY_ATTACK:
			return createMajorityAttack();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttackScenario createAttackScenario() {
		AttackScenarioImpl attackScenario = new AttackScenarioImpl();
		return attackScenario;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttackerNode createAttackerNode() {
		AttackerNodeImpl attackerNode = new AttackerNodeImpl();
		return attackerNode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttackResult createAttackResult() {
		AttackResultImpl attackResult = new AttackResultImpl();
		return attackResult;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttackParameter createAttackParameter() {
		AttackParameterImpl attackParameter = new AttackParameterImpl();
		return attackParameter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ForkDepthEntry createForkDepthEntry() {
		ForkDepthEntryImpl forkDepthEntry = new ForkDepthEntryImpl();
		return forkDepthEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public RaceAttack createRaceAttack() {
		RaceAttackImpl raceAttack = new RaceAttackImpl();
		return raceAttack;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FinneyAttack createFinneyAttack() {
		FinneyAttackImpl finneyAttack = new FinneyAttackImpl();
		return finneyAttack;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SelfishMiningAttack createSelfishMiningAttack() {
		SelfishMiningAttackImpl selfishMiningAttack = new SelfishMiningAttackImpl();
		return selfishMiningAttack;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EqualForkStubbornAttack createEqualForkStubbornAttack() {
		EqualForkStubbornAttackImpl equalForkStubbornAttack = new EqualForkStubbornAttackImpl();
		return equalForkStubbornAttack;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LeadStubbornAttack createLeadStubbornAttack() {
		LeadStubbornAttackImpl leadStubbornAttack = new LeadStubbornAttackImpl();
		return leadStubbornAttack;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TrailStubbornAttack createTrailStubbornAttack() {
		TrailStubbornAttackImpl trailStubbornAttack = new TrailStubbornAttackImpl();
		return trailStubbornAttack;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MajorityAttack createMajorityAttack() {
		MajorityAttackImpl majorityAttack = new MajorityAttackImpl();
		return majorityAttack;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttackmodelPackage getAttackmodelPackage() {
		return (AttackmodelPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static AttackmodelPackage getPackage() {
		return AttackmodelPackage.eINSTANCE;
	}

} //AttackmodelFactoryImpl
