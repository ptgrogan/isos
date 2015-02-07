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
package edu.mit.isos.state;

import edu.mit.isos.context.Location;
import edu.mit.isos.context.Resource;
import edu.mit.isos.context.ResourceFactory;
import edu.mit.isos.element.Element;
import edu.mit.isos.element.ElementImpl;

public class DefaultState implements State, ResourceStoring, ResourceTransforming, ResourceTransporting, ElementTransforming {
	private final String name;
	
	protected DefaultState() {
		this.name = "";
	}
	
	public DefaultState(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}

	public Resource getInput(ElementImpl element, long duration) {
		return ResourceFactory.create();
	}

	public Resource getOutput(ElementImpl element, long duration) {
		return ResourceFactory.create();
	}

	public Resource getProduced(ElementImpl element, long duration) {
		return ResourceFactory.create();
	}

	public Resource getConsumed(ElementImpl element, long duration) {
		return ResourceFactory.create();
	}

	public Resource getStored(ElementImpl element, long duration) {
		return ResourceFactory.create();
	}

	public Resource getRetrieved(ElementImpl element, long duration) {
		return ResourceFactory.create();
	}

	@Override
	public void iterateTick(ElementImpl element, long duration) { }
	
	@Override
	public void iterateTock() { }

	@Override
	public void initialize(ElementImpl element, long initialTime) {
		if(!element.getStates().contains(this)) {
			throw new IllegalStateException(
					"Element does not contain state " + this);
		}
	}

	@Override
	public void tick(ElementImpl element, long duration) {
		if(equals(element.getState())) {
			store(element, getStored(element, duration), getRetrieved(element, duration));
			transport(element, getInput(element, duration), getOutput(element, duration));
			transform(element, getConsumed(element, duration), getProduced(element, duration));
		}
	}

	@Override
	public void tock() { }

	@Override
	public void store(ElementImpl element, Resource stored, Resource retrieved) {
		element.addContents(stored);
		element.removeContents(retrieved);
	}

	@Override
	public void transport(ElementImpl element, Resource input, Resource output) {
		// no longer modifies element contents
		// element.addContents(input);
		// element.removeContents(output);
	}

	@Override
	public void transform(ElementImpl element, Resource consumed, Resource produced) {
		// no longer modifies element contents
		// element.add(produced);
		// element.remove(consumed);
	}

	@Override
	public Resource getNetFlow(ElementImpl element, Location location, long duration) {
		Resource netFlow = ResourceFactory.create();
		if(element.getLocation().equals(location)) {
			netFlow = netFlow.add(getRetrieved(element, duration)).subtract(getStored(element, duration))
					.add(getProduced(element, duration)).subtract(getConsumed(element, duration))
					.add(getInput(element, duration)).subtract(getOutput(element, duration));
		}
		if(location.isStatic() && location.getOrigin().equals(element.getLocation().getOrigin())) {
			netFlow = netFlow.subtract(getInput(element, duration));
		}
		if(location.isStatic() && location.getOrigin().equals(element.getLocation().getDestination())) {
			netFlow = netFlow.add(getOutput(element, duration));
		}
		return netFlow;
	}

	@Override
	public Resource getNetExchange(ElementImpl element1, Element element2,
			long duration) {
		return ResourceFactory.create();
	}
	
	@Override
	public void transform(ElementImpl element, State nextState) {
		element.setState(nextState);
	}
}
