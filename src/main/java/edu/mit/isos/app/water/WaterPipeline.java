/*
 * Copyright 2015 Paul T. Grogan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mit.isos.app.water;

import edu.mit.isos.context.Location;
import edu.mit.isos.element.DefaultElement;

/**
 * Water transportation element to convey water between nodes.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class WaterPipeline extends DefaultElement {
	
	/**
	 * Instantiates a new water pipeline with a 
	 * {@link WaterPipelineState} operational state.
	 *
	 * @param name the name
	 * @param location the location
	 * @param capacity the throughput capacity
	 * @param efficiency the input-output efficiency
	 * @param pumpElect the electricity transformation factor for pumping
	 */
	public WaterPipeline(String name, Location location, 
			double capacity, double efficiency, double pumpElect) {
		super(name, location, new WaterPipelineState(
				capacity, efficiency, pumpElect));
	}

	/**
	 * Gets the operating state.
	 *
	 * @return the operating state
	 */
	public WaterPipelineState getOperatingState() {
		return (WaterPipelineState) getState();
	}
}
