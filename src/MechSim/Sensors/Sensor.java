package MechSim.Sensors;

import MechSim.Skeleton.Robot;

/**
 * A Sensor associated with a particular robot
 * @author Gurwinder Gulati
 *
 */
public abstract class Sensor
{
	Robot m_robot;
	double m_pollingInterval, m_timeLeft;
	
	/**
	 * Creates a Sensor associated with a particular robot
	 * @param robot Robot the Sensor is attached to
	 * @param pollingInterval Time, in milliseconds, between updating the Sensor's data
	 */
	public Sensor(Robot robot, double pollingInterval)
	{
		m_robot = robot;
		robot.AddSensor(this);
		m_pollingInterval = pollingInterval < 0.0 ? 0.0 : pollingInterval;
	}
	
	/**
	 * Updates the Sensor and polls if necessary
	 * @param timeDelta Time since last update
	 */
	public final void Update(double timeDelta)
	{
		m_timeLeft -= timeDelta;
		if (m_timeLeft <= 0)
		{
			m_timeLeft = m_pollingInterval;
			Poll();
		}
		else
			m_timeLeft -= timeDelta;
	}
	
	/**
	 * OVERRIDE THIS. Polls and updates data internally.
	 */
	protected abstract void Poll();
	
	/**
	 * @return Robot the device is attached to
	 */
	public Robot GetRobot() { return m_robot; }
}
