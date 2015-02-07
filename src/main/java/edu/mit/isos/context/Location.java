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
/**
 * An immutable object which aggregates origin and destination nodes.
 * A static location has identical origin and destination nodes.
 * A dynamic location has different origin and destination nodes.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class Location {
	private Node origin, destination;
	
	/**
	 * Instantiates a new location with null origin and destination.
	 */
	protected Location() {
		origin = null;
		destination = null;
	}
	
	/**
	 * Instantiates a new location.
	 *
	 * @param origin the origin
	 * @param destination the destination
	 */
	public Location(Node origin, Node destination) {
		this.origin = origin;
		this.destination = destination;
	}
	
	/**
	 * Instantiates a new static location.
	 *
	 * @param node the node
	 */
	public Location(Node node) {
		this(node, node);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if(isStatic()) {
			return origin.toString();
		} else {
			return origin + "-" + destination;
		}
	}
	
	/**
	 * Gets the origin node.
	 *
	 * @return the origin
	 */
	public Node getOrigin() {
		return origin;
	}
	
	/**
	 * Gets the destination node.
	 *
	 * @return the destination
	 */
	public Node getDestination() {
		return destination;
	}
	
	/**
	 * Checks if is static (origin an destination are equal).
	 *
	 * @return true, if is static
	 */
	public boolean isStatic() {
		return origin != null && origin.equals(destination);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof Location)) {
			return false;
		}
		Location l = (Location)o;
		return l.origin.equals(origin) 
				&& l.destination.equals(destination);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return 17 + 31*origin.hashCode() + 31*destination.hashCode();
	}
}
