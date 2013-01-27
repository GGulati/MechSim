package MechSim.Sensors;

import MechSim.Skeleton.Robot;

/**
 * A Sensor associated with a particular robot. Its data is analogy - that is, any real number. Represented by a double.
 * @author Gurwinder Gulati
 *
 */
public abstract class AnalogSensor extends Sensor
{
	/**
	 * Creates a Sensor associated with a particular robot. Its data is analogy - that is, any real number. Represented by a double.
	 * @param robot Robot the Sensor is attached to
	 * @param pollingInterval Time, in milliseconds, between updating the Sensor's data
	 */
	public AnalogSensor(Robot robot, double pollingInterval)
	{
		super(robot, pollingInterval);
	}

	protected double m_data;

	/*
	 * @return Retrieves the most recent data the Sensor has gathered
	 */
	public double GetData() { return m_data; }
}
