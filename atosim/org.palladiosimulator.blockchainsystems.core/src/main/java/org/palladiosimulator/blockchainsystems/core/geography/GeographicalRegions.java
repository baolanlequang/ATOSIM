package org.palladiosimulator.blockchainsystems.core.geography;

import java.util.Set;

public class GeographicalRegions {

    private final Set<GeographicalRegion> _regions;

    public GeographicalRegions(Set<GeographicalRegion> regions) {
        _regions = regions;
    }

    public GeographicalRegion getRegionByName(String regionName) {
        return _regions.stream()
                .filter(r -> r.getRegion().equals(regionName))
                .findFirst()
                .orElse(null);
    }

    public int getNumberOfRegions() {
        return _regions.size();
    }
}
