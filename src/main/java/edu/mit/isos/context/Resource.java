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
 * An immutable object which quantifies one or more resource types.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Resource {
	
	/**
	 * Adds a resource and returns the sum.
	 *
	 * @param resource the resource to add
	 * @return the sum
	 */
	public Resource add(Resource resource);
	
	/**
	 * Returns the opposite quantity of this resource.
	 *
	 * @return the negated resource
	 */
	public Resource negate();

	/**
	 * Subtracts a resource and returns the difference.
	 *
	 * @param resource the resource to substract
	 * @return the difference
	 */
	public Resource subtract(Resource resource);
	
	/**
	 * Multiplies a resource by a scalar and returns the product.
	 *
	 * @param scalar the scalar
	 * @return the product
	 */
	public Resource multiply(double scalar);
	
	/**
	 * Multiplies a resource by another and returns the product.
	 *
	 * @param resource the resource by which to multiply
	 * @return the product
	 */
	public Resource multiply(Resource resource);
	
	/**
	 * Divides a resource by another (safely) and returns the quotient.
	 *
	 * @param resource the resource by which to divide 
	 * @return the quotient
	 */
	public Resource safeDivide(Resource resource);
	
	/**
	 * Returns the absolute value of this resource quantity.
	 *
	 * @return the absolute value quantity
	 */
	public Resource absoluteValue();
	
	/**
	 * Copies this resource.
	 *
	 * @return the resource copy
	 */
	public Resource copy();
	
	/**
	 * Swaps resource quantities of two types.
	 *
	 * @param oldType the old type
	 * @param newType the new type
	 * @return the resource
	 */
	public Resource swap(ResourceType oldType, ResourceType newType);
	
	/**
	 * Gets the resource of a single resource type.
	 *
	 * @param type the type
	 * @return the resource
	 */
	public Resource get(ResourceType type);
	
	/**
	 * Gets the quantity of a single resource type.
	 *
	 * @param type the type
	 * @return the quantity
	 */
	public double getQuantity(ResourceType type);
	
	/**
	 * Checks if this resource has zero quantity.
	 *
	 * @return true, if is zero
	 */
	public boolean isZero();
	
	/**
	 * Truncates all positive quantities in this resource.
	 *
	 * @return the resource
	 */
	public Resource truncatePositive();
	
	/**
	 * Truncates all negative quantities in this resource.
	 *
	 * @return the resource
	 */
	public Resource truncateNegative();
}
