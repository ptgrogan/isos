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

import edu.mit.isos.app.ElectElement;
import edu.mit.isos.app.PetrolElement;
import edu.mit.isos.app.WaterElement;
import edu.mit.isos.context.Location;
import edu.mit.isos.context.Resource;
import edu.mit.isos.context.ResourceFactory;
import edu.mit.isos.context.ResourceMatrix;
import edu.mit.isos.context.ResourceType;
import edu.mit.isos.element.Element;
import edu.mit.isos.element.ElementImpl;
import edu.mit.isos.state.DefaultState;
import edu.mit.isos.state.ResourceExchanging;

/**
 * Operational state for a social system element. Stores population with 
 * logistic growth. Consumes electricity, petroleum, and water based 
 * on per-capita demands.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class SocialElementState extends DefaultState implements ResourceExchanging {
	private ResourceMatrix demandMatrix = new ResourceMatrix();
	private double growthRate;
	
	protected ElectElement electSupplier = null;
	protected PetrolElement petrolSupplier = null;
	protected WaterElement waterSupplier = null;
	private double electReceived, nextElectReceived;
	private double petrolReceived, nextPetrolReceived;
	private double waterReceived, nextWaterReceived;
	
	/**
	 * Instantiates a new social system element state.
	 *
	 * @param waterPC the per-capita water demand
	 * @param electPC the per-capital electricity demand
	 * @param oilPC the per-capita petroleum demand
	 * @param growthRate the population growth rate
	 */
	public SocialElementState(double waterPC, double electPC, double oilPC, double growthRate) {
		super("Ops");
		demandMatrix = new ResourceMatrix(
				ResourceType.PEOPLE, ResourceFactory.create(
						new ResourceType[]{ResourceType.ELECTRICITY, 
								ResourceType.WATER, ResourceType.OIL},
								new double[]{electPC, waterPC, oilPC}));
		this.growthRate = growthRate;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.ResourceExchanging#exchange(edu.mit.isos.element.ElementImpl, edu.mit.isos.element.Element, edu.mit.isos.context.Resource, edu.mit.isos.context.Resource)
	 */
	@Override
	public void exchange(ElementImpl element1, Element element2, Resource sent, Resource received) {
		if(!sent.isZero() && !element1.getLocation().getDestination().equals(
				element2.getLocation().getOrigin())) {
			throw new IllegalArgumentException("Incompatible resource exchange, " 
					+ element1.getName() + " destination " 
					+ element1.getLocation().getDestination() + " =/= " 
					+ element2.getName() + " origin " 
					+ element2.getLocation().getOrigin());
		}
		if(!received.isZero() && !element1.getLocation().getOrigin().equals(
				element2.getLocation().getDestination())) {
			throw new IllegalArgumentException("Incompatible resource exchange: " 
					+ element1.getName() + " origin " 
					+ element1.getLocation().getOrigin() + " =/= " + 
					element2.getName() + " destination " 
					+ element2.getLocation().getDestination());
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#getConsumed(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getConsumed(ElementImpl element, long duration) {
		return demandMatrix.multiply(element.getContents().get(ResourceType.PEOPLE))
				.multiply(duration);
	}

	/**
	 * Gets the electricity received.
	 *
	 * @return the electricity received
	 */
	public double getElectReceived() {
		return electReceived;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#getNetExchange(edu.mit.isos.element.ElementImpl, edu.mit.isos.element.Element, long)
	 */
	@Override
	public Resource getNetExchange(ElementImpl element1, Element element2,
			long duration) {
		Resource netExchange = super.getNetExchange(element1, element2, duration);
		netExchange = netExchange.add(getSentTo(element1, element2, duration))
				.subtract(getReceivedFrom(element1, element2, duration));
		return netExchange;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#getNetFlow(edu.mit.isos.element.ElementImpl, edu.mit.isos.context.Location, long)
	 */
	@Override
	public Resource getNetFlow(ElementImpl element, Location location, long duration) {
		Resource netFlow = super.getNetFlow(element, location, duration);
		if(location.isStatic() && location.getOrigin().equals(element.getLocation().getOrigin())) {
			netFlow = netFlow.subtract(getSent(element, duration))
					.add(getReceived(element, duration));
		}
		return netFlow;
	}
	
	/**
	 * Gets the petroleum received.
	 *
	 * @return the petroleum received
	 */
	public double getPetrolReceived() {
		return petrolReceived;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#getProduced(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getProduced(ElementImpl element, long duration) {
		return element.getContents().get(ResourceType.PEOPLE)
				.multiply(Math.exp(growthRate*duration)-1);
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.state.ResourceExchanging#getReceived(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getReceived(ElementImpl element, long duration) {
		return getConsumed(element, duration);
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.state.ResourceExchanging#getReceivedFrom(edu.mit.isos.element.ElementImpl, edu.mit.isos.element.Element, long)
	 */
	@Override
	public Resource getReceivedFrom(ElementImpl element1, Element element2, long duration) {
		Resource received = ResourceFactory.create();
		if(element2 != null && element2.equals(electSupplier)) {
			received = received.add(getReceived(element1, duration).get(ResourceType.ELECTRICITY));
		}
		if(element2 != null && element2.equals(petrolSupplier)) {
			received = received.add(getReceived(element1, duration).get(ResourceType.OIL));
		}
		if(element2 != null && element2.equals(waterSupplier)) {
			received = received.add(getReceived(element1, duration).get(ResourceType.WATER));
		}
		return received;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.ResourceExchanging#getSent(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getSent(ElementImpl element, long duration) {
		Resource sent = ResourceFactory.create();
		return sent;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.ResourceExchanging#getSentTo(edu.mit.isos.element.ElementImpl, edu.mit.isos.element.Element, long)
	 */
	@Override
	public Resource getSentTo(ElementImpl element1, Element element2, long duration) {
		Resource sent = ResourceFactory.create();
		return sent;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#getStored(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getStored(ElementImpl element, long duration) {
		return getProduced(element, duration);
	}
	
	/**
	 * Gets the water received.
	 *
	 * @return the water received
	 */
	public double getWaterReceived() {
		return waterReceived;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#initialize(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public void initialize(ElementImpl element, long initialTime) {
		super.initialize(element, initialTime);
		electSupplier = null;
		petrolSupplier = null;
		waterSupplier = null;
		electReceived = nextElectReceived = 0;
		petrolReceived = nextPetrolReceived = 0;
		waterReceived = nextWaterReceived = 0;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#iterateTick(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public void iterateTick(ElementImpl element, long duration) {
		super.iterateTick(element, duration);
		nextElectReceived = getReceived(element, duration)
				.getQuantity(ResourceType.ELECTRICITY);
		nextPetrolReceived = getReceived(element, duration)
				.getQuantity(ResourceType.OIL);
		nextWaterReceived = getReceived(element, duration)
				.getQuantity(ResourceType.WATER);
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#iterateTock()
	 */
	@Override
	public void iterateTock() {
		super.iterateTock();
		electReceived = nextElectReceived;
		petrolReceived = nextPetrolReceived;
		waterReceived = nextWaterReceived;
	}
}