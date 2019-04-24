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

import java.util.*;

import static com.graphhopper.routing.util.PriorityCode.*;

/**
 * Specifies the settings for race biking
 * <p>
 *
 * @author ratrun
 * @author Peter Karich
 */
public class MapcRiderFlagEncoder extends BikeCommonFlagEncoder {
    final Set<String> safeHighwayTags = new HashSet<>();
    public MapcRiderFlagEncoder() {
        this(5, 1, 0);
    }

    public MapcRiderFlagEncoder(PMap properties) {
        this(
                (int) properties.getLong("speed_bits", 5),
                properties.getDouble("speed_factor", 1),
                properties.getBool("turn_costs", false) ? 1 : 0
        );
        this.properties = properties;
        this.setBlockFords(properties.getBool("block_fords", true));
    }



    public MapcRiderFlagEncoder(int speedBits, double speedFactor, int maxTurnCosts) {
        super(speedBits, speedFactor, maxTurnCosts);
        avoidHighwayTags.add("trunk");
        avoidHighwayTags.add("trunk_link");
        avoidHighwayTags.add("primary");
        avoidHighwayTags.add("primary_link");
        avoidHighwayTags.add("secondary");
        avoidHighwayTags.add("secondary_link");
        avoidHighwayTags.add("residential");


        preferHighwayTags.add("road");
        preferHighwayTags.add("secondary_link");
        preferHighwayTags.add("tertiary_link");
        preferHighwayTags.add("track");


        safeHighwayTags.add("residential");
        safeHighwayTags.add("track");
        safeHighwayTags.add("cycle_track");
        safeHighwayTags.add("cycleway");


        setTrackTypeSpeed("grade1", 18); // paved
        setTrackTypeSpeed("grade2", 15); // now unpaved ...
        setTrackTypeSpeed("grade3", PUSHING_SECTION_SPEED);
        setTrackTypeSpeed("grade4", PUSHING_SECTION_SPEED);
        setTrackTypeSpeed("grade5", PUSHING_SECTION_SPEED);

        setSurfaceSpeed("paved", 18);
        setSurfaceSpeed("asphalt", 16);
        setSurfaceSpeed("cobblestone", 10);
        setSurfaceSpeed("cobblestone:flattened", 10);
        setSurfaceSpeed("sett", 10);
        setSurfaceSpeed("concrete", 20);
        setSurfaceSpeed("concrete:lanes", 16);
        setSurfaceSpeed("concrete:plates", 16);
        setSurfaceSpeed("paving_stones", 10);
        setSurfaceSpeed("paving_stones:30", 10);
        setSurfaceSpeed("unpaved", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("compacted", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("dirt", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("earth", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("fine_gravel", PUSHING_SECTION_SPEED);
        setSurfaceSpeed("grass", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("grass_paver", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("gravel", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("ground", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("ice", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("metal", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("mud", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("pebblestone", PUSHING_SECTION_SPEED);
        setSurfaceSpeed("salt", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("sand", PUSHING_SECTION_SPEED );
        setSurfaceSpeed("wood", PUSHING_SECTION_SPEED );

        setHighwaySpeed("cycleway", 18);
        setHighwaySpeed("path", 14);
        setHighwaySpeed("footway", 10);
        setHighwaySpeed("pedestrian", 10);
        setHighwaySpeed("road", 14);
        setHighwaySpeed("track", PUSHING_SECTION_SPEED ); // assume unpaved
        setHighwaySpeed("service", 12);
        setHighwaySpeed("unclassified", 14);
        setHighwaySpeed("residential", 12);

        setHighwaySpeed("trunk", 15);
        setHighwaySpeed("trunk_link", 15);
        setHighwaySpeed("primary", 14);
        setHighwaySpeed("primary_link", 14);
        setHighwaySpeed("secondary", 14);
        setHighwaySpeed("secondary_link", 14);
        setHighwaySpeed("tertiary", 14);
        setHighwaySpeed("tertiary_link", 14);

//        addPushingSection("path");
//        addPushingSection("footway");
//        addPushingSection("pedestrian");
//        addPushingSection("steps");

        setCyclingNetworkPreference("icn", PriorityCode.BEST.getValue());
        setCyclingNetworkPreference("ncn", PriorityCode.BEST.getValue());
        setCyclingNetworkPreference("rcn", PriorityCode.VERY_NICE.getValue());
        setCyclingNetworkPreference("lcn", PriorityCode.PREFER.getValue());
        setCyclingNetworkPreference("mtb", PriorityCode.UNCHANGED.getValue());

        absoluteBarriers.add("kissing_gate");

        setAvoidSpeedLimit(81);
        setSpecificClassBicycle("mapcrider");

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
    void collect(ReaderWay way, double wayTypeSpeed, TreeMap<Double, Integer> weightToPrioMap) {
        super.collect(way, wayTypeSpeed, weightToPrioMap);
        String service = way.getTag("service");
        String cycleway = way.getTag("cycleway");

        String highway = way.getTag("highway");
//        System.out.println("cycleway   "+cycleway);
//        System.out.println("highway   "+highway);
        double maxSpeed = getMaxSpeed(way);


//        the best is if it's a cycleway


        if  ("track".equals(cycleway)){
//            String foot = way.getTag("foot");
            Boolean hasFoot = way.hasTag("foot");
            //System.out.println("hasFoot   "+hasFoot);
//            String footway = way.getTag("footway");x
            if(hasFoot){
                weightToPrioMap.put(34d, PREFER.getValue());
            } else {
                weightToPrioMap.put(44d, VERY_NICE.getValue());
            }
//            weightToPrioMap.put(44d, BEST.getValue());

        }

        if  (way.hasTag("cycleway", "lane") || (way.hasTag("cycleway:right", "lane")) || (way.hasTag("cycleway:left", "lane"))){
//            String foot = way.getTag("foot");
            weightToPrioMap.put(44d, PREFER.getValue());


        }


        if  (way.hasTag("cycleway", "no") ){
            weightToPrioMap.put(44d, AVOID_IF_POSSIBLE.getValue());


        }


        if ("residential".equals(highway)!=true){
            if  (way.hasTag("cycleway", "no") || way.hasTag("cycleway")!=true){
                weightToPrioMap.put(44d, AVOID_AT_ALL_COSTS.getValue());


            }
        }
//        if the highway doesn't have any bike lanes then avoid



        if (safeHighwayTags.contains(highway) || maxSpeed > 0 && maxSpeed <= 80) {
            weightToPrioMap.put(44d, VERY_NICE.getValue());
            if (way.hasTag("tunnel", intendedValues)) {
                weightToPrioMap.put(40d, AVOID_IF_POSSIBLE.getValue());
            }
        }

        if (way.hasTag("bicycle", "official") || way.hasTag("bicycle", "designated"))
            weightToPrioMap.put(44d, UNCHANGED.getValue());
        if ("service".equals(highway)) {
            weightToPrioMap.put(40d, UNCHANGED.getValue());
        } else if ("track".equals(highway)) {
            String trackType = way.getTag("tracktype");
            if ("grade1".equals(trackType))
                weightToPrioMap.put(110d, BEST.getValue());
            else if (trackType == null || trackType.startsWith("grade"))
                weightToPrioMap.put(110d, PREFER.getValue());
            else {
                weightToPrioMap.put(110d, VERY_NICE.getValue());
            }
        }

        if (pushingSectionsHighways.contains(highway)
                || way.hasTag("bicycle", "use_sidepath")
                || "parking_aisle".equals(service)) {
            int pushingSectionPrio = AVOID_IF_POSSIBLE.getValue();
            if (way.hasTag("bicycle", "yes") || way.hasTag("bicycle", "permissive"))
                pushingSectionPrio = UNCHANGED.getValue();
            if (way.hasTag("bicycle", "designated") || way.hasTag("bicycle", "official"))
                pushingSectionPrio = VERY_NICE.getValue();
            if (way.hasTag("foot", "yes")) {
                pushingSectionPrio = Math.max(pushingSectionPrio - 1, WORST.getValue());
                if (way.hasTag("segregated", "yes"))
                    pushingSectionPrio = Math.min(pushingSectionPrio + 1, BEST.getValue());
            }
            weightToPrioMap.put(100d, pushingSectionPrio);
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
        return "mapcrider";
    }
}
