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
import edu.mit.isos.app.SocialElement;
import edu.mit.isos.app.WaterElement;
import edu.mit.isos.context.Location;
import edu.mit.isos.context.ResourceFactory;
import edu.mit.isos.context.ResourceType;
import edu.mit.isos.element.DefaultElement;

/**
 * Local implementation of a social system element for interface {@link SocialElement}.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class SocialElementImpl extends DefaultElement implements SocialElement {	
	/**
	 * Instantiates a new local social element with a 
	 * {@link SocialElementState} operational state.
	 *
	 * @param name the name
	 * @param location the location
	 * @param waterPC the per-capita water demand
	 * @param electPC the per-capital electricity demand
	 * @param oilPC the per-capita petroleum demand
	 * @param initialPopulation the initial population
	 * @param growthRate the population growth rate
	 */
	public SocialElementImpl(String name, Location location, 
			double waterPC, double electPC, double oilPC, 
			double initialPopulation, double growthRate) {
		super(name, location, new SocialElementState(
				waterPC, electPC, oilPC, growthRate));
		initialContents(ResourceFactory.create(
				ResourceType.PEOPLE, initialPopulation));
	}
	
	/**
	 * Sets the electricity supplier element.
	 *
	 * @param element the new electricity supplier
	 */
	public void setElectSupplier(ElectElement element) {
		if(getInitialState() instanceof SocialElementState) {
			SocialElementState state = (SocialElementState) getInitialState();
			state.electSupplier = element;
		}
	}
	
	/**
	 * Sets the petroleum supplier element.
	 *
	 * @param element the new petroleum supplier
	 */
	public void setPetrolSupplier(PetrolElement element) {
		if(getInitialState() instanceof SocialElementState) {
			SocialElementState state = (SocialElementState) getInitialState();
			state.petrolSupplier = element;
		}
	}
	
	/**
	 * Sets the water supplier element.
	 *
	 * @param element the new water supplier
	 */
	public void setWaterSupplier(WaterElement element) {
		if(getInitialState() instanceof SocialElementState) {
			SocialElementState state = (SocialElementState) getInitialState();
			state.waterSupplier = element;
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.SocialElement#getElectReceived()
	 */
	@Override
	public double getElectReceived() {
		if(getState() instanceof SocialElementState) {
			return ((SocialElementState)getState()).getElectReceived();
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.SocialElement#getPetrolReceived()
	 */
	@Override
	public double getPetrolReceived() {
		if(getState() instanceof SocialElementState) {
			return ((SocialElementState)getState()).getPetrolReceived();
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.app.SocialElement#getWaterReceived()
	 */
	@Override
	public double getWaterReceived() {
		if(getState() instanceof SocialElementState) {
			return ((SocialElementState)getState()).getWaterReceived();
		}
		return 0;
	}
}