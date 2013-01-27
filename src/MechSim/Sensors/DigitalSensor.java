package MechSim.Sensors;

import MechSim.Skeleton.Robot;

/**
 * A Sensor associated with a particular robot. Its data is digital - that is, exactly 0, 1, 2, etc. Represented by an integer.
 * @author Gurwinder Gulati
 *
 */
public abstract class DigitalSensor extends Sensor
{
	/**
	 * Creates a Sensor associated with a particular robot. Its data is digital - that is, exactly 0, 1, 2, etc. Represented by an integer.
	 * @param robot Robot the Sensor is attached to
	 * @param pollingInterval Time, in milliseconds, between updating the Sensor's data
	 */
	public DigitalSensor(Robot robot, double pollingInterval)
	{
		super(robot, pollingInterval);
	}

	protected int m_data;
	
	/**
	 * @return Retrieves the most recent data the Sensor has gathered
	 */
	public int GetData() { return m_data; }
}
