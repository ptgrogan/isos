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

public class EmptyState implements State, ElementTransforming {
	private long stateChangeTime;
	private State nextState;
	
	private long time;
	private transient long nextTime;
	
	public EmptyState(long stateChangeTime, State nextState) { 
		this.stateChangeTime = stateChangeTime;
		this.nextState = nextState;
	}
	
	public String getName() {
		return "Empty";
	}
	
	public String toString() {
		return getName();
	}
	
	public long getStateChangeTime() {
		return stateChangeTime;
	}
	
	public State getNextState() {
		return nextState;
	}
	
	public void initialize(ElementImpl element, long initialTime) {
		if(!element.getStates().contains(this)) {
			throw new IllegalStateException(
					"Element does not contain state " + this);
		}
		if(!element.getStates().contains(nextState)) {
			throw new IllegalStateException(
					"Element does not contain next state " + nextState);
		}
		time = nextTime = initialTime;
	}
	
	public void tick(ElementImpl element, long duration) {
		nextTime = time + duration;

		if(element.getState().equals(this)
				&& nextTime >= stateChangeTime) {
			transform(element, nextState);
		}
	}
	
	public void tock() {
		time = nextTime;
	}

	@Override
	public void iterateTick(ElementImpl element, long duration) { }

	@Override
	public void iterateTock() { }

	@Override
	public void transform(ElementImpl element, State nextState) {
		element.setState(nextState);
	}

	@Override
	public Resource getNetFlow(ElementImpl element, Location location, long duration) {
		return ResourceFactory.create();
	}

	@Override
	public Resource getNetExchange(ElementImpl element1, Element element2,
			long duration) {
		return ResourceFactory.create();
	}
}
