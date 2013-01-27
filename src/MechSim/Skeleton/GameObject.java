package MechSim.Skeleton;

import MechSim.Graphics.SelfRendering;
import MechSim.Physics.Circle;
import MechSim.Physics.CollisionBounds;
import MechSim.Physics.InteractionType;
import MechSim.Physics.PhysicsModel;
import MechSim.Physics.PhysicsObject;
import MechSim.Physics.Rectangle;


/**
 * A GameObject that physically interacts with the rest of the simulated world
 * @author Gurwinder Gulati
 *
 */
public abstract class GameObject implements SelfRendering
{
	PhysicsObject m_physics;
	
	/**
	 * Constructs a GameObject that physically interacts with the rest of the simulated world
	 * @param physics PhysicsModel applied to the GameObject
	 * @param mass Mass of the GameObject, in kilograms
	 * @param bounds Collision boundaries of the GameObject
	 */
	public GameObject(PhysicsModel physics, InteractionType interType, double mass, Circle bounds)
	{
		m_physics = new PhysicsObject(physics, interType, mass, bounds);
	}

	/**
	 * Constructs a GameObject that physically interacts with the rest of the simulated world
	 * @param physics PhysicsModel applied to the GameObject
	 * @param mass Mass of the GameObject, in kilograms
	 * @param bounds Collision boundaries of the GameObject
	 */
	public GameObject(PhysicsModel physics, InteractionType interType, double mass, Rectangle bounds)
	{
		m_physics = new PhysicsObject(physics, interType, mass, bounds);
	}

	/**
	 * Constructs a GameObject that physically interacts with the rest of the simulated world
	 * @param physics PhysicsModel applied to the GameObject
	 * @param mass Mass of the GameObject, in kilograms
	 * @param bounds Collision boundaries of the GameObject
	 */
	public GameObject(PhysicsModel physics, InteractionType interType, double mass, CollisionBounds bounds)
	{
		m_physics = new PhysicsObject(physics, interType, mass, bounds);
	}
	
	/**
	 * Updates the GameObject's physical properties
	 * @param timeDelta Time since last update
	 */
	public void Update(double timeDelta)
	{
		m_physics.Update(timeDelta);
	}
	
	/**
	 * @return Gets the PhysicsObject that represents this GameObject in the simulation
	 */
	public PhysicsObject GetPhysicsObject() { return m_physics; }
}
