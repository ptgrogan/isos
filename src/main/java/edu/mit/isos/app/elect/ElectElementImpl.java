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

import edu.mit.isos.app.ElectElement;
import edu.mit.isos.app.PetrolElement;
import edu.mit.isos.app.SocialElement;
import edu.mit.isos.app.WaterElement;
import edu.mit.isos.context.Location;
import edu.mit.isos.element.DefaultElement;

/**
 * Local implementation of an electricity system element for interface {@link ElectElement}.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class ElectElementImpl extends DefaultElement implements ElectElement {
	/**
	 * Instantiates a new local electricity system element with a 
	 * {@link ElectElementState} operational state.
	 *
	 * @param name the name
	 * @param location the location
	 * @param solarCap the solar generation capacity
	 * @param thermalOil the oil transformation factor for thermal generation
	 */
	public ElectElementImpl(String name, Location location, 
			double solarCap, double thermalOil) {
		super(name, location, new ElectElementState(solarCap, thermalOil));
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.ElectElement#getElectSentToPetrol()
	 */
	@Override
	public double getElectSentToPetrol() {
		if(getState() instanceof ElectElementState) {
			return ((ElectElementState)getState()).getElectSentToPetrol();
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.ElectElement#getElectSentToSocial()
	 */
	@Override
	public double getElectSentToSocial() {
		if(getState() instanceof ElectElementState) {
			return ((ElectElementState)getState()).getElectSentToSocial();
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.ElectElement#getElectSentToWater()
	 */
	@Override
	public double getElectSentToWater() {
		if(getState() instanceof ElectElementState) {
			return ((ElectElementState)getState()).getElectSentToWater();
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.ElectElement#getPetrolReceived()
	 */
	@Override
	public double getPetrolReceived() {
		if(getState() instanceof ElectElementState) {
			return ((ElectElementState)getState()).getPetrolReceived();
		}
		return 0;
	}
	
	/**
	 * Sets the petroleum system customer element for electricity.
	 *
	 * @param element the new customer
	 */
	public void setCustomer(PetrolElement element) {
		if(getInitialState() instanceof ElectElementState) {
			ElectElementState state = (ElectElementState) getInitialState();
			state.petrolCustomer = element;
		}
	}
	
	/**
	 * Sets the social system customer element for electricity.
	 *
	 * @param element the new customer
	 */
	public void setCustomer(SocialElement element) {
		if(getInitialState() instanceof ElectElementState) {
			ElectElementState state = (ElectElementState) getInitialState();
			state.socialCustomer = element;
		}
	}
	
	/**
	 * Sets the water system customer element for electricity.
	 *
	 * @param element the new customer
	 */
	public void setCustomer(WaterElement element) {
		if(getInitialState() instanceof ElectElementState) {
			ElectElementState state = (ElectElementState) getInitialState();
			state.waterCustomer = element;
		}
	}
	
	/**
	 * Sets the petroleum supplier element.
	 *
	 * @param element the new petroleum supplier
	 */
	public void setPetrolSupplier(PetrolElement element) {
		if(getInitialState() instanceof ElectElementState) {
			ElectElementState state = (ElectElementState) getInitialState();
			state.petrolSupplier = element;
		}
	}
}