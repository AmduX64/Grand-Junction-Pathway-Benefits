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
import com.graphhopper.util.PointList;

import java.util.*;

import static com.graphhopper.routing.util.PriorityCode.*;
import static com.graphhopper.util.Helper.keepIn;

/**
 * Specifies the settings for race biking
 * <p>
 *
 * @author ratrun
 * @author Peter Karich
 */
public class RacingBikeFlagEncoder extends BikeCommonFlagEncoder {
    protected final Map<String, Integer> surfaceMap = new HashMap<>();
    protected final Map<String, Integer> stressMap = new HashMap<>();
    protected final Map<String, Integer> highwayMap = new HashMap<>();
    private EncodedDoubleValue reverseSpeedEncoder;
    private EncodedValue surfaceEncoder;
    private EncodedValue stressEncoder;
    private EncodedValue highwayEncoder;
    public RacingBikeFlagEncoder() {
        this(4, 2, 0);
    }

    public RacingBikeFlagEncoder(PMap properties) {
        this(
                (int) properties.getLong("speed_bits", 4),
                properties.getDouble("speed_factor", 2),
                properties.getBool("turn_costs", false) ? 1 : 0
        );
        this.properties = properties;
        this.setBlockFords(properties.getBool("block_fords", true));
    }

    public RacingBikeFlagEncoder(String propertiesStr) {
        this(new PMap(propertiesStr));
    }

    public RacingBikeFlagEncoder(int speedBits, double speedFactor, int maxTurnCosts) {
        super(speedBits, speedFactor, maxTurnCosts);
        List<String> highwayList = Arrays.asList(
                /* reserve index=0 for unset roads (not accessible) */
                "_default",
                "motorway", "motorway_link", "motorroad", "trunk", "trunk_link",
                "primary", "primary_link", "secondary", "secondary_link", "tertiary", "tertiary_link",
                "unclassified", "residential", "living_street", "service", "road", "track",
                "forestry", "cycleway", "steps", "path", "footway", "pedestrian",
                "ferry", "shuttle_train");
        int counter = 0;
        for (String hw : highwayList) {
            highwayMap.put(hw, counter++);
        }
        List<String> surfaceList = Arrays.asList("_default", "asphalt", "unpaved", "paved", "gravel",
                "ground", "dirt", "grass", "concrete", "paving_stones", "sand", "compacted", "cobblestone", "mud", "ice");
        counter = 0;
        for (String s : surfaceList) {
            surfaceMap.put(s, counter++);
        }

        List<String> stressList = Arrays.asList("_default", "low", "high");
        counter = 0;
        for (String srs : stressList) {
            stressMap.put(srs, counter++);
        }

        preferHighwayTags.add("road");
        preferHighwayTags.add("secondary");
        preferHighwayTags.add("secondary_link");
        preferHighwayTags.add("tertiary");
        preferHighwayTags.add("tertiary_link");
        preferHighwayTags.add("residential");

        setTrackTypeSpeed("grade1", 20); // paved
        setTrackTypeSpeed("grade2", 10); // now unpaved ...
        setTrackTypeSpeed("grade3", PUSHING_SECTION_SPEED);
        setTrackTypeSpeed("grade4", PUSHING_SECTION_SPEED);
        setTrackTypeSpeed("grade5", PUSHING_SECTION_SPEED);

        setSurfaceSpeed("paved", 20);
        setSurfaceSpeed("asphalt", 20);
        setSurfaceSpeed("cobblestone", 10);
        setSurfaceSpeed("cobblestone:flattened", 10);
        setSurfaceSpeed("sett", 10);
        setSurfaceSpeed("concrete", 20);
        setSurfaceSpeed("concrete:lanes", 16);
        setSurfaceSpeed("concrete:plates", 16);
        setSurfaceSpeed("paving_stones", 10);
        setSurfaceSpeed("paving_stones:30", 10);
        setSurfaceSpeed("unpaved", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("compacted", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("dirt", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("earth", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("fine_gravel", PUSHING_SECTION_SPEED);
        setSurfaceSpeed("grass", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("grass_paver", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("gravel", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("ground", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("ice", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("metal", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("mud", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("pebblestone", PUSHING_SECTION_SPEED);
        setSurfaceSpeed("salt", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("sand", PUSHING_SECTION_SPEED / 2);
        setSurfaceSpeed("wood", PUSHING_SECTION_SPEED / 2);

        setHighwaySpeed("cycleway", 18);
        setHighwaySpeed("path", 8);
        setHighwaySpeed("footway", 6);
        setHighwaySpeed("pedestrian", 6);
        setHighwaySpeed("road", 12);
        setHighwaySpeed("track", PUSHING_SECTION_SPEED / 2); // assume unpaved
        setHighwaySpeed("service", 12);
        setHighwaySpeed("unclassified", 16);
        setHighwaySpeed("residential", 16);

        setHighwaySpeed("trunk", 20);
        setHighwaySpeed("trunk_link", 20);
        setHighwaySpeed("primary", 20);
        setHighwaySpeed("primary_link", 20);
        setHighwaySpeed("secondary", 20);
        setHighwaySpeed("secondary_link", 20);
        setHighwaySpeed("tertiary", 20);
        setHighwaySpeed("tertiary_link", 20);

        addPushingSection("path");
        addPushingSection("footway");
        addPushingSection("pedestrian");
        addPushingSection("steps");

        setCyclingNetworkPreference("icn", PriorityCode.BEST.getValue());
        setCyclingNetworkPreference("ncn", PriorityCode.BEST.getValue());
        setCyclingNetworkPreference("rcn", PriorityCode.VERY_NICE.getValue());
        setCyclingNetworkPreference("lcn", PriorityCode.UNCHANGED.getValue());
        setCyclingNetworkPreference("mtb", PriorityCode.UNCHANGED.getValue());

        absoluteBarriers.add("kissing_gate");

        setAvoidSpeedLimit(81);
        setSpecificClassBicycle("roadcycling");

        init();
    }

    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    public int defineWayBits(int index, int shift) {
        shift = super.defineWayBits(index, shift);
        reverseSpeedEncoder = new EncodedDoubleValue("Reverse Speed", shift, speedBits, speedFactor,
                getHighwaySpeed("cycleway"), maxPossibleSpeed);
        shift += reverseSpeedEncoder.getBits();
        highwayEncoder = new EncodedValue("highway", shift, 5, 1, 0, highwayMap.size(), true);
        shift += highwayEncoder.getBits();
        surfaceEncoder = new EncodedValue("surface", shift, 4, 1, 0, surfaceMap.size(), true);
        shift += surfaceEncoder.getBits();
        stressEncoder = new EncodedValue("stress_level", shift, 4, 1, 0, stressMap.size(), true);
        shift += surfaceEncoder.getBits();
        return shift;
    }

    public int getHighway(EdgeIteratorState edge) {
        return (int) highwayEncoder.getValue(edge.getFlags());
    }

    /**
     * Do not use within weighting as this is suboptimal from performance point of view.
     */

    @Override
    public String getHighwayAsString(EdgeIteratorState edge) {
        int val = getHighway(edge);
        for (Map.Entry<String, Integer> e : highwayMap.entrySet()) {
            if (e.getValue() == val)
                return e.getKey();
        }
        return null;
    }
    int getHighwayValue(ReaderWay way) {
        String highwayValue = way.getTag("highway");
        Integer hwValue = highwayMap.get(highwayValue);
        if (way.hasTag("impassable", "yes") || way.hasTag("status", "impassable"))
            hwValue = 0;

        if (hwValue == null) {
            hwValue = 0;
            if (way.hasTag("route", ferries)) {
                String motorcarTag = way.getTag("motorcar");
                if (motorcarTag == null)
                    motorcarTag = way.getTag("motor_vehicle");

                if (motorcarTag == null && !way.hasTag("foot") && !way.hasTag("bicycle")
                        || "yes".equals(motorcarTag))
                    hwValue = highwayMap.get("ferry");
            }
        }
        return hwValue;
    }
    @Override
    public int getSurface(EdgeIteratorState edge) {
        return (int) surfaceEncoder.getValue(edge.getFlags());
    }

    @Override
    public int getStress(EdgeIteratorState edge) {
        return (int) stressEncoder.getValue(edge.getFlags());
    }

    @Override
    public int getSurface(long flags) {
        return (int) surfaceEncoder.getValue(flags);
    }

    @Override
    public int getStress(long flags) {
        return (int) stressEncoder.getValue(flags);
    }

    @Override
    public String getSurfaceAsString(long flags) {
        int val = getSurface(flags);
        // System.out.println("Get surface in mapc2  flags"+flags);
        // System.out.println("Get surface in mapc2  val   "+val);
        // System.out.println("surfaceMap: "+surfaceMap.keySet());
        for (Map.Entry<String, Integer> e : surfaceMap.entrySet()) {
            if (e.getValue() == val)
                return e.getKey();
        }
        return null;
    }

    @Override
    public String getStressAsString(long flags) {
        int val = getStress(flags);
        // System.out.println("Get surface in mapc2  flags"+flags);
        // System.out.println("Get surface in mapc2  val   "+val);
        // System.out.println("surfaceMap: "+surfaceMap.keySet());
        for (Map.Entry<String, Integer> es : stressMap.entrySet()) {
            if (es.getValue() == val)
                return es.getKey();
        }
        return null;
    }

    @Override
    public String getSurfaceAsString(EdgeIteratorState edge) {
        int val = getSurface(edge);
//        System.out.println("Get surface in bike commen flag"+val);
//        System.out.println("surfaceMap: "+surfaceMap.keySet());
        for (Map.Entry<String, Integer> e : surfaceMap.entrySet()) {
            if (e.getValue() == val)
                return e.getKey();
        }
        return null;
    }


    @Override
    public String getStressAsString(EdgeIteratorState edge) {
        int val = getStress(edge);
//        System.out.println("Get surface in bike commen flag"+val);
//        System.out.println("surfaceMap: "+surfaceMap.keySet());
        for (Map.Entry<String, Integer> es : stressMap.entrySet()) {
            if (es.getValue() == val)
                return es.getKey();
        }
        return null;
    }

    @Override
    public void applyWayTags(ReaderWay way, EdgeIteratorState edge) {
        PointList pl = edge.fetchWayGeometry(3);
        if (!pl.is3D())
            throw new IllegalStateException("To support speed calculation based on elevation data it is necessary to enable import of it.");

        long flags = edge.getFlags();
        int hwValue = getHighwayValue(way);
        // exclude any routing like if you have car and need to exclude all rails or ships
//        if (hwValue == 0)
//            return 0;
        flags = highwayEncoder.setValue(flags, hwValue);


        String surfaceValue = way.getTag("surface");
        Integer sValue = surfaceMap.get(surfaceValue);
        if (sValue == null)
            sValue = 0;
        flags = surfaceEncoder.setValue(flags, sValue);
        // stress

        String stressValue = way.getTag("stress_level");
        Integer strValue = stressMap.get(stressValue);
        if (strValue == null)
            strValue = 0;
        flags = stressEncoder.setValue(flags, strValue);




//        surfaceEncoder.getValue(flags)


        edge.setFlags(flags);
    }

    @Override
    void collect(ReaderWay way, double wayTypeSpeed, TreeMap<Double, Integer> weightToPrioMap) {
        super.collect(way, wayTypeSpeed, weightToPrioMap);

        String highway = way.getTag("highway");
        if ("service".equals(highway)) {
            weightToPrioMap.put(40d, UNCHANGED.getValue());
        } else if ("track".equals(highway)) {
            String trackType = way.getTag("tracktype");
            if ("grade1".equals(trackType))
                weightToPrioMap.put(110d, PREFER.getValue());
            else if (trackType == null || trackType.startsWith("grade"))
                weightToPrioMap.put(110d, AVOID_AT_ALL_COSTS.getValue());
        }
    }

    @Override
    boolean isPushingSection(ReaderWay way) {
        String highway = way.getTag("highway");
        String trackType = way.getTag("tracktype");
        return way.hasTag("highway", pushingSectionsHighways)
                || way.hasTag("railway", "platform")
                || way.hasTag("bicycle", "dismount")
                || "track".equals(highway) && trackType != null && !"grade1".equals(trackType);
    }

    @Override
    boolean isSacScaleAllowed(String sacScale) {
        // for racing bike it is only allowed if empty
        return false;
    }

    @Override
    public String toString() {
        return "racingbike";
    }
}
