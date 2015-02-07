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

/**
 * Abstract implementation of the {@link Resource} interface 
 * to define common methods.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public abstract class DefaultResource implements Resource {

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(Object object) {
		if(object instanceof Resource) {
			return subtract((Resource) object).isZero();
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#subtract(edu.mit.isos.context.Resource)
	 */
	@Override
	public final Resource subtract(Resource resource) {
		return add(resource.negate());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		NumberFormat format = new DecimalFormat("#.############");
		StringBuilder b = new StringBuilder().append("[");
		for(ResourceType t : ResourceType.values()) {
			b.append(format.format(getQuantity(t)));
			if(t.ordinal() < ResourceType.values().length - 1) {
				b.append(", ");
			}
		}
		return b.append("]").toString();
	}
	
	/**
	 * Truncates this resource to positive or negative values.
	 *
	 * @param sign the sign
	 * @return the truncated resource
	 */
	private final Resource truncate(int sign) {
		Resource resource = this.copy();
		for(ResourceType e : ResourceType.values()) {
			if(resource.getQuantity(e)*sign < 0) {
				resource = resource.subtract(resource.get(e));
			}
		}
		return resource;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#truncateNegative()
	 */
	public final Resource truncateNegative() {
		return truncate(-1);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.context.Resource#truncatePositive()
	 */
	public final Resource truncatePositive() {
		return truncate(1);
	}
}
