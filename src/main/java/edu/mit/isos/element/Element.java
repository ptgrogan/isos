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

import edu.mit.isos.context.Location;
import edu.mit.isos.context.Resource;

/**
 * The Interface Element.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Element {
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public Location getLocation();
	
	/**
	 * Gets the net exchange of resources from this element to another element.
	 *
	 * @param element the other element
	 * @param duration the duration
	 * @return the net exchange
	 */
	public Resource getNetExchange(Element element, long duration);
}
