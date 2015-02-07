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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.mit.isos.app.elect.ElectFederate;
import edu.mit.isos.app.petrol.PetrolFederate;
import edu.mit.isos.app.social.SocialFederate;
import edu.mit.isos.app.water.WaterFederate;

/**
 * Automates the batch execution of federates for performance analysis.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class FederateController {
	protected static Logger logger = Logger.getLogger("edu.mit.isos");
	
	/**
	 * Defines the possible roles for multi-threaded simulation.
	 */
	private static enum Role {SOCIAL, WATER, ELECT, PETROL};
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();
		logger.setLevel(Level.INFO);
		
		// roles are specified in command line arguments
		Collection<Role> roles = new HashSet<Role>();
		for(String arg : args) {
			if(Role.valueOf(arg) != null) {
				roles.add(Role.valueOf(arg));
			}
		}
		String dir = "isos";
		
		multiThread(roles, dir, 5, 2, 1000, 30.0);
		/*
		multiThread(roles, dir, 5, 1, 1000, 30.0);
		for(int itr : new int[]{1, 2, 4, 10, 20, 50}) {
			multiThread(roles, dir, itr, 20, 1000, 30.0);
		}
		for(int stp : new int[]{100, 250, 500, 2500, 10000}) {
			multiThread(roles, dir, 10, 20, stp, 30.0);
		}
		multiThread(roles, dir, 2, 20, 250, 30.0);
		*/
		
		// singleThread(dir, 5, 100, 1000, 30.0);
		/*
		for(int itr : new int[]{1, 2, 4, 10, 20, 50}) {
			singleThread(dir, itr, 20, 1000, 30.0);
		}
		for(int stp : new int[]{100, 250, 500, 2500, 10000}) {
			singleThread(dir, 10, 20, stp, 30.0);
		}
		singleThread(dir, 2, 20, 250, 30.0);
		*/
	}
	
	/**
	 * Launches a single-threaded simulation using a null federate.
	 *
	 * @param dir the output directory
	 * @param itr the number of iterations per time step
	 * @param rep the number of simulation execution replications
	 * @param stp the time step duration
	 * @param dur the simulation execution duration
	 */
	public static void singleThread(String dir, int itr, final int rep, final long stp, final double dur) {
		try {
			new NullFederate(dir, itr, rep, stp).execute(0, dur);
		} catch (RTIexception | IOException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Launches a multi-threaded simulation using one or more federates.
	 *
	 * @param roles the roles to run
	 * @param dir the output directory
	 * @param itr the number of iterations per time step
	 * @param rep the number of simulation execution replications
	 * @param stp the time step duration
	 * @param dur the simulation execution duration
	 */
	public static void multiThread(Collection<Role> roles, String dir, int itr, 
			final int rep, final long stp, final double dur) {
		Map<Role, Boolean> running = Collections.synchronizedMap(new HashMap<Role, Boolean>());

		if(roles.contains(Role.SOCIAL)) {
			running.put(Role.SOCIAL, true);
			new Thread(new Runnable() {
				public void run() {
					try {
						new SocialFederate(dir, itr, rep, stp).execute(5000, dur);
						running.put(Role.SOCIAL, false);
					} catch (RTIexception | IOException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}).start();
		}

		if(roles.contains(Role.WATER)) {
			running.put(Role.WATER, true);
			new Thread(new Runnable() {
				public void run() {
					try {
						new WaterFederate(dir, itr, rep, stp).execute(5000, dur);
						running.put(Role.WATER, false);
					} catch (RTIexception | IOException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}).start();
		}
		
		if(roles.contains(Role.ELECT)) {
			running.put(Role.ELECT, true);
			new Thread(new Runnable() {
				public void run() {
					try {
						new ElectFederate(dir, itr, rep, stp).execute(5000, dur);
						running.put(Role.ELECT, false);
					} catch (RTIexception | IOException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}).start();
		}

		if(roles.contains(Role.PETROL)) {
			running.put(Role.PETROL, true);
			new Thread(new Runnable() {
				public void run() {
					try {
						new PetrolFederate(dir, itr, rep, stp).execute(5000, dur);
						running.put(Role.PETROL, false);
					} catch (RTIexception | IOException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}).start();
		}
		
		// end thread after all federates are complete
		while((roles.contains(Role.SOCIAL) && running.get(Role.SOCIAL))
				|| (roles.contains(Role.WATER) && running.get(Role.WATER))
				|| (roles.contains(Role.ELECT) && running.get(Role.ELECT))
				|| (roles.contains(Role.PETROL) && running.get(Role.PETROL))) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
