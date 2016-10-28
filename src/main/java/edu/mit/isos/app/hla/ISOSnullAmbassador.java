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
package edu.mit.isos.app.hla;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.mit.isos.context.Scenario;
import edu.mit.isos.element.Element;
import edu.mit.isos.element.ElementImpl;
import edu.mit.isos.hla.ISOSambassador;
import edu.mit.isos.sim.SimEntity;

/**
 * Ambassador implementation for non-HLA federates. Aggregates locally-defined objects.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class ISOSnullAmbassador extends ISOSdefaultAmbassador implements ISOSambassador {
	protected static Logger logger = Logger.getLogger(ISOSnullAmbassador.class);
	private int numIterations;
	private long timeStep;
	private final Set<ElementImpl> localObjects = new HashSet<ElementImpl>();
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.hla.ISOSambassador#advance()
	 */
	public void advance() {
		for(int i = 0; i < numIterations; i++) {
			for(SimEntity entity : localObjects) {
				entity.iterateTick(timeStep);
			}
			for(SimEntity entity : localObjects) {
				entity.iterateTock();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.hla.ISOSambassador#connect(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void connect(String federationName, String fomPath, 
			String federateName, String federateType) {
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.hla.ISOSambassador#disconnect(java.lang.String)
	 */
	public void disconnect(String federationName) {
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.hla.ISOSambassador#getElements()
	 */
	public Collection<Element> getElements() {
		return new HashSet<Element>(localObjects);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.hla.ISOSambassador#initialize(edu.mit.isos.context.Scenario, int, long)
	 */
	public void initialize(Scenario scenario, int numIterations, long timeStep) {
		this.numIterations = numIterations;
		this.timeStep = timeStep;
		
		logger.debug("Registering object instantiations.");
		localObjects.addAll(scenario.getElements());

		logger.debug("Setting up object links.");
		for(ElementImpl entity : localObjects) {
			setUpElement(entity);
		}
	}
}