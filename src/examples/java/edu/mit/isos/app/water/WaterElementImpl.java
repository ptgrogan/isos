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

import edu.mit.isos.app.ElectElement;
import edu.mit.isos.app.SocialElement;
import edu.mit.isos.app.WaterElement;
import edu.mit.isos.context.Location;
import edu.mit.isos.context.ResourceFactory;
import edu.mit.isos.context.ResourceType;
import edu.mit.isos.element.DefaultElement;

/**
 * Local implementation of a water system element for interface {@link WaterElement}.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class WaterElementImpl extends DefaultElement implements WaterElement {	
	
	/**
	 * Instantiates a new local water element with a 
	 * {@link WaterElementState} operational state.
	 *
	 * @param name the name
	 * @param location the location
	 * @param liftAquifer the aquifer transformation factor for lifting
	 * @param liftElect the electricity transformation factor for lifting
	 * @param initialAquifer the initial aquifer quantity
	 */
	public WaterElementImpl(String name, Location location,
			double liftAquifer, double liftElect, double initialAquifer) {
		super(name, location, new WaterElementState(liftAquifer, liftElect));
		initialContents(ResourceFactory.create(ResourceType.AQUIFER, initialAquifer));
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.WaterElement#getElectReceived()
	 */
	@Override
	public double getElectReceived() {
		if(getState() instanceof WaterElementState) {
			return ((WaterElementState)getState()).getElectReceived();
		}
		return 0;
	}
	
	/**
	 * Gets the operating state.
	 *
	 * @return the operating state
	 */
	public WaterElementState getOperatingState() {
		return (WaterElementState) getInitialState();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.WaterElement#getWaterSentToSocial()
	 */
	@Override
	public double getWaterSentToSocial() {
		if(getState() instanceof WaterElementState) {
			return ((WaterElementState)getState()).getWaterSentToSocial();
		}
		return 0;
	}
	
	/**
	 * Sets the customer.
	 *
	 * @param element the new customer
	 */
	public void setCustomer(SocialElement element) {
		if(getInitialState() instanceof WaterElementState) {
			WaterElementState state = (WaterElementState) getInitialState();
			state.socialCustomer = element;
		}
	}
	
	/**
	 * Sets the elect supplier.
	 *
	 * @param element the new elect supplier
	 */
	public void setElectSupplier(ElectElement element) {
		if(getInitialState() instanceof WaterElementState) {
			WaterElementState state = (WaterElementState) getInitialState();
			state.electSupplier = element;
		}
	}
}