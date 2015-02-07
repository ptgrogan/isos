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

import java.util.Collection;

import edu.mit.isos.context.Location;
import edu.mit.isos.element.DefaultElement;
import edu.mit.isos.element.ElementImpl;

/**
 * Water controller element to set operational production and 
 * distribution quantities for other elements.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class WaterController extends DefaultElement {
	/**
	 * Instantiates a new water controller with a 
	 * {@link WaterControllerState} operational state.
	 *
	 * @param name the name
	 * @param location the location
	 * @param elements the elements
	 */
	public WaterController(String name, Location location, 
			Collection<? extends ElementImpl> elements) {
		super(name, location, new WaterControllerState(elements));
	}
}
