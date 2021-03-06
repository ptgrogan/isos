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
package edu.mit.isos.sim;

import java.util.EventListener;

/**
 * The listener interface for receiving simulationTime events.
 * The class that is interested in processing a simulationTime
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSimulationTimeListener<code> method. When
 * the simulationTime event occurs, that object's appropriate
 * method is invoked.
 *
 * @see SimulationTimeEvent
 * 
 * @author Paul T Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public interface SimulationTimeListener extends EventListener {
	
	/**
	 * Time advanced.
	 *
	 * @param event the event
	 */
	public void timeAdvanced(SimulationTimeEvent event);
}
