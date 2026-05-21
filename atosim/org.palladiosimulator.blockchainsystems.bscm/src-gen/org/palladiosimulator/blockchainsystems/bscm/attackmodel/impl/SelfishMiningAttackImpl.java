/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackerNode;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Selfish Mining Attack</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.SelfishMiningAttackImpl#getPrivateChainLeadTarget <em>Private Chain Lead Target</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.SelfishMiningAttackImpl#getWithholdProbability <em>Withhold Probability</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.SelfishMiningAttackImpl#isPublishOnCatchup <em>Publish On Catchup</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.SelfishMiningAttackImpl#getExpectedForksPerHour <em>Expected Forks Per Hour</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.SelfishMiningAttackImpl#getMonitoredNodes <em>Monitored Nodes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SelfishMiningAttackImpl extends AttackImpl implements SelfishMiningAttack {
	/**
	 * The default value of the '{@link #getPrivateChainLeadTarget() <em>Private Chain Lead Target</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrivateChainLeadTarget()
	 * @generated
	 * @ordered
	 */
	protected static final int PRIVATE_CHAIN_LEAD_TARGET_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getPrivateChainLeadTarget() <em>Private Chain Lead Target</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPrivateChainLeadTarget()
	 * @generated
	 * @ordered
	 */
	protected int privateChainLeadTarget = PRIVATE_CHAIN_LEAD_TARGET_EDEFAULT;

	/**
	 * The default value of the '{@link #getWithholdProbability() <em>Withhold Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWithholdProbability()
	 * @generated
	 * @ordered
	 */
	protected static final double WITHHOLD_PROBABILITY_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getWithholdProbability() <em>Withhold Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWithholdProbability()
	 * @generated
	 * @ordered
	 */
	protected double withholdProbability = WITHHOLD_PROBABILITY_EDEFAULT;

	/**
	 * The default value of the '{@link #isPublishOnCatchup() <em>Publish On Catchup</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPublishOnCatchup()
	 * @generated
	 * @ordered
	 */
	protected static final boolean PUBLISH_ON_CATCHUP_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isPublishOnCatchup() <em>Publish On Catchup</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPublishOnCatchup()
	 * @generated
	 * @ordered
	 */
	protected boolean publishOnCatchup = PUBLISH_ON_CATCHUP_EDEFAULT;

	/**
	 * The default value of the '{@link #getExpectedForksPerHour() <em>Expected Forks Per Hour</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpectedForksPerHour()
	 * @generated
	 * @ordered
	 */
	protected static final double EXPECTED_FORKS_PER_HOUR_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getExpectedForksPerHour() <em>Expected Forks Per Hour</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpectedForksPerHour()
	 * @generated
	 * @ordered
	 */
	protected double expectedForksPerHour = EXPECTED_FORKS_PER_HOUR_EDEFAULT;

	/**
	 * The cached value of the '{@link #getMonitoredNodes() <em>Monitored Nodes</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMonitoredNodes()
	 * @generated
	 * @ordered
	 */
	protected EList<AttackerNode> monitoredNodes;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SelfishMiningAttackImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AttackmodelPackage.Literals.SELFISH_MINING_ATTACK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getPrivateChainLeadTarget() {
		return privateChainLeadTarget;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPrivateChainLeadTarget(int newPrivateChainLeadTarget) {
		int oldPrivateChainLeadTarget = privateChainLeadTarget;
		privateChainLeadTarget = newPrivateChainLeadTarget;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.SELFISH_MINING_ATTACK__PRIVATE_CHAIN_LEAD_TARGET, oldPrivateChainLeadTarget,
					privateChainLeadTarget));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getWithholdProbability() {
		return withholdProbability;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWithholdProbability(double newWithholdProbability) {
		double oldWithholdProbability = withholdProbability;
		withholdProbability = newWithholdProbability;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.SELFISH_MINING_ATTACK__WITHHOLD_PROBABILITY, oldWithholdProbability,
					withholdProbability));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isPublishOnCatchup() {
		return publishOnCatchup;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPublishOnCatchup(boolean newPublishOnCatchup) {
		boolean oldPublishOnCatchup = publishOnCatchup;
		publishOnCatchup = newPublishOnCatchup;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.SELFISH_MINING_ATTACK__PUBLISH_ON_CATCHUP, oldPublishOnCatchup,
					publishOnCatchup));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getExpectedForksPerHour() {
		return expectedForksPerHour;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExpectedForksPerHour(double newExpectedForksPerHour) {
		double oldExpectedForksPerHour = expectedForksPerHour;
		expectedForksPerHour = newExpectedForksPerHour;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.SELFISH_MINING_ATTACK__EXPECTED_FORKS_PER_HOUR, oldExpectedForksPerHour,
					expectedForksPerHour));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<AttackerNode> getMonitoredNodes() {
		if (monitoredNodes == null) {
			monitoredNodes = new EObjectResolvingEList<AttackerNode>(AttackerNode.class, this,
					AttackmodelPackage.SELFISH_MINING_ATTACK__MONITORED_NODES);
		}
		return monitoredNodes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case AttackmodelPackage.SELFISH_MINING_ATTACK__PRIVATE_CHAIN_LEAD_TARGET:
			return getPrivateChainLeadTarget();
		case AttackmodelPackage.SELFISH_MINING_ATTACK__WITHHOLD_PROBABILITY:
			return getWithholdProbability();
		case AttackmodelPackage.SELFISH_MINING_ATTACK__PUBLISH_ON_CATCHUP:
			return isPublishOnCatchup();
		case AttackmodelPackage.SELFISH_MINING_ATTACK__EXPECTED_FORKS_PER_HOUR:
			return getExpectedForksPerHour();
		case AttackmodelPackage.SELFISH_MINING_ATTACK__MONITORED_NODES:
			return getMonitoredNodes();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case AttackmodelPackage.SELFISH_MINING_ATTACK__PRIVATE_CHAIN_LEAD_TARGET:
			setPrivateChainLeadTarget((Integer) newValue);
			return;
		case AttackmodelPackage.SELFISH_MINING_ATTACK__WITHHOLD_PROBABILITY:
			setWithholdProbability((Double) newValue);
			return;
		case AttackmodelPackage.SELFISH_MINING_ATTACK__PUBLISH_ON_CATCHUP:
			setPublishOnCatchup((Boolean) newValue);
			return;
		case AttackmodelPackage.SELFISH_MINING_ATTACK__EXPECTED_FORKS_PER_HOUR:
			setExpectedForksPerHour((Double) newValue);
			return;
		case AttackmodelPackage.SELFISH_MINING_ATTACK__MONITORED_NODES:
			getMonitoredNodes().clear();
			getMonitoredNodes().addAll((Collection<? extends AttackerNode>) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case AttackmodelPackage.SELFISH_MINING_ATTACK__PRIVATE_CHAIN_LEAD_TARGET:
			setPrivateChainLeadTarget(PRIVATE_CHAIN_LEAD_TARGET_EDEFAULT);
			return;
		case AttackmodelPackage.SELFISH_MINING_ATTACK__WITHHOLD_PROBABILITY:
			setWithholdProbability(WITHHOLD_PROBABILITY_EDEFAULT);
			return;
		case AttackmodelPackage.SELFISH_MINING_ATTACK__PUBLISH_ON_CATCHUP:
			setPublishOnCatchup(PUBLISH_ON_CATCHUP_EDEFAULT);
			return;
		case AttackmodelPackage.SELFISH_MINING_ATTACK__EXPECTED_FORKS_PER_HOUR:
			setExpectedForksPerHour(EXPECTED_FORKS_PER_HOUR_EDEFAULT);
			return;
		case AttackmodelPackage.SELFISH_MINING_ATTACK__MONITORED_NODES:
			getMonitoredNodes().clear();
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case AttackmodelPackage.SELFISH_MINING_ATTACK__PRIVATE_CHAIN_LEAD_TARGET:
			return privateChainLeadTarget != PRIVATE_CHAIN_LEAD_TARGET_EDEFAULT;
		case AttackmodelPackage.SELFISH_MINING_ATTACK__WITHHOLD_PROBABILITY:
			return withholdProbability != WITHHOLD_PROBABILITY_EDEFAULT;
		case AttackmodelPackage.SELFISH_MINING_ATTACK__PUBLISH_ON_CATCHUP:
			return publishOnCatchup != PUBLISH_ON_CATCHUP_EDEFAULT;
		case AttackmodelPackage.SELFISH_MINING_ATTACK__EXPECTED_FORKS_PER_HOUR:
			return expectedForksPerHour != EXPECTED_FORKS_PER_HOUR_EDEFAULT;
		case AttackmodelPackage.SELFISH_MINING_ATTACK__MONITORED_NODES:
			return monitoredNodes != null && !monitoredNodes.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (privateChainLeadTarget: ");
		result.append(privateChainLeadTarget);
		result.append(", withholdProbability: ");
		result.append(withholdProbability);
		result.append(", publishOnCatchup: ");
		result.append(publishOnCatchup);
		result.append(", expectedForksPerHour: ");
		result.append(expectedForksPerHour);
		result.append(')');
		return result.toString();
	}

} //SelfishMiningAttackImpl
