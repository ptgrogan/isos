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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NoFeasibleSolutionException;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import edu.mit.isos.context.Resource;
import edu.mit.isos.context.ResourceFactory;
import edu.mit.isos.context.ResourceType;
import edu.mit.isos.element.ElementImpl;
import edu.mit.isos.state.NullState;

/**
 * Operational state for a water controller. Implements a linear program to 
 * optimize production quantities for water plants (desalination) and system 
 * elements (aquifer lifting) and distribution quantities for water pipelines.
 * Meets demands at minimum aquifer consumption.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class WaterControllerState extends NullState {
	private List<ElementImpl> elements = new ArrayList<ElementImpl>();
	
	/**
	 * Instantiates a new controller state.
	 *
	 * @param elements the elements
	 */
	public WaterControllerState(Collection<? extends ElementImpl> elements) {
		this.elements.addAll(elements);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.NullState#iterateTick(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public void iterateTick(ElementImpl element, long duration) {
		Set<WaterPlant> plants = new HashSet<WaterPlant>();
		Set<WaterPipeline> pipelines = new HashSet<WaterPipeline>();
		Set<WaterElementImpl> systems  = new HashSet<WaterElementImpl>();
		for(ElementImpl e : elements) {
			if(e instanceof WaterPlant && ((WaterPlant)e).isOperating()) {
				plants.add((WaterPlant)e);
			}
			if(e instanceof WaterPipeline) {
				pipelines.add((WaterPipeline)e);
			}
			if(e instanceof WaterElementImpl) {
				systems.add((WaterElementImpl)e);
			}
		}

		List<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		double[] costCoefficients = new double[elements.size()];
		double[] initialValues = new double[elements.size()];
		for(WaterElementImpl e : systems) {
			// cost of lifting aquifer is 1
			costCoefficients[elements.indexOf(e)] = 1;
			initialValues[elements.indexOf(e)] = ((WaterElementState)e.getState())
					.getProduced(e, duration).getQuantity(ResourceType.WATER);
		}
		for(WaterPlant e : plants) {
			initialValues[elements.indexOf(e)] = e.getOperatingState()
					.getProduced(e, duration).getQuantity(ResourceType.WATER);
			double[] productionCoefficients = new double[elements.size()];
			productionCoefficients[elements.indexOf(e)] = 1;
			constraints.add(new LinearConstraint(productionCoefficients, Relationship.LEQ, 
					e.getOperatingState().productionCapacity.multiply(duration).getQuantity(ResourceType.WATER)));
		}
		for(WaterPipeline e : pipelines) {
			initialValues[elements.indexOf(e)] = e.getOperatingState()
					.getOutput(e, duration).getQuantity(ResourceType.WATER);
			double[] outputCoefficients = new double[elements.size()];
			outputCoefficients[elements.indexOf(e)] = 1;
			constraints.add(new LinearConstraint(outputCoefficients, Relationship.LEQ, 
					e.getOperatingState().outputCapacity.multiply(duration).getQuantity(ResourceType.WATER)));
		}

		for(WaterElementImpl e : systems) {
			double[] flowCoefficients = new double[elements.size()];
			flowCoefficients[elements.indexOf(e)] = 1; // system production
			for(WaterPlant plant : plants) {
				if(plant.getLocation().equals(e.getLocation())) {
					flowCoefficients[elements.indexOf(plant)] = 1; // plant production
				}
			}
			for(WaterPipeline pipeline : pipelines) {
				if(pipeline.getLocation().getOrigin().equals(
						e.getLocation().getOrigin())) {
					flowCoefficients[elements.indexOf(pipeline)] 
							= -1/pipeline.getOperatingState().eta; // pipeline input
				} else if(pipeline.getLocation().getDestination().equals(
						e.getLocation().getOrigin())) {
					flowCoefficients[elements.indexOf(pipeline)] = 1; // pipeline output
				}
			}
			constraints.add(new LinearConstraint(flowCoefficients, Relationship.EQ, 
					((WaterElementState)e.getState()).getSent(e, duration).getQuantity(ResourceType.WATER)));
		}

		try {
			// Run optimization and get results.
			PointValuePair output = new SimplexSolver().optimize(
					GoalType.MINIMIZE,
					new MaxIter(1000),
					new NonNegativeConstraint(true), 
					new LinearConstraintSet(constraints), 
					new LinearObjectiveFunction(costCoefficients, 0d),
					new InitialGuess(initialValues));
			for(WaterElementImpl e : systems) {
				e.getOperatingState().setProduced(e, ResourceFactory.create(ResourceType.WATER,
						output.getPoint()[elements.indexOf(e)]), duration);
			}
			for(WaterPlant e : plants) {
				e.getOperatingState().setProduced(e, ResourceFactory.create(ResourceType.WATER,
						output.getPoint()[elements.indexOf(e)]), duration);
			}
			for(WaterPipeline e : pipelines) {
				e.getOperatingState().setOutput(e, ResourceFactory.create(ResourceType.WATER,
						output.getPoint()[elements.indexOf(e)]), duration);
			}
		} catch(TooManyIterationsException ignore) { 
			// Don't overwrite existing values.
			ignore.printStackTrace();
		} catch(NoFeasibleSolutionException ignore) {
			// Don't overwrite existing values.
			ignore.printStackTrace();
		}

		for(WaterElementImpl system : systems) {
			Resource received = system.getOperatingState()
					.getConsumed(system, duration).get(ResourceType.ELECTRICITY);
			for(WaterPlant plant : plants) {
				if(plant.getLocation().equals(system.getLocation())){
					received = received.add(plant.getOperatingState()
							.getConsumed(plant, duration).get(ResourceType.ELECTRICITY));
				}
			}
			for(WaterPipeline pipeline : pipelines) {
				if(pipeline.getLocation().getOrigin().equals(
						system.getLocation().getOrigin())) {
					received = received.add(pipeline.getOperatingState()
							.getInput(pipeline, duration).get(ResourceType.ELECTRICITY));
				}
			}
			system.getOperatingState().setReceived(system, received, duration);
		}
	}
}