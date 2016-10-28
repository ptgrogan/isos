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
package edu.mit.isos.app.social;

import hla.rti1516e.exceptions.RTIexception;

import java.util.Arrays;

import org.apache.log4j.Logger;

import edu.mit.isos.app.DefaultFederate;
import edu.mit.isos.app.hla.ISOSfedAmbassador;
import edu.mit.isos.context.Scenario;
import edu.mit.isos.hla.ISOSambassador;

/**
 * An HLA-compliant federate for the social system. 
 * Defines three social elements:
 *  - e_S1: Social system element at node A
 *  - e_S2: Social system element at node B
 *  - e_S3: Social system element at node C
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class SocialFederate extends DefaultFederate {
	protected static Logger logger = Logger.getLogger(SocialFederate.class);
	
	/**
	 * Instantiates a new social federate.
	 *
	 * @param outputDir the output directory
	 * @param numIterations the number of iterations per time step
	 * @param numReplications the number of simulation execution replications
	 * @param timeStep the time step duration
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public SocialFederate(String outputDir, int numIterations, 
			int numReplications, long timeStep) throws RTIexception {
		super("Social", outputDir, numIterations, numReplications, timeStep);
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.app.DefaultFederate#buildScenario(double)
	 */
	@Override
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
		return new Scenario("Demo", 2014000, 
				Arrays.asList(l_aa, l_bb, l_cc, l_ab, l_ba, l_bc, l_cb), 
				Arrays.asList(e_s1, e_s2, e_s3));
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.app.DefaultFederate#getAmbassador()
	 */
	@Override
	public ISOSambassador getAmbassador() throws RTIexception {
		return new ISOSfedAmbassador();
	}
}
