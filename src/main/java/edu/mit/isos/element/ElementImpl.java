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
package edu.mit.isos.element;

import java.util.Collection;

import edu.mit.isos.context.Location;
import edu.mit.isos.context.Resource;
import edu.mit.isos.sim.SimEntity;
import edu.mit.isos.state.State;

/**
 * Interface to a local element implementation.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public interface ElementImpl extends Element, SimEntity {
	
	/**
	 * Gets the possible operational states.
	 *
	 * @return the states
	 */
	public Collection<? extends State> getStates();
	
	/**
	 * Gets the initial resource contents at the start of a simulation.
	 *
	 * @return the initial contents
	 */
	public Resource getInitialContents();
	
	/**
	 * Gets the initial location at the start of a simulation.
	 *
	 * @return the initial location
	 */
	public Location getInitialLocation();
	
	/**
	 * Gets the initial parent element at the start of a simulation.
	 *
	 * @return the initial parent
	 */
	public Element getInitialParent();
	
	/**
	 * Gets the initial operational state at the start of a simulation.
	 *
	 * @return the initial state
	 */
	public State getInitialState();
	
	/**
	 * Gets the current resource contents during a simulation.
	 *
	 * @return the contents
	 */
	public Resource getContents();
	
	/**
	 * Gets the current parent during a simulation.
	 *
	 * @return the parent
	 */
	public Element getParent();
	
	/**
	 * Gets the current operational state during a simulation.
	 *
	 * @return the state
	 */
	public State getState();
	
	/**
	 * Adds resources to the resource contents to be processed during 
	 * the next time advance cycle.
	 *
	 * @param resource the resource
	 */
	public void addContents(Resource resource);
	
	/**
	 * Removes resources from the resource contents to be processed during 
	 * the next time advance cycle.
	 *
	 * @param resource the resource
	 */
	public void removeContents(Resource resource);
	
	/**
	 * Sets the new operational state to be processed during 
	 * the next time advance cycle.
	 *
	 * @param state the new state
	 */
	public void setState(State state);
	
	/**
	 * Sets the new parent element to be processed during 
	 * the next time advance cycle.
	 *
	 * @param parent the new parent
	 */
	public void setParent(Element parent);
	
	/**
	 * Sets the new location to be processed during 
	 * the next time advance cycle.
	 *
	 * @param location the new location
	 */
	public void setLocation(Location location);
	
	/**
	 * Gets the net flow of resources from this element to a location.
	 *
	 * @param location the location
	 * @param duration the duration
	 * @return the net flow
	 */
	public Resource getNetFlow(Location location, long duration);
}
