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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

/**
 * Defines a square matrix of resource quantities indexed by resource types.
 * Useful for computing transformations.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public final class ResourceMatrix {
	private Resource[] resources = new Resource[ResourceType.values().length];
	
	/**
	 * Instantiates a new empty resource matrix.
	 */
	public ResourceMatrix() {
		for(int i = 0; i < resources.length; i++) {
			resources[i] = ResourceFactory.create();
		}
	}
	
	/**
	 * Instantiates a new resource matrix by copying and filling a row.
	 *
	 * @param resources the resources
	 */
	public ResourceMatrix(Resource[] resources) {
		if(resources.length != this.resources.length) {
			throw new IllegalArgumentException("Not enough amounts. Expected " 
					+ this.resources.length + ", received " + resources.length + ".");
		} else {
			this.resources = Arrays.copyOf(resources, resources.length);
		}
	}
	
	/**
	 * Instantiates a new resource matrix with single row.
	 *
	 * @param type the type
	 * @param resource the resource
	 */
	public ResourceMatrix(ResourceType type, Resource resource) {
		resources[type.ordinal()] = resource;
		for(int i = 0; i < resources.length; i++) {
			if(resources[i] == null) {
				resources[i] = ResourceFactory.create();
			}
		}
	}
	
	/**
	 * Instantiates a new resource matrix specified rows.
	 *
	 * @param types the types
	 * @param resources the resources
	 */
	public ResourceMatrix(ResourceType[] types, Resource[] resources) {
		if(types.length != resources.length) {
			throw new IllegalArgumentException("Unbalanced arguments. " 
					+ types.length + "types, " + resources.length + " resources.");
		} else {
			for(int i = 0; i < types.length; i++) {
				this.resources[types[i].ordinal()] = resources[i].copy();
			}
			for(int i = 0; i < resources.length; i++) {
				if(resources[i] == null) {
					resources[i] = ResourceFactory.create();
				}
			}
		}
	}
	
	/**
	 * Adds a resource matrix and returns the sum.
	 *
	 * @param matrix the matrix to add
	 * @return the sum
	 */
	public ResourceMatrix add(ResourceMatrix matrix) {
		Resource[] newResources = new Resource[resources.length];
		for(ResourceType t : ResourceType.values()) {
			newResources[t.ordinal()] = resources[t.ordinal()].add(matrix.getResource(t));
		}
		return new ResourceMatrix(newResources);
	}

	/**
	 * Copies this resource matrix.
	 *
	 * @return the resource matrix
	 */
	public ResourceMatrix copy() {
		return new ResourceMatrix(Arrays.copyOf(resources, resources.length));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(Object object) {
		if(object instanceof ResourceMatrix) {
			return subtract((ResourceMatrix) object).isZero();
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the resource associated with a resource type.
	 *
	 * @param type the type
	 * @return the resource
	 */
	public Resource getResource(ResourceType type) {
		return resources[type.ordinal()].copy();
	}
	
	/**
	 * Checks if this resource matrix has zero quantity.
	 *
	 * @return true, if is zero
	 */
	public boolean isZero() {
		for(ResourceType t : ResourceType.values()) {
			if(!resources[t.ordinal()].isZero()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Multiplies this resource matrix by a scalar and returns the product.
	 *
	 * @param scalar the scalar
	 * @return the product
	 */
	public ResourceMatrix multiply(double scalar) {
		Resource[] newResources = new Resource[resources.length];
		for(ResourceType t : ResourceType.values()) {
			newResources[t.ordinal()] = resources[t.ordinal()].multiply(scalar);
		}
		return new ResourceMatrix(newResources);
	}
	
	/**
	 * Multiplies this resource matrix by a resource 
	 * (e.g. vector multiplication) and returns the product.
	 *
	 * @param resource the resource
	 * @return the resource
	 */
	public Resource multiply(Resource resource) {
		Resource newResource = ResourceFactory.create();
		for(ResourceType t : ResourceType.values()) {
			newResource = newResource.add(getResource(t).multiply(resource.getQuantity(t)));
		}
		return newResource;
	}
	
	/**
	 * Returns the opposite quantities of this resource matrix.
	 *
	 * @return the negated resource matrix
	 */
	public ResourceMatrix negate() {
		Resource[] newResources = new Resource[resources.length];
		for(ResourceType t : ResourceType.values()) {
			newResources[t.ordinal()] = resources[t.ordinal()].negate();
		}
		return new ResourceMatrix(newResources);
	}
	
	/**
	 * Subtracts a resource matrix and returns the difference.
	 *
	 * @param matrix the matrix to subtract
	 * @return the difference
	 */
	public ResourceMatrix subtract(ResourceMatrix matrix) {
		return add(matrix.negate());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		NumberFormat format = new DecimalFormat("0.00");
		StringBuilder b = new StringBuilder().append("[");
		for(ResourceType r : ResourceType.values()) {
			b.append("[");
			for(ResourceType c : ResourceType.values()) {
				b.append(format.format(getResource(r).getQuantity(c)));
				if(c.ordinal() < ResourceType.values().length - 1) {
					b.append(", ");
				}
			}
			b.append("]");
			if(r.ordinal() < ResourceType.values().length - 1) {
				b.append(", ");
			}
		}
		return b.append("]").toString();
	}
}
