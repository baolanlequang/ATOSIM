/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.ForkDepthEntry;

import org.palladiosimulator.pcm.core.entity.impl.EntityImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attack Result</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl#isSuccess <em>Success</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl#getDuration <em>Duration</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl#getAffectedBlocks <em>Affected Blocks</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl#getSuccessProbabilityObserved <em>Success Probability Observed</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl#getAvgForkRate <em>Avg Fork Rate</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl#getStaleBlockRate <em>Stale Block Rate</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl#getDoubleSpendCount <em>Double Spend Count</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl#getTimeToTakeover <em>Time To Takeover</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl#getForkDepthDistribution <em>Fork Depth Distribution</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackResultImpl#getAttackerRevenueRatio <em>Attacker Revenue Ratio</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AttackResultImpl extends EntityImpl implements AttackResult {
	/**
	 * The default value of the '{@link #isSuccess() <em>Success</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSuccess()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SUCCESS_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isSuccess() <em>Success</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSuccess()
	 * @generated
	 * @ordered
	 */
	protected boolean success = SUCCESS_EDEFAULT;

	/**
	 * The default value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected static final double DURATION_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getDuration() <em>Duration</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDuration()
	 * @generated
	 * @ordered
	 */
	protected double duration = DURATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getAffectedBlocks() <em>Affected Blocks</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAffectedBlocks()
	 * @generated
	 * @ordered
	 */
	protected static final int AFFECTED_BLOCKS_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getAffectedBlocks() <em>Affected Blocks</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAffectedBlocks()
	 * @generated
	 * @ordered
	 */
	protected int affectedBlocks = AFFECTED_BLOCKS_EDEFAULT;

	/**
	 * The default value of the '{@link #getSuccessProbabilityObserved() <em>Success Probability Observed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuccessProbabilityObserved()
	 * @generated
	 * @ordered
	 */
	protected static final double SUCCESS_PROBABILITY_OBSERVED_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getSuccessProbabilityObserved() <em>Success Probability Observed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSuccessProbabilityObserved()
	 * @generated
	 * @ordered
	 */
	protected double successProbabilityObserved = SUCCESS_PROBABILITY_OBSERVED_EDEFAULT;

	/**
	 * The default value of the '{@link #getAvgForkRate() <em>Avg Fork Rate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAvgForkRate()
	 * @generated
	 * @ordered
	 */
	protected static final double AVG_FORK_RATE_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getAvgForkRate() <em>Avg Fork Rate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAvgForkRate()
	 * @generated
	 * @ordered
	 */
	protected double avgForkRate = AVG_FORK_RATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getStaleBlockRate() <em>Stale Block Rate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStaleBlockRate()
	 * @generated
	 * @ordered
	 */
	protected static final double STALE_BLOCK_RATE_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getStaleBlockRate() <em>Stale Block Rate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStaleBlockRate()
	 * @generated
	 * @ordered
	 */
	protected double staleBlockRate = STALE_BLOCK_RATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDoubleSpendCount() <em>Double Spend Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDoubleSpendCount()
	 * @generated
	 * @ordered
	 */
	protected static final int DOUBLE_SPEND_COUNT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getDoubleSpendCount() <em>Double Spend Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDoubleSpendCount()
	 * @generated
	 * @ordered
	 */
	protected int doubleSpendCount = DOUBLE_SPEND_COUNT_EDEFAULT;

	/**
	 * The default value of the '{@link #getTimeToTakeover() <em>Time To Takeover</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimeToTakeover()
	 * @generated
	 * @ordered
	 */
	protected static final double TIME_TO_TAKEOVER_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getTimeToTakeover() <em>Time To Takeover</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTimeToTakeover()
	 * @generated
	 * @ordered
	 */
	protected double timeToTakeover = TIME_TO_TAKEOVER_EDEFAULT;

	/**
	 * The cached value of the '{@link #getForkDepthDistribution() <em>Fork Depth Distribution</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getForkDepthDistribution()
	 * @generated
	 * @ordered
	 */
	protected EList<ForkDepthEntry> forkDepthDistribution;

	/**
	 * The default value of the '{@link #getAttackerRevenueRatio() <em>Attacker Revenue Ratio</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttackerRevenueRatio()
	 * @generated
	 * @ordered
	 */
	protected static final double ATTACKER_REVENUE_RATIO_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getAttackerRevenueRatio() <em>Attacker Revenue Ratio</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAttackerRevenueRatio()
	 * @generated
	 * @ordered
	 */
	protected double attackerRevenueRatio = ATTACKER_REVENUE_RATIO_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AttackResultImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AttackmodelPackage.Literals.ATTACK_RESULT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSuccess() {
		return success;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSuccess(boolean newSuccess) {
		boolean oldSuccess = success;
		success = newSuccess;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK_RESULT__SUCCESS, oldSuccess,
					success));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getDuration() {
		return duration;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDuration(double newDuration) {
		double oldDuration = duration;
		duration = newDuration;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK_RESULT__DURATION,
					oldDuration, duration));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getAffectedBlocks() {
		return affectedBlocks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAffectedBlocks(int newAffectedBlocks) {
		int oldAffectedBlocks = affectedBlocks;
		affectedBlocks = newAffectedBlocks;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK_RESULT__AFFECTED_BLOCKS,
					oldAffectedBlocks, affectedBlocks));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getSuccessProbabilityObserved() {
		return successProbabilityObserved;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSuccessProbabilityObserved(double newSuccessProbabilityObserved) {
		double oldSuccessProbabilityObserved = successProbabilityObserved;
		successProbabilityObserved = newSuccessProbabilityObserved;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.ATTACK_RESULT__SUCCESS_PROBABILITY_OBSERVED, oldSuccessProbabilityObserved,
					successProbabilityObserved));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getAvgForkRate() {
		return avgForkRate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAvgForkRate(double newAvgForkRate) {
		double oldAvgForkRate = avgForkRate;
		avgForkRate = newAvgForkRate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK_RESULT__AVG_FORK_RATE,
					oldAvgForkRate, avgForkRate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getStaleBlockRate() {
		return staleBlockRate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStaleBlockRate(double newStaleBlockRate) {
		double oldStaleBlockRate = staleBlockRate;
		staleBlockRate = newStaleBlockRate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK_RESULT__STALE_BLOCK_RATE,
					oldStaleBlockRate, staleBlockRate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getDoubleSpendCount() {
		return doubleSpendCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDoubleSpendCount(int newDoubleSpendCount) {
		int oldDoubleSpendCount = doubleSpendCount;
		doubleSpendCount = newDoubleSpendCount;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK_RESULT__DOUBLE_SPEND_COUNT,
					oldDoubleSpendCount, doubleSpendCount));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getTimeToTakeover() {
		return timeToTakeover;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTimeToTakeover(double newTimeToTakeover) {
		double oldTimeToTakeover = timeToTakeover;
		timeToTakeover = newTimeToTakeover;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK_RESULT__TIME_TO_TAKEOVER,
					oldTimeToTakeover, timeToTakeover));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ForkDepthEntry> getForkDepthDistribution() {
		if (forkDepthDistribution == null) {
			forkDepthDistribution = new EObjectContainmentEList<ForkDepthEntry>(ForkDepthEntry.class, this,
					AttackmodelPackage.ATTACK_RESULT__FORK_DEPTH_DISTRIBUTION);
		}
		return forkDepthDistribution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getAttackerRevenueRatio() {
		return attackerRevenueRatio;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAttackerRevenueRatio(double newAttackerRevenueRatio) {
		double oldAttackerRevenueRatio = attackerRevenueRatio;
		attackerRevenueRatio = newAttackerRevenueRatio;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.ATTACK_RESULT__ATTACKER_REVENUE_RATIO, oldAttackerRevenueRatio,
					attackerRevenueRatio));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case AttackmodelPackage.ATTACK_RESULT__FORK_DEPTH_DISTRIBUTION:
			return ((InternalEList<?>) getForkDepthDistribution()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case AttackmodelPackage.ATTACK_RESULT__SUCCESS:
			return isSuccess();
		case AttackmodelPackage.ATTACK_RESULT__DURATION:
			return getDuration();
		case AttackmodelPackage.ATTACK_RESULT__AFFECTED_BLOCKS:
			return getAffectedBlocks();
		case AttackmodelPackage.ATTACK_RESULT__SUCCESS_PROBABILITY_OBSERVED:
			return getSuccessProbabilityObserved();
		case AttackmodelPackage.ATTACK_RESULT__AVG_FORK_RATE:
			return getAvgForkRate();
		case AttackmodelPackage.ATTACK_RESULT__STALE_BLOCK_RATE:
			return getStaleBlockRate();
		case AttackmodelPackage.ATTACK_RESULT__DOUBLE_SPEND_COUNT:
			return getDoubleSpendCount();
		case AttackmodelPackage.ATTACK_RESULT__TIME_TO_TAKEOVER:
			return getTimeToTakeover();
		case AttackmodelPackage.ATTACK_RESULT__FORK_DEPTH_DISTRIBUTION:
			return getForkDepthDistribution();
		case AttackmodelPackage.ATTACK_RESULT__ATTACKER_REVENUE_RATIO:
			return getAttackerRevenueRatio();
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
		case AttackmodelPackage.ATTACK_RESULT__SUCCESS:
			setSuccess((Boolean) newValue);
			return;
		case AttackmodelPackage.ATTACK_RESULT__DURATION:
			setDuration((Double) newValue);
			return;
		case AttackmodelPackage.ATTACK_RESULT__AFFECTED_BLOCKS:
			setAffectedBlocks((Integer) newValue);
			return;
		case AttackmodelPackage.ATTACK_RESULT__SUCCESS_PROBABILITY_OBSERVED:
			setSuccessProbabilityObserved((Double) newValue);
			return;
		case AttackmodelPackage.ATTACK_RESULT__AVG_FORK_RATE:
			setAvgForkRate((Double) newValue);
			return;
		case AttackmodelPackage.ATTACK_RESULT__STALE_BLOCK_RATE:
			setStaleBlockRate((Double) newValue);
			return;
		case AttackmodelPackage.ATTACK_RESULT__DOUBLE_SPEND_COUNT:
			setDoubleSpendCount((Integer) newValue);
			return;
		case AttackmodelPackage.ATTACK_RESULT__TIME_TO_TAKEOVER:
			setTimeToTakeover((Double) newValue);
			return;
		case AttackmodelPackage.ATTACK_RESULT__FORK_DEPTH_DISTRIBUTION:
			getForkDepthDistribution().clear();
			getForkDepthDistribution().addAll((Collection<? extends ForkDepthEntry>) newValue);
			return;
		case AttackmodelPackage.ATTACK_RESULT__ATTACKER_REVENUE_RATIO:
			setAttackerRevenueRatio((Double) newValue);
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
		case AttackmodelPackage.ATTACK_RESULT__SUCCESS:
			setSuccess(SUCCESS_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACK_RESULT__DURATION:
			setDuration(DURATION_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACK_RESULT__AFFECTED_BLOCKS:
			setAffectedBlocks(AFFECTED_BLOCKS_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACK_RESULT__SUCCESS_PROBABILITY_OBSERVED:
			setSuccessProbabilityObserved(SUCCESS_PROBABILITY_OBSERVED_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACK_RESULT__AVG_FORK_RATE:
			setAvgForkRate(AVG_FORK_RATE_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACK_RESULT__STALE_BLOCK_RATE:
			setStaleBlockRate(STALE_BLOCK_RATE_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACK_RESULT__DOUBLE_SPEND_COUNT:
			setDoubleSpendCount(DOUBLE_SPEND_COUNT_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACK_RESULT__TIME_TO_TAKEOVER:
			setTimeToTakeover(TIME_TO_TAKEOVER_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACK_RESULT__FORK_DEPTH_DISTRIBUTION:
			getForkDepthDistribution().clear();
			return;
		case AttackmodelPackage.ATTACK_RESULT__ATTACKER_REVENUE_RATIO:
			setAttackerRevenueRatio(ATTACKER_REVENUE_RATIO_EDEFAULT);
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
		case AttackmodelPackage.ATTACK_RESULT__SUCCESS:
			return success != SUCCESS_EDEFAULT;
		case AttackmodelPackage.ATTACK_RESULT__DURATION:
			return duration != DURATION_EDEFAULT;
		case AttackmodelPackage.ATTACK_RESULT__AFFECTED_BLOCKS:
			return affectedBlocks != AFFECTED_BLOCKS_EDEFAULT;
		case AttackmodelPackage.ATTACK_RESULT__SUCCESS_PROBABILITY_OBSERVED:
			return successProbabilityObserved != SUCCESS_PROBABILITY_OBSERVED_EDEFAULT;
		case AttackmodelPackage.ATTACK_RESULT__AVG_FORK_RATE:
			return avgForkRate != AVG_FORK_RATE_EDEFAULT;
		case AttackmodelPackage.ATTACK_RESULT__STALE_BLOCK_RATE:
			return staleBlockRate != STALE_BLOCK_RATE_EDEFAULT;
		case AttackmodelPackage.ATTACK_RESULT__DOUBLE_SPEND_COUNT:
			return doubleSpendCount != DOUBLE_SPEND_COUNT_EDEFAULT;
		case AttackmodelPackage.ATTACK_RESULT__TIME_TO_TAKEOVER:
			return timeToTakeover != TIME_TO_TAKEOVER_EDEFAULT;
		case AttackmodelPackage.ATTACK_RESULT__FORK_DEPTH_DISTRIBUTION:
			return forkDepthDistribution != null && !forkDepthDistribution.isEmpty();
		case AttackmodelPackage.ATTACK_RESULT__ATTACKER_REVENUE_RATIO:
			return attackerRevenueRatio != ATTACKER_REVENUE_RATIO_EDEFAULT;
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
		result.append(" (success: ");
		result.append(success);
		result.append(", duration: ");
		result.append(duration);
		result.append(", affectedBlocks: ");
		result.append(affectedBlocks);
		result.append(", successProbabilityObserved: ");
		result.append(successProbabilityObserved);
		result.append(", avgForkRate: ");
		result.append(avgForkRate);
		result.append(", staleBlockRate: ");
		result.append(staleBlockRate);
		result.append(", doubleSpendCount: ");
		result.append(doubleSpendCount);
		result.append(", timeToTakeover: ");
		result.append(timeToTakeover);
		result.append(", attackerRevenueRatio: ");
		result.append(attackerRevenueRatio);
		result.append(')');
		return result.toString();
	}

} //AttackResultImpl
