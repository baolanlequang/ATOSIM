package org.palladiosimulator.blockchainsystems.core.geography;

import java.util.Objects;

public final class GeographicalRegion {

    private final String _region;

    public GeographicalRegion(String region) {
        _region = region;
    }

    public String getRegion() {
        return _region;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeographicalRegion)) return false;
        GeographicalRegion that = (GeographicalRegion) o;
        return Objects.equals(_region, that._region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_region);
    }

    @Override
    public String toString() {
        return "GeographicalRegion(" + _region + ")";
    }
}
