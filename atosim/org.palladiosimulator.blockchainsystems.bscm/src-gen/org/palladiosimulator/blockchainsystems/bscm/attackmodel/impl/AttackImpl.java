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

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.Attack;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackParameter;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;

import org.palladiosimulator.pcm.core.entity.impl.EntityImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attack</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackImpl#getRequiredHashPower <em>Required Hash Power</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackImpl#isDependsOnNetworkDelay <em>Depends On Network Delay</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackImpl#getBaselineSuccessProbability <em>Baseline Success Probability</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.impl.AttackImpl#getGamma <em>Gamma</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class AttackImpl extends EntityImpl implements Attack {
	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected String type = TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getRequiredHashPower() <em>Required Hash Power</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequiredHashPower()
	 * @generated
	 * @ordered
	 */
	protected static final double REQUIRED_HASH_POWER_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getRequiredHashPower() <em>Required Hash Power</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRequiredHashPower()
	 * @generated
	 * @ordered
	 */
	protected double requiredHashPower = REQUIRED_HASH_POWER_EDEFAULT;

	/**
	 * The default value of the '{@link #isDependsOnNetworkDelay() <em>Depends On Network Delay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDependsOnNetworkDelay()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DEPENDS_ON_NETWORK_DELAY_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isDependsOnNetworkDelay() <em>Depends On Network Delay</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDependsOnNetworkDelay()
	 * @generated
	 * @ordered
	 */
	protected boolean dependsOnNetworkDelay = DEPENDS_ON_NETWORK_DELAY_EDEFAULT;

	/**
	 * The default value of the '{@link #getBaselineSuccessProbability() <em>Baseline Success Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBaselineSuccessProbability()
	 * @generated
	 * @ordered
	 */
	protected static final double BASELINE_SUCCESS_PROBABILITY_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getBaselineSuccessProbability() <em>Baseline Success Probability</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBaselineSuccessProbability()
	 * @generated
	 * @ordered
	 */
	protected double baselineSuccessProbability = BASELINE_SUCCESS_PROBABILITY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getParameters() <em>Parameters</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameters()
	 * @generated
	 * @ordered
	 */
	protected EList<AttackParameter> parameters;

	/**
	 * The default value of the '{@link #getGamma() <em>Gamma</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGamma()
	 * @generated
	 * @ordered
	 */
	protected static final double GAMMA_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getGamma() <em>Gamma</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGamma()
	 * @generated
	 * @ordered
	 */
	protected double gamma = GAMMA_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AttackImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return AttackmodelPackage.Literals.ATTACK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setType(String newType) {
		String oldType = type;
		type = newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getRequiredHashPower() {
		return requiredHashPower;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRequiredHashPower(double newRequiredHashPower) {
		double oldRequiredHashPower = requiredHashPower;
		requiredHashPower = newRequiredHashPower;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK__REQUIRED_HASH_POWER,
					oldRequiredHashPower, requiredHashPower));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isDependsOnNetworkDelay() {
		return dependsOnNetworkDelay;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDependsOnNetworkDelay(boolean newDependsOnNetworkDelay) {
		boolean oldDependsOnNetworkDelay = dependsOnNetworkDelay;
		dependsOnNetworkDelay = newDependsOnNetworkDelay;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK__DEPENDS_ON_NETWORK_DELAY,
					oldDependsOnNetworkDelay, dependsOnNetworkDelay));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getBaselineSuccessProbability() {
		return baselineSuccessProbability;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBaselineSuccessProbability(double newBaselineSuccessProbability) {
		double oldBaselineSuccessProbability = baselineSuccessProbability;
		baselineSuccessProbability = newBaselineSuccessProbability;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					AttackmodelPackage.ATTACK__BASELINE_SUCCESS_PROBABILITY, oldBaselineSuccessProbability,
					baselineSuccessProbability));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<AttackParameter> getParameters() {
		if (parameters == null) {
			parameters = new EObjectContainmentEList<AttackParameter>(AttackParameter.class, this,
					AttackmodelPackage.ATTACK__PARAMETERS);
		}
		return parameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getGamma() {
		return gamma;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setGamma(double newGamma) {
		double oldGamma = gamma;
		gamma = newGamma;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, AttackmodelPackage.ATTACK__GAMMA, oldGamma, gamma));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case AttackmodelPackage.ATTACK__PARAMETERS:
			return ((InternalEList<?>) getParameters()).basicRemove(otherEnd, msgs);
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
		case AttackmodelPackage.ATTACK__TYPE:
			return getType();
		case AttackmodelPackage.ATTACK__REQUIRED_HASH_POWER:
			return getRequiredHashPower();
		case AttackmodelPackage.ATTACK__DEPENDS_ON_NETWORK_DELAY:
			return isDependsOnNetworkDelay();
		case AttackmodelPackage.ATTACK__BASELINE_SUCCESS_PROBABILITY:
			return getBaselineSuccessProbability();
		case AttackmodelPackage.ATTACK__PARAMETERS:
			return getParameters();
		case AttackmodelPackage.ATTACK__GAMMA:
			return getGamma();
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
		case AttackmodelPackage.ATTACK__TYPE:
			setType((String) newValue);
			return;
		case AttackmodelPackage.ATTACK__REQUIRED_HASH_POWER:
			setRequiredHashPower((Double) newValue);
			return;
		case AttackmodelPackage.ATTACK__DEPENDS_ON_NETWORK_DELAY:
			setDependsOnNetworkDelay((Boolean) newValue);
			return;
		case AttackmodelPackage.ATTACK__BASELINE_SUCCESS_PROBABILITY:
			setBaselineSuccessProbability((Double) newValue);
			return;
		case AttackmodelPackage.ATTACK__PARAMETERS:
			getParameters().clear();
			getParameters().addAll((Collection<? extends AttackParameter>) newValue);
			return;
		case AttackmodelPackage.ATTACK__GAMMA:
			setGamma((Double) newValue);
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
		case AttackmodelPackage.ATTACK__TYPE:
			setType(TYPE_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACK__REQUIRED_HASH_POWER:
			setRequiredHashPower(REQUIRED_HASH_POWER_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACK__DEPENDS_ON_NETWORK_DELAY:
			setDependsOnNetworkDelay(DEPENDS_ON_NETWORK_DELAY_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACK__BASELINE_SUCCESS_PROBABILITY:
			setBaselineSuccessProbability(BASELINE_SUCCESS_PROBABILITY_EDEFAULT);
			return;
		case AttackmodelPackage.ATTACK__PARAMETERS:
			getParameters().clear();
			return;
		case AttackmodelPackage.ATTACK__GAMMA:
			setGamma(GAMMA_EDEFAULT);
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
		case AttackmodelPackage.ATTACK__TYPE:
			return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
		case AttackmodelPackage.ATTACK__REQUIRED_HASH_POWER:
			return requiredHashPower != REQUIRED_HASH_POWER_EDEFAULT;
		case AttackmodelPackage.ATTACK__DEPENDS_ON_NETWORK_DELAY:
			return dependsOnNetworkDelay != DEPENDS_ON_NETWORK_DELAY_EDEFAULT;
		case AttackmodelPackage.ATTACK__BASELINE_SUCCESS_PROBABILITY:
			return baselineSuccessProbability != BASELINE_SUCCESS_PROBABILITY_EDEFAULT;
		case AttackmodelPackage.ATTACK__PARAMETERS:
			return parameters != null && !parameters.isEmpty();
		case AttackmodelPackage.ATTACK__GAMMA:
			return gamma != GAMMA_EDEFAULT;
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
		result.append(" (type: ");
		result.append(type);
		result.append(", requiredHashPower: ");
		result.append(requiredHashPower);
		result.append(", dependsOnNetworkDelay: ");
		result.append(dependsOnNetworkDelay);
		result.append(", baselineSuccessProbability: ");
		result.append(baselineSuccessProbability);
		result.append(", gamma: ");
		result.append(gamma);
		result.append(')');
		return result.toString();
	}

} //AttackImpl
