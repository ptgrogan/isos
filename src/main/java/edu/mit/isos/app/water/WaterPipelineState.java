package edu.mit.isos.app.water;

import edu.mit.isos.context.Resource;
import edu.mit.isos.context.ResourceFactory;
import edu.mit.isos.context.ResourceMatrix;
import edu.mit.isos.context.ResourceType;
import edu.mit.isos.element.ElementImpl;
import edu.mit.isos.state.DefaultState;

/**
 * Operational state for a water pipeline element. Consumes electricity to 
 * transport water from the origin to destination node with specified losses.
 * Desired water output must be set by a controller.
 * 
 * @author Paul T. Grogan, ptgrogan@mit.edu
 * @version 0.1.0
 * @since 0.1.0
 */
public class WaterPipelineState extends DefaultState {
	protected Resource outputCapacity;
	protected double eta = 1;
	protected ResourceMatrix tpMatrix = new ResourceMatrix();
	protected Resource output = ResourceFactory.create();
	private final Resource initialOutput = ResourceFactory.create();

	/**
	 * Instantiates a new pipeline state.
	 *
	 * @param capacity the throughput capacity
	 * @param efficiency the input-output efficiency
	 * @param pumpElect the electricity transformation factor for pumping
	 */
	public WaterPipelineState(double capacity, double efficiency, double pumpElect) {
		super("Ops");
		this.outputCapacity = ResourceFactory.create(ResourceType.WATER, capacity);
		eta = efficiency;
		tpMatrix = new ResourceMatrix(
				ResourceType.WATER, 
				ResourceFactory.create(ResourceType.ELECTRICITY, pumpElect));
	}

	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#getConsumed(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getConsumed(ElementImpl element, long duration) {
		return getInput(element, duration).subtract(getOutput(element, duration));
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#getInput(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getInput(ElementImpl element, long duration) {
		return getOutput(element, duration).multiply(1/eta)
				.add(tpMatrix.multiply(getOutput(element, duration)));
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#getOutput(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public Resource getOutput(ElementImpl element, long duration) {
		return output;
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.isos.state.DefaultState#initialize(edu.mit.isos.element.ElementImpl, long)
	 */
	@Override
	public void initialize(ElementImpl element, long initialTime) {
		super.initialize(element, initialTime);
		output = initialOutput;
	}
	
	/**
	 * Method for the controller to set the water output.
	 *
	 * @param element the element
	 * @param output the output
	 * @param duration the duration
	 */
	protected void setOutput(ElementImpl element, Resource output, long duration) {
		if(output.getQuantity(ResourceType.WATER) > 
		outputCapacity.multiply(duration).getQuantity(ResourceType.WATER)) {
			this.output = outputCapacity.multiply(duration);
		} else {
			this.output = output.truncatePositive();
		}
		// re-iterate tick to resolve controller order dependencies
		iterateTick(element, duration);
	}
}