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

import java.util.Arrays;

import edu.mit.isos.app.PetrolElement;
import edu.mit.isos.app.SocialElement;
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
 * Operational state for an electricity system element. Generates electricity 
 * to meet petroleum, social, and water system needs with solar and 
 * thermal generation. Consumes petroleum for thermal generation.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class ElectElementState extends DefaultState implements ResourceExchanging {
	private ResourceMatrix tfMatrix = new ResourceMatrix();
	private Resource solarCapacity = ResourceFactory.create();
	
	protected PetrolElement petrolSupplier = null;
	protected PetrolElement petrolCustomer = null;
	protected SocialElement socialCustomer = null;
	protected WaterElement waterCustomer = null;
	private double petrolReceived, nextPetrolReceived;
	private double electSentSocial, nextElectSentSocial;
	private double electSentWater, nextElectSentWater;
	private double electSentPetrol, nextElectSentPetrol;
	
	/**
	 * Instantiates a new electricity system element state.
	 *
	 * @param solarCap the solar generation capacity
	 * @param thermalOil the thermal oil transformation factor
	 */
	public ElectElementState(double solarCap, double thermalOil) {
		super("Ops");
		tfMatrix = new ResourceMatrix(
				ResourceType.ELECTRICITY, 
				ResourceFactory.create(ResourceType.OIL, thermalOil));

		solarCapacity = ResourceFactory.create(
				ResourceType.ELECTRICITY, solarCap);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.ResourceExchanging#exchange(edu.mit.isos.element.ElementImpl, edu.mit.isos.element.Element, edu.mit.isos.context.Resource, edu.mit.isos.context.Resource)
	 */
	@Override
	public void exchange(ElementImpl element1, Element element2, 
			Resource sent, Resource received) {
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
		return tfMatrix.multiply(getProduced(element, duration)
				.subtract(solarCapacity.multiply(duration))
				.truncatePositive());
	}
	
	/**
	 * Gets the quantity of electricity sent to the petroleum system.
	 *
	 * @return the electricity sent
	 */
	public double getElectSentToPetrol() {
		return electSentPetrol;
	}
	
	/**
	 * Gets the quantity of electricity sent to the social system.
	 *
	 * @return the electricity sent
	 */
	public double getElectSentToSocial() {
		return electSentSocial;
	}
	
	/**
	 * Gets the quantity of electricity sent to the water system.
	 *
	 * @return the electricity sent
	 */
	public double getElectSentToWater() {
		return electSentWater;
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
	public Resource getNetFlow(ElementImpl element, Location location, 
			long duration) {
		Resource netFlow = super.getNetFlow(element, location, duration);
		if(location.isStatic() && location.getOrigin().equals(
				element.getLocation().getOrigin())) {
			netFlow = netFlow.subtract(getSent(element, duration))
					.add(getReceived(element, duration));
		}
		return netFlow;
	}

	/**
	 * Gets the quantity of petroleum received.
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
		return getSent(element, duration);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.ResourceExchanging#getReceived(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getReceived(ElementImpl element, long duration) {
		return getConsumed(element, duration).get(ResourceType.OIL);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.ResourceExchanging#getReceivedFrom(edu.mit.isos.element.ElementImpl, edu.mit.isos.element.Element, long)
	 */
	@Override
	public Resource getReceivedFrom(ElementImpl element1, Element element2, 
			long duration) {
		Resource received = ResourceFactory.create();
		if(element2 != null && element2.equals(petrolSupplier)) {
			received = received.add(getReceived(element1, duration).get(ResourceType.OIL));
		}
		return received;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.ResourceExchanging#getSent(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getSent(ElementImpl element, long duration) {
		Resource sent = ResourceFactory.create();
		for(Element customer : Arrays.asList(socialCustomer, petrolCustomer, waterCustomer)) {
			sent = sent.add(getSentTo(element, customer, duration));
		}
		return sent;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.ResourceExchanging#getSentTo(edu.mit.isos.element.ElementImpl, edu.mit.isos.element.Element, long)
	 */
	@Override
	public Resource getSentTo(ElementImpl element1, Element element2, long duration) {
		Resource sent = ResourceFactory.create();
		if(element2 != null && element2.equals(socialCustomer)) {
			sent = sent.add(ResourceFactory.create(
					ResourceType.ELECTRICITY, electSentSocial));
		}
		if(element2 != null && element2.equals(petrolCustomer)) {
			sent = sent.add(ResourceFactory.create(
					ResourceType.ELECTRICITY, electSentPetrol));
		}
		if(element2 != null && element2.equals(waterCustomer)) {
			sent = sent.add(ResourceFactory.create(
					ResourceType.ELECTRICITY, electSentWater));
		}
		return sent;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#initialize(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public void initialize(ElementImpl element, long initialTime) {
		super.initialize(element, initialTime);
		petrolReceived = nextPetrolReceived = 0;
		petrolSupplier = null;
		petrolCustomer = null;
		socialCustomer = null;
		waterCustomer = null;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#iterateTick(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public void iterateTick(ElementImpl element, long duration) {
		super.iterateTick(element, duration);
		nextPetrolReceived = getReceived(element, duration)
				.getQuantity(ResourceType.OIL);
		nextElectSentSocial = socialCustomer==null?0:socialCustomer.getElectReceived();
		nextElectSentWater = waterCustomer==null?0:waterCustomer.getElectReceived();
		nextElectSentPetrol = petrolCustomer==null?0:petrolCustomer.getElectReceived();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#iterateTock()
	 */
	@Override
	public void iterateTock() {
		super.iterateTock();
		petrolReceived = nextPetrolReceived;
		electSentSocial = nextElectSentSocial;
		electSentWater = nextElectSentWater;
		electSentPetrol = nextElectSentPetrol;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#tick(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public void tick(ElementImpl element, long duration) {
		super.tick(element, duration);
		for(Element customer : Arrays.asList(socialCustomer, petrolCustomer, waterCustomer)) {
			exchange(element, customer, getSentTo(element, customer, duration), 
					ResourceFactory.create());
		}
		for(Element supplier : Arrays.asList(petrolSupplier)) {
			exchange(element, supplier, ResourceFactory.create(), 
					getReceivedFrom(element, supplier, duration));
		}
	}
}