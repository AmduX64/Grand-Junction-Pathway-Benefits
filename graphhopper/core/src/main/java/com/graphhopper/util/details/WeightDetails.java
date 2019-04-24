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
package com.graphhopper.util.details;

import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.util.EdgeIteratorState;

//import static com.graphhopper.util.Parameters.DETAILS.TIME;
import static com.graphhopper.util.Parameters.DETAILS.WEIGHT_VALUE;

/**
 * Calculate the time segments for a Path
 *
 * @author Robin Boldt
 */
public class WeightDetails extends AbstractPathDetailsBuilder {

    private final Weighting weighting;
//    private final FlagEncoder encoder;

    private int edgeId = -1;
    private long time = 0;
    private double weight = 0;

    public WeightDetails(Weighting weighting) {
        super(WEIGHT_VALUE);
        this.weighting = weighting;
//        this.encoder = encoder;
    }

    @Override
    public boolean isEdgeDifferentToLastEdge(EdgeIteratorState edge) {
        if (edge.getEdge() != edgeId) {
            edgeId = edge.getEdge();
//            long fs = edge.getFlags();


//            encoder.getLong(fs,2);

//            String stname = edge.getName();
//            edge./
//            double ds = edge.getDistance();
//            edge.getFlags(g)
//            long time = weighting.calcMillis(edge, false, -1);
//            encoder.getLong(fs,)
            // System.out.println("fs   flags value"+ fs );
            // System.out.println("fs   "+ " encoder.getSpeed(fs)  + encoder.getSurfaceAsString(fs): "+ encoder.getSpeed(fs) + encoder.getSurfaceAsString(fs) +"getHighwayAsString:   "  +  encoder.getHighwayAsString(edge));
//            encoder.ge
//            System.out.println("fs   "+ " flag value for the edge with street name " + stname + " is " + fs + "the surface is" + encoder.getSurfaceAsString(fs)+encoder.getSurface(fs));
            // System.out.println("fs   "+ " distance value for the edge is "+ ds );
//            System.out.println("get longs" + encoder.getLong(fs,-1));

//            FlagEncoder f = weighting.getFlagEncoder();
            weight = weighting.calcWeight(edge,false,-1);
            // System.out.println("fs   "+ " weight is calculated for the edge is "+ weight+ " and time is " + time );


//            time = weighting.calcMillis(edge, false, -1);
            return true;
        }
        return false;
    }

    @Override
    public Object getCurrentValue() {
        return this.weight;
    }
}
