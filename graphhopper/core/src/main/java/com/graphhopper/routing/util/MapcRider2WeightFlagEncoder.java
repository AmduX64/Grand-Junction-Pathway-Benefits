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
import com.graphhopper.util.BitUtil;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;
import com.graphhopper.util.PointList;

import java.util.*;

import static com.graphhopper.routing.util.PriorityCode.*;
import static com.graphhopper.routing.util.PriorityCode.BEST;
import static com.graphhopper.util.Helper.keepIn;

/**
 * Stores two speed values into an edge to support avoiding too much incline
 * <p>
 *
 * @author Peter Karich
 */
public class MapcRider2WeightFlagEncoder extends BikeCommonFlagEncoder {
    final Set<String> safeHighwayTags = new HashSet<>();
    protected final Map<String, Integer> surfaceMap = new HashMap<>();
    protected final Map<String, Integer> stressMap = new HashMap<>();
    protected final Map<String, Integer> highwayMap = new HashMap<>();
    private EncodedDoubleValue reverseSpeedEncoder;
    private EncodedValue surfaceEncoder;
    private EncodedValue stressEncoder;
    private EncodedValue highwayEncoder;
    private boolean storeSurface = false;
//
//    public MapcRider2WeightFlagEncoder(int speedBits, double speedFactor, int maxTurnCosts) {
//        super(speedBits, speedFactor, maxTurnCosts);
//    }


    public MapcRider2WeightFlagEncoder(PMap properties) {
        this((int) properties.getLong("speed_bits", 5),
                properties.getDouble("speed_factor", 1),
                properties.getBool("turn_costs", false) ? 1 : 0);
        this.properties = properties;
        this.setStoreSurface(properties.getBool("store_surface", true));
    }

//    public MapcRider2WeightFlagEncoder(PMap properties) {
//        super(properties);
//    }

    public MapcRider2WeightFlagEncoder(int speedBits, double speedFactor, int maxTurnCosts) {
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


//        safeHighwayTags.add("residential");
        safeHighwayTags.add("track");
        safeHighwayTags.add("cycle_track");
        safeHighwayTags.add("cycleway");


        setTrackTypeSpeed("grade1", 20); // paved
        setTrackTypeSpeed("grade2", 18); // now unpaved ...
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
        setHighwaySpeed("path", 16);
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
        addPushingSection("footway");
        addPushingSection("pedestrian");
        addPushingSection("steps");

        setCyclingNetworkPreference("icn", PriorityCode.BEST.getValue());
        setCyclingNetworkPreference("ncn", PriorityCode.BEST.getValue());
        setCyclingNetworkPreference("rcn", PriorityCode.VERY_NICE.getValue());
        setCyclingNetworkPreference("lcn", PriorityCode.VERY_NICE.getValue());
        setCyclingNetworkPreference("mtb", PriorityCode.UNCHANGED.getValue());

        absoluteBarriers.add("kissing_gate");

        setAvoidSpeedLimit(81);
        setSpecificClassBicycle("mapcrider2");

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
        shift += stressEncoder.getBits();
        return shift;
    }

    @Override
    public double getReverseSpeed(long flags) {
        return reverseSpeedEncoder.getDoubleValue(flags);
    }


    public MapcRider2WeightFlagEncoder setStoreSurface(boolean storeSurface) {
        this.storeSurface = storeSurface;
        return this;
    }

    public boolean isstoreSurface() {
        return storeSurface;
    }


    @Override
    public long setReverseSpeed(long flags, double speed) {
        if (speed < 0)
            throw new IllegalArgumentException("Speed cannot be negative: " + speed + ", flags:" + BitUtil.LITTLE.toBitString(flags));

        if (speed < speedEncoder.factor / 2)
            return setLowSpeed(flags, speed, true);

        if (speed > getMaxSpeed())
            speed = getMaxSpeed();

        return reverseSpeedEncoder.setDoubleValue(flags, speed);
    }

    @Override
    public long handleSpeed(ReaderWay way, double speed, long flags) {
        // handle oneways
        flags = super.handleSpeed(way, speed, flags);
        if (isBackward(flags))
            flags = setReverseSpeed(flags, speed);

        if (isForward(flags))
            flags = setSpeed(flags, speed);

        return flags;
    }


    @Override
    protected long setLowSpeed(long flags, double speed, boolean reverse) {
        if (reverse)
            return setBool(reverseSpeedEncoder.setDoubleValue(flags, 0), K_BACKWARD, false);

        return setBool(speedEncoder.setDoubleValue(flags, 0), K_FORWARD, false);
    }

    @Override
    public long flagsDefault(boolean forward, boolean backward) {
        long flags = super.flagsDefault(forward, backward);
        if (backward)
            return reverseSpeedEncoder.setDefaultValue(flags);

        return flags;
    }

    @Override
    public long setProperties(double speed, boolean forward, boolean backward) {
        long flags = super.setProperties(speed, forward, backward);
        if (backward)
            return setReverseSpeed(flags, speed);

        return flags;
    }

    @Override
    public long reverseFlags(long flags) {
        // swap access
        flags = super.reverseFlags(flags);

        // swap speeds 
        double otherValue = reverseSpeedEncoder.getDoubleValue(flags);
        flags = setReverseSpeed(flags, speedEncoder.getDoubleValue(flags));
        return setSpeed(flags, otherValue);
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
    protected String getPropertiesString() {
        return super.getPropertiesString() +
                "|store_surface=" + storeSurface;

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

        if (way.hasTag("tunnel", "yes") || way.hasTag("bridge", "yes") || way.hasTag("highway", "steps")) {
            // do not change speed
            // note: although tunnel can have a difference in elevation it is very unlikely that the elevation data is correct for a tunnel
        } else {
            // Decrease the speed for ele increase (incline), and decrease the speed for ele decrease (decline). The speed-decrease 
            // has to be bigger (compared to the speed-increase) for the same elevation difference to simulate loosing energy and avoiding hills.
            // For the reverse speed this has to be the opposite but again keeping in mind that up+down difference.
            double incEleSum = 0, incDist2DSum = 0;
            double decEleSum = 0, decDist2DSum = 0;
            // double prevLat = pl.getLatitude(0), prevLon = pl.getLongitude(0);
            double prevEle = pl.getElevation(0);
            double fullDist2D = edge.getDistance();

            if (Double.isInfinite(fullDist2D))
                throw new IllegalStateException("Infinite distance should not happen due to #435. way ID=" + way.getId());

            // for short edges an incline makes no sense and for 0 distances could lead to NaN values for speed, see #432
            if (fullDist2D < 1)
                return;

            double eleDelta = pl.getElevation(pl.size() - 1) - prevEle;
            if (eleDelta > 0.1) {
                incEleSum = eleDelta;
                incDist2DSum = fullDist2D;
            } else if (eleDelta < -0.1) {
                decEleSum = -eleDelta;
                decDist2DSum = fullDist2D;
            }

//            // get a more detailed elevation information, but due to bad SRTM data this does not make sense now.
//            for (int i = 1; i < pl.size(); i++)
//            {
//                double lat = pl.getLatitude(i);
//                double lon = pl.getLongitude(i);
//                double ele = pl.getElevation(i);
//                double eleDelta = ele - prevEle;
//                double dist2D = distCalc.calcDist(prevLat, prevLon, lat, lon);
//                if (eleDelta > 0.1)
//                {
//                    incEleSum += eleDelta;
//                    incDist2DSum += dist2D;
//                } else if (eleDelta < -0.1)
//                {
//                    decEleSum += -eleDelta;
//                    decDist2DSum += dist2D;
//                }
//                fullDist2D += dist2D;
//                prevLat = lat;
//                prevLon = lon;
//                prevEle = ele;
//            }
            // Calculate slop via tan(asin(height/distance)) but for rather smallish angles where we can assume tan a=a and sin a=a.
            // Then calculate a factor which decreases or increases the speed.
            // Do this via a simple quadratic equation where y(0)=1 and y(0.3)=1/4 for incline and y(0.3)=2 for decline        
            double fwdIncline = incDist2DSum > 1 ? incEleSum / incDist2DSum : 0;
            double fwdDecline = decDist2DSum > 1 ? decEleSum / decDist2DSum : 0;
            double restDist2D = fullDist2D - incDist2DSum - decDist2DSum;
            double maxSpeed = getHighwaySpeed("cycleway");
            if (isForward(flags)) {
                // use weighted mean so that longer incline influences speed more than shorter
                double speed = getSpeed(flags);
                double fwdFaster = 1 + 2 * keepIn(fwdDecline, 0, 0.2);
                fwdFaster = fwdFaster * fwdFaster;
                double fwdSlower = 1 - 3 * keepIn(fwdIncline, 0, 0.2);
                fwdSlower = fwdSlower * fwdSlower;
                speed = speed * (fwdSlower * incDist2DSum + fwdFaster * decDist2DSum + 1 * restDist2D) / fullDist2D;
                flags = this.setSpeed(flags, keepIn(speed, PUSHING_SECTION_SPEED, maxSpeed));
            }

            if (isBackward(flags)) {
                double speedReverse = getReverseSpeed(flags);
                double bwFaster = 1 + 2 * keepIn(fwdIncline, 0, 0.2);
                bwFaster = bwFaster * bwFaster;
                double bwSlower = 1 - 5 * keepIn(fwdDecline, 0, 0.2);
                bwSlower = bwSlower * bwSlower;
                speedReverse = speedReverse * (bwFaster * incDist2DSum + bwSlower * decDist2DSum + 1 * restDist2D) / fullDist2D;
                flags = this.setReverseSpeed(flags, keepIn(speedReverse, PUSHING_SECTION_SPEED, maxSpeed));
            }
        }
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
            // System.out.println("hasFoot   "+hasFoot);
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


        if (!"residential".equals(highway)){
            if  (way.hasTag("cycleway", "no") || !way.hasTag("cycleway")){
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
        return "mapcrider2";
    }
}
