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
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack;

/**
 * This is the item provider adapter for a {@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.FinneyAttack} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class FinneyAttackItemProvider extends AttackItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FinneyAttackItemProvider(AdapterFactory adapterFactory) {
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

			addNumberOfPreminedBlocksPropertyDescriptor(object);
			addWithholdTimePropertyDescriptor(object);
			addMerchantAcceptsZeroConfPropertyDescriptor(object);
			addDoubleSpendSuccessCountPropertyDescriptor(object);
			addObservedTimeToReleaseMsPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Number Of Premined Blocks feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addNumberOfPreminedBlocksPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_FinneyAttack_numberOfPreminedBlocks_feature"),
				getString("_UI_PropertyDescriptor_description", "_UI_FinneyAttack_numberOfPreminedBlocks_feature",
						"_UI_FinneyAttack_type"),
				AttackmodelPackage.Literals.FINNEY_ATTACK__NUMBER_OF_PREMINED_BLOCKS, true, false, false,
				ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Withhold Time feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addWithholdTimePropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_FinneyAttack_withholdTime_feature"),
						getString("_UI_PropertyDescriptor_description", "_UI_FinneyAttack_withholdTime_feature",
								"_UI_FinneyAttack_type"),
						AttackmodelPackage.Literals.FINNEY_ATTACK__WITHHOLD_TIME, true, false, false,
						ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Merchant Accepts Zero Conf feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addMerchantAcceptsZeroConfPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_FinneyAttack_merchantAcceptsZeroConf_feature"),
				getString("_UI_PropertyDescriptor_description", "_UI_FinneyAttack_merchantAcceptsZeroConf_feature",
						"_UI_FinneyAttack_type"),
				AttackmodelPackage.Literals.FINNEY_ATTACK__MERCHANT_ACCEPTS_ZERO_CONF, true, false, false,
				ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Double Spend Success Count feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addDoubleSpendSuccessCountPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_FinneyAttack_doubleSpendSuccessCount_feature"),
				getString("_UI_PropertyDescriptor_description", "_UI_FinneyAttack_doubleSpendSuccessCount_feature",
						"_UI_FinneyAttack_type"),
				AttackmodelPackage.Literals.FINNEY_ATTACK__DOUBLE_SPEND_SUCCESS_COUNT, true, false, false,
				ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Observed Time To Release Ms feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addObservedTimeToReleaseMsPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(),
						getResourceLocator(), getString("_UI_FinneyAttack_observedTimeToReleaseMs_feature"),
						getString("_UI_PropertyDescriptor_description",
								"_UI_FinneyAttack_observedTimeToReleaseMs_feature", "_UI_FinneyAttack_type"),
						AttackmodelPackage.Literals.FINNEY_ATTACK__OBSERVED_TIME_TO_RELEASE_MS, true, false, false,
						ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE, null, null));
	}

	/**
	 * This returns FinneyAttack.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/FinneyAttack"));
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
		String label = ((FinneyAttack) object).getId();
		return label == null || label.length() == 0 ? getString("_UI_FinneyAttack_type")
				: getString("_UI_FinneyAttack_type") + " " + label;
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

		switch (notification.getFeatureID(FinneyAttack.class)) {
		case AttackmodelPackage.FINNEY_ATTACK__NUMBER_OF_PREMINED_BLOCKS:
		case AttackmodelPackage.FINNEY_ATTACK__WITHHOLD_TIME:
		case AttackmodelPackage.FINNEY_ATTACK__MERCHANT_ACCEPTS_ZERO_CONF:
		case AttackmodelPackage.FINNEY_ATTACK__DOUBLE_SPEND_SUCCESS_COUNT:
		case AttackmodelPackage.FINNEY_ATTACK__OBSERVED_TIME_TO_RELEASE_MS:
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
