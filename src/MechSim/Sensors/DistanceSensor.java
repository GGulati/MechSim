package MechSim.Sensors;

import java.util.Iterator;

import MechSim.Physics.BoundsType;
import MechSim.Physics.InteractionType;
import MechSim.Physics.PhysicsModel;
import MechSim.Physics.PhysicsObject;
import MechSim.Physics.Ray;
import MechSim.Skeleton.Robot;


/**
 * A Sensor finds the distance to the nearest object directly in front of it.
 * @author Gurwinder Gulati
 *
 */
public class DistanceSensor extends AnalogSensor
{
	double m_rotation;
	double m_cacheCos, m_cacheSin, m_cacheNetRotation;//values cached for optimization purposes
	
	/**
	 * Creates a Sensor finds the distance to the nearest object directly in front of it.
	 * @param robot Robot the Sensor is attached to
	 * @param pollingInterval Time, in milliseconds, between updating the Sensor's data
	 * @param rotation Orientation of the Sensor relative to the Robot
	 */
	public DistanceSensor(Robot robot, double pollingInterval, double rotation)
	{
		super(robot, pollingInterval);
		
		m_rotation = rotation;
		m_cacheNetRotation = m_robot.GetTransformedData().GetRotation() + m_rotation;
		m_cacheCos = Math.cos(m_cacheNetRotation);
		m_cacheSin = Math.sin(m_cacheNetRotation);
	}

	@Override
	protected void Poll()
	{
		//if the robot re-oriented itself, cached sine and cosine values need to be re-calculated
		double rotation = m_robot.GetTransformedData().GetRotation() + m_rotation;
		if (Math.abs(rotation - m_cacheNetRotation) > PhysicsModel.EPSILON)
		{
			m_cacheNetRotation = rotation;
			m_cacheCos = Math.cos(m_cacheNetRotation);
			m_cacheSin = Math.sin(m_cacheNetRotation);
		}
		
		//create a ray at the center of the robot
		PhysicsObject physics = m_robot.GetPhysicsObject();
		double x = GetRobot().GetPhysicsObject().GetBounds().GetBoundsType() == BoundsType.Circle ? GetRobot().GetPhysicsObject().GetBounds().GetCollisionCircle().X : GetRobot().GetPhysicsObject().GetBounds().GetCollisionRectangle().GetX() + GetRobot().GetPhysicsObject().GetBounds().GetCollisionRectangle().GetWidth() / 2.0;
		double y = GetRobot().GetPhysicsObject().GetBounds().GetBoundsType() == BoundsType.Circle ? GetRobot().GetPhysicsObject().GetBounds().GetCollisionCircle().Y : GetRobot().GetPhysicsObject().GetBounds().GetCollisionRectangle().GetY() + GetRobot().GetPhysicsObject().GetBounds().GetCollisionRectangle().GetHeight() / 2.0;
		Ray ray = new Ray(x, y, m_cacheCos, m_cacheSin);
		
		//iterate through all the PhyiscsObjects on the map to find the nearest one
		double nearestDist = Double.MAX_VALUE, distCache;
		Iterator<PhysicsObject> objIter = physics.GetPhysicsModel().GetPhysicsObjectIterator();
		PhysicsObject current;
		
		while (objIter.hasNext())
		{
			current = objIter.next();
			if (current == physics || current.GetInteractionType() == InteractionType.Ghost)//the object cannot be the robot, nor can it have InteractionType.Ghost
				continue;
			distCache = ray.DistanceTo(current.GetBounds());
			if (distCache < nearestDist && distCache >= 0.0)
				nearestDist = distCache;
		}
		
		m_data = nearestDist;//store the distance to the nearest PhyiscsObject - if there isn't one in front of the sensor, it stores Double.MAX_VALUE 
	}
}
