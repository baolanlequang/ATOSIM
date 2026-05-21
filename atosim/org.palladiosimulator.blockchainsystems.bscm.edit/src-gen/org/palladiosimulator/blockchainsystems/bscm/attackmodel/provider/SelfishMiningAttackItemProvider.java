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
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack;

/**
 * This is the item provider adapter for a {@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.SelfishMiningAttack} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class SelfishMiningAttackItemProvider extends AttackItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SelfishMiningAttackItemProvider(AdapterFactory adapterFactory) {
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

			addPrivateChainLeadTargetPropertyDescriptor(object);
			addWithholdProbabilityPropertyDescriptor(object);
			addPublishOnCatchupPropertyDescriptor(object);
			addExpectedForksPerHourPropertyDescriptor(object);
			addMonitoredNodesPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Private Chain Lead Target feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addPrivateChainLeadTargetPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_SelfishMiningAttack_privateChainLeadTarget_feature"),
				getString("_UI_PropertyDescriptor_description",
						"_UI_SelfishMiningAttack_privateChainLeadTarget_feature", "_UI_SelfishMiningAttack_type"),
				AttackmodelPackage.Literals.SELFISH_MINING_ATTACK__PRIVATE_CHAIN_LEAD_TARGET, true, false, false,
				ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Withhold Probability feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addWithholdProbabilityPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_SelfishMiningAttack_withholdProbability_feature"),
						getString("_UI_PropertyDescriptor_description",
								"_UI_SelfishMiningAttack_withholdProbability_feature", "_UI_SelfishMiningAttack_type"),
						AttackmodelPackage.Literals.SELFISH_MINING_ATTACK__WITHHOLD_PROBABILITY, true, false, false,
						ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Publish On Catchup feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addPublishOnCatchupPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_SelfishMiningAttack_publishOnCatchup_feature"),
						getString("_UI_PropertyDescriptor_description",
								"_UI_SelfishMiningAttack_publishOnCatchup_feature", "_UI_SelfishMiningAttack_type"),
						AttackmodelPackage.Literals.SELFISH_MINING_ATTACK__PUBLISH_ON_CATCHUP, true, false, false,
						ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Expected Forks Per Hour feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addExpectedForksPerHourPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_SelfishMiningAttack_expectedForksPerHour_feature"),
						getString("_UI_PropertyDescriptor_description",
								"_UI_SelfishMiningAttack_expectedForksPerHour_feature", "_UI_SelfishMiningAttack_type"),
						AttackmodelPackage.Literals.SELFISH_MINING_ATTACK__EXPECTED_FORKS_PER_HOUR, true, false, false,
						ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Monitored Nodes feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addMonitoredNodesPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_SelfishMiningAttack_monitoredNodes_feature"),
						getString("_UI_PropertyDescriptor_description",
								"_UI_SelfishMiningAttack_monitoredNodes_feature", "_UI_SelfishMiningAttack_type"),
						AttackmodelPackage.Literals.SELFISH_MINING_ATTACK__MONITORED_NODES, true, false, true, null,
						null, null));
	}

	/**
	 * This returns SelfishMiningAttack.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/SelfishMiningAttack"));
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
		String label = ((SelfishMiningAttack) object).getId();
		return label == null || label.length() == 0 ? getString("_UI_SelfishMiningAttack_type")
				: getString("_UI_SelfishMiningAttack_type") + " " + label;
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

		switch (notification.getFeatureID(SelfishMiningAttack.class)) {
		case AttackmodelPackage.SELFISH_MINING_ATTACK__PRIVATE_CHAIN_LEAD_TARGET:
		case AttackmodelPackage.SELFISH_MINING_ATTACK__WITHHOLD_PROBABILITY:
		case AttackmodelPackage.SELFISH_MINING_ATTACK__PUBLISH_ON_CATCHUP:
		case AttackmodelPackage.SELFISH_MINING_ATTACK__EXPECTED_FORKS_PER_HOUR:
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
