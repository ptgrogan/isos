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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.isos.context.Location;
import edu.mit.isos.element.DefaultElement;
import edu.mit.isos.state.EmptyState;
import edu.mit.isos.state.State;

/**
 * Water desalination plant element to produce water.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class WaterPlant extends DefaultElement {
	private WaterPlantState operatingState;
	private EmptyState emptyState;
	private List<? extends State> states;
	
	/**
	 * Instantiates a new water plant with
	 * {@link EmptyState} and {@link WaterPlantState} operational states.
	 *
	 * @param name the name
	 * @param location the location
	 * @param commissionTime the time the plant is commissioned
	 * @param capacity the production capacity
	 * @param desalElect the electricity transformation factor for desalination
	 */
	public WaterPlant(String name, Location location, 
			final long commissionTime, final double capacity, final double desalElect) {
		super(name, location);
		operatingState = new WaterPlantState(capacity, desalElect);
		emptyState = new EmptyState(commissionTime, operatingState);
		states = Arrays.asList(emptyState, operatingState);
	}
	
	/**
	 * Gets the operating state.
	 *
	 * @return the operating state
	 */
	public WaterPlantState getOperatingState() {
		return operatingState;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.DefaultElement#getStates()
	 */
	@Override
	public Set<State> getStates() {
		return new HashSet<State>(states);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.DefaultElement#initialize(long)
	 */
	@Override
	public void initialize(long initialTime) {
		if(initialTime < emptyState.getStateChangeTime()) {
			initialState(emptyState);
		} else {
			initialState(operatingState);
		}
		super.initialize(initialTime);
	}
	
	/**
	 * Checks if this element is operating.
	 *
	 * @return true, if is operating
	 */
	public boolean isOperating() {
		return getState().equals(operatingState);
	}
}
