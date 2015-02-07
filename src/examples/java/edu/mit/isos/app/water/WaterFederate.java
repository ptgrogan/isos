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

import hla.rti1516e.exceptions.RTIexception;

import java.util.Arrays;

import org.apache.log4j.Logger;

import edu.mit.isos.app.DefaultFederate;
import edu.mit.isos.app.hla.ISOSfedAmbassador;
import edu.mit.isos.context.Scenario;
import edu.mit.isos.hla.ISOSambassador;

/**
 * An HLA-compliant federate for the water system. 
 * Defines nine water elements:
 *  - e_W1: Water system element at node A
 *  - e_W2: Water system element at node B
 *  - e_W3: Water system element at node C
 *  - e_W4: Water desalination plant element at node A
 *  - e_W5: Water desalination plant element at node C
 *  - e_W6: Water desalination plant element at node C
 *  - e_W7: Water pipeline element at location (A,B)
 *  - e_W8: Water pipeline element at node (C,B)
 *  - e_W9: Water controller element at node A
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class WaterFederate extends DefaultFederate {
	protected static Logger logger = Logger.getLogger(WaterFederate.class);
	
	/**
	 * Instantiates a new water federate.
	 *
	 * @param outputDir the output directory
	 * @param numIterations the number of iterations per time step
	 * @param numReplications the number of simulation execution replications
	 * @param timeStep the time step duration
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public WaterFederate(String outputDir, int numIterations,
			int numReplications, long timeStep) throws RTIexception {
		super("Water", outputDir, numIterations, numReplications, timeStep);
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.app.DefaultFederate#buildScenario(double)
	 */
	@Override
	public Scenario buildScenario(double stepsPerYear) {
		WaterElementImpl e_w1 = new WaterElementImpl("e_W1", l_aa, 
				1.0, 0.9, 200);
		WaterElementImpl e_w2 = new WaterElementImpl("e_W2", l_bb, 
				1.0, 0.9, 150);
		WaterElementImpl e_w3 = new WaterElementImpl("e_W3", l_cc, 
				1.0, 0.9, 250);
		WaterPlant e_w4 = new WaterPlant("e_W4", l_aa, 
				(long)(2014000+0*stepsPerYear), 0.5/stepsPerYear, 4.5);
		WaterPlant e_w5 = new WaterPlant("e_W5", l_cc, 
				(long)(2014000+0*stepsPerYear), 0.4/stepsPerYear, 4.5);
		WaterPlant e_w6 = new WaterPlant("e_W6", l_cc, 
				(long)(2014000+5*stepsPerYear), 0.6/stepsPerYear, 4.5);
		WaterPipeline e_w7 = new WaterPipeline("e_W7", l_ab, 
				0.02/stepsPerYear, 0.9, 2.5);
		WaterPipeline e_w8 = new WaterPipeline("e_W8", l_cb, 
				0.02/stepsPerYear, 0.9, 2.0);
		WaterController e_w9 = new WaterController("e_W9", l_aa, 
				Arrays.asList(e_w1, e_w2, e_w3, e_w4, e_w5, e_w6, e_w7, e_w8));
		return new Scenario("Demo", 2014000, 
				Arrays.asList(l_aa, l_bb, l_cc, l_ab, l_ba, l_bc, l_cb), 
				Arrays.asList(e_w1, e_w2, e_w3, e_w4, e_w5, e_w6, e_w7, e_w8, e_w9));
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.app.DefaultFederate#getAmbassador()
	 */
	@Override
	public ISOSambassador getAmbassador() throws RTIexception {
		return new ISOSfedAmbassador();
	}
}
