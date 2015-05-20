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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.mit.isos.context.Location;
import edu.mit.isos.context.Node;
import edu.mit.isos.context.Resource;
import edu.mit.isos.context.ResourceFactory;
import edu.mit.isos.context.Scenario;
import edu.mit.isos.element.Element;
import edu.mit.isos.element.ElementImpl;
import edu.mit.isos.hla.ISOSambassador;
import edu.mit.isos.sim.SimulationTimeEvent;
import edu.mit.isos.sim.SimulationTimeListener;
import edu.mit.isos.sim.Simulator;
import edu.mit.isos.state.ResourceExchanging;
import edu.mit.isos.state.ResourceTransforming;
import edu.mit.isos.state.ResourceTransporting;

/**
 * A default class common to both HLA and non-HLA (Null) federates. Defines three 
 * nodes (A, B, C) and seven locations:
 *  - (A,A): Static location at node A
 *  - (B,B): Static location at node B
 *  - (C,C): Static location at node C
 *  - (A,B): Dynamic location between nodes A and B
 *  - (B,A): Dynamic location between nodes B and A
 *  - (B,C): Dynamic location between nodes B and C
 *  - (C,B): Dynamic location between nodes C and B
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public abstract class DefaultFederate {
	protected static Logger logger = Logger.getLogger(DefaultFederate.class);
	
	// Simulation and output options.
	private boolean replicationOutputs = true;
	private boolean retainReplicationOutputs = false;
	private final int numIterations;
	private final int numReplications;
	private final int stepsPerYear = 1000;
	private final long timeStep;
	private final String outputDir;
	private final String federateName;
	
	// Define scenario nodes and locations.
	protected final Node n_a = new Node("A");
	protected final Node n_b = new Node("B");
	protected final Node n_c = new Node("C");
	protected final Location l_aa = new Location(n_a, n_a);
	protected final Location l_bb = new Location(n_b, n_b);
	protected final Location l_cc = new Location(n_c, n_c);
	protected final Location l_ab = new Location(n_a, n_b);
	protected final Location l_ba = new Location(n_b, n_a);
	protected final Location l_bc = new Location(n_b, n_c);
	protected final Location l_cb = new Location(n_c, n_b);
	
	/**
	 * Instantiates a new default federate.
	 *
	 * @param federateName the federate name
	 * @param outputDir the output directory path
	 * @param numIterations the number of iterations per time step
	 * @param numReplications the number of simulation execution replications
	 * @param timeStep the time step duration
	 */
	public DefaultFederate(String federateName, String outputDir, 
			int numIterations, int numReplications, long timeStep) {
		this.federateName = federateName;
		this.outputDir = outputDir;
		this.numIterations = numIterations;
		this.numReplications = numReplications;
		this.timeStep = timeStep;
	}
	
	/**
	 * Gets this federate's ambassador.
	 *
	 * @return the ambassador
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public abstract ISOSambassador getAmbassador() throws RTIexception;
	
	/**
	 * Executes this federate's simulation.
	 *
	 * @param initTimeout milliseconds to wait before initialize activity
	 * @param simulationDuration the simulation duration
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public void execute(long initTimeout, double simulationDuration) 
			throws IOException, RTIexception {
		Path dirPath = Paths.get(outputDir);
		if(!dirPath.toFile().exists()) {
			dirPath.toFile().mkdir();
		}
		
		String testName = numReplications+"rep"
				+numIterations+"itr"
				+timeStep+"stp";
		
		Path outputDirPath = Paths.get(outputDir,testName);
		if(!outputDirPath.toFile().exists()) {
			outputDirPath.toFile().mkdir();
		}
		
		Path summaryPath = Paths.get(outputDir,testName,federateName+"-summary.txt");
		if(!summaryPath.toFile().exists()) {
			summaryPath.toFile().createNewFile();
		}

		// create an output writer to handle file output
		final BufferedWriter summaryWriter = Files.newBufferedWriter(
				summaryPath, Charset.defaultCharset(), 
				StandardOpenOption.WRITE);
		summaryWriter.write(String.format("%6s%20s%20s%20s\n",
				"Run","Total Time (ms)","Init Time (ms)","Exec Time (ms)"));
		summaryWriter.flush();

		final Simulator sim = new Simulator(buildScenario(stepsPerYear));;
		
		for(int i = 0; i < numReplications; i++) {
			Path currentOutputDirPath = Paths.get(outputDir,testName,
					new Integer(i+1).toString());
			if(!currentOutputDirPath.toFile().exists()) {
				currentOutputDirPath.toFile().mkdir();
			}
			
			final Map<ElementImpl, BufferedWriter> elementWriters = 
					new HashMap<ElementImpl, BufferedWriter>();
			if(replicationOutputs || i == 1) {
				for(ElementImpl e : sim.getScenario().getElements()) {
					Path elementPath = Paths.get(outputDir,testName,
							new Integer(i+1).toString(),e.getName() + ".txt");
					if(!elementPath.toFile().exists()) {
						elementPath.toFile().createNewFile();
					}
					BufferedWriter writer = Files.newBufferedWriter(
							elementPath, Charset.defaultCharset(), 
							StandardOpenOption.WRITE);
					writer.write(String.format(
							"%6s%10s%10s%10s%10s%60s%60s%60s%60s%60s%60s%60s\n", 
							"Time", "Element", "State", "Location", "Parent", "Contents", 
							"Consumed", "Produced", "Input", "Output", "Sent", "Received"));
					elementWriters.put(e, writer);
				}
			}
			
			Path warningPath = Paths.get(outputDir,testName,
					new Integer(i+1).toString(),federateName+"-warnings.txt");
			if(!warningPath.toFile().exists()) {
				warningPath.toFile().createNewFile();
			}
			final BufferedWriter warningWriter = Files.newBufferedWriter(warningPath, 
					Charset.defaultCharset(), StandardOpenOption.WRITE);
			warningWriter.write(String.format("%6s%10s%20s%60s%60s\n",
					"Time","Type","Unit(s)","Error","% Error"));
			
			final ISOSambassador amb = getAmbassador();
			
			SimulationTimeListener listener = new SimulationTimeListener() {
				@Override
				public void timeAdvanced(SimulationTimeEvent event) {
					for(ElementImpl e : elementWriters.keySet()) {
						try {
							elementWriters.get(e).write(String.format(
									"%6d%10s%10s%10s%10s%60s%60s%60s%60s%60s%60s%60s\n", 
									event.getTime(), 
									e.getName(),
									e.getState(),
									e.getLocation(), 
									e.getParent().getName(),
									e.getContents(), 
									(e.getState() instanceof ResourceTransforming)?((ResourceTransforming)e.getState()).getConsumed(e, event.getDuration()):"NaN", 
									(e.getState() instanceof ResourceTransforming)?((ResourceTransforming)e.getState()).getProduced(e, event.getDuration()):"NaN", 
									(e.getState() instanceof ResourceTransporting)?((ResourceTransporting)e.getState()).getInput(e, event.getDuration()):"NaN", 
									(e.getState() instanceof ResourceTransporting)?((ResourceTransporting)e.getState()).getOutput(e, event.getDuration()):"NaN", 
									(e.getState() instanceof ResourceExchanging)?((ResourceExchanging)e.getState()).getSent(e, event.getDuration()):"NaN", 
									(e.getState() instanceof ResourceExchanging)?((ResourceExchanging)e.getState()).getReceived(e, event.getDuration()):"NaN"));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					for(Location l : sim.getScenario().getLocations()) {
						Resource netFlow = ResourceFactory.create();
						for(ElementImpl element : sim.getScenario().getElements()) {
							netFlow = netFlow.add(element.getNetFlow(l, event.getDuration()));
						}
						if(!netFlow.isZero()) {
							try {
								warningWriter.write(String.format("%6d%10s%20s%60s%60s\n",
										event.getTime(), 
										"Net Flow", l.toString(), 
										netFlow, "NaN"));
							} catch (IOException e) {
								logger.error(e);
								e.printStackTrace();
							}
						}
					}
					for(Element e1 : sim.getScenario().getElements()) {
						for(Element e2 : amb.getElements()) {
							Resource r12 = e1.getNetExchange(e2, event.getDuration());
							Resource r21 = e2.getNetExchange(e1, event.getDuration());
							if(!r12.add(r21).isZero()) {
								try {
									warningWriter.write(String.format("%6d%10s%20s%60s%60s\n",
											event.getTime(), 
											"Exchange", e1.getName() + "<->" + e2.getName(), 
											r12.add(r21), r12.add(r21).absoluteValue().safeDivide(r12.absoluteValue())));
								} catch (IOException e) {
									logger.error(e);
									e.printStackTrace();
								}
							}
						}
					}
				}
			};
			
			sim.addSimulationTimeListener(listener);
			
			String fomPath = "edu/mit/isos/app/hla/isos.xml";

			amb.connect("ISOS Test " + (i+1), fomPath, federateName, "Test");
			
			// wait for other federates to join
			if(initTimeout > 0) {
				try {
					Thread.sleep(initTimeout);
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
			
			long initStartTime = new Date().getTime();
			sim.initialize(amb, federateName, timeStep, numIterations);
			long initEndTime = new Date().getTime();
			
			long execStartTime = new Date().getTime();
			sim.execute(amb, federateName, (int) (simulationDuration*stepsPerYear), 
					timeStep, numIterations);
			long execEndTime = new Date().getTime();
			long initTime = initEndTime - initStartTime;
			long execTime = execEndTime - execStartTime;
			long totalTime = initTime + execTime;
			
			logger.info("Simulation completed in " + totalTime + " ms");
			summaryWriter.write(String.format("%6d%20d%20d%20d\n",(i+1),totalTime, initTime, execTime));
			summaryWriter.flush();
			
			for(Element e : elementWriters.keySet()) {
				elementWriters.get(e).close();
				Path elementPath = Paths.get(outputDir,testName,new Integer(i+1).toString(),e.getName() + ".txt");
				if(i >= 1 && elementPath.toFile().exists() && !retainReplicationOutputs) {
					elementPath.toFile().delete();
				}
			}
			
			warningWriter.close();
			
			sim.removeSimulationTimeListener(listener);
			
			amb.disconnect("ISOS Test " + (i+1));
		}
		
		summaryWriter.close();
	}
	
	/**
	 * Builds this federate's scenario.
	 *
	 * @param stepsPerYear the steps per year
	 * @return the scenario
	 */
	public abstract Scenario buildScenario(double stepsPerYear);
}
