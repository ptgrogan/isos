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
package edu.mit.isos.app.petrol;

import hla.rti1516e.exceptions.RTIexception;

import java.util.Arrays;

import org.apache.log4j.Logger;

import edu.mit.isos.app.DefaultFederate;
import edu.mit.isos.app.hla.ISOSfedAmbassador;
import edu.mit.isos.context.Scenario;
import edu.mit.isos.hla.ISOSambassador;

/**
 * An HLA-compliant federate for the petroleum system. 
 * Defines three petroleum elements:
 *  - e_P1: Petroleum system element at node A
 *  - e_P2: Petroleum system element at node B
 *  - e_P3: Petroleum system element at node C
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class PetrolFederate extends DefaultFederate {
	protected static Logger logger = Logger.getLogger(PetrolFederate.class);

	/**
	 * Instantiates a new petroleum federate.
	 *
	 * @param outputDir the output directory
	 * @param numIterations the number of iterations per time step
	 * @param numReplications the number of simulation execution replications
	 * @param timeStep the time step duration
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public PetrolFederate(String outputDir, int numIterations, 
			int numReplications, long timeStep) throws RTIexception {
		super("Petrol", outputDir, numIterations, numReplications, timeStep);
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.app.DefaultFederate#buildScenario(double)
	 */
	@Override
	public Scenario buildScenario(double stepsPerYear) {
		PetrolElementImpl e_o1 = new PetrolElementImpl("e_P1", l_aa, 
				0.5, 1.0, 5000);
		PetrolElementImpl e_o2 = new PetrolElementImpl("e_P2", l_bb, 
				0.8, 1.0, 1000);
		PetrolElementImpl e_o3 = new PetrolElementImpl("e_P3", l_cc, 
				0.6, 1.0, 4000);
		return new Scenario("Demo", 2014000, 
				Arrays.asList(l_aa, l_bb, l_cc, l_ab, l_ba, l_bc, l_cb), 
				Arrays.asList(e_o1, e_o2, e_o3));
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.app.DefaultFederate#getAmbassador()
	 */
	@Override
	public ISOSambassador getAmbassador() throws RTIexception {
		return new ISOSfedAmbassador();
	}
}
