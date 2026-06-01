package org.palladiosimulator.blockchainsystems.atosim;

import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainSystem;
import org.palladiosimulator.blockchainsystems.bscm.blockchainsystem.BlockchainsystemPackage;
import org.palladiosimulator.blockchainsystems.bscm.nodeallocation.NodeallocationPackage;
import org.palladiosimulator.blockchainsystems.bscm.p2pnetwork.P2pnetworkPackage;
import org.palladiosimulator.blockchainsystems.bscm.blockchainsystemComponentRepository.BlockchainsystemComponentRepositoryPackage;

import com.google.common.io.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class BlockchainSystemModelLoader {

    public BlockchainSystem load(String uri) {
        Path modelPath = Paths.get(uri).toAbsolutePath();
        Path folderPath = modelPath.getParent();
        String folderName = folderPath.getFileName().toString();
        String fileName = modelPath.getFileName().toString();
        String baseName = Files.getNameWithoutExtension(fileName);

        ResourceSet resourceSet = new ResourceSetImpl();
        XMIResourceFactoryImpl xmiFactory = new XMIResourceFactoryImpl();

        for (String ext : new String[]{
                "blockchainsystem", "p2pnetwork", "bscmrepository",
                "nodeallocation", "geographicalregions", "linkallocation", "transactions"}) {
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(ext, xmiFactory);
        }

        resourceSet.getPackageRegistry().put(BlockchainsystemPackage.eNS_URI, BlockchainsystemPackage.eINSTANCE);
        resourceSet.getPackageRegistry().put(P2pnetworkPackage.eNS_URI, P2pnetworkPackage.eINSTANCE);
        resourceSet.getPackageRegistry().put(NodeallocationPackage.eNS_URI, NodeallocationPackage.eINSTANCE);
        resourceSet.getPackageRegistry().put(BlockchainsystemComponentRepositoryPackage.eNS_URI,
                BlockchainsystemComponentRepositoryPackage.eINSTANCE);

        String[] relativeNames = {
                fileName,
                baseName + ".p2pnetwork",
                baseName + ".nodeallocation",
                baseName + ".bscmrepository",
                baseName + ".geographicalregions",
                baseName + ".linkallocation",
                baseName + ".transactions"
        };

        for (String name : relativeNames) {
            resourceSet.getURIConverter().getURIMap().put(
                    createPluginURI(folderName, name),
                    URI.createFileURI(folderPath.resolve(name).toString()));
        }

        for (String name : relativeNames) {
            resourceSet.getResource(createPluginURI(folderName, name), true);
        }

        ArrayList<Resource> currentResources;
        do {
            currentResources = new ArrayList<>(resourceSet.getResources());
            for (Resource r : currentResources) {
                EcoreUtil.resolveAll(r);
            }
        } while (currentResources.size() != resourceSet.getResources().size());

        return (BlockchainSystem) resourceSet.getResources().get(0).getContents().get(0);
    }

    public BlockchainSystem load(String uri, Map<String, String> configuration) {
        return load(uri);
    }

    private URI createPluginURI(String folder, String relativePath) {
        String path = Paths.get(
                "org.palladiosimulator.blockchainsystems.atosim/testmodels/" + folder,
                relativePath).toString();
        return URI.createPlatformPluginURI(path, false);
    }
}
