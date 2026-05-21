/**
 */
package org.palladiosimulator.blockchainsystems.bscm.attackmodel.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelFactory;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;

import org.palladiosimulator.blockchainsystems.bscm.nodeallocation.provider.BscmEditPlugin;

import org.palladiosimulator.pcm.core.entity.provider.EntityItemProvider;

/**
 * This is the item provider adapter for a {@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackResult} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class AttackResultItemProvider extends EntityItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttackResultItemProvider(AdapterFactory adapterFactory) {
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

			addSuccessPropertyDescriptor(object);
			addDurationPropertyDescriptor(object);
			addAffectedBlocksPropertyDescriptor(object);
			addSuccessProbabilityObservedPropertyDescriptor(object);
			addAvgForkRatePropertyDescriptor(object);
			addStaleBlockRatePropertyDescriptor(object);
			addDoubleSpendCountPropertyDescriptor(object);
			addTimeToTakeoverPropertyDescriptor(object);
			addAttackerRevenueRatioPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Success feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addSuccessPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_AttackResult_success_feature"),
						getString("_UI_PropertyDescriptor_description", "_UI_AttackResult_success_feature",
								"_UI_AttackResult_type"),
						AttackmodelPackage.Literals.ATTACK_RESULT__SUCCESS, true, false, false,
						ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Duration feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addDurationPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_AttackResult_duration_feature"),
						getString("_UI_PropertyDescriptor_description", "_UI_AttackResult_duration_feature",
								"_UI_AttackResult_type"),
						AttackmodelPackage.Literals.ATTACK_RESULT__DURATION, true, false, false,
						ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Affected Blocks feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addAffectedBlocksPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_AttackResult_affectedBlocks_feature"),
						getString("_UI_PropertyDescriptor_description", "_UI_AttackResult_affectedBlocks_feature",
								"_UI_AttackResult_type"),
						AttackmodelPackage.Literals.ATTACK_RESULT__AFFECTED_BLOCKS, true, false, false,
						ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Success Probability Observed feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addSuccessProbabilityObservedPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_AttackResult_successProbabilityObserved_feature"),
						getString("_UI_PropertyDescriptor_description",
								"_UI_AttackResult_successProbabilityObserved_feature", "_UI_AttackResult_type"),
						AttackmodelPackage.Literals.ATTACK_RESULT__SUCCESS_PROBABILITY_OBSERVED, true, false, false,
						ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Avg Fork Rate feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addAvgForkRatePropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_AttackResult_avgForkRate_feature"),
						getString("_UI_PropertyDescriptor_description", "_UI_AttackResult_avgForkRate_feature",
								"_UI_AttackResult_type"),
						AttackmodelPackage.Literals.ATTACK_RESULT__AVG_FORK_RATE, true, false, false,
						ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Stale Block Rate feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addStaleBlockRatePropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_AttackResult_staleBlockRate_feature"),
						getString("_UI_PropertyDescriptor_description", "_UI_AttackResult_staleBlockRate_feature",
								"_UI_AttackResult_type"),
						AttackmodelPackage.Literals.ATTACK_RESULT__STALE_BLOCK_RATE, true, false, false,
						ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Double Spend Count feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addDoubleSpendCountPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_AttackResult_doubleSpendCount_feature"),
						getString("_UI_PropertyDescriptor_description", "_UI_AttackResult_doubleSpendCount_feature",
								"_UI_AttackResult_type"),
						AttackmodelPackage.Literals.ATTACK_RESULT__DOUBLE_SPEND_COUNT, true, false, false,
						ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Time To Takeover feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addTimeToTakeoverPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_AttackResult_timeToTakeover_feature"),
						getString("_UI_PropertyDescriptor_description", "_UI_AttackResult_timeToTakeover_feature",
								"_UI_AttackResult_type"),
						AttackmodelPackage.Literals.ATTACK_RESULT__TIME_TO_TAKEOVER, true, false, false,
						ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Attacker Revenue Ratio feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addAttackerRevenueRatioPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_AttackResult_attackerRevenueRatio_feature"),
				getString("_UI_PropertyDescriptor_description", "_UI_AttackResult_attackerRevenueRatio_feature",
						"_UI_AttackResult_type"),
				AttackmodelPackage.Literals.ATTACK_RESULT__ATTACKER_REVENUE_RATIO, true, false, false,
				ItemPropertyDescriptor.REAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(AttackmodelPackage.Literals.ATTACK_RESULT__FORK_DEPTH_DISTRIBUTION);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns AttackResult.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/AttackResult"));
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
		String label = ((AttackResult) object).getId();
		return label == null || label.length() == 0 ? getString("_UI_AttackResult_type")
				: getString("_UI_AttackResult_type") + " " + label;
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

		switch (notification.getFeatureID(AttackResult.class)) {
		case AttackmodelPackage.ATTACK_RESULT__SUCCESS:
		case AttackmodelPackage.ATTACK_RESULT__DURATION:
		case AttackmodelPackage.ATTACK_RESULT__AFFECTED_BLOCKS:
		case AttackmodelPackage.ATTACK_RESULT__SUCCESS_PROBABILITY_OBSERVED:
		case AttackmodelPackage.ATTACK_RESULT__AVG_FORK_RATE:
		case AttackmodelPackage.ATTACK_RESULT__STALE_BLOCK_RATE:
		case AttackmodelPackage.ATTACK_RESULT__DOUBLE_SPEND_COUNT:
		case AttackmodelPackage.ATTACK_RESULT__TIME_TO_TAKEOVER:
		case AttackmodelPackage.ATTACK_RESULT__ATTACKER_REVENUE_RATIO:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
			return;
		case AttackmodelPackage.ATTACK_RESULT__FORK_DEPTH_DISTRIBUTION:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
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

		newChildDescriptors.add(createChildParameter(AttackmodelPackage.Literals.ATTACK_RESULT__FORK_DEPTH_DISTRIBUTION,
				AttackmodelFactory.eINSTANCE.createForkDepthEntry()));
	}

	/**
	 * Return the resource locator for this item provider's resources.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return BscmEditPlugin.INSTANCE;
	}

}
