package de.mh4j.solver.simulatedAnnealing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mh4j.solver.Solution;

public abstract class AbstractCoolingScheme<GenericSolutionType extends Solution> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Determines how long (in number of steps) the algorithm runs until the
     * current acceptance probability is decreased. A step is one call to
     * {@link #updateTemperature()}.<br>
     * <br>
     * <b>Note:</b> Mathematically spoken this is the length of the markov
     * chain.
     */
    protected int epochLength;

    /**
     * The temperature on which this cooling scheme is currently operating. The
     * First value of this variable will be set to the result of
     * {@link #getInitialTemperature()}.
     */
    protected double currentTemperature;

    private int nrOfStepsInThisEpoch;

    /**
     * Creates and initializes a cooling scheme. The initial temperature and
     * epoch length will be set.
     * 
     * @see #getInitialTemperature()
     * @see #getInitialEpochLength()
     */
    public AbstractCoolingScheme() {
        initialize();
    }

    private void initialize() {
        nrOfStepsInThisEpoch = 0;
        currentTemperature = getInitialTemperature();
        epochLength = getInitialEpochLength();
    }

    /**
     * This method is called when this cooling scheme is initialized (usually in
     * the constructor).
     * 
     * @return The initial temperature from where the algorithms start.
     */
    protected abstract double getInitialTemperature();

    /**
     * This method is called when this cooling scheme is initialized (usually in
     * the constructor).
     * 
     * @return The length of the first epoch.
     * @see #epochLength
     */
    protected abstract int getInitialEpochLength();

    /**
     * Checks whether the {@link #epochLength} has been reached. If so the
     * current temperature will be {@link #decreaseTemperature() decreased }and
     * a new epoch will be started.<br>
     * <br>
     * <b>Note:</b> Each time this method is called a counter is increased. If
     * the counter reaches the value of {@link #epochLength} then the end of
     * this epoch has been reached and a new epoch starts by setting the counter
     * back to zero.
     */
    public void updateTemperature() {
        if (nrOfStepsInThisEpoch >= epochLength) {
            nrOfStepsInThisEpoch = 0;
            decreaseTemperature();
            log.trace("Temperature has been decreased to {}", currentTemperature);
        }
        nrOfStepsInThisEpoch++;
    }

    /**
     * Decreases the {@link #currentTemperature} of this cooling scheme.
     */
    protected abstract void decreaseTemperature();

    /**
     * Returns the probability with which a solution should be accepted in
     * relation to another (often better) solution. The possible range of the
     * returned probability p will be 0 <= p <= 1.<br>
     * <br>
     * <b>Note:</b> this standard implementation calculates the probability with
     * this formula:<br>
     * <br>
     * <code>Math.exp(-Math.log(neighborCosts - currentCosts) / currentTemperature)</code>
     */
    public double getAcceptanceProbability(GenericSolutionType currentSolution, GenericSolutionType neighborSolution) {
        int currentCosts = currentSolution.getCosts();
        int neighborCosts = neighborSolution.getCosts();
        return Math.exp(-Math.log(neighborCosts - currentCosts) / currentTemperature);
    }

}
