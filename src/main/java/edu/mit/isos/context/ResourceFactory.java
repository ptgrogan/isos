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
 * A factory for creating Resource objects. Defines both floating-point and 
 * text arguments for numerical quantities. String arguments avoid potential 
 * round-off errors and are used widely in high-performance number formats.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public abstract class ResourceFactory {
	public static enum Impl {DOUBLE, BIG_DECIMAL};
	public static Impl impl = Impl.DOUBLE;
	
	/**
	 * Creates an empty resource.
	 *
	 * @return the resource
	 */
	public static Resource create() {
		if(impl==Impl.DOUBLE) {
			return new DoubleArrayResource();
		} else if(impl==Impl.BIG_DECIMAL) {
			return new BigDecimalArrayResource();
		} else throw new RuntimeException(
				"Unknown resource implementation selected.");
	}
	
	/**
	 * Creates a resource with specified amounts.
	 *
	 * @param amounts the amounts
	 * @return the resource
	 */
	public static Resource create(double[] amounts) {
		String[] strAmounts = new String[amounts.length];
		for(int i = 0; i < amounts.length; i++) {
			strAmounts[i] = String.valueOf(amounts[i]);
		}
		return create(strAmounts);
	}
	
	/**
	 * Creates a resource with a specified amount of a resource type.
	 *
	 * @param type the type
	 * @param amount the amount
	 * @return the resource
	 */
	public static Resource create(ResourceType type, double amount) {
		return create(type, String.valueOf(amount));
	}
	
	/**
	 * Creates a resource with a specified amount of a resource type.
	 * String amount argument avoids possible floating point round-off.
	 *
	 * @param type the type
	 * @param amount the amount
	 * @return the resource
	 */
	public static Resource create(ResourceType type, String amount) {
		if(impl==Impl.DOUBLE) {
			return new DoubleArrayResource(type, amount);
		} else if(impl==Impl.BIG_DECIMAL) {
			return new BigDecimalArrayResource(type, amount);
		} else throw new RuntimeException(
				"Unknown resource implementation selected.");
	}
	
	/**
	 * Creates a resource with specified amounts of resource types.
	 *
	 * @param types the types
	 * @param amounts the amounts
	 * @return the resource
	 */
	public static Resource create(ResourceType[] types, double amounts[]) {
		if(types.length != amounts.length) {
			throw new IllegalArgumentException("Unbalanced arguments. " 
					+ types.length + "types, " + amounts.length + " amounts.");
		} else {
			Resource resource = create();
			for(int i = 0; i < types.length; i++) {
				resource = resource.add(create(types[i], amounts[i]));
			}
			return resource;
		}
	}
	
	/**
	 * Creates a resource with specified amounts of resource types.
	 * String amount argument avoids possible floating point round-off.
	 *
	 * @param types the types
	 * @param amounts the amounts
	 * @return the resource
	 */
	public static Resource create(ResourceType[] types, String amounts[]) {
		if(types.length != amounts.length) {
			throw new IllegalArgumentException("Unbalanced arguments. " 
					+ types.length + "types, " + amounts.length + " amounts.");
		} else {
			Resource resource = create();
			for(int i = 0; i < types.length; i++) {
				resource = resource.add(create(types[i], amounts[i]));
			}
			return resource;
		}
	}
	
	/**
	 * Creates a resource with specified amounts.
	 * String amount argument avoids possible floating point round-off.
	 *
	 * @param amounts the amounts
	 * @return the resource
	 */
	public static Resource create(String[] amounts) {
		if(impl==Impl.DOUBLE) {
			return new DoubleArrayResource(amounts);
		} else if(impl==Impl.BIG_DECIMAL) {
			return new BigDecimalArrayResource(amounts);
		} else throw new RuntimeException(
				"Unknown resource implementation selected.");
	}
}