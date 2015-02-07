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

import edu.mit.isos.context.Resource;
import edu.mit.isos.element.ElementImpl;

public interface ResourceStoring {
	public Resource getStored(ElementImpl element, long duration);
	public Resource getRetrieved(ElementImpl element, long duration);
	public void store(ElementImpl element, Resource stored, Resource retrieved);
}
