package MechSim.Sensors;

import java.awt.Color;
import java.util.Iterator;

import MechSim.Physics.BoundsType;
import MechSim.Physics.InteractionType;
import MechSim.Physics.PhysicsModel;
import MechSim.Physics.PhysicsObject;
import MechSim.Physics.Ray;
import MechSim.Skeleton.Robot;

public class ColorSensor extends Sensor
{
	/**
	 * When no color is detected by the Sensor, it's GetData() will return this constant
	 */
	public static final Color NO_OBJECT_DETECTED_COLOR = new Color(0, 0, 0, 0);
	
	Color m_data;
	double m_rotation;
	double m_cacheCos, m_cacheSin, m_cacheNetRotation;//values cached for optimization purposes

	/**
	 * Creates a Sensor that detects the color of the nearest PhysicsObject directly in front of it
	 * @param robot Robot the Sensor is attached to
	 * @param pollingInterval Time, in milliseconds, between updating the Sensor's data
	 * @param rotation Orientation of the Sensor relative to the Robot
	 */
	public ColorSensor(Robot robot, double pollingInterval, double rotation)
	{
		super(robot, pollingInterval);
		
		m_rotation = rotation;
		m_data = NO_OBJECT_DETECTED_COLOR;
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
		PhysicsObject current, nearest = null;
		
		while (objIter.hasNext())
		{
			current = objIter.next();
			if (current == physics || current.GetInteractionType() == InteractionType.Ghost)//the object cannot be the robot, nor can it have InteractionType.Ghost
				continue;
			distCache = ray.DistanceTo(current.GetBounds());
			if (distCache < nearestDist && distCache >= 0.0)
			{
				nearestDist = distCache;
				nearest = current;
			}
		}
		
		//if there is something in front of us, store the PhysicsObject's color
		if (nearest != null)
			m_data = nearest.GetColor();
		else
			m_data = NO_OBJECT_DETECTED_COLOR;//otherwise store a default error value
	}
	
	/**
	 * @return Last detected color
	 */
	public Color GetData() { return m_data; }
}
