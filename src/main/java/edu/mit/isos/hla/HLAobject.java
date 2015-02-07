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

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.exceptions.RTIexception;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.mit.isos.sim.SimEntity;

/**
 * An HLAobject is the base class for communicating persistent object classes
 * with the HLA RTI. It performs some low-level functions related to updating
 * attributes (for local objects) or reflecting attribute updates (for remote
 * objects).
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public abstract class HLAobject {
	private static Logger logger = Logger.getLogger(HLAobject.class);
	private final boolean local;
	private final ObjectClassHandle objectClassHandle;
	private final Map<String,AttributeHandle> attributeHandles = 
			new HashMap<String,AttributeHandle>();
	private final AttributeHandleSet attributeHandleSet;
	private String instanceName;
	private ObjectInstanceHandle objectInstanceHandle;
	protected final Map<AttributeHandle,DataElement> attributeValues = 
			new HashMap<AttributeHandle,DataElement>();
	protected final Map<AttributeHandle,OrderType> sendOrderMap = 
			new HashMap<AttributeHandle,OrderType>();
	
	/**
	 * Instantiates a new HLA object. The object is interpreted as local
	 * if {@link instanceName} is null and is interpreted as remote if 
	 * {@link instanceName} is not null.
	 *
	 * @param rtiAmbassador the RTI ambassador
	 * @param instanceName the instance name
	 * @throws RTIexception the RTI exception
	 */
	protected HLAobject(RTIambassador rtiAmbassador, String instanceName) 
			throws RTIexception {
		this.local = instanceName == null;
		
		logger.trace("Getting the object class handle from the RTI.");
		objectClassHandle = rtiAmbassador.getObjectClassHandle(
				getObjectClassName());
		
		logger.trace("Getting the attribute handles from the RTI.");
		attributeHandleSet = rtiAmbassador.
				getAttributeHandleSetFactory().create();
		for(String attributeName : getAttributeNames()) {
			attributeHandles.put(attributeName, 
					rtiAmbassador.getAttributeHandle(
							objectClassHandle, attributeName));
			attributeHandleSet.add(getAttributeHandle(attributeName));
		}
		
		if(local) {
			logger.trace("Registering the local object with the RTI.");
			objectInstanceHandle = rtiAmbassador.registerObjectInstance(
					getObjectClassHandle());
			logger.trace("Getting the object instance name from the RTI.");
			this.instanceName = rtiAmbassador.getObjectInstanceName(
					objectInstanceHandle);
		} else {
			logger.trace("Get the object instance handle from the RTI.");
			this.instanceName = instanceName;
			objectInstanceHandle = rtiAmbassador.getObjectInstanceHandle(
					instanceName);
		}
	}
	
	/**
	 * Gets this object's RTI-assigned attribute handle for a given FOM 
	 * attribute name. Returns null if the attribute handle does not exist.
	 *
	 * @param attributeName the attribute name
	 * @return the attribute handle
	 */
	public final AttributeHandle getAttributeHandle(String attributeName)  {
		return attributeHandles.get(attributeName);
	}
	
	/**
	 * Gets this object's set of RTI-assigned attribute handles.
	 *
	 * @return the attribute handle set
	 */
	public final AttributeHandleSet getAttributeHandleSet() {
		return attributeHandleSet;
	}
	
	/**
	 * Gets this object's FOM attribute name for an RTI-assigned attribute 
	 * handle. Returns null if the attribute name does not exist.
	 *
	 * @param attributeHandle the attribute handle
	 * @return the attribute name
	 */
	public final String getAttributeName(AttributeHandle attributeHandle) {
		for(String attributeName : attributeHandles.keySet()) {
			if(attributeHandles.get(attributeName).equals(attributeHandle)) {
				return attributeName;
			}
		}
		return null;
	}
	
	/**
	 * Gets this object's FOM attribute names as an array of strings.
	 *
	 * @return the attribute names
	 */
	public abstract String[] getAttributeNames();
	
	/**
	 * Gets this object's data element value for a given attribute handle.
	 *
	 * @param attributeHandle the attribute handle
	 * @return the attribute value
	 */
	public final DataElement getAttributeValue(AttributeHandle attributeHandle) {
		return attributeValues.get(attributeHandle);
	}
	
	/**
	 * Gets this object's RTI-assigned instance name.
	 *
	 * @return the instance name
	 */
	public final String getInstanceName() {
		return instanceName;
	}

	/**
	 * Gets this object's RTI-assigned object class handle.
	 *
	 * @return the object class handle
	 */
	public final ObjectClassHandle getObjectClassHandle() {
		return objectClassHandle;
	}
	
	/**
	 * Gets this object's FOM object class name.
	 *
	 * @return the object class name
	 */
	public abstract String getObjectClassName();
	
	/**
	 * Gets this object's RTI-assigned object instance handle.
	 *
	 * @return the object instance handle
	 */
	public final ObjectInstanceHandle getObjectInstanceHandle() {
		return objectInstanceHandle;
	}
	
	/**
	 * Gets this object's FOM send order for a given attribute handle.
	 *
	 * @param attributeHandle the attribute handle
	 * @return the send order
	 */
	public final OrderType getSendOrder(AttributeHandle attributeHandle) {
		return sendOrderMap.get(attributeHandle);
	}
	
	/**
	 * Initialize.
	 *
	 * @param time the time
	 */
	public final void initialize(long time)  { }
	
	/**
	 * Checks if this is a local object.
	 *
	 * @return true, if is local
	 */
	public final boolean isLocal() {
		return local;
	}
	
	/**
	 * Publishes all of this object's attributes.
	 *
	 * @param rtiAmbassador the rti ambassador
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public final void publishAllAttributes(RTIambassador rtiAmbassador) throws RTIexception {
		rtiAmbassador.publishObjectClassAttributes(
				getObjectClassHandle(), getAttributeHandleSet());
	}
	
	/**
	 * Sets all of this object's attribute values from an RTI-provided
	 * attribute handle value map.
	 *
	 * @param attributeHandleValueMap the new all attributes
	 * @throws DecoderException the decoder exception
	 */
	public final void setAllAttributes(
			AttributeHandleValueMap attributeHandleValueMap) 
					throws DecoderException {
		for(AttributeHandle attributeHandle : getAttributeHandleSet()) {
			ByteWrapper wrapper = attributeHandleValueMap.getValueReference(
					attributeHandle);
			if(wrapper != null) {
				getAttributeValue(attributeHandle).decode(wrapper);
			}
		}
	}

	/**
	 * Sets all of this object's attribute values from a local simulation
	 * object. Throws an {@link IllegalArgumentException} if the simulation
	 * object is not compatible with this object.
	 *
	 * @param object the new attributes
	 */
	public abstract void setAttributes(SimEntity object);
	
	/**
	 * Subscribes to all of this object's attributes.
	 *
	 * @param rtiAmbassador the rti ambassador
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public final void subscribeAllAttributes(RTIambassador rtiAmbassador) throws RTIexception {
		rtiAmbassador.subscribeObjectClassAttributes(
				getObjectClassHandle(), getAttributeHandleSet());
	}
	
	/**
	 * Tick.
	 *
	 * @param duration the duration
	 */
	public final void tick(long duration) { }
	
	/**
	 * Tock.
	 */
	public final void tock() { }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getInstanceName() + " (" + getObjectInstanceHandle() + ")";
	}
	
	/**
	 * Updates a subset of this object's attributes.
	 *
	 * @param rtiAmbassador the rti ambassador
	 * @param attributeHandleSet the attribute handle set
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	public final void updateAttributes(RTIambassador rtiAmbassador,
			AttributeHandleSet attributeHandleSet) 
			throws RTIexception {
		updateTimestampOrderAttributes(rtiAmbassador, attributeHandleSet);
		updateReceiveOrderAttributes(rtiAmbassador, attributeHandleSet);
	}
	
	/**
	 * Update only this object's receive-order attributes. These updates do not
	 * require a timestamp.
	 *
	 * @param rtiAmbassador the rti ambassador
	 * @param attributeHandleSet the attributes
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	private void updateReceiveOrderAttributes(RTIambassador rtiAmbassador,
			AttributeHandleSet attributeHandleSet) throws RTIexception {
		AttributeHandleValueMap receiveOrderedAttributes = 
				rtiAmbassador.getAttributeHandleValueMapFactory().create(0);
		for(AttributeHandle attributeHandle : attributeHandleSet) {
			if(getSendOrder(attributeHandle) == OrderType.RECEIVE) {
				byte[] currentValue = getAttributeValue(attributeHandle).toByteArray();
				receiveOrderedAttributes.put(attributeHandle, currentValue);
			}
		}
		if(receiveOrderedAttributes.size() > 0) {
			logger.trace("Updating attributes for object " + this + ".");
			rtiAmbassador.updateAttributeValues(getObjectInstanceHandle(), 
					receiveOrderedAttributes, new byte[0]);
		}
	}
	
	/**
	 * Update only this object's timestamp-order attributes. These updates
	 * require an associated timestamp.
	 *
	 * @param rtiAmbassador the rti ambassador
	 * @param attributeHandleSet the attributes
	 * @throws RTIexception Signals that an RTI exception has occurred.
	 */
	private void updateTimestampOrderAttributes(RTIambassador rtiAmbassador,
			AttributeHandleSet attributeHandleSet) throws RTIexception {
		AttributeHandleValueMap timestampedAttributes = 
				rtiAmbassador.getAttributeHandleValueMapFactory().create(0);
		for(AttributeHandle attributeHandle : attributeHandleSet) {
			if(getSendOrder(attributeHandle) == OrderType.TIMESTAMP) {
				byte[] currentValue = getAttributeValue(attributeHandle).toByteArray();
				timestampedAttributes.put(attributeHandle, currentValue);
			}
		}
		if(timestampedAttributes.size() > 0) {
			LogicalTime timestamp = rtiAmbassador.queryLogicalTime().add(
					rtiAmbassador.queryLookahead());
			logger.trace("Updating attributes for object " + this 
					+ " with timestamp " + timestamp.toString() + ".");
			rtiAmbassador.updateAttributeValues(getObjectInstanceHandle(), 
					timestampedAttributes, new byte[0], timestamp);
		}
	}
}