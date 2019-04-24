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

import com.graphhopper.util.EdgeIteratorState;

import static com.graphhopper.util.Parameters.DETAILS.STREET_NAME;
import static com.graphhopper.util.Parameters.DETAILS.SURFACE;

/**
 * Calculate the surface type of a segments of a Path
 *
 * @author Robin Boldt
 */
public class SurfaceDetails extends AbstractPathDetailsBuilder {

    private String curSurface = null;

    public SurfaceDetails() {
        super(SURFACE);
    }

    @Override
    public boolean isEdgeDifferentToLastEdge(EdgeIteratorState edge) {
        if (curSurface == null || !curSurface.equals(edge.getName())) {
//            curSurface = edge.getSurface();
            return true;
        }
        return false;
    }

    @Override
    public Object getCurrentValue() {
        return this.curSurface;
    }
}
