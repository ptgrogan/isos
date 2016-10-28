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

import edu.mit.isos.app.ElectElement;
import edu.mit.isos.app.PetrolElement;
import edu.mit.isos.app.SocialElement;
import edu.mit.isos.context.Location;
import edu.mit.isos.context.ResourceFactory;
import edu.mit.isos.context.ResourceType;
import edu.mit.isos.element.DefaultElement;

/**
 * Local implementation of a petroleum system element for interface {@link PetrolElement}.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class PetrolElementImpl extends DefaultElement implements PetrolElement {
	
	/**
	 * Instantiates a new local petroleum system element with a 
	 * {@link PetroleumElementState} operational state.
	 *
	 * @param name the name
	 * @param location the location
	 * @param extractElect the electricity transformation factor for extraction
	 * @param extractReserves the reserves transformation factor for extraction
	 * @param initialReserves the initial reserves quantity
	 */
	public PetrolElementImpl(String name, Location location, 
			double extractElect, double extractReserves, 
			double initialReserves) {
		super(name, location, new PetrolElementState(
				extractElect, extractReserves));
		initialContents(ResourceFactory.create(
				ResourceType.RESERVES, initialReserves));
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.PetrolElement#getElectReceived()
	 */
	@Override
	public double getElectReceived() {
		if(getState() instanceof PetrolElementState) {
			return ((PetrolElementState)getState()).getElectReceived();
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.PetrolElement#getPetrolSentToElect()
	 */
	@Override
	public double getPetrolSentToElect() {
		if(getState() instanceof PetrolElementState) {
			return ((PetrolElementState)getState()).getPetrolSentToElect();
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.PetrolElement#getPetrolSentToSocial()
	 */
	@Override
	public double getPetrolSentToSocial() {
		if(getState() instanceof PetrolElementState) {
			return ((PetrolElementState)getState()).getPetrolSentToSocial();
		}
		return 0;
	}
	
	/**
	 * Sets the petroleum customer in the electricity system.
	 *
	 * @param element the new customer
	 */
	public void setCustomer(ElectElement element) {
		if(getInitialState() instanceof PetrolElementState) {
			PetrolElementState state = (PetrolElementState) getInitialState();
			state.electCustomer = element;
		}
	}
	
	/**
	 * Sets the petroleum customer element in the social system.
	 *
	 * @param element the new customer
	 */
	public void setCustomer(SocialElement element) {
		if(getInitialState() instanceof PetrolElementState) {
			PetrolElementState state = (PetrolElementState) getInitialState();
			state.socialCustomer = element;
		}
	}
	
	/**
	 * Sets the electricity supplier element.
	 *
	 * @param element the new electricity supplier
	 */
	public void setElectSupplier(ElectElement element) {
		if(getInitialState() instanceof PetrolElementState) {
			PetrolElementState state = (PetrolElementState) getInitialState();
			state.electSupplier = element;
		}
	}
}