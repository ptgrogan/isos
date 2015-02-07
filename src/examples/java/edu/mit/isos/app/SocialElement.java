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
package edu.mit.isos.app;

import edu.mit.isos.element.Element;

/**
 * Common interface to a social system element.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public interface SocialElement extends Element {
	
	/**
	 * Gets the quantity of electricity received from the electricity system.
	 *
	 * @return the quantity of electricity received
	 */
	public double getElectReceived();
	
	/**
	 * Gets the quantity of petroleum received from the petroleum system.
	 *
	 * @return the quantity of petroleum received
	 */
	public double getPetrolReceived();
	
	/**
	 * Gets the quantity of water received from the water system.
	 *
	 * @return the quantity of water received
	 */
	public double getWaterReceived();
}
