/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.util;

import de.uka.ipd.sdq.identifier.Identifier;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.*;

import org.palladiosimulator.pcm.PCMBaseClass;
import org.palladiosimulator.pcm.PCMClass;

import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.core.entity.NamedElement;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage
 * @generated
 */
public class AttackmodelSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static AttackmodelPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttackmodelSwitch() {
		if (modelPackage == null) {
			modelPackage = AttackmodelPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
		case AttackmodelPackage.ATTACK: {
			Attack attack = (Attack) theEObject;
			T result = caseAttack(attack);
			if (result == null)
				result = caseEntity(attack);
			if (result == null)
				result = caseIdentifier(attack);
			if (result == null)
				result = caseNamedElement(attack);
			if (result == null)
				result = casePCMBaseClass(attack);
			if (result == null)
				result = casePCMClass(attack);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.ATTACK_SCENARIO: {
			AttackScenario attackScenario = (AttackScenario) theEObject;
			T result = caseAttackScenario(attackScenario);
			if (result == null)
				result = caseEntity(attackScenario);
			if (result == null)
				result = caseIdentifier(attackScenario);
			if (result == null)
				result = caseNamedElement(attackScenario);
			if (result == null)
				result = casePCMBaseClass(attackScenario);
			if (result == null)
				result = casePCMClass(attackScenario);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.ATTACKER_NODE: {
			AttackerNode attackerNode = (AttackerNode) theEObject;
			T result = caseAttackerNode(attackerNode);
			if (result == null)
				result = caseEntity(attackerNode);
			if (result == null)
				result = caseIdentifier(attackerNode);
			if (result == null)
				result = caseNamedElement(attackerNode);
			if (result == null)
				result = casePCMBaseClass(attackerNode);
			if (result == null)
				result = casePCMClass(attackerNode);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.ATTACK_RESULT: {
			AttackResult attackResult = (AttackResult) theEObject;
			T result = caseAttackResult(attackResult);
			if (result == null)
				result = caseEntity(attackResult);
			if (result == null)
				result = caseIdentifier(attackResult);
			if (result == null)
				result = caseNamedElement(attackResult);
			if (result == null)
				result = casePCMBaseClass(attackResult);
			if (result == null)
				result = casePCMClass(attackResult);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.ATTACK_PARAMETER: {
			AttackParameter attackParameter = (AttackParameter) theEObject;
			T result = caseAttackParameter(attackParameter);
			if (result == null)
				result = caseEntity(attackParameter);
			if (result == null)
				result = caseIdentifier(attackParameter);
			if (result == null)
				result = caseNamedElement(attackParameter);
			if (result == null)
				result = casePCMBaseClass(attackParameter);
			if (result == null)
				result = casePCMClass(attackParameter);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.FORK_DEPTH_ENTRY: {
			ForkDepthEntry forkDepthEntry = (ForkDepthEntry) theEObject;
			T result = caseForkDepthEntry(forkDepthEntry);
			if (result == null)
				result = caseEntity(forkDepthEntry);
			if (result == null)
				result = caseIdentifier(forkDepthEntry);
			if (result == null)
				result = caseNamedElement(forkDepthEntry);
			if (result == null)
				result = casePCMBaseClass(forkDepthEntry);
			if (result == null)
				result = casePCMClass(forkDepthEntry);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.RACE_ATTACK: {
			RaceAttack raceAttack = (RaceAttack) theEObject;
			T result = caseRaceAttack(raceAttack);
			if (result == null)
				result = caseAttack(raceAttack);
			if (result == null)
				result = caseEntity(raceAttack);
			if (result == null)
				result = caseIdentifier(raceAttack);
			if (result == null)
				result = caseNamedElement(raceAttack);
			if (result == null)
				result = casePCMBaseClass(raceAttack);
			if (result == null)
				result = casePCMClass(raceAttack);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.FINNEY_ATTACK: {
			FinneyAttack finneyAttack = (FinneyAttack) theEObject;
			T result = caseFinneyAttack(finneyAttack);
			if (result == null)
				result = caseAttack(finneyAttack);
			if (result == null)
				result = caseEntity(finneyAttack);
			if (result == null)
				result = caseIdentifier(finneyAttack);
			if (result == null)
				result = caseNamedElement(finneyAttack);
			if (result == null)
				result = casePCMBaseClass(finneyAttack);
			if (result == null)
				result = casePCMClass(finneyAttack);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.SELFISH_MINING_ATTACK: {
			SelfishMiningAttack selfishMiningAttack = (SelfishMiningAttack) theEObject;
			T result = caseSelfishMiningAttack(selfishMiningAttack);
			if (result == null)
				result = caseAttack(selfishMiningAttack);
			if (result == null)
				result = caseEntity(selfishMiningAttack);
			if (result == null)
				result = caseIdentifier(selfishMiningAttack);
			if (result == null)
				result = caseNamedElement(selfishMiningAttack);
			if (result == null)
				result = casePCMBaseClass(selfishMiningAttack);
			if (result == null)
				result = casePCMClass(selfishMiningAttack);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.STUBBORN_MINING_ATTACK: {
			StubbornMiningAttack stubbornMiningAttack = (StubbornMiningAttack) theEObject;
			T result = caseStubbornMiningAttack(stubbornMiningAttack);
			if (result == null)
				result = caseAttack(stubbornMiningAttack);
			if (result == null)
				result = caseEntity(stubbornMiningAttack);
			if (result == null)
				result = caseIdentifier(stubbornMiningAttack);
			if (result == null)
				result = caseNamedElement(stubbornMiningAttack);
			if (result == null)
				result = casePCMBaseClass(stubbornMiningAttack);
			if (result == null)
				result = casePCMClass(stubbornMiningAttack);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.EQUAL_FORK_STUBBORN_ATTACK: {
			EqualForkStubbornAttack equalForkStubbornAttack = (EqualForkStubbornAttack) theEObject;
			T result = caseEqualForkStubbornAttack(equalForkStubbornAttack);
			if (result == null)
				result = caseStubbornMiningAttack(equalForkStubbornAttack);
			if (result == null)
				result = caseAttack(equalForkStubbornAttack);
			if (result == null)
				result = caseEntity(equalForkStubbornAttack);
			if (result == null)
				result = caseIdentifier(equalForkStubbornAttack);
			if (result == null)
				result = caseNamedElement(equalForkStubbornAttack);
			if (result == null)
				result = casePCMBaseClass(equalForkStubbornAttack);
			if (result == null)
				result = casePCMClass(equalForkStubbornAttack);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.LEAD_STUBBORN_ATTACK: {
			LeadStubbornAttack leadStubbornAttack = (LeadStubbornAttack) theEObject;
			T result = caseLeadStubbornAttack(leadStubbornAttack);
			if (result == null)
				result = caseStubbornMiningAttack(leadStubbornAttack);
			if (result == null)
				result = caseAttack(leadStubbornAttack);
			if (result == null)
				result = caseEntity(leadStubbornAttack);
			if (result == null)
				result = caseIdentifier(leadStubbornAttack);
			if (result == null)
				result = caseNamedElement(leadStubbornAttack);
			if (result == null)
				result = casePCMBaseClass(leadStubbornAttack);
			if (result == null)
				result = casePCMClass(leadStubbornAttack);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.TRAIL_STUBBORN_ATTACK: {
			TrailStubbornAttack trailStubbornAttack = (TrailStubbornAttack) theEObject;
			T result = caseTrailStubbornAttack(trailStubbornAttack);
			if (result == null)
				result = caseStubbornMiningAttack(trailStubbornAttack);
			if (result == null)
				result = caseAttack(trailStubbornAttack);
			if (result == null)
				result = caseEntity(trailStubbornAttack);
			if (result == null)
				result = caseIdentifier(trailStubbornAttack);
			if (result == null)
				result = caseNamedElement(trailStubbornAttack);
			if (result == null)
				result = casePCMBaseClass(trailStubbornAttack);
			if (result == null)
				result = casePCMClass(trailStubbornAttack);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case AttackmodelPackage.MAJORITY_ATTACK: {
			MajorityAttack majorityAttack = (MajorityAttack) theEObject;
			T result = caseMajorityAttack(majorityAttack);
			if (result == null)
				result = caseAttack(majorityAttack);
			if (result == null)
				result = caseEntity(majorityAttack);
			if (result == null)
				result = caseIdentifier(majorityAttack);
			if (result == null)
				result = caseNamedElement(majorityAttack);
			if (result == null)
				result = casePCMBaseClass(majorityAttack);
			if (result == null)
				result = casePCMClass(majorityAttack);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		default:
			return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attack</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attack</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttack(Attack object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attack Scenario</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attack Scenario</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttackScenario(AttackScenario object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attacker Node</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attacker Node</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttackerNode(AttackerNode object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attack Result</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attack Result</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttackResult(AttackResult object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attack Parameter</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attack Parameter</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttackParameter(AttackParameter object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Fork Depth Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Fork Depth Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseForkDepthEntry(ForkDepthEntry object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Race Attack</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Race Attack</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRaceAttack(RaceAttack object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Finney Attack</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Finney Attack</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFinneyAttack(FinneyAttack object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Selfish Mining Attack</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Selfish Mining Attack</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSelfishMiningAttack(SelfishMiningAttack object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Stubborn Mining Attack</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Stubborn Mining Attack</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStubbornMiningAttack(StubbornMiningAttack object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Equal Fork Stubborn Attack</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Equal Fork Stubborn Attack</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEqualForkStubbornAttack(EqualForkStubbornAttack object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Lead Stubborn Attack</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Lead Stubborn Attack</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLeadStubbornAttack(LeadStubbornAttack object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Trail Stubborn Attack</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Trail Stubborn Attack</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTrailStubbornAttack(TrailStubbornAttack object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Majority Attack</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Majority Attack</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMajorityAttack(MajorityAttack object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Identifier</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Identifier</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIdentifier(Identifier object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>PCM Class</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>PCM Class</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePCMClass(PCMClass object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>PCM Base Class</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>PCM Base Class</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePCMBaseClass(PCMBaseClass object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Named Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Named Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNamedElement(NamedElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Entity</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Entity</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEntity(Entity object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //AttackmodelSwitch
