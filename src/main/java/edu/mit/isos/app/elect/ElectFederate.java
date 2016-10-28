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
package edu.mit.isos.app.elect;

import hla.rti1516e.exceptions.RTIexception;

import java.util.Arrays;

import org.apache.log4j.Logger;

import edu.mit.isos.app.DefaultFederate;
import edu.mit.isos.app.hla.ISOSfedAmbassador;
import edu.mit.isos.context.Scenario;
import edu.mit.isos.hla.ISOSambassador;

/**
 * An HLA-compliant federate for the electricity system. 
 * Defines three electricity elements:
 *  - e_E1: System element at node A
 *  - e_E2: System element at node B
 *  - e_E3: System element at node C
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class ElectFederate extends DefaultFederate {
	protected static Logger logger = Logger.getLogger(ElectFederate.class);
	
	/**
	 * Instantiates a new electricity federate.
	 *
	 * @param outputDir the output directory
	 * @param numIterations the number of iterations per time step
	 * @param numReplications the number of simulation execution replications
	 * @param timeStep the time step duration
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public ElectFederate(String outputDir, int numIterations, 
			int numReplications, long timeStep) throws RTIexception {
		super("Elect", outputDir, numIterations, numReplications, timeStep);
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.app.DefaultFederate#buildScenario(double)
	 */
	@Override
	public Scenario buildScenario(double stepsPerYear) {
		ElectElementImpl e_e1 = new ElectElementImpl("e_E1", l_aa, 
				1.0/stepsPerYear, 0.25);
		ElectElementImpl e_e2 = new ElectElementImpl("e_E2", l_bb, 
				0.5/stepsPerYear, 0.30);
		ElectElementImpl e_e3 = new ElectElementImpl("e_E3", l_cc, 
				0.8/stepsPerYear, 0.25);
		return new Scenario("Demo", 2014000, 
				Arrays.asList(l_aa, l_bb, l_cc, l_ab, l_ba, l_bc, l_cb), 
				Arrays.asList(e_e1, e_e2, e_e3));
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.app.DefaultFederate#getAmbassador()
	 */
	@Override
	public ISOSambassador getAmbassador() throws RTIexception {
		return new ISOSfedAmbassador();
	}
}
