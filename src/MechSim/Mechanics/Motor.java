package MechSim.Mechanics;

import MechSim.Physics.PhysicsModel;
import MechSim.Skeleton.Robot;

/**
 * A MechanicalDevice that moves the associated Robot
 * @author Gurwinder Gulati
 *
 */
public class Motor extends MechanicalDevice
{
	double m_maxPower;
	double m_powerCountdown, m_powerOrdered;
	
	double m_rotation;
	double m_cacheCos, m_cacheSin, m_cacheNetRotation;//values cached for optimization purposes
	
	/**
	 * Creates a Motor that moves the Robot
	 * @param robot Robot the MechanicalDevice is attached to
	 * @param maxPower Maximum force that can be applied to the robot in kilogram-meters, or Joules, per second
	 * @param rotation Rotation relative to the Robot
	 */
	public Motor(Robot robot, double maxPower, double rotation)
	{
		super(robot);
		
		m_maxPower = maxPower < 0.0 ? maxPower * -0.001 : maxPower * 0.001;
		
		m_rotation = rotation;
		m_cacheNetRotation = m_robot.GetTransformedData().GetRotation() + m_rotation;
		m_cacheCos = Math.cos(m_cacheNetRotation);
		m_cacheSin = Math.sin(m_cacheNetRotation);
	}
	
	/**
	 * Updates the Motor and moves the Robot if need be
	 */
	@Override
	public void Update(double timeDelta)
	{
		//Quick if check for optimization reasons - no reason to do work when the motor isn't supposed to be on
		if (m_powerCountdown != 0.0)
		{
			//if the robot re-oriented itself, cached sine and cosine values need to be re-calculated
			double rotation = m_robot.GetTransformedData().GetRotation() + m_rotation;
			if (Math.abs(rotation - m_cacheNetRotation) > PhysicsModel.EPSILON)
			{
				m_cacheNetRotation = rotation;
				m_cacheCos = Math.cos(m_cacheNetRotation);
				m_cacheSin = Math.sin(m_cacheNetRotation);
			}
			
			double mult = m_powerOrdered * m_maxPower;//calculate the force applied by the motor per millisecond
			if (m_powerCountdown >= timeDelta)
			{
				//apply force for the time since last update
				m_robot.GetPhysicsObject().ApplyForce(m_cacheCos * mult * timeDelta, m_cacheSin * mult * timeDelta);
				m_powerCountdown -= timeDelta;
			}
			else
			{
				//apply force for the last few milliseconds the motor was to stay on for
				m_robot.GetPhysicsObject().ApplyForce(m_cacheCos * mult * m_powerCountdown, m_cacheSin * mult * m_powerCountdown);
				m_powerCountdown = 0.0;
			}
		}
	}

	/**
	 * Turns on the motor for a specified amount of time
	 * @param power Percentage of maximum power to use, between -1.0 for full reverse and 1.0 for full forward
	 * @param time Time the motor should be active for, in milliseconds
	 */
	public void Activate(double power, double time)
	{
		m_powerOrdered = power < -1.0 ? -1.0 : power > 1.0 ? 1.0 : power;
		m_powerCountdown = time < 0.0 ? 0.0 : time;
	}
}
