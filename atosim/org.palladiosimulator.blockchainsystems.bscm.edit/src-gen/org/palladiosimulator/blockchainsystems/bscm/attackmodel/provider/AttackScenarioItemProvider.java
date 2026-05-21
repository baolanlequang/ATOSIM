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
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelFactory;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;

import org.palladiosimulator.blockchainsystems.bscm.nodeallocation.provider.BscmEditPlugin;

import org.palladiosimulator.pcm.core.entity.provider.EntityItemProvider;

/**
 * This is the item provider adapter for a {@link org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class AttackScenarioItemProvider extends EntityItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AttackScenarioItemProvider(AdapterFactory adapterFactory) {
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

			addBlockchainSystemPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Blockchain System feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addBlockchainSystemPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(
				((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
				getString("_UI_AttackScenario_blockchainSystem_feature"),
				getString("_UI_PropertyDescriptor_description", "_UI_AttackScenario_blockchainSystem_feature",
						"_UI_AttackScenario_type"),
				AttackmodelPackage.Literals.ATTACK_SCENARIO__BLOCKCHAIN_SYSTEM, true, false, true, null, null, null));
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
			childrenFeatures.add(AttackmodelPackage.Literals.ATTACK_SCENARIO__ATTACK);
			childrenFeatures.add(AttackmodelPackage.Literals.ATTACK_SCENARIO__ATTACKERS);
			childrenFeatures.add(AttackmodelPackage.Literals.ATTACK_SCENARIO__RESULT);
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
	 * This returns AttackScenario.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/AttackScenario"));
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
		String label = ((AttackScenario) object).getId();
		return label == null || label.length() == 0 ? getString("_UI_AttackScenario_type")
				: getString("_UI_AttackScenario_type") + " " + label;
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

		switch (notification.getFeatureID(AttackScenario.class)) {
		case AttackmodelPackage.ATTACK_SCENARIO__ATTACK:
		case AttackmodelPackage.ATTACK_SCENARIO__ATTACKERS:
		case AttackmodelPackage.ATTACK_SCENARIO__RESULT:
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

		newChildDescriptors.add(createChildParameter(AttackmodelPackage.Literals.ATTACK_SCENARIO__ATTACK,
				AttackmodelFactory.eINSTANCE.createRaceAttack()));

		newChildDescriptors.add(createChildParameter(AttackmodelPackage.Literals.ATTACK_SCENARIO__ATTACK,
				AttackmodelFactory.eINSTANCE.createFinneyAttack()));

		newChildDescriptors.add(createChildParameter(AttackmodelPackage.Literals.ATTACK_SCENARIO__ATTACK,
				AttackmodelFactory.eINSTANCE.createSelfishMiningAttack()));

		newChildDescriptors.add(createChildParameter(AttackmodelPackage.Literals.ATTACK_SCENARIO__ATTACK,
				AttackmodelFactory.eINSTANCE.createEqualForkStubbornAttack()));

		newChildDescriptors.add(createChildParameter(AttackmodelPackage.Literals.ATTACK_SCENARIO__ATTACK,
				AttackmodelFactory.eINSTANCE.createLeadStubbornAttack()));

		newChildDescriptors.add(createChildParameter(AttackmodelPackage.Literals.ATTACK_SCENARIO__ATTACK,
				AttackmodelFactory.eINSTANCE.createTrailStubbornAttack()));

		newChildDescriptors.add(createChildParameter(AttackmodelPackage.Literals.ATTACK_SCENARIO__ATTACK,
				AttackmodelFactory.eINSTANCE.createMajorityAttack()));

		newChildDescriptors.add(createChildParameter(AttackmodelPackage.Literals.ATTACK_SCENARIO__ATTACKERS,
				AttackmodelFactory.eINSTANCE.createAttackerNode()));

		newChildDescriptors.add(createChildParameter(AttackmodelPackage.Literals.ATTACK_SCENARIO__RESULT,
				AttackmodelFactory.eINSTANCE.createAttackResult()));
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
