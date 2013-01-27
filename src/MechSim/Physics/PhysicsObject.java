package MechSim.Physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

import MechSim.Graphics.SelfRendering;

/**
 * A PhysicsObject that physically interacts with the rest of the simulated world
 * @author Gurwinder Gulati
 *
 */
public final class PhysicsObject implements SelfRendering
{
	static final Color DEFAULT_COLOR = Color.BLACK;
	static final Color NOT_COLLIDING_COLOR = Color.GREEN, COLLIDING_COLOR = Color.RED;
	
	PhysicsModel m_physicsModel;
	InteractionType m_interType;
	CollisionBounds m_bounds;
	double m_mass;
	double m_posX, m_posY;
	double m_velocityX, m_velocityY;
	double m_accelX, m_accelY;
	double m_rotation;

	Color m_color;
	boolean m_render;
	
	List<CollisionListener> m_listeners;
	boolean m_collidedLastFrame;
	
	/**
	 * Constructs a PhysicsObject that physically interacts with the rest of the simulated world
	 * @param physics PhysicsModel applied to the PhysicsObject
	 * @param mass Mass of the PhysicsObject, in kilograms
	 * @param bounds Collision boundaries of the PhysicsObject
	 */
	public PhysicsObject(PhysicsModel physics, InteractionType interType, double mass, Circle bounds)
	{
		this(physics, interType, mass);
		m_bounds = new CollisionBounds(bounds);
	}

	/**
	 * Constructs a PhysicsObject that physically interacts with the rest of the simulated world
	 * @param physics PhysicsModel applied to the PhysicsObject
	 * @param mass Mass of the PhysicsObject, in kilograms
	 * @param bounds Collision boundaries of the PhysicsObject
	 */
	public PhysicsObject(PhysicsModel physics, InteractionType interType, double mass, Rectangle bounds)
	{
		this(physics, interType, mass);
		m_bounds = new CollisionBounds(bounds);
	}

	/**
	 * Constructs a PhysicsObject that physically interacts with the rest of the simulated world
	 * @param physics PhysicsModel applied to the PhysicsObject
	 * @param mass Mass of the PhysicsObject, in kilograms
	 * @param bounds Collision boundaries of the PhysicsObject
	 */
	public PhysicsObject(PhysicsModel physics, InteractionType interType, double mass, CollisionBounds bounds)
	{
		this(physics, interType, mass);
		m_bounds = bounds;
	}

	private PhysicsObject(PhysicsModel physics, InteractionType interType, double mass)
	{
		m_physicsModel = physics;
		m_mass = mass <= 0.0 ? .01 : mass;
		m_interType = interType;
		m_render = true;
		m_color = DEFAULT_COLOR;
		m_listeners = new ArrayList<CollisionListener>();
	}
	
	/**
	 * Updates the PhysicsObject's physical properties
	 * @param timeDelta Time since last update
	 */
	public void Update(double timeDelta)
	{
		m_collidedLastFrame = false;
		if (m_interType == InteractionType.Kinetic || m_interType == InteractionType.Ghost || m_velocityX != 0.0 || m_velocityY != 0.0)
			m_physicsModel.PerformCollisionFor(this);
		
		double dX = m_velocityX * timeDelta + m_accelX * timeDelta * timeDelta * 0.5, dY = m_velocityY * timeDelta + m_accelY * timeDelta * timeDelta * 0.5,
		friction = Math.pow(m_physicsModel.m_friction, timeDelta);
		if (m_bounds.m_boundsType == BoundsType.Circle)
		{
			m_bounds.m_circle.X += dX;
			m_bounds.m_circle.Y += dY;
		}
		else
		{
			m_bounds.m_rect.m_x += dX;
			m_bounds.m_rect.m_y += dY;
			m_bounds.m_rect.m_right = m_bounds.m_rect.m_x + m_bounds.m_rect.m_width;
			m_bounds.m_rect.m_bottom = m_bounds.m_rect.m_y + m_bounds.m_rect.m_height;
		}
		m_posX += dX;
		m_posY += dY;
		m_velocityX = m_velocityX * friction + m_accelX * timeDelta;
		m_velocityY = m_velocityY * friction + m_accelY * timeDelta;
		m_accelX = m_accelY = 0;
	}
	
	/**
	 * Renders the PhysicsObject on to the screen
	 */
	@Override
	public void Render(Graphics2D g, ImageObserver renderer)
	{
		if (m_render)
		{
			g.setColor(m_color);
			if (m_bounds.m_boundsType == BoundsType.Circle)
				g.fillOval((int)(m_bounds.GetCollisionCircle().X - m_bounds.GetCollisionCircle().Radius), (int)(m_bounds.GetCollisionCircle().Y - m_bounds.GetCollisionCircle().Radius), (int)(m_bounds.GetCollisionCircle().Radius * 2.0), (int)(m_bounds.GetCollisionCircle().Radius * 2.0));
			else
				g.fillRect((int)m_bounds.GetCollisionRectangle().m_x, (int)m_bounds.GetCollisionRectangle().m_y, (int)m_bounds.GetCollisionRectangle().m_width, (int)m_bounds.GetCollisionRectangle().m_height);
		}
	}
	
	/**
	 * Renders an outline of the PhysicsObject for debugging purposes
	 * @param g Graphics2D used to draw the resource
	 * @param renderer ImageObserver used to optimize drawing by not drawing off-screen
	 */
	public void RenderWireframe(Graphics2D g, ImageObserver renderer)
	{
		g.setColor(m_collidedLastFrame ? COLLIDING_COLOR : NOT_COLLIDING_COLOR);
		if (m_bounds.m_boundsType == BoundsType.Circle)
			g.drawOval((int)(m_bounds.GetCollisionCircle().X - m_bounds.GetCollisionCircle().Radius), (int)(m_bounds.GetCollisionCircle().Y - m_bounds.GetCollisionCircle().Radius), (int)(m_bounds.GetCollisionCircle().Radius * 2.0), (int)(m_bounds.GetCollisionCircle().Radius * 2.0));
		else
			g.drawRect((int)m_bounds.GetCollisionRectangle().m_x, (int)m_bounds.GetCollisionRectangle().m_y, (int)m_bounds.GetCollisionRectangle().m_width, (int)m_bounds.GetCollisionRectangle().m_height);
	}
	
	/**
	 * Notifies all subscribed listeners of the collision
	 * @param other PhysicsObject that was collided against
	 */
	void NotifyOfCollision(PhysicsObject other)
	{
		m_collidedLastFrame = true;
		for (CollisionListener listener : m_listeners)
			listener.NotifyOfCollision(this, other);
	}
	
	/**
	 * The CollisionListener will be notified of further collisions that this PhysicsObject experiences
	 * @param listener Subscribing listener
	 */
	public void RegisterListener(CollisionListener listener)
	{
		m_listeners.add(listener);
	}
	
	/**
	 * The CollisionListener will no longer be notified of further collisions that this PhysicsObject experiences
	 * @param listener Unsubscribing listener
	 */
	public void UnregisterListener(CollisionListener listener)
	{
		m_listeners.remove(listener);
	}
	
	/**
	 * Checks whether the PhysicsObject collides with another object
	 * @param other PhysicsObject to check collision against
	 * @return Whether or not they collide
	 */
	public boolean Intersects(PhysicsObject other)
	{
		return m_bounds.Intersects(other.m_bounds);
	}
	
	/**
	 * @return Whether or not the PhysicsObject collided last frame
	 */
	public boolean CollidedLastFrame() { return m_collidedLastFrame; }
	
	/**
	 * @return Color the PhysicsObject is rendered with
	 */
	public Color GetColor() { return m_color; }
	
	/**
	 * @param color Color the PhysicsObject is rendered with
	 */
	public void SetColor(Color color) { m_color = color; }
	
	/**
	 * @return Whether or not the PhysicsObject renders itself
	 */
	public boolean IsRendered() { return m_render; }
	
	/**
	 * Toggles whether or not the PhysicsObject renders itself
	 */
	public void ToggleRendering() { m_render = !m_render; }
	
	/**
	 * @param render Whether or not the PhysicsObject renders itself
	 */
	public void SetRendering(boolean render) { m_render = render; }
	
	/**
	 * @return X position of the PhysicsObject in world space
	 */
	public double GetPositionX() { return m_posX; }

	/**
	 * @return Y position of the PhysicsObject in world space
	 */
	public double GetPositionY() { return m_posY; }

	/**
	 * @return Current velocity of the PhysicsObject in the X dimension
	 */
	public double GetVelocityX() { return m_velocityX; }

	/**
	 * @return Current velocity of the PhysicsObject in the Y dimension
	 */
	public double GetVelocityY() { return m_velocityY; }
	
	/**
	 * @return Current acceleration of the PhysicsObject in the X dimension
	 */
	public double GetAccelerationX() { return m_accelX; }

	/**
	 * @return Current acceleration of the PhysicsObject in the Y dimension
	 */
	public double GetAccelerationY() { return m_accelY; }

	/**
	 * @return The physics model used by the PhysicsObject
	 */
	public PhysicsModel GetPhysicsModel() { return m_physicsModel; }
	
	/**
	 * @return Collision information associated with the PhysicsObject
	 */
	public final CollisionBounds GetBounds() { return m_bounds; }
	
	/**
	 * @return Mass of the PhysicsObject, in kilograms
	 */
	public final double GetMass() { return m_mass; }
		
	/**
	 * Applies a Newtonion force to the PhysicsObject
	 * @param forceX Force, in Joules, in the X dimension
	 * @param forceY Force, in Joules, in the Y dimension
	 */
	public void ApplyForce(double forceX, double forceY)
	{
		ApplyAcceleration(forceX / m_mass, forceY / m_mass);
	}
	
	/**
	 * Accelerates the PhysicsObject
	 * @param accelX Acceleration in the X dimension
	 * @param accelY Acceleration in the Y dimension
	 */
	public void ApplyAcceleration(double accelX, double accelY)
	{
		m_accelX += accelX;
		m_accelY += accelY;
	}
	
	/**
	 * Manually sets the acceleration of the PhysicsObject, replacing whatever it may have been previously
	 * @param accelX Acceleration in the X dimension
	 * @param accelY Acceleration in the Y dimension
	 */
	public final void SetAcceleration(double accelX, double accelY)
	{
		m_accelX = accelX;
		m_accelY = accelY;
	}
	
	/**
	 * Sets the position of the PhysicsObject
	 * @param x X position of the PhysicsObject in world space
	 * @param y Y position of the PhysicsObject in world space
	 */
	public void SetPosition(double x, double y)
	{
		if (m_bounds.GetBoundsType() == BoundsType.Circle)
		{
			m_bounds.GetCollisionCircle().X += x - m_posX;
			m_bounds.GetCollisionCircle().Y += y - m_posY;
		}
		else
		{
			m_bounds.GetCollisionRectangle().m_x += x - m_posX;
			m_bounds.GetCollisionRectangle().m_y += y - m_posY;
		}
		m_posX = x;
		m_posY = y;
	}
	
	/**
	 * @return Way the PhysicsObject interacts with other PhysicsObjects
	 */
	public InteractionType GetInteractionType() { return m_interType; }
}
