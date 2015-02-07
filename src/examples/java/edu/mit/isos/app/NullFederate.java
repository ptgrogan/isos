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

import hla.rti1516e.exceptions.RTIexception;

import java.util.Arrays;

import org.apache.log4j.Logger;

import edu.mit.isos.app.elect.ElectElementImpl;
import edu.mit.isos.app.hla.ISOSnullAmbassador;
import edu.mit.isos.app.petrol.PetrolElementImpl;
import edu.mit.isos.app.social.SocialElementImpl;
import edu.mit.isos.app.water.WaterElementImpl;
import edu.mit.isos.app.water.WaterController;
import edu.mit.isos.app.water.WaterPipeline;
import edu.mit.isos.app.water.WaterPlant;
import edu.mit.isos.context.Scenario;
import edu.mit.isos.hla.ISOSambassador;

/**
 * A non-HLA federate encompassing water, electricity, petroleum, 
 * and social systems.
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
 * Defines three petroleum elements:
 *  - e_P1: Petroleum system element at node A
 *  - e_P2: Petroleum system element at node B
 *  - e_P3: Petroleum system element at node C
 * Defines three electricity elements:
 *  - e_E1: Electricity system element at node A
 *  - e_E2: Electricity system element at node B
 *  - e_E3: Electricity system element at node C
 * Defines three social elements:
 *  - e_S1: Social system element at node A
 *  - e_S2: Social system element at node B
 *  - e_S3: Social system element at node C
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class NullFederate extends DefaultFederate {
	protected static Logger logger = Logger.getLogger(NullFederate.class);

	/**
	 * Instantiates a new null federate.
	 *
	 * @param outputDir the output directory
	 * @param numIterations the number of iterations per time step
	 * @param numReplications the number of simulation execution replications
	 * @param timeStep the time step duration
	 */
	public NullFederate(String outputDir, int numIterations, 
			int numReplications, long timeStep) {
		super("Null", outputDir, numIterations, numReplications, timeStep);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.DefaultFederate#buildScenario(double)
	 */
	public Scenario buildScenario(double stepsPerYear) {
		SocialElementImpl e_s1 = new SocialElementImpl("e_S1", l_aa, 
				0.065/stepsPerYear, 4.0/stepsPerYear, 
				1.0/stepsPerYear, 3.0, 0.07/stepsPerYear);
		SocialElementImpl e_s2 = new SocialElementImpl("e_S2", l_bb, 
				0.050/stepsPerYear, 3.0/stepsPerYear, 
				1.2/stepsPerYear, 1.0, 0.05/stepsPerYear);
		SocialElementImpl e_s3 = new SocialElementImpl("e_S3", l_cc, 
				0.060/stepsPerYear, 3.5/stepsPerYear, 
				1.0/stepsPerYear, 6.0, 0.06/stepsPerYear);
		
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
		
		PetrolElementImpl e_o1 = new PetrolElementImpl("e_P1", l_aa, 
				0.5, 1.0, 5000);
		PetrolElementImpl e_o2 = new PetrolElementImpl("e_P2", l_bb, 
				0.8, 1.0, 1000);
		PetrolElementImpl e_o3 = new PetrolElementImpl("e_P3", l_cc, 
				0.6, 1.0, 4000);
		
		ElectElementImpl e_e1 = new ElectElementImpl("e_E1", l_aa, 
				1.0/stepsPerYear, 0.25);
		ElectElementImpl e_e2 = new ElectElementImpl("e_E2", l_bb, 
				0.5/stepsPerYear, 0.30);
		ElectElementImpl e_e3 = new ElectElementImpl("e_E3", l_cc, 
				0.8/stepsPerYear, 0.25);
		return new Scenario("Demo", 2014000, 
				Arrays.asList(l_aa, l_bb, l_cc, l_ab, l_ba, l_bc, l_cb), 
				Arrays.asList(e_s1, e_s2, e_s3, 
						e_w1, e_w2, e_w3, e_w4, e_w5, e_w6, e_w7, e_w8, e_w9, 
						e_o1, e_o2, e_o3, 
						e_e1, e_e2, e_e3));
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.app.DefaultFederate#getAmbassador()
	 */
	@Override
	public ISOSambassador getAmbassador() throws RTIexception {
		return new ISOSnullAmbassador();
	}

}
