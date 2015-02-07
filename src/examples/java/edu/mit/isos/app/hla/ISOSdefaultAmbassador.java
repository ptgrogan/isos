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
package edu.mit.isos.app.hla;

import hla.rti1516e.NullFederateAmbassador;

import org.apache.log4j.Logger;

import edu.mit.isos.app.ElectElement;
import edu.mit.isos.app.PetrolElement;
import edu.mit.isos.app.SocialElement;
import edu.mit.isos.app.WaterElement;
import edu.mit.isos.app.elect.ElectElementImpl;
import edu.mit.isos.app.petrol.PetrolElementImpl;
import edu.mit.isos.app.social.SocialElementImpl;
import edu.mit.isos.app.water.WaterElementImpl;
import edu.mit.isos.element.Element;
import edu.mit.isos.element.ElementImpl;
import edu.mit.isos.hla.ISOSambassador;

/**
 * Default ambassador implementation for HLA or non-HLA federates. 
 * Contains methods to attempt to set up supplier and customer links for 
 * a local element based on co-location with remote elements.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public abstract class ISOSdefaultAmbassador extends NullFederateAmbassador implements ISOSambassador {
	protected static Logger logger = Logger.getLogger(ISOSdefaultAmbassador.class);
		
	/**
	 * Attempts to set up co-located supplier and customer links 
	 * for a local electricity element. This method may fail (return false)
	 * if the corresponding remote model has not yet been discovered.
	 *
	 * @param elect the electricity element
	 * @return true, if successful
	 */
	protected boolean setUpElect(ElectElementImpl elect) {
		PetrolElement petrol = null;
		SocialElement social = null;
		WaterElement water = null;
		for(Element element : getElements()) {
			if(element instanceof PetrolElement 
					&& elect.getLocation().equals(element.getLocation())) {
				petrol = (PetrolElement) element;
				elect.setPetrolSupplier(petrol);
				elect.setCustomer(petrol);
				break;
			}
		}
		for(Element element : getElements()) {
			if(element instanceof SocialElement 
					&& elect.getLocation().equals(element.getLocation())) {
				social = (SocialElement) element;
				elect.setCustomer(social);
				break;
			}
		}
		for(Element element : getElements()) {
			if(element instanceof WaterElement
					&& elect.getLocation().equals(element.getLocation())) {
				water = (WaterElement) element;
				elect.setCustomer(water);
				break;
			}
		}
		if(petrol==null || social==null || water==null) {
			logger.warn(elect + " missing " + (petrol==null?"petrol":"") 
					+ " " + (social==null?"social":"") 
					+ " " + (water==null?"water":""));
		}

		return petrol != null && social != null && water != null;
	}
	
	/**
	 * Attempts to set up co-located supplier and customer links 
	 * for a local element. This method may fail (return false)
	 * if the corresponding remote model has not yet been discovered.
	 *
	 * @param elect the electricity element
	 * @return true, if successful
	 */
	protected boolean setUpElement(ElementImpl element) {
		if(element instanceof ElectElementImpl) {
			return setUpElect((ElectElementImpl)element);
		}
		if(element instanceof PetrolElementImpl) {
			return setUpPetrol((PetrolElementImpl)element);
		}
		if(element instanceof SocialElementImpl) {
			return setUpSocial((SocialElementImpl)element);
		}
		if(element instanceof WaterElementImpl) {
			return setUpWater((WaterElementImpl)element);
		}
		return true; // nothing to set up
	}
	
	/**
	 * Attempts to set up co-located supplier and customer links 
	 * for a local petroleum element. This method may fail (return false)
	 * if the corresponding remote model has not yet been discovered.
	 *
	 * @param petrol the petroleum element
	 * @return true, if successful
	 */
	protected boolean setUpPetrol(PetrolElementImpl petrol) {
		ElectElement elect = null;
		SocialElement social = null;
		for(Element element : getElements()) {
			if(element instanceof ElectElement 
					&& petrol.getLocation().equals(element.getLocation())) {
				elect = (ElectElement) element;
				petrol.setCustomer(elect);
				petrol.setElectSupplier(elect);
				break;
			}
		}
		for(Element element : getElements()) {
			if(element instanceof SocialElement 
					&& petrol.getLocation().equals(element.getLocation())) {
				social = (SocialElement) element;
				petrol.setCustomer(social);
				break;
			}
		}
		if(elect==null || social==null) {
			logger.warn(petrol + " missing " + (elect==null?"elect":"") 
					+ " " + (social==null?"social":""));
		}

		return elect != null && social != null;
	}
	
	/**
	 * Attempts to set up co-located supplier links 
	 * for a local social element. This method may fail (return false)
	 * if the corresponding remote model has not yet been discovered.
	 *
	 * @param social the social element
	 * @return true, if successful
	 */
	protected boolean setUpSocial(SocialElementImpl social) {
		ElectElement elect = null;
		PetrolElement petrol = null;
		WaterElement water = null;
		for(Element element : getElements()) {
			if(element instanceof ElectElement 
					&& social.getLocation().equals(element.getLocation())) {
				elect = (ElectElement) element;
				social.setElectSupplier(elect);
				break;
			}
		}
		for(Element element : getElements()) {
			if(element instanceof PetrolElement 
					&& social.getLocation().equals(element.getLocation())) {
				petrol = (PetrolElement) element;
				social.setPetrolSupplier(petrol);
				break;
			}
		}
		for(Element element : getElements()) {
			if(element instanceof WaterElement 
					&& social.getLocation().equals(element.getLocation())) {
				water = (WaterElement) element;
				social.setWaterSupplier(water);
				break;
			}
		}
		if(elect==null || petrol==null || water==null) {
			logger.warn(social + " missing " + (elect==null?"elect":"") 
					+ " " + (petrol==null?"petrol":"") 
					+ " " + (water==null?"water":""));
		}

		return elect != null && petrol != null && water != null;
	}
	
	/**
	 * Attempts to set up co-located supplier and customer links 
	 * for a local water element. This method may fail (return false)
	 * if the corresponding remote model has not yet been discovered.
	 *
	 * @param water the water element
	 * @return true, if successful
	 */
	protected boolean setUpWater(WaterElementImpl water) {
		ElectElement elect = null;
		SocialElement social = null;
		for(Element element : getElements()) {
			if(element instanceof ElectElement 
					&& water.getLocation().equals(element.getLocation())) {
				elect = (ElectElement) element;
				water.setElectSupplier(elect);
				break;
			}
		}
		for(Element element : getElements()) {
			if(element instanceof SocialElement 
					&& water.getLocation().equals(element.getLocation())) {
				social = (SocialElement) element;
				water.setCustomer(social);
				break;
			}
		}
		if(elect==null || social==null) {
			logger.warn(water + " missing " + (elect==null?"elect":"") 
					+ " " + (social==null?"social":""));
		}

		return elect != null && social != null;
	}
}