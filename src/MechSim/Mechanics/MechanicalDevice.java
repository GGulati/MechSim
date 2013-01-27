package MechSim.Mechanics;

import MechSim.Skeleton.Robot;

/**
 * A MechanicalDevice associated with a particular robot
 * @author Gurwinder Gulati
 *
 */
public abstract class MechanicalDevice
{
	Robot m_robot;
	
	/**
	 * Creates a MechanicalDevice associated with a particular robot
	 * @param robot Robot the device is attached to
	 */
	public MechanicalDevice(Robot robot)
	{
		m_robot = robot;
		robot.AddDevice(this);
	}
	
	/**
	 * Updates the MechanicalDevice
	 * @param timeDelta Time since last update
	 */
	public abstract void Update(double timeDelta);
	
	/**
	 * @return Robot the device is attached to
	 */
	public Robot GetRobot() { return m_robot; }
}
