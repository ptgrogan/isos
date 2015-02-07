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
package edu.mit.isos.context;

import java.util.ArrayList;
import java.util.Collection;

import edu.mit.isos.element.Element;
import edu.mit.isos.element.ElementImpl;

/**
 * A scenario is an immutable object which aggregates locations 
 * and elements to be executed in a simulator.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class Scenario {
	private final String name;
	private final Collection<Location> locations = new ArrayList<Location>();
	private final Collection<ElementImpl> elements = new ArrayList<ElementImpl>();
	private final long initialTime;
	
	/**
	 * Instantiates a new scenario.
	 */
	protected Scenario() {
		name = "";
		initialTime = 0;
	}
	
	/**
	 * Instantiates a new scenario.
	 *
	 * @param name the name
	 * @param initialTime the initial time
	 * @param locations the locations
	 * @param elements the elements
	 */
	public Scenario(String name, long initialTime,
			Collection<Location> locations, 
			Collection<? extends ElementImpl> elements) {
		this.name = name;
		this.initialTime = initialTime;
		this.locations.addAll(locations);
		this.elements.addAll(elements);
	}
	
	/**
	 * Gets the element.
	 *
	 * @param name the name
	 * @return the element
	 */
	public Element getElement(String name) {
		for(Element element : elements) {
			if(element.getName().equals(name)) {
				return element;
			}
		}
		return null;
	}
	
	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public Collection<ElementImpl> getElements() {
		return new ArrayList<ElementImpl>(elements);
	}
	
	/**
	 * Gets the initial time.
	 *
	 * @return the initial time
	 */
	public long getInitialTime() {
		return initialTime;
	}
	
	/**
	 * Gets the locations.
	 *
	 * @return the locations
	 */
	public Collection<Location> getLocations() {
		return new ArrayList<Location>(locations);
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new StringBuilder(getName()).append(" {initialTime: ")
			.append(initialTime).append("}").toString();
	}
}
