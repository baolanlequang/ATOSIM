package org.palladiosimulator.blockchainsystems.threesim_plugin

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage
import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainSystem
import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainsystemPackage
import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.impl.BlockchainSystemImpl

/**
 * Loads blockchain system models and (optionally) attack models
 * into a shared EMF ResourceSet so cross-references resolve.
 *
 * @author Yannik Sproll, Davis Riedel
 */
class BlockchainSystemModelLoader {

  data class LoadedModels(
    val resourceSet: ResourceSet,
    val blockchainSystem: BlockchainSystem,
    val attackScenario: AttackScenario?
  )

  /**
   * Backwards-compatible loader: only blockchain system.
   */
  fun load(uri: String): BlockchainSystem {
    return load(uri, null).blockchainSystem
  }

  /**
   * Extended loader: blockchain system + optional attack model.
   */
  fun load(blockchainSystemUri: String, attackModelUri: String?): LoadedModels {
    // Initialize metamodels
    BlockchainsystemPackage.eINSTANCE.eClass()
    AttackmodelPackage.eINSTANCE.eClass()

    // Register resource factories
    with(Resource.Factory.Registry.INSTANCE.extensionToFactoryMap) {
      put("blockchainsystem", XMIResourceFactoryImpl())
      put("p2pnetwork", XMIResourceFactoryImpl())
      put("nodeallocation", XMIResourceFactoryImpl())
      put("blockchainsystemComponentRepository", XMIResourceFactoryImpl())
      put("nodesystem", XMIResourceFactoryImpl())
      put("nodeenvironment", XMIResourceFactoryImpl())
      put("geographcalregions", XMIResourceFactoryImpl())
      put("linkallocation", XMIResourceFactoryImpl())
      put("transactions", XMIResourceFactoryImpl())
      put("attackmodel", XMIResourceFactoryImpl())
    }

    val resSet: ResourceSet = ResourceSetImpl()

    // Load blockchain system
    val bsResource = resSet.getResource(
      URI.createURI(blockchainSystemUri),
      true
    )

    // Load attack model if provided
    val attackScenario: AttackScenario? =
      if (!attackModelUri.isNullOrBlank()) {
        val attackResource = resSet.getResource(
          URI.createURI(attackModelUri),
          true
        )
        attackResource.contents.firstOrNull() as? AttackScenario
      } else {
        null
      }

    // Resolve cross references across BOTH models
    EcoreUtil.resolveAll(resSet)

    val blockchainSystem =
      bsResource.contents.first() as BlockchainSystemImpl

    return LoadedModels(
      resourceSet = resSet,
      blockchainSystem = blockchainSystem,
      attackScenario = attackScenario
    )
  }
}
