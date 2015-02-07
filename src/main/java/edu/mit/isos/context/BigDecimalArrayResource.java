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

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Immutable implementation of the {@link Resource} interface 
 * backed by an array of {@link java.math.BigDecimal} values.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class BigDecimalArrayResource extends DefaultResource implements Resource {
	private static final BigDecimal epsilon = new BigDecimal("1e-12");
	private static final MathContext context = MathContext.DECIMAL32;
	private final BigDecimal[] amount = new BigDecimal[ResourceType.values().length];
	
	/**
	 * Instantiates a new empty big decimal array resource.
	 */
	protected BigDecimalArrayResource() {
		for(int i = 0; i < amount.length; i++) {
			amount[i] = BigDecimal.ZERO;
		}
	}
	
	/**
	 * Instantiates a new big decimal array resource
	 * with a specified amount of a resource type.
	 *
	 * @param type the type
	 * @param amount the amount
	 */
	private BigDecimalArrayResource(ResourceType type, BigDecimal amount) {
		this();
		this.amount[type.ordinal()] = amount;
	}
	
	/**
	 * Instantiates a new big decimal array resource 
	 * with a specified amount of a resource type.
	 *
	 * @param type the type
	 * @param amount the amount
	 */
	protected BigDecimalArrayResource(ResourceType type, String amount) {
		this(type, new BigDecimal(amount, context));
	}
	
	/**
	 * Instantiates a new big decimal array resource 
	 * with specified resource amounts.
	 *
	 * @param amounts the amounts
	 */
	protected BigDecimalArrayResource(String[] amounts) {
		this();
		if(amounts.length != amount.length) {
			throw new IllegalArgumentException("Not enough amounts. Expected " 
					+ amount.length + ", received " + amounts.length + ".");
		}
		for(ResourceType t : ResourceType.values()) {
			amount[t.ordinal()] = new BigDecimal(amounts[t.ordinal()], context);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#absoluteValue()
	 */
	public final Resource absoluteValue() {
		BigDecimalArrayResource newResource = new BigDecimalArrayResource();
		for(ResourceType t : ResourceType.values()) {
			newResource.amount[t.ordinal()] = amount[t.ordinal()].abs();
		}
		return newResource;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#add(edu.mit.isos.context.Resource)
	 */
	public BigDecimalArrayResource add(Resource resource) {
		BigDecimalArrayResource newResource = new BigDecimalArrayResource();
		for(ResourceType t : ResourceType.values()) {
			newResource.amount[t.ordinal()] = this.amount[t.ordinal()].add(
					new BigDecimal(resource.getQuantity(t)), context);
		}
		return newResource;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#copy()
	 */
	public BigDecimalArrayResource copy() {
		BigDecimalArrayResource newResource = new BigDecimalArrayResource();
		for(ResourceType t : ResourceType.values()) {
			newResource.amount[t.ordinal()] = this.amount[t.ordinal()];
		}
		return newResource;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#get(edu.mit.isos.context.ResourceType)
	 */
	public BigDecimalArrayResource get(ResourceType type) {
		return new BigDecimalArrayResource(type, amount[type.ordinal()]);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#getQuantity(edu.mit.isos.context.ResourceType)
	 */
	@Override
	public double getQuantity(ResourceType type) {
		return amount[type.ordinal()].doubleValue();
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#isZero()
	 */
	@Override
	public boolean isZero() {
		for(ResourceType t : ResourceType.values()) {
			if(amount[t.ordinal()].abs().compareTo(epsilon) > 0) {
				return false;
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#multiply(double)
	 */
	public BigDecimalArrayResource multiply(double scalar) {
		BigDecimalArrayResource newResource = new BigDecimalArrayResource();
		for(ResourceType t : ResourceType.values()) {
			newResource.amount[t.ordinal()] = this.amount[t.ordinal()].multiply(
					new BigDecimal(scalar, context), context);
		}
		return newResource;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#multiply(edu.mit.isos.context.Resource)
	 */
	public BigDecimalArrayResource multiply(Resource resource) {
		BigDecimalArrayResource newResource = copy();
		for(ResourceType t : ResourceType.values()) {
			newResource.amount[t.ordinal()] = this.amount[t.ordinal()].multiply(
					new BigDecimal(resource.getQuantity(t)), context);
		}
		return newResource;
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#negate()
	 */
	public BigDecimalArrayResource negate() {
		BigDecimalArrayResource newResource = new BigDecimalArrayResource();
		for(ResourceType type : ResourceType.values()) {
			newResource.amount[type.ordinal()] = this.amount[type.ordinal()].negate();
		}
		return newResource;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#safeDivide(edu.mit.isos.context.Resource)
	 */
	public final Resource safeDivide(Resource resource) {
		BigDecimalArrayResource newResource = new BigDecimalArrayResource();
		for(ResourceType t : ResourceType.values()) {
			if(new BigDecimal(resource.getQuantity(t)).abs().compareTo(epsilon)>0) {
				newResource.amount[t.ordinal()] = amount[t.ordinal()]
						.divide(new BigDecimal(resource.getQuantity(t), context));
			}
		}
		return newResource;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#swap(edu.mit.isos.context.ResourceType, edu.mit.isos.context.ResourceType)
	 */
	public BigDecimalArrayResource swap(ResourceType oldType, ResourceType newType) {
		BigDecimalArrayResource newResource = copy();
		BigDecimal value = newResource.amount[oldType.ordinal()];
		newResource.amount[oldType.ordinal()] = newResource.amount[newType.ordinal()];
		newResource.amount[newType.ordinal()] = value;
		return newResource;
	}
}
