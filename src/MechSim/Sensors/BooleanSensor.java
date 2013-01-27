package MechSim.Sensors;

import MechSim.Skeleton.Robot;

/**
 * A Sensor associated with a particular robot. Its data is boolean - that is, true or false. Represented by a boolean.
 * @author Gurwinder Gulati
 *
 */
public abstract class BooleanSensor extends Sensor
{
	/**
	 * Creates a Sensor associated with a particular robot. Its data is boolean - that is, true or false. Represented by a boolean.
	 * @param robot Robot the Sensor is attached to
	 * @param pollingInterval Time, in milliseconds, between updating the Sensor's data
	 */
	public BooleanSensor(Robot robot, double pollingInterval)
	{
		super(robot, pollingInterval);
	}

	protected boolean m_data;

	/**
	 * @return Retrieves the most recent data the Sensor has gathered
	 */
	public boolean GetData() { return m_data; }
}
