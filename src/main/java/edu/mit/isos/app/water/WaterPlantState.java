package edu.mit.isos.app.water;

import edu.mit.isos.context.Resource;
import edu.mit.isos.context.ResourceFactory;
import edu.mit.isos.context.ResourceMatrix;
import edu.mit.isos.context.ResourceType;
import edu.mit.isos.element.ElementImpl;
import edu.mit.isos.state.DefaultState;

/**
 * Operational state for a water pipeline element. Consumes electricity to 
 * produce water.
 * Desired water production must be set by a controller.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class WaterPlantState extends DefaultState {
	protected Resource productionCapacity;
	protected ResourceMatrix tfMatrix = new ResourceMatrix();
	protected Resource produced = ResourceFactory.create();
	private final Resource initialProduced = ResourceFactory.create();

	/**
	 * Instantiates a new plant state.
	 *
	 * @param capacity the capacity
	 * @param desalElect the desal elect
	 */
	public WaterPlantState(double capacity, double desalElect) {
		super("Ops");
		this.productionCapacity = ResourceFactory.create(ResourceType.WATER, capacity);
		tfMatrix = new ResourceMatrix(
				ResourceType.WATER,
				ResourceFactory.create(ResourceType.ELECTRICITY, desalElect));
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#getConsumed(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getConsumed(ElementImpl element, long duration) {
		return tfMatrix.multiply(getProduced(element, duration));
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#getProduced(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getProduced(ElementImpl element, long duration) {
		return produced;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#initialize(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public void initialize(ElementImpl element, long initialTime) {
		super.initialize(element, initialTime);
		produced = initialProduced;
	}
	
	/**
	 * Method for the controller to set water production.
	 *
	 * @param element the element
	 * @param produced the produced
	 * @param duration the duration
	 */
	protected void setProduced(ElementImpl element, Resource produced, long duration) {
		if(produced.getQuantity(ResourceType.WATER) > 
		productionCapacity.multiply(duration).getQuantity(ResourceType.WATER)) {
			this.produced = productionCapacity.multiply(duration);
		} else {
			this.produced = produced.truncatePositive();
		}
		// re-iterate tick to resolve controller order dependencies
		iterateTick(element, duration);
	}
}