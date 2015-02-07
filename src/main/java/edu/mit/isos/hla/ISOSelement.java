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
package edu.mit.isos.hla;

import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.OrderType;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.exceptions.RTIexception;

import org.apache.log4j.Logger;

import edu.mit.isos.context.Location;
import edu.mit.isos.context.Node;
import edu.mit.isos.element.Element;
import edu.mit.isos.sim.SimEntity;

/**
 * ISOSelement is the HLA object class implementing the {@link Element} 
 * interface for communication with the RTI.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public abstract class ISOSelement extends HLAobject implements Element {
	private static Logger logger = Logger.getLogger(ISOSelement.class);
	public static final String CLASS_NAME = "HLAobjectRoot.Element";
	public static final String NAME_ATTRIBUTE = "Name",
			LOCATION_ATTRIBUTE = "Location";
	public static final String[] ATTRIBUTES = new String[]{
		NAME_ATTRIBUTE,
		LOCATION_ATTRIBUTE
	};
	
	/**
	 * Publishes all of this object class's attributes.
	 *
	 * @param rtiAmbassador the RTI ambassador
	 * @throws RTIexception the RTI exception
	 */
	public static void publishAll(RTIambassador rtiAmbassador) 
			throws RTIexception {
		AttributeHandleSet handles = 
				rtiAmbassador.getAttributeHandleSetFactory().create();
		for(String attributeName : ATTRIBUTES) {
			handles.add(rtiAmbassador.getAttributeHandle(
					rtiAmbassador.getObjectClassHandle(CLASS_NAME), 
					attributeName));
		}
		rtiAmbassador.publishObjectClassAttributes(
				rtiAmbassador.getObjectClassHandle(CLASS_NAME), handles);
	}
	/**
	 * Subscribes to all of this object class's attributes.
	 *
	 * @param rtiAmbassador the RTI ambassador
	 * @throws RTIexception the RTI exception
	 */
	public static void subscribeAll(RTIambassador rtiAmbassador) 
			throws RTIexception {
		AttributeHandleSet handles = 
				rtiAmbassador.getAttributeHandleSetFactory().create();
		for(String attributeName : ATTRIBUTES) {
			handles.add(rtiAmbassador.getAttributeHandle(
					rtiAmbassador.getObjectClassHandle(CLASS_NAME), 
					attributeName));
		}
		rtiAmbassador.subscribeObjectClassAttributes(
				rtiAmbassador.getObjectClassHandle(CLASS_NAME), handles);
	}

	private final HLAunicodeString name;
	private final HLAunicodeString location;
	
	/**
	 * Instantiates a new ISOS element. The object is interpreted as local
	 * if {@link instanceName} is null and is interpreted as remote if 
	 * {@link instanceName} is not null.
	 *
	 * @param rtiAmbassador the RTI ambassador
	 * @param encoderFactory the encoder factory
	 * @param instanceName the instance name
	 * @throws RTIexception the RTI exception
	 */
	public ISOSelement(RTIambassador rtiAmbassador, 
			EncoderFactory encoderFactory, String instanceName) 
					throws RTIexception {
		super(rtiAmbassador, instanceName);
		
		logger.trace("Creating the name data element, " 
				+ "adding it as an attribute, "
				+ " and setting the send order.");
		name = encoderFactory.createHLAunicodeString();
		attributeValues.put(getAttributeHandle(NAME_ATTRIBUTE),  name);
		sendOrderMap.put(getAttributeHandle(NAME_ATTRIBUTE), 
				OrderType.RECEIVE);

		logger.trace("Creating the location data element, " 
				+ "adding it as an attribute, "
				+ " and setting the send order.");
		location = encoderFactory.createHLAunicodeString();
		attributeValues.put(getAttributeHandle(LOCATION_ATTRIBUTE), location);
		sendOrderMap.put(getAttributeHandle(LOCATION_ATTRIBUTE), 
				OrderType.RECEIVE);
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.hla.HLAobject#getAttributeNames()
	 */
	@Override
	public String[] getAttributeNames() {
		return ATTRIBUTES;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.element.Element#getLocation()
	 */
	@Override
	public Location getLocation() {
		String[] nodeNames = location.getValue().split("-");
		if(nodeNames.length==1) {
			return new Location(new Node(nodeNames[0]), new Node(nodeNames[0]));
		} else if(nodeNames.length == 2) {
			return new Location(new Node(nodeNames[0]), new Node(nodeNames[1]));
		} else {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.element.Element#getName()
	 */
	@Override
	public String getName() {
		return name.getValue();
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.hla.HLAobject#getObjectClassName()
	 */
	@Override
	public String getObjectClassName() {
		return CLASS_NAME;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.hla.HLAobject#setAttributes(edu.mit.isos.sim.SimEntity)
	 */
	@Override
	public void setAttributes(SimEntity object) {
		if(object instanceof Element) {
			Element element = (Element) object;
			name.setValue(element.getName());
			location.setValue(element.getLocation().toString());
		} else {
			logger.warn("Incompatible object passed: expected " 
					+ Element.class + " but received "
					+ object.getClass() + ".");
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.hla.HLAobject#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder().append("ISOSelement { name: ").append(getName())
				.append(", location: ").append(getLocation())
				.append("}").toString();
	}
	
	/**
	 * Update periodic attributes.
	 *
	 * @param rtiAmbassador the RTI ambassador
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public abstract void updatePeriodicAttributes(RTIambassador rtiAmbassador) throws RTIexception;
	
	/**
	 * Update static attributes.
	 *
	 * @param rtiAmbassador the RTI ambassador
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public abstract void updateStaticAttributes(RTIambassador rtiAmbassador) throws RTIexception;
}
