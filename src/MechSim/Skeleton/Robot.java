package MechSim.Skeleton;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

import MechSim.Graphics.SelfRendering;
import MechSim.Mechanics.MechanicalDevice;
import MechSim.Physics.Circle;
import MechSim.Physics.CollisionBounds;
import MechSim.Physics.PhysicsModel;
import MechSim.Physics.Rectangle;
import MechSim.Sensors.Sensor;


/**
 * a Robot as per traditional architecture, with...
 * * Sensors that Sense
 * * The method Think(double timeDelta) to be overriden to Think
 * * MechanicalDevices that Act
 * @author Gurwinder Gulati
 *
 */
public abstract class Robot extends GameEntity
{
	List<Sensor> m_sensors;
	List<MechanicalDevice> m_devices;
	
	/**
	 * Constructs a Robot as per traditional architecture, with...
	 * * Sensors that Sense
	 * * The method Think(double timeDelta) to be overriden to Think
	 * * MechanicalDevices that Act
	 * @param physics PhysicsModel applied to the Robot
	 * @param mass Mass of the Robot, in kilograms
	 * @param image Image used to draw the Robot on the screen
	 * @param bounds Collision boundaries of the Robot
	 */
	public Robot(PhysicsModel physics, double mass, Image image, Rectangle bounds)
	{
		super(physics, mass, image, bounds);
		m_sensors = new ArrayList<Sensor>();
		m_devices = new ArrayList<MechanicalDevice>();
	}

	/**
	 * Constructs a Robot as per traditional architecture, with...
	 * * Sensors that Sense
	 * * The method Think(double timeDelta) to be overriden to Think
	 * * MechanicalDevices that Act
	 * @param physics PhysicsModel applied to the Robot
	 * @param mass Mass of the Robot, in kilograms
	 * @param image Image used to draw the Robot on the screen
	 * @param bounds Collision boundaries of the Robot
	 */
	public Robot(PhysicsModel physics, double mass, Image image, Circle bounds)
	{
		super(physics, mass, image, bounds);
		m_sensors = new ArrayList<Sensor>();
		m_devices = new ArrayList<MechanicalDevice>();
	}

	/**
	 * Constructs a Robot as per traditional architecture, with...
	 * * Sensors that Sense
	 * * The method Think(double timeDelta) to be overriden to Think
	 * * MechanicalDevices that Act
	 * @param physics PhysicsModel applied to the Robot
	 * @param mass Mass of the Robot, in kilograms
	 * @param image Image used to draw the Robot on the screen
	 * @param bounds Collision boundaries of the Robot
	 */
	public Robot(PhysicsModel physics, double mass, Image image, CollisionBounds bounds)
	{
		super(physics, mass, image, bounds);
		m_sensors = new ArrayList<Sensor>();
		m_devices = new ArrayList<MechanicalDevice>();
	}
	
	/**
	 * Updates the Robot's Sensing, Thinking and Acting parts
	 */
	public final void Update(double timeDelta)
	{
		super.Update(timeDelta);
		
		//Sense
		for (Sensor sensor : m_sensors)
			sensor.Update(timeDelta);
		
		//Think
		Think(timeDelta);
		
		//Act
		for (MechanicalDevice device : m_devices)
			device.Update(timeDelta);
	}
	
	/**
	 * OVERRIDE THIS METHOD. Processes sensory data and acts upon it.
	 * Reasons, Behaves and Remembers
	 * @param timeDelta Time since last update
	 */
	protected abstract void Think(double timeDelta);
	
	/**
	 * Draws the Robot on-screen
	 */
	public void Render(Graphics2D g, ImageObserver renderer)
	{
		super.Render(g, renderer);

		for (Sensor sensor : m_sensors)
		{
			if (sensor instanceof SelfRendering)
				((SelfRendering)sensor).Render(g, renderer);
		}
		for (MechanicalDevice device : m_devices)
		{
			if (device instanceof SelfRendering)
				((SelfRendering)device).Render(g, renderer);
		}
	}
	
	/**
	 * Adds a sensor to the Robot's Sensing aspect
	 * @param sensor Sensor to add
	 */
	public void AddSensor(Sensor sensor)
	{
		if (sensor.GetRobot() == this)
			m_sensors.add(sensor);
	}
	
	/**
	 * Adds a mechanical device to the Robot's Acting aspect
	 * @param device Device to add
	 */
	public void AddDevice(MechanicalDevice device)
	{
		if (device.GetRobot() == this)
			m_devices.add(device);
	}
}
