package MechSim.SimUI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import MechSim.Physics.PhysicsModel;
import MechSim.Physics.PhysicsObject;
import MechSim.Skeleton.GameObject;


/**
 * A Form for simulating one or more Robots
 * @author Gurwinder Gulati
 *
 */
public class RobotForm extends GameForm
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6887209662832536071L;

	PhysicsModel m_physics;
	List<GameObject> m_objects;
	List<PhysicsObject> m_physicsOnlyObjects;
	boolean m_debugMode;
	
	/**
	 * Creates a Form for simulating one or more Robots
	 * @param fps Target frames per second to simulate the world at - must be at least 30 and at most 120
	 * @param visible Whether or not the form is initially visible
	 */
	public RobotForm(int fps, boolean visible)
	{
		this(fps, visible, 1024, 768);
	}
	
	/**
	 * Creates a Form for simulating one or more Robots
	 * @param fps Target frames per second to simulate the world at - must be at least 30 and at most 120
	 * @param visible Whether or not the form is initially visible
	 * @param width Width of the form in pixels
	 * @param height Height of the form in pixels
	 */
	public RobotForm(int fps, boolean visible, int width, int height)
	{
		super(fps, false, width, height);
		
		m_physics = new PhysicsModel();
		m_objects = new ArrayList<GameObject>();
		m_physicsOnlyObjects = new ArrayList<PhysicsObject>();
		
		Init();
		if (visible)
			this.setVisible(true);
	}
	
	/**
	 * OVERRIDE THIS METHOD. Initializes any data particular to the form.
	 */
	protected void Init()
	{
	}
	
	/**
	 * OVERRIDE THIS METHOD. Updates all GameObjects registered to be updated
	 */
	protected void Update(double timeDelta)
	{
		super.Update(timeDelta);
		
		if (IsKeyTriggered(KeyEvent.VK_F1))
			ToggleDebugMode();

		for (PhysicsObject obj : m_physicsOnlyObjects)
			obj.Update(timeDelta);
		for (GameObject obj : m_objects)
			obj.Update(timeDelta);
	}

	/**
	 * OVERRIDE THIS METHOD. Renders out the map and all GameObjects that can be rendered
	 */
	@Override
	protected void Render(Graphics2D g)
	{
		for (GameObject obj : m_objects)
			obj.Render(g, this);

		m_physics.Render(g, this, m_debugMode);
		if (m_debugMode)
		{
			g.setColor(Color.BLACK);
			g.fillRect(m_bufferWidth - 150, 0, 150, 55);
			g.setColor(Color.WHITE);
			g.drawRect(m_bufferWidth - 151, 0, 150, 54);
			g.drawRect(m_bufferWidth - 150, 1, 148, 54);
			g.setColor(Color.WHITE);
			g.drawString("DEBUG INFO", m_bufferWidth - 145, 15);
			g.drawString("FPS: " + GetCurrentFPS() + " / " + GetTargetFPS(), m_bufferWidth - 140, 30);
		}
	}
	
	/**
	 * Registers a GameObject to be updated, both normally and physics-wise
	 * @param obj GameObject to register
	 */
	public void AddObject(GameObject obj)
	{
		m_objects.add(obj);
		m_physics.RegisterObject(obj.GetPhysicsObject());
	}

	/**
	 * Unregisters a GameObject from being updated, both normally and physics-wise
	 * @param obj GameObject to unregister
	 */
	public void RemoveObject(GameObject obj)
	{
		m_objects.remove(obj);
		m_physics.UnregisterObject(obj.GetPhysicsObject());
	}
	
	/**
	 * Registers a PhysicsObject to be updated physics-wise
	 * @param obj PhysicsObject to register
	 */
	public void AddObject(PhysicsObject obj)
	{
		m_physicsOnlyObjects.add(obj);
		m_physics.RegisterObject(obj);
	}

	/**
	 * Unregisters a PhysicsObject from being updated physics-wise
	 * @param obj PhysicsObject to unregister
	 */
	public void RemoveObject(PhysicsObject obj)
	{
		m_physicsOnlyObjects.remove(obj);
		m_physics.UnregisterObject(obj);
	}
	
	public void ToggleDebugMode()
	{
		m_debugMode = !m_debugMode;
	}
	
	public boolean IsInDebugMode() { return m_debugMode; }
	
	/**
	 * @return PhysicsModel associated with the world
	 */
	public PhysicsModel GetPhysics() { return m_physics; }
}
