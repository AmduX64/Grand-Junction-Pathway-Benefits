/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.routing.util;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;

/**
 * Specifies the settings for cycletouring/trekking
 * <p>
 *
 * @author ratrun
 * @author Peter Karich
 */
public class BikeFlagEncoder extends BikeCommonFlagEncoder {
    public BikeFlagEncoder() {
        this(4, 1, 0);
    }

    public BikeFlagEncoder(String propertiesString) {
        this(new PMap(propertiesString));
    }

    public BikeFlagEncoder(PMap properties) {
        this((int) properties.getLong("speed_bits", 4),
                properties.getLong("speed_factor", 1),
                properties.getBool("turn_costs", false) ? 1 : 0);
        this.properties = properties;
        this.setBlockFords(properties.getBool("block_fords", true));
    }

    public BikeFlagEncoder(int speedBits, double speedFactor, int maxTurnCosts) {
        super(speedBits, speedFactor, maxTurnCosts);
        addPushingSection("path");
        addPushingSection("footway");
        addPushingSection("pedestrian");
        addPushingSection("steps");

        avoidHighwayTags.add("trunk");
        avoidHighwayTags.add("trunk_link");
        avoidHighwayTags.add("primary");
        avoidHighwayTags.add("primary_link");
        avoidHighwayTags.add("secondary");
        avoidHighwayTags.add("secondary_link");

        // preferHighwayTags.add("road");
        preferHighwayTags.add("service");
        preferHighwayTags.add("tertiary");
        preferHighwayTags.add("tertiary_link");
        preferHighwayTags.add("residential");
        preferHighwayTags.add("unclassified");

        absoluteBarriers.add("kissing_gate");
        setSpecificClassBicycle("touring");

        init();
    }


    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    public String getSurfaceAsString(long flags) {
        return null;
    }

    @Override
    public String getHighwayAsString(EdgeIteratorState edge) {
        return null;
    }

    @Override
    public int getSurface(EdgeIteratorState edge) {
        return 0;
    }

    @Override
    public int getSurface(long flags) {
        return 0;
    }

    @Override
    public String getSurfaceAsString(EdgeIteratorState edge) {
        return null;
    }

    @Override
    public String getStressAsString(long flags) {
        return null;
    }

    @Override
    public int getStress(EdgeIteratorState edge) {
        return 0;
    }

    @Override
    public int getStress(long flags) {
        return 0;
    }

    @Override
    public String getStressAsString(EdgeIteratorState edge) {
        return null;
    }

    @Override
    boolean isPushingSection(ReaderWay way) {
        String highway = way.getTag("highway");
        String trackType = way.getTag("tracktype");
        return super.isPushingSection(way) || "track".equals(highway) && trackType != null && !"grade1".equals(trackType);
    }

    @Override
    public String toString() {
        return "bike";
    }
}
