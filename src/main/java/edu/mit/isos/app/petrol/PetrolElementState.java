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

import java.util.Arrays;

import edu.mit.isos.app.ElectElement;
import edu.mit.isos.app.SocialElement;
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
 * Operational state for a petroleum system element. Produces petroleum to 
 * meet electricity and social system demands with reserves extraction. 
 * Consumes electricity for reserves extraction.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class PetrolElementState extends DefaultState implements ResourceExchanging {
	private ResourceMatrix tfMatrix = new ResourceMatrix();
	
	protected SocialElement socialCustomer = null;
	protected ElectElement electCustomer = null;
	protected ElectElement electSupplier = null;
	private double electReceived, nextElectReceived;
	private double petrolSentSocial, nextPetrolSentSocial;
	private double petrolSentElect, nextPetrolSentElect;
	
	/**
	 * Instantiates a new petroleum element state.
	 *
	 * @param extractElect the electricity transformation factor for extraction
	 * @param extractReserves the reserves transformation factor for extraction
	 */
	public PetrolElementState(double extractElect, double extractReserves) {
		super("Ops");
		tfMatrix = new ResourceMatrix(
				ResourceType.OIL, ResourceFactory.create(
						new ResourceType[]{ResourceType.ELECTRICITY, ResourceType.RESERVES},
						new double[]{extractElect, extractReserves}));
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
		return tfMatrix.multiply(getProduced(element, duration));
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
	 * Gets the petroleum sent to the electricity system.
	 *
	 * @return the petroleum sent
	 */
	public double getPetrolSentToElect() {
		return petrolSentElect;
	}

	/**
	 * Gets the petroleum sent to the social system.
	 *
	 * @return the petroleum sent
	 */
	public double getPetrolSentToSocial() {
		return petrolSentSocial;
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
		return getConsumed(element, duration).get(ResourceType.ELECTRICITY);
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
		return received;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#getRetrieved(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getRetrieved(ElementImpl element, long duration) {
		return getConsumed(element, duration).get(ResourceType.RESERVES);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.ResourceExchanging#getSent(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getSent(ElementImpl element, long duration) {
		Resource sent = ResourceFactory.create();
		for(Element customer : Arrays.asList(socialCustomer, electCustomer)) {
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
			sent = sent.add(ResourceFactory.create(ResourceType.OIL, petrolSentSocial));
		}
		if(element2 != null && element2.equals(electCustomer)) {
			sent = sent.add(ResourceFactory.create(ResourceType.OIL, petrolSentElect));
		}
		return sent;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#initialize(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public void initialize(ElementImpl element, long initialTime) {
		super.initialize(element, initialTime);
		electReceived = nextElectReceived = 0;
		electSupplier = null;
		electCustomer = null;
		socialCustomer = null;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#iterateTick(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public void iterateTick(ElementImpl element, long duration) {
		super.iterateTick(element, duration);
		nextElectReceived = getReceived(element, duration)
				.getQuantity(ResourceType.ELECTRICITY);
		nextPetrolSentSocial = socialCustomer==null?0:socialCustomer.getPetrolReceived();
		nextPetrolSentElect = electCustomer==null?0:electCustomer.getPetrolReceived();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#iterateTock()
	 */
	@Override
	public void iterateTock() {
		super.iterateTock();
		electReceived = nextElectReceived;
		petrolSentSocial = nextPetrolSentSocial;
		petrolSentElect = nextPetrolSentElect;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#tick(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public void tick(ElementImpl element, long duration) {
		super.tick(element, duration);
		for(Element customer : Arrays.asList(socialCustomer, electCustomer)) {
			exchange(element, customer, getSentTo(element, customer, duration), 
					ResourceFactory.create());
		}
		for(Element supplier : Arrays.asList(electSupplier)) {
			exchange(element, supplier, ResourceFactory.create(), 
					getReceivedFrom(element, supplier, duration));
		}
	}
}