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
package edu.mit.isos.hla;

import java.util.Collection;

import edu.mit.isos.context.Scenario;
import edu.mit.isos.element.Element;

/**
 * Ambassador interface. Defines required activities including connect,
 * initialize, advance, and disconnect.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public interface ISOSambassador {
	
	/**
	 * Gets the elements.
	 *
	 * @return the elements
	 */
	public Collection<Element> getElements();
	
	/**
	 * Connect to a federation.
	 *
	 * @param federationName the federation name
	 * @param fomPath the fom path
	 * @param federateName the federate name
	 * @param federateType the federate type
	 */
	public void connect(String federationName, String fomPath, 
			String federateName, String federateType);
	
	/**
	 * Initialize a simulation execution.
	 *
	 * @param scenario the scenario
	 * @param numIterations the num iterations
	 * @param timeStep the time step
	 */
	public void initialize(Scenario scenario, int numIterations, long timeStep);
			
	/**
	 * Advance a simulation execution by one time step.
	 */
	public void advance();
	
	/**
	 * Disconnect from a federation.
	 *
	 * @param federationName the federation name
	 */
	public void disconnect(String federationName);
}