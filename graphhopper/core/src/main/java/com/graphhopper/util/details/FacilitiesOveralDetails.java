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
import com.graphhopper.util.EdgeIteratorState;

import static com.graphhopper.util.Parameters.DETAILS.FACILITIES_OVERAL;

/**
 * Calculate the general describtions of facilities
 *
 * @author Robin Boldt
 */
public class FacilitiesOveralDetails extends AbstractPathDetailsBuilder {

    private final FlagEncoder encoder;
    private String facilitiesOverals = null;

    public FacilitiesOveralDetails(FlagEncoder encoder) {
        super(FACILITIES_OVERAL);
        this.encoder = encoder;
    }

    @Override
    public boolean isEdgeDifferentToLastEdge(EdgeIteratorState edge) {
//        if (Math.abs(encoder.getSpeed(edge.getFlags()) - curAvgSpeed) > 0.0001) {
//            this.curAvgSpeed = this.encoder.getSpeed(edge.getFlags());
//            return true;
//        }
//        String facils = encoder();
        this.facilitiesOverals = this.encoder.getSurfaceAsString(edge.getFlags()) + " | " + this.encoder.getHighwayAsString(edge) + " | "+ this.encoder.getReverseSpeed(edge.getFlags())+ " | " + edge.getDistance() + " | " + this.encoder.getStressAsString(edge.getFlags());
        return true;
    }

    @Override
    public Object getCurrentValue() {
        return this.facilitiesOverals;
    }
}
