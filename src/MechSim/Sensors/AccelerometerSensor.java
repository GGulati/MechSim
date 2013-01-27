package MechSim.Sensors;

import MechSim.Skeleton.Robot;

/**
 * Measures the force currently applied to the Robot to make it move (e.g., Robot's velocity)
 * @author Gurwinder Gulati
 *
 */
public class AccelerometerSensor extends AnalogSensor
{
	/**
	 * Direction an AccelerometerSensor can sense force in
	 * @author Gurwinder Gulati
	 *
	 */
	public enum Direction { X, Y }
	
	Direction m_dir;
	
	/**
	 * Creates a Sensor to measure the force currently applied to the Robot to make it move (e.g., Robot's velocity) in one direction
	 * @param robot Robot the Sensor is attached to
	 * @param pollingInterval Time between Sensor updates
	 * @param dir Direction the AccelerometerSensor measures in
	 */
	public AccelerometerSensor(Robot robot, double pollingInterval, Direction dir)
	{
		super(robot, pollingInterval);
		
		m_dir = dir;
	}

	@Override
	protected void Poll()
	{
		m_data = m_dir == Direction.X ? GetRobot().GetPhysicsObject().GetVelocityX() : GetRobot().GetPhysicsObject().GetVelocityY();
	}
	
	/**
	 * @return Direction the AccelerometerSensor is measuring force in
	 */
	public Direction GetDir() { return m_dir; }
}
