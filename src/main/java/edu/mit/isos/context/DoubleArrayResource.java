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
package edu.mit.isos.context;

/**
 * Immutable implementation of the {@link Resource} interface 
 * backed by an array of primitive {@link double} values.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class DoubleArrayResource extends DefaultResource implements Resource {
	private static final double epsilon = 1e-12;
	private final double[] amount = new double[ResourceType.values().length];
	
	/**
	 * Instantiates a new empty double array resource.
	 */
	protected DoubleArrayResource() {
		for(int i = 0; i < amount.length; i++) {
			amount[i] = 0;
		}
	}
	
	/**
	 * Instantiates a new double array resource
	 * with a specified amount of a resource type.
	 *
	 * @param type the type
	 * @param amount the amount
	 */
	private DoubleArrayResource(ResourceType type, double amount) {
		this();
		this.amount[type.ordinal()] = amount;
	}
	
	/**
	 * Instantiates a new double array resource
	 * with a specified amount of a resource type.
	 *
	 * @param type the type
	 * @param amount the amount
	 */
	protected DoubleArrayResource(ResourceType type, String amount) {
		this(type, Double.parseDouble(amount));
	}
	
	/**
	 * Instantiates a new double array resource.
	 *
	 * @param amounts the amounts
	 */
	protected DoubleArrayResource(String[] amounts) {
		this();
		if(amounts.length != amount.length) {
			throw new IllegalArgumentException("Not enough amounts. Expected " 
					+ amount.length + ", received " + amounts.length + ".");
		}
		for(ResourceType t : ResourceType.values()) {
			amount[t.ordinal()] = Double.parseDouble(amounts[t.ordinal()]);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#absoluteValue()
	 */
	public final Resource absoluteValue() {
		DoubleArrayResource newResource = new DoubleArrayResource();
		for(ResourceType t : ResourceType.values()) {
			newResource.amount[t.ordinal()] = Math.abs(amount[t.ordinal()]);
		}
		return newResource;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#add(edu.mit.isos.context.Resource)
	 */
	public DoubleArrayResource add(Resource resource) {
		DoubleArrayResource newResource = new DoubleArrayResource();
		for(ResourceType t : ResourceType.values()) {
			newResource.amount[t.ordinal()] = this.amount[t.ordinal()] 
					+ resource.getQuantity(t);
		}
		return newResource;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#copy()
	 */
	public DoubleArrayResource copy() {
		DoubleArrayResource newResource = new DoubleArrayResource();
		for(ResourceType t : ResourceType.values()) {
			newResource.amount[t.ordinal()] = this.amount[t.ordinal()];
		}
		return newResource;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#get(edu.mit.isos.context.ResourceType)
	 */
	public DoubleArrayResource get(ResourceType type) {
		return new DoubleArrayResource(type, amount[type.ordinal()]);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#getQuantity(edu.mit.isos.context.ResourceType)
	 */
	@Override
	public double getQuantity(ResourceType type) {
		return amount[type.ordinal()];
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#isZero()
	 */
	@Override
	public boolean isZero() {
		for(ResourceType t : ResourceType.values()) {
			if(Math.abs(amount[t.ordinal()]) > epsilon) {
				return false;
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#multiply(double)
	 */
	public DoubleArrayResource multiply(double scalar) {
		DoubleArrayResource newResource = new DoubleArrayResource();
		for(ResourceType t : ResourceType.values()) {
			newResource.amount[t.ordinal()] = this.amount[t.ordinal()] * scalar;
		}
		return newResource;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#multiply(edu.mit.isos.context.Resource)
	 */
	public DoubleArrayResource multiply(Resource resource) {
		DoubleArrayResource newResource = copy();
		for(ResourceType t : ResourceType.values()) {
			newResource.amount[t.ordinal()] = this.amount[t.ordinal()]
					* resource.getQuantity(t);
		}
		return newResource;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#negate()
	 */
	public DoubleArrayResource negate() {
		DoubleArrayResource newResource = new DoubleArrayResource();
		for(ResourceType type : ResourceType.values()) {
			newResource.amount[type.ordinal()] = -this.amount[type.ordinal()];
		}
		return newResource;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#safeDivide(edu.mit.isos.context.Resource)
	 */
	public final Resource safeDivide(Resource resource) {
		DoubleArrayResource newResource = new DoubleArrayResource();
		for(ResourceType t : ResourceType.values()) {
			if(Math.abs(resource.getQuantity(t)) > epsilon) {
				newResource.amount[t.ordinal()] = amount[t.ordinal()] / resource.getQuantity(t);
			}
		}
		return newResource;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#swap(edu.mit.isos.context.ResourceType, edu.mit.isos.context.ResourceType)
	 */
	public DoubleArrayResource swap(ResourceType oldType, ResourceType newType) {
		DoubleArrayResource newResource = copy();
		double value = newResource.amount[oldType.ordinal()];
		newResource.amount[oldType.ordinal()] = newResource.amount[newType.ordinal()];
		newResource.amount[newType.ordinal()] = value;
		return newResource;
	}
}
