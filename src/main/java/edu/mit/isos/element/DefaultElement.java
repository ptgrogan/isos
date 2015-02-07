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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.isos.context.Location;
import edu.mit.isos.context.Resource;
import edu.mit.isos.context.ResourceFactory;
import edu.mit.isos.state.DefaultState;
import edu.mit.isos.state.State;

/**
 * The default implementation of the {@link ElementImpl} interface.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class DefaultElement implements ElementImpl {
	private String name;
	private Set<State> states = new HashSet<State>();
	
	private Element initialParent, parent, nextParent;
	private Resource initialContents, contents, nextContents;
	private State initialState, state, nextState;
	private Location initialLocation, location, nextLocation;
	
	/**
	 * Instantiates a new default element.
	 */
	protected DefaultElement() {
		this("", null);
	}
	
	/**
	 * Instantiates a new default element with a name, initial location, 
	 * and a {@link DefaultState} initial operational state.
	 *
	 * @param name the name
	 * @param initialLocation the initial location
	 */
	public DefaultElement(String name, Location initialLocation) {
		this(name, initialLocation, new DefaultState("Default"));
	}
	
	/**
	 * Instantiates a new default element with a name, initial location, 
	 * and list of states. The first supplied state is set as the initial.
	 *
	 * @param name the name
	 * @param initialLocation the initial location
	 * @param states the states
	 */
	public DefaultElement(String name, Location initialLocation, 
			List<State> states) {
		this.name = name;
		initialParent = this;
		this.initialLocation = initialLocation;
		initialContents = ResourceFactory.create();
		this.states.addAll(states);
		if(states.size() > 0) {
			initialState = states.get(0);
		}
	}
	
	/**
	 * Instantiates a new default element with a name, initial location, 
	 * and initial operational state.
	 *
	 * @param name the name
	 * @param initialLocation the initial location
	 * @param initialState the initial state
	 */
	public DefaultElement(String name, Location initialLocation, 
			State initialState) {
		this(name, initialLocation, Arrays.asList(initialState));
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#addContents(edu.mit.isos.context.Resource)
	 */
	public void addContents(Resource resource) {
		nextContents = nextContents.add(resource);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#getContents()
	 */
	public Resource getContents() {
		return contents;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#getInitialContents()
	 */
	public Resource getInitialContents() {
		return initialContents;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#getInitialLocation()
	 */
	public Location getInitialLocation() {
		return initialLocation;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#getInitialParent()
	 */
	public Element getInitialParent() {
		return initialParent;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#getInitialState()
	 */
	public State getInitialState() {
		return initialState;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.Element#getLocation()
	 */
	public Location getLocation() {
		if(!equals(parent)) {
			return parent.getLocation();
		} else {
			return location;
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.Element#getName()
	 */
	public String getName() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.Element#getNetExchange(edu.mit.isos.element.Element, long)
	 */
	public Resource getNetExchange(Element element, long duration) {
		if(state != null) {
			return state.getNetExchange(this, element, duration);
		} else {
			return ResourceFactory.create();
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#getNetFlow(edu.mit.isos.context.Location, long)
	 */
	public Resource getNetFlow(Location location, long duration) {
		if(state != null) {
			return state.getNetFlow(this, location, duration);
		} else {
			return ResourceFactory.create();
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#getParent()
	 */
	public Element getParent() {
		return parent;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#getState()
	 */
	public State getState() {
		return state;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#getStates()
	 */
	public Set<State> getStates() {
		return new HashSet<State>(states);
	}
	
	/**
	 * Builder pattern to set initial resource contents.
	 *
	 * @param initialContents the initial contents
	 * @return the default element
	 */
	public DefaultElement initialContents(Resource initialContents) {
		this.initialContents = initialContents;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.sim.SimEntity#initialize(long)
	 */
	public void initialize(long initialTime) {
		contents = nextContents = initialContents;
		state = nextState = initialState;
		parent = nextParent = initialParent;
		location = nextLocation = initialLocation;
		for(State state : getStates()) {
			state.initialize(this, initialTime);
		}
	}

	/**
	 * Builder pattern to set initial parent.
	 *
	 * @param initialParent the initial parent
	 * @return the default element
	 */
	public DefaultElement initialParent(Element initialParent) {
		this.initialParent = initialParent;
		return this;
	}

	/**
	 * Builder pattern to set initial state.
	 *
	 * @param initialState the initial state
	 * @return the default element
	 */
	public DefaultElement initialState(State initialState) {
		if(!getStates().contains(initialState)) {
			states.add(initialState);
		}
		this.initialState = initialState;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.sim.SimEntity#iterateTick(long)
	 */
	public void iterateTick(long duration) {
		if(state != null) {
			state.iterateTick(this, duration);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.sim.SimEntity#iterateTock()
	 */
	public void iterateTock() {
		if(state != null) {
			state.iterateTock();
		}
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#removeContents(edu.mit.isos.context.Resource)
	 */
	public void removeContents(Resource resource) {
		nextContents = nextContents.subtract(resource);
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#setLocation(edu.mit.isos.context.Location)
	 */
	public void setLocation(Location location) {
		if(!equals(parent)) {
			throw new IllegalStateException(
					"Cannot change location for nested element " + this);
		}
		nextLocation = location;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#setParent(edu.mit.isos.element.Element)
	 */
	public void setParent(Element parent) {
		if(!parent.getLocation().equals(getLocation())) {
			throw new IllegalArgumentException(
					"Parent must have same location as child.");
		}
		nextParent = parent;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.element.ElementImpl#setState(edu.mit.isos.state.State)
	 */
	public void setState(State state) {
		if(!getStates().contains(initialState)) {
			throw new IllegalArgumentException(
					"States does not include " + state);
		}
		nextState = state;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.sim.SimEntity#tick(long)
	 */
	public void tick(long duration) {
		nextContents = contents.copy();
		for(State state : getStates()) {
			state.tick(this, duration);
		}
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.sim.SimEntity#tock()
	 */
	public void tock() {
		contents = nextContents.copy();
		state = nextState;
		parent = nextParent;
		location = nextLocation;
		for(State state : getStates()) {
			state.tock();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name + " " + " (" + state + " @ " + location + ", " + contents + ") ";
	}
}
