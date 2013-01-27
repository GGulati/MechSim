package MechSim.Sensors;

import MechSim.Physics.CollisionListener;
import MechSim.Physics.InteractionType;
import MechSim.Physics.PhysicsObject;
import MechSim.Skeleton.Robot;

/**
 * A Sensor that notices when a bumper collides with anything
 * @author Gurwinder Gulati
 *
 */
public class BumperSensor extends BooleanSensor implements CollisionListener
{
	PhysicsObject m_bumper;
	boolean m_colNoticed;
		
	/**
	 * Creates a Sensor that notices when the Robot collides with anything
	 * @param robot Robot the Sensor is attached to
	 * @param pollingInterval Time between updates
	 */
	public BumperSensor(Robot robot, double pollingInterval)
	{
		super(robot, pollingInterval);
		m_bumper = robot.GetPhysicsObject();
		m_bumper.RegisterListener(this);
	}

	@Override
	protected void Poll()
	{
		if (m_colNoticed)
		{
			m_data = m_colNoticed;
			m_colNoticed = false;
		}
		else
			m_data = m_bumper.CollidedLastFrame();
	}

	@Override
	public void NotifyOfCollision(PhysicsObject alpha, PhysicsObject beta)
	{
		if (beta.GetInteractionType() != InteractionType.Ghost)
			m_colNoticed = true;
	}
}
