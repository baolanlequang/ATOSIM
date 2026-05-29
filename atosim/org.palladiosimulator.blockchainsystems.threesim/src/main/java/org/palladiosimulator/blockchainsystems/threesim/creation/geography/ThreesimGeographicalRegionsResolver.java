package org.palladiosimulator.blockchainsystems.threesim.creation.geography;

import org.palladiosimulator.blockchainsystems.bscm.geographicalregions.GeographicalRegionsSpecification;
import org.palladiosimulator.blockchainsystems.core.geography.GeographicalRegion;
import org.palladiosimulator.blockchainsystems.core.geography.GeographicalRegions;
import org.palladiosimulator.blockchainsystems.core.geography.GeographicalRegionsResolver;
import org.palladiosimulator.blockchainsystems.threesim.creation.abstractions.NodeAllocationResolver;

import java.util.HashSet;
import java.util.Set;

public class ThreesimGeographicalRegionsResolver implements GeographicalRegionsResolver {

    private final NodeAllocationResolver _nodeAllocationResolver;
    private final GeographicalRegions _geoRegions;

    public ThreesimGeographicalRegionsResolver(GeographicalRegionsSpecification geoRegionsSpec,
            NodeAllocationResolver nodeAllocationResolver) {
        _nodeAllocationResolver = nodeAllocationResolver;

        Set<GeographicalRegion> regions = new HashSet<>();
        for (org.palladiosimulator.blockchainsystems.bscm.geographicalregions.GeographicalRegion r : geoRegionsSpec.getRegions()) {
            regions.add(new GeographicalRegion(r.getRegionName()));
        }
        _geoRegions = new GeographicalRegions(regions);
    }

    @Override
    public GeographicalRegions resolveGeographicalRegions() {
        return _geoRegions;
    }

    @Override
    public GeographicalRegion getGeographicalRegionOfNode(String nodeId) {
        var allocation = _nodeAllocationResolver.getNodeAllocation(nodeId);
        if (allocation == null || allocation.getGeographicalRegion() == null)
            throw new IllegalArgumentException(
                    "Node with ID '" + nodeId + "' does not have a geographical region defined in the node allocation.");
        String regionName = allocation.getGeographicalRegion().getRegionName();
        if (_geoRegions.getRegionByName(regionName) == null)
            throw new IllegalArgumentException(
                    "Geographical region with name '" + regionName + "' not found in the geographical regions specification.");
        return new GeographicalRegion(regionName);
    }
}
