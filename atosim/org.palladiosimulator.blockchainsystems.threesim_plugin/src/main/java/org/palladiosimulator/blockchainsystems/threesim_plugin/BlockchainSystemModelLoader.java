package org.palladiosimulator.blockchainsystems.threesim_plugin;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackScenario;
import org.palladiosimulator.blockchainsystems.bscm.attackmodel.AttackmodelPackage;
import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainSystem;
import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainsystemPackage;
import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.impl.BlockchainSystemImpl;

public class BlockchainSystemModelLoader {

    public static class LoadedModels {
        private final ResourceSet resourceSet;
        private final BlockchainSystem blockchainSystem;
        private final AttackScenario attackScenario;

        public LoadedModels(ResourceSet resourceSet, BlockchainSystem blockchainSystem,
                AttackScenario attackScenario) {
            this.resourceSet = resourceSet;
            this.blockchainSystem = blockchainSystem;
            this.attackScenario = attackScenario;
        }

        public ResourceSet getResourceSet() { return resourceSet; }
        public BlockchainSystem getBlockchainSystem() { return blockchainSystem; }
        public AttackScenario getAttackScenario() { return attackScenario; }
    }

    public BlockchainSystem load(String uri) {
        return load(uri, null).getBlockchainSystem();
    }

    public LoadedModels load(String blockchainSystemUri, String attackModelUri) {
        BlockchainsystemPackage.eINSTANCE.eClass();
        AttackmodelPackage.eINSTANCE.eClass();

        Resource.Factory.Registry registry = Resource.Factory.Registry.INSTANCE;
        java.util.Map<String, Object> m = registry.getExtensionToFactoryMap();
        m.put("blockchainsystem", new XMIResourceFactoryImpl());
        m.put("p2pnetwork", new XMIResourceFactoryImpl());
        m.put("nodeallocation", new XMIResourceFactoryImpl());
        m.put("blockchainsystemComponentRepository", new XMIResourceFactoryImpl());
        m.put("nodesystem", new XMIResourceFactoryImpl());
        m.put("nodeenvironment", new XMIResourceFactoryImpl());
        m.put("geographcalregions", new XMIResourceFactoryImpl());
        m.put("linkallocation", new XMIResourceFactoryImpl());
        m.put("transactions", new XMIResourceFactoryImpl());
        m.put("attackmodel", new XMIResourceFactoryImpl());

        ResourceSet resSet = new ResourceSetImpl();

        Resource bsResource = resSet.getResource(URI.createURI(blockchainSystemUri), true);

        AttackScenario attackScenario = null;
        if (attackModelUri != null && !attackModelUri.isBlank()) {
            Resource attackResource = resSet.getResource(URI.createURI(attackModelUri), true);
            if (!attackResource.getContents().isEmpty() &&
                    attackResource.getContents().get(0) instanceof AttackScenario s) {
                attackScenario = s;
            }
        }

        EcoreUtil.resolveAll(resSet);

        BlockchainSystem blockchainSystem = (BlockchainSystemImpl) bsResource.getContents().get(0);

        return new LoadedModels(resSet, blockchainSystem, attackScenario);
    }
}
