package org.palladiosimulator.blockchainsystems.core.geography;

public interface GeographicalRegionsResolver {
    GeographicalRegions resolveGeographicalRegions();
    GeographicalRegion getGeographicalRegionOfNode(String nodeId);
}
