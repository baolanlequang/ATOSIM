/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack;

/**
 * This is the item provider adapter for a {@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.RaceAttack} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class RaceAttackItemProvider extends AttackItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RaceAttackItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addPropagationAdvantagePropertyDescriptor(object);
			addTransactionADelayPropertyDescriptor(object);
			addTransactionBAccelerationPropertyDescriptor(object);
			addFeeDifferenceThresholdPropertyDescriptor(object);
			addZeroConfAcceptanceRiskPropertyDescriptor(object);
			addObservedDoubleSpendsPropertyDescriptor(object);
			addAvgPropagationAdvMsPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Propagation Advantage feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addPropagationAdvantagePropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_RaceAttack_propagationAdvantage_feature"),
						getString("_UI_PropertyDescriptor_description", "_UI_RaceAttack_propagationAdvantage_feature",
								"_UI_RaceAttack_type"),
						AttackmodelPackage.Literals.RACE_ATTACK__PROPAGATION_ADVANTAGE, true, false, false,
						ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Transaction ADelay feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addTransactionADelayPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_RaceAttack_transactionADelay_feature"),
						getString("_UI_PropertyDescriptor_description", "_UI_RaceAttack_transactionADelay_feature",
								"_UI_RaceAttack_type"),
						AttackmodelPackage.Literals.RACE_ATTACK__TRANSACTION_ADELAY, true, false, false,
						ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Transaction BAcceleration feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addTransactionBAccelerationPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_RaceAttack_transactionBAcceleration_feature"),
				getString("_UI_PropertyDescriptor_description", "_UI_RaceAttack_transactionBAcceleration_feature",
						"_UI_RaceAttack_type"),
				AttackmodelPackage.Literals.RACE_ATTACK__TRANSACTION_BACCELERATION, true, false, false,
				ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Fee Difference Threshold feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addFeeDifferenceThresholdPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_RaceAttack_feeDifferenceThreshold_feature"),
				getString("_UI_PropertyDescriptor_description", "_UI_RaceAttack_feeDifferenceThreshold_feature",
						"_UI_RaceAttack_type"),
				AttackmodelPackage.Literals.RACE_ATTACK__FEE_DIFFERENCE_THRESHOLD, true, false, false,
				ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Zero Conf Acceptance Risk feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addZeroConfAcceptanceRiskPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_RaceAttack_zeroConfAcceptanceRisk_feature"),
				getString("_UI_PropertyDescriptor_description", "_UI_RaceAttack_zeroConfAcceptanceRisk_feature",
						"_UI_RaceAttack_type"),
				AttackmodelPackage.Literals.RACE_ATTACK__ZERO_CONF_ACCEPTANCE_RISK, true, false, false,
				ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Observed Double Spends feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addObservedDoubleSpendsPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_RaceAttack_observedDoubleSpends_feature"),
						getString("_UI_PropertyDescriptor_description", "_UI_RaceAttack_observedDoubleSpends_feature",
								"_UI_RaceAttack_type"),
						AttackmodelPackage.Literals.RACE_ATTACK__OBSERVED_DOUBLE_SPENDS, true, false, false,
						ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Avg Propagation Adv Ms feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addAvgPropagationAdvMsPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_RaceAttack_avgPropagationAdvMs_feature"),
						getString("_UI_PropertyDescriptor_description", "_UI_RaceAttack_avgPropagationAdvMs_feature",
								"_UI_RaceAttack_type"),
						AttackmodelPackage.Literals.RACE_ATTACK__AVG_PROPAGATION_ADV_MS, true, false, false,
						ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This returns RaceAttack.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/RaceAttack"));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected boolean shouldComposeCreationImage() {
		return true;
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((RaceAttack) object).getId();
		return label == null || label.length() == 0 ? getString("_UI_RaceAttack_type")
				: getString("_UI_RaceAttack_type") + " " + label;
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(RaceAttack.class)) {
		case AttackmodelPackage.RACE_ATTACK__PROPAGATION_ADVANTAGE:
		case AttackmodelPackage.RACE_ATTACK__TRANSACTION_ADELAY:
		case AttackmodelPackage.RACE_ATTACK__TRANSACTION_BACCELERATION:
		case AttackmodelPackage.RACE_ATTACK__FEE_DIFFERENCE_THRESHOLD:
		case AttackmodelPackage.RACE_ATTACK__ZERO_CONF_ACCEPTANCE_RISK:
		case AttackmodelPackage.RACE_ATTACK__OBSERVED_DOUBLE_SPENDS:
		case AttackmodelPackage.RACE_ATTACK__AVG_PROPAGATION_ADV_MS:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
			return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);
	}

}
