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
package edu.mit.isos.sim;

import hla.rti1516e.exceptions.RTIexception;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import edu.mit.isos.context.Location;
import edu.mit.isos.context.Resource;
import edu.mit.isos.context.ResourceFactory;
import edu.mit.isos.context.Scenario;
import edu.mit.isos.element.ElementImpl;
import edu.mit.isos.hla.ISOSambassador;

/**
 * Manages simulation of a scenario.
 * 
 * @author Paul T Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class Simulator {
	private static Logger logger = Logger.getLogger(Simulator.class);
	
	private final Scenario scenario;
	private EventListenerList listeners = new EventListenerList();
	
	// TODO: verification options, should be moved to separate listeners
	public boolean verifyFlow = false, verifyExchange = false;

	/**
	 * Instantiates a new simulator for a scenario.
	 *
	 * @param scenario the scenario
	 */
	public Simulator(Scenario scenario) {
		this.scenario = scenario;
	}
	
	/**
	 * Adds a simulation time listener.
	 *
	 * @param listener the listener
	 */
	public void addSimulationTimeListener(SimulationTimeListener listener) {
		listeners.add(SimulationTimeListener.class, listener);
	}
	
	/**
	 * Executes a simulation.
	 *
	 * @param amb the federate ambassador
	 * @param federateName the federate name
	 * @param duration the simulation duration
	 * @param timeStep the time step duration
	 * @param iterations the number of iterations per time step
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public void execute(ISOSambassador amb, String federateName, 
			long duration, long timeStep, int iterations) throws RTIexception {
		long time = scenario.getInitialTime();
		
		logger.info("Executing scenario " + scenario 
				+ " for duration " + duration 
				+ " with a timestep of " + timeStep 
				+ " and " + iterations + " iterations" 
				+ " and options {" 
				+ "verifyFlow: " + verifyFlow 
				+ ", verifyExchange: " + verifyExchange + "}.");
		
		while(time <= scenario.getInitialTime() + duration) {
			// advance the federate ambassador
			amb.advance();
			
			if(verifyFlow) {
				verifyFlow(scenario, time, timeStep);
			}
			if(verifyExchange) {
				verifyExchange(scenario, time, timeStep);
			}
			logger.trace("Simulation time is " + time + ".");
			
			// tick entities
			for(SimEntity entity : scenario.getElements()) {
				entity.tick(timeStep);
			}
			
			// signal the end of the current time step
			fireTimeAdvanced(time, timeStep);
			
			// tock entities
			for(SimEntity entity : scenario.getElements()) {
				entity.tock();
			}
			
			// advance time to next time step
			time = time + timeStep;
		}
	}
	
	/**
	 * Fires a time advanced event.
	 *
	 * @param time the time
	 * @param duration the duration
	 */
	private void fireTimeAdvanced(long time, long duration) {
		SimulationTimeListener[] listeners = this.listeners.getListeners(
				SimulationTimeListener.class);
		for(int i = 0; i < listeners.length; i++) {
			listeners[i].timeAdvanced(new SimulationTimeEvent(this, time, duration));
		}
	} 
	
	/**
	 * Gets the scenario.
	 *
	 * @return the scenario
	 */
	public Scenario getScenario() {
		return scenario;
	}
	
	/**
	 * Initializes the simulation.
	 *
	 * @param amb the federate ambassador
	 * @param federateName the federate name
	 * @param timeStep the time step duration
	 * @param iterations the number of iterations per time step
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public void initialize(ISOSambassador amb, String federateName, 
			long timeStep, int iterations) throws RTIexception {
		for(SimEntity entity : scenario.getElements()) {
			entity.initialize(scenario.getInitialTime());
		}
		amb.initialize(scenario, iterations, timeStep);
	}

	/**
	 * Removes a simulation time listener.
	 *
	 * @param listener the listener
	 */
	public void removeSimulationTimeListener(SimulationTimeListener listener) {
		listeners.remove(SimulationTimeListener.class, listener);
	}
	
	/**
	 * Verify resource exchange validity constraints for each pair of elements.
	 *
	 * @param scenario the scenario
	 * @param time the time
	 * @param timeStep the time step
	 */
	private void verifyExchange(Scenario scenario, long time, long timeStep) {
		for(ElementImpl e1 : scenario.getElements()) {
			for(ElementImpl e2 : scenario.getElements()) {
				Resource e12 = e1.getNetExchange(e2, timeStep);
				Resource e21 = e2.getNetExchange(e1, timeStep);

				if(!e12.equals(e21.negate())) {
					logger.warn("@ t = " + time + ": Unbalanced resource exchange: " + 
							e1.getName() + "<->"  + e2.getName() + ", delta=" +
							e12.add(e21) + ", error=" + (e12.add(e21)).safeDivide(e12));
				}
			}
		}
	}
	
	/**
	 * Verify flow validity constraints at each location.
	 *
	 * @param scenario the scenario
	 * @param time the time
	 * @param timeStep the time step
	 */
	private void verifyFlow(Scenario scenario, long time, long timeStep) {
		for(Location location : scenario.getLocations()) {
			Resource flowRate = ResourceFactory.create();
			for(ElementImpl element : scenario.getElements()) {
				flowRate = flowRate.add(element.getNetFlow(location, timeStep));
			}
			if(!flowRate.isZero()) {
				logger.warn(location + " @ t = " + time + 
						": Non-zero flow rate at " + location + ": " 
						+ flowRate);
			}
		}
	}
}