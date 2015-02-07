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
package edu.mit.isos.sim;

/**
 * The Interface SimEntity.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public interface SimEntity {
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Initializes this entity to an initial time.
	 *
	 * @param initialTime the initial time
	 */
	public void initialize(long initialTime);
	
	/**
	 * Performs an iterative state update for a specified 
	 * duration without advancing time.
	 *
	 * @param duration the duration
	 */
	public void iterateTick(long duration);
	
	/**
	 * Commits changes from the previous iterative state update.
	 */
	public void iterateTock();
	
	/**
	 * Performs a state update for a specified duration 
	 * with advancing time.
	 *
	 * @param duration the duration
	 */
	public void tick(long duration);
	
	/**
	 * Commits changes from the previous state update.
	 */
	public void tock();
}
