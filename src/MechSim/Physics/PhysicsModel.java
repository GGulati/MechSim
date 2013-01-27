package MechSim.Physics;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import MechSim.Graphics.SelfRendering;

/**
 * Representation of a Netwonion physics model, including relevant constants
 * Also tracks and performs collision checks and responses for registered PhysicsObjects
 * @author Gurwinder Gulati
 *
 */
public final class PhysicsModel implements SelfRendering
{
	public static final double EPSILON = .001;
	static final double ONE_OVER_SQRT_TWO = 1.0 / Math.sqrt(2.0);
	
	double m_friction;
	double m_coefficientRestitution;
	
	List<PhysicsObject> m_objects;
	
	/**
	 * Initializes a Newtonian physics model
	 */
	public PhysicsModel()
	{
		m_friction = 1.0;//no friction
		m_coefficientRestitution = 1.0;//perfectly inelastic collisions
		m_objects = new ArrayList<PhysicsObject>();
	}
	
	/**
	 * Performs collision checking for a particular PhysicsObject
	 * @param obj PhysicsObject to utilize
	 */
	public void PerformCollisionFor(PhysicsObject obj)
	{
		int size = m_objects.size();
		for (int j = 0; j < size; j++)
		{
			if (obj == m_objects.get(j))
				continue;
			Collide(obj, m_objects.get(j));
		}
	}
	
	private boolean Collide(PhysicsObject alpha, PhysicsObject beta)
	{
		if (alpha.m_bounds.Intersects(beta.m_bounds))
		{
			//if neither object is a ghost, collision response should occur
			if (alpha.m_interType != InteractionType.Ghost && beta.m_interType != InteractionType.Ghost)
			{
				boolean bothCircles = alpha.m_bounds.m_boundsType == BoundsType.Circle && beta.m_bounds.m_boundsType == BoundsType.Circle,
						bothRectangles = alpha.m_bounds.m_boundsType == BoundsType.Rectangle && beta.m_bounds.m_boundsType == BoundsType.Rectangle,
						notYetNudged = true;
				boolean alphaHasResponse = alpha.m_interType == InteractionType.Kinetic || alpha.m_interType == InteractionType.Passive,
						betaHasResponse = beta.m_interType == InteractionType.Kinetic || beta.m_interType == InteractionType.Passive;
				
				//use an if-else chain to first nudge the PhysicsObjects so they barely touch
				if (alphaHasResponse)
				{					
					double x = alpha.m_bounds.m_boundsType == BoundsType.Circle ? alpha.m_bounds.m_circle.X : alpha.m_bounds.m_rect.m_x;
					double y = alpha.m_bounds.m_boundsType == BoundsType.Circle ? alpha.m_bounds.m_circle.Y : alpha.m_bounds.m_rect.m_y;
					
					if (bothCircles)
					{
						alpha.m_bounds.m_circle.Nudge(beta.m_bounds.m_circle);

						notYetNudged = false;
					}
					else if (bothRectangles)
					{
						alpha.m_bounds.m_rect.Nudge(beta.m_bounds.m_rect);
						notYetNudged = false;
					}
					else
					{
						if (alpha.m_bounds.m_boundsType == BoundsType.Circle)
							alpha.m_bounds.m_circle.Nudge(beta.m_bounds.m_rect);
						else
							alpha.m_bounds.m_rect.Nudge(beta.m_bounds.m_circle);
						notYetNudged = false;
					}
					
					double newX = alpha.m_bounds.m_boundsType == BoundsType.Circle ? alpha.m_bounds.m_circle.X : alpha.m_bounds.m_rect.m_x;
					double newY = alpha.m_bounds.m_boundsType == BoundsType.Circle ? alpha.m_bounds.m_circle.Y : alpha.m_bounds.m_rect.m_y;
					alpha.m_posX += newX - x;
					alpha.m_posY += newY - y;
				}
				if (betaHasResponse)
				{
					double x = beta.m_bounds.m_boundsType == BoundsType.Circle ? beta.m_bounds.m_circle.X : beta.m_bounds.m_rect.m_x;
					double y = beta.m_bounds.m_boundsType == BoundsType.Circle ? beta.m_bounds.m_circle.Y : beta.m_bounds.m_rect.m_y;
										
					if (notYetNudged)
					{	
						if (bothCircles)
						{
							alpha.m_bounds.m_circle.Nudge(beta.m_bounds.m_circle);
						}
						else if (bothRectangles)
						{
							beta.m_bounds.m_rect.Nudge(alpha.m_bounds.m_rect);
						}
						else
						{
							if (beta.m_bounds.m_boundsType == BoundsType.Circle)
								beta.m_bounds.m_circle.Nudge(alpha.m_bounds.m_rect);
							else
								beta.m_bounds.m_rect.Nudge(alpha.m_bounds.m_circle);
						}
					}
					
					double newX = beta.m_bounds.m_boundsType == BoundsType.Circle ? beta.m_bounds.m_circle.X : beta.m_bounds.m_rect.m_x;
					double newY = beta.m_bounds.m_boundsType == BoundsType.Circle ? beta.m_bounds.m_circle.Y : beta.m_bounds.m_rect.m_y;
					beta.m_posX += newX - x;
					beta.m_posY += newY - y;
				}
				
				//then perform collision response
				if (alphaHasResponse || betaHasResponse)
					CollisionResponse(alpha, beta);
			}

			//in any case, the PhysicsObjects must be notified of the collision
			alpha.NotifyOfCollision(beta);
			beta.NotifyOfCollision(alpha);
			return true;
		}
		return false;
	}
	private void CollisionResponse(PhysicsObject alpha, PhysicsObject beta)
	{
		//pre-calculated booleans for readability purposes
		boolean bothCircles = alpha.m_bounds.m_boundsType == BoundsType.Circle && beta.m_bounds.m_boundsType == BoundsType.Circle,
				bothRectangles = alpha.m_bounds.m_boundsType == BoundsType.Rectangle && beta.m_bounds.m_boundsType == BoundsType.Rectangle;
		boolean alphaHasResponse = alpha.m_interType == InteractionType.Kinetic || alpha.m_interType == InteractionType.Passive,
				betaHasResponse = beta.m_interType == InteractionType.Kinetic || beta.m_interType == InteractionType.Passive;
		
		if (bothCircles)
		{
			//calculate a Newtonian reaction for a pair of circles
			double impactX = beta.m_velocityX - alpha.m_velocityX,
					impactY = beta.m_velocityY - alpha.m_velocityY;
			
			//calculate the normalized impulse force
			double impulseX = (alpha.m_bounds.m_boundsType == BoundsType.Circle ? alpha.m_bounds.m_circle.X : alpha.m_bounds.m_rect.m_x + alpha.m_bounds.m_rect.m_width / 2.0) - (beta.m_bounds.m_boundsType == BoundsType.Circle ? beta.m_bounds.m_circle.X : beta.m_bounds.m_rect.m_x + beta.m_bounds.m_rect.m_width / 2.0),
				impulseY = (alpha.m_bounds.m_boundsType == BoundsType.Circle ? alpha.m_bounds.m_circle.Y : alpha.m_bounds.m_rect.m_y + alpha.m_bounds.m_rect.m_height / 2.0) - (beta.m_bounds.m_boundsType == BoundsType.Circle ? beta.m_bounds.m_circle.Y : beta.m_bounds.m_rect.m_y + beta.m_bounds.m_rect.m_height / 2.0);
			double impulseMag = Math.sqrt(impulseX * impulseX + impulseY * impulseY);
			impulseX /= impulseMag;
			impulseY /= impulseMag;
						
			//scale the impulse by the total momentum in the collision response
			double impulseMult = m_coefficientRestitution * Math.sqrt(Math.abs(impactX * impulseX + impactY * impulseY) * alpha.m_mass * beta.m_mass);
			impulseX *= impulseMult;
			impulseY *= impulseMult;
			
			//then, for both objects, apply the portion of the impulse scaled to its mass
			if (alphaHasResponse)
			{
				alpha.m_velocityX += impulseX / alpha.m_mass;
				alpha.m_velocityY += impulseY / alpha.m_mass;
			}
			if (betaHasResponse)
			{
				beta.m_velocityX -= impulseX / beta.m_mass;
				beta.m_velocityY -= impulseY / beta.m_mass;
			}
		}
		else if (bothRectangles)
		{
			//calculate collision normal(s)
			//since they're both rectangles, the normals will be the opposite of each other
			double alphaNormalX = 0.0, alphaNormalY = 0.0, betaNormalX = 0.0, betaNormalY = 0.0;
			if (Math.abs(alpha.m_bounds.m_rect.m_right - beta.m_bounds.m_rect.m_x) <= EPSILON)//A is to the left of B
			{
				alphaNormalX = 1.0;
				betaNormalX = -1.0;
			}
			else if (Math.abs(beta.m_bounds.m_rect.m_right - alpha.m_bounds.m_rect.m_x) <= EPSILON)//A is to the right of B
			{
				alphaNormalX = -1.0;
				betaNormalX = 1.0;
			}
			else if (Math.abs(alpha.m_bounds.m_rect.m_bottom - beta.m_bounds.m_rect.m_y) <= EPSILON)//A is above B
			{
				alphaNormalY = 1.0;
				betaNormalY = -1.0;
			}
			else//A is below B
			{
				alphaNormalY = -1.0;
				betaNormalY = 1.0;
			}
			
			if (alphaHasResponse)
			{
				//reflects the velocity over the collision normal
				double scale = m_coefficientRestitution * 2.0 * (alpha.m_velocityX * betaNormalX + alpha.m_velocityY * betaNormalY) / (betaNormalX * betaNormalX + betaNormalY * betaNormalY);
				alpha.m_velocityX -= scale * betaNormalX;
				alpha.m_velocityY -= scale * betaNormalY;
				if (betaHasResponse)
				{
					//Netwon's 2nd law - equal and opposite reaction
					//the scalar reflection vector must be modified to account for the different masses of the two objects so that the two forces are still equal
					scale = scale * alpha.m_mass / beta.m_mass;
					
					beta.m_velocityX += scale * betaNormalX;
					beta.m_velocityY += scale * betaNormalY;
				}
			}
			else if (betaHasResponse)
			{
				//Netwon's 2nd law - equal and opposite reaction
				//the scalar reflection vector must be modified to account for the different masses of the two objects so that the two forces are still equal
				double scale = m_coefficientRestitution * 2.0 * (beta.m_velocityX * alphaNormalX + beta.m_velocityY * alphaNormalY) / (alphaNormalX * alphaNormalX + alphaNormalY * alphaNormalY);
				beta.m_velocityX -= scale * alphaNormalX;
				beta.m_velocityY -= scale * alphaNormalY;
				//don't need to affect alpha - if the code is executing this branch, alpha must not have a collision response
			}
		}
		//one is a rectangle and the other is a circle
		else if (alpha.m_bounds.m_boundsType == BoundsType.Rectangle)//alpha is the rectangle
		{
			if (alphaHasResponse)
			{
				//since alpha is a circle, the collision normal is the same as the line between the center of the circle and the center of the rectangle
				double dX = beta.m_bounds.m_circle.X - alpha.m_bounds.m_rect.m_x - alpha.m_bounds.m_rect.m_width / 2.0,
						dY = beta.m_bounds.m_circle.Y - alpha.m_bounds.m_rect.m_y - alpha.m_bounds.m_rect.m_height / 2.0;
				
				//normalize the collision normal
				double mag = Math.sqrt(dX * dX + dY * dY);
				dX /= mag;
				dY /= mag;

				//reflects the velocity over the collision normal
				double scale = m_coefficientRestitution * 2.0 * (alpha.m_velocityX * dX + alpha.m_velocityY * dY) / (dX * dX + dY * dY);
				alpha.m_velocityX -= scale * dX;
				alpha.m_velocityY -= scale * dY;
				if (betaHasResponse)
				{
					//Netwon's 2nd law - equal and opposite reaction
					//the scalar reflection vector must be modified to account for the different masses of the two objects so that the two forces are still equal
					scale = scale * alpha.m_mass / beta.m_mass;
					beta.m_velocityX += scale * dX;
					beta.m_velocityY += scale * dY;
				}
			}
			else if (betaHasResponse)
			{
				double alphaNormalX = 0.0, alphaNormalY = 0.0;
				if (Math.abs(beta.m_bounds.m_circle.X - alpha.m_bounds.m_rect.m_x) - beta.m_bounds.m_circle.Radius <= EPSILON)//A is to the left of B
					alphaNormalX = 1.0;
				else if (Math.abs(alpha.m_bounds.m_rect.m_right - beta.m_bounds.m_circle.X) - beta.m_bounds.m_circle.Radius <= EPSILON)//A is to the right of B
					alphaNormalX = -1.0;
				if (Math.abs(beta.m_bounds.m_circle.Y - alpha.m_bounds.m_rect.m_y) - beta.m_bounds.m_circle.Radius <= EPSILON)//A is above B
					alphaNormalY = 1.0;
				else if (Math.abs(alpha.m_bounds.m_rect.m_bottom - beta.m_bounds.m_circle.Y) - beta.m_bounds.m_circle.Radius <= EPSILON)//A is below B
					alphaNormalY = -1.0;
				
				if (alphaNormalX != 0.0 && alphaNormalY != 0.0)//normalize
				{
					//little optimization trick - if both of the normals are non-zero, then they must each be -1 or 1
					//so the magnitude of the vector is 2
					//saves an expensive Math.sqrt calculation
					alphaNormalX *= ONE_OVER_SQRT_TWO;
					alphaNormalY *= ONE_OVER_SQRT_TWO;
				}
				//reflects the velocity over the collision normal
				double scale = m_coefficientRestitution * 2.0 * (beta.m_velocityX * alphaNormalX + beta.m_velocityY * alphaNormalY) / (alphaNormalX * alphaNormalX + alphaNormalY * alphaNormalY);
				beta.m_velocityX -= scale * alphaNormalX;
				beta.m_velocityY -= scale * alphaNormalY;
				//don't need to affect alpha - if the code is executing this branch, alpha must not have a collision response
			}
		}
		else//beta is the rectangle
		{
			if (alphaHasResponse)
			{
				double betaNormalX = 0.0, betaNormalY = 0.0;
				if (Math.abs(alpha.m_bounds.m_circle.X - beta.m_bounds.m_rect.m_x) - alpha.m_bounds.m_circle.Radius <= EPSILON)//B is to the left of S
					betaNormalX = 1.0;
				else if (Math.abs(beta.m_bounds.m_rect.m_right - alpha.m_bounds.m_circle.X) - alpha.m_bounds.m_circle.Radius <= EPSILON)//B is to the right of A
					betaNormalX = -1.0;
				if (Math.abs(alpha.m_bounds.m_circle.Y - beta.m_bounds.m_rect.m_y) - alpha.m_bounds.m_circle.Radius <= EPSILON)//B is above A
					betaNormalY = 1.0;
				else if (Math.abs(beta.m_bounds.m_rect.m_bottom - alpha.m_bounds.m_circle.Y) - alpha.m_bounds.m_circle.Radius <= EPSILON)//B is below A
					betaNormalY = -1.0;
				
				if (betaNormalX != 0.0 && betaNormalY != 0.0)//normalize
				{
					//little optimization trick - if both of the normals are non-zero, then they must each be -1 or 1
					//so the magnitude of the vector is 2
					//saves an expensive Math.sqrt calculation
					betaNormalX *= ONE_OVER_SQRT_TWO;
					betaNormalY *= ONE_OVER_SQRT_TWO;
				}

				//reflects the velocity over the collision normal
				double scale = m_coefficientRestitution * 2.0 * (alpha.m_velocityX * betaNormalX + alpha.m_velocityY * betaNormalY) / (betaNormalX * betaNormalX + betaNormalY * betaNormalY);
				alpha.m_velocityX -= scale * betaNormalX;
				alpha.m_velocityY -= scale * betaNormalY;
			}
			else if (betaHasResponse)
			{
				//since alpha is a circle, the collision normal is the same as the line between the center of the circle and the center of the rectangle
				double dX = alpha.m_bounds.m_circle.X - beta.m_bounds.m_rect.m_x - beta.m_bounds.m_rect.m_width / 2.0,
				dY = alpha.m_bounds.m_circle.Y - beta.m_bounds.m_rect.m_y - beta.m_bounds.m_rect.m_height / 2.0;
		
				//normalize the collision normal
				double mag = Math.sqrt(dX * dX + dY * dY);
				dX /= mag;
				dY /= mag;

				//reflects the velocity over the collision normal
				double scale = m_coefficientRestitution * 2.0 * (beta.m_velocityX * dX + beta.m_velocityY * dY) / (dX * dX + dY * dY);
				beta.m_velocityX -= scale * dX;
				beta.m_velocityY -= scale * dY;
				//don't need to affect alpha - if the code is executing this branch, alpha must not have a collision response
			}
		}
	}
		
	/**
	 * Renders all registered PhysicsObjects' CollisionBounds
	 */
	@Override
	public void Render(Graphics2D g, ImageObserver renderer)
	{
		for (PhysicsObject obj : m_objects)
			obj.Render(g, renderer);
	}
	
	/**
	 * Renders all registered PhysicsObjects' CollisionBounds
	 * @param g Graphics2D used to draw the resource
	 * @param renderer ImageObserver used to optimize drawing by not drawing off-screen
	 * @param renderWireframe Whether or not the wireframes of the registered PhysicsObjects should be drawn
	 */
	public void Render(Graphics2D g, ImageObserver renderer, boolean renderWireframe)
	{
		for (PhysicsObject obj : m_objects)
			obj.Render(g, renderer);

		if (renderWireframe)
		{
			for (PhysicsObject obj : m_objects)
				obj.RenderWireframe(g, renderer);
		}
	}
	
	/**
	 * Registers a PhysicsObject to be updated
	 * @param obj PhysicsObject to register
	 */
	public void RegisterObject(PhysicsObject obj)
	{
		m_objects.add(obj);
	}
	
	/**
	 * Unregisters a PhysicsObject from being updated
	 * @param obj PhysicsObject to register
	 */
	public void UnregisterObject(PhysicsObject obj)
	{
		m_objects.remove(obj);
	}

	/**
	 * Sets the coefficient of friction, which is the energy lost as a GameObject moves as a percentage
	 * @param friction Coefficient, as a value between 0.0 and 1.0, inclusive
	 */
	public void SetFriction(double friction)
	{
		//The internal m_friction is calculated so that it represents energy conserved each millisecond rather than energy lost every millisecond
		//makes the friction-applying code simpler and easier
		m_friction = 1.0 - (friction < 0.0 ? 0.0 : friction > 1.0 ? 1.0 : friction);
	}

	/**
	 * Sets the coefficient of restitution, which is the energy lost in collisions as a percentage
	 * @param coefficient Coefficient, as a value between 0.0 and 1.0, inclusive
	 */
	public void SetCoefficientOfRestitution(double coefficient)
	{
		m_coefficientRestitution = coefficient < 0.0 ? 0.0 : coefficient > 1.0 ? 1.0 : coefficient;
	}
	
	/**
	 * @return The energy retained as a PhysicsObject moves as a percentage between 0.0 and 1.0, inclusive
	 */
	public double GetFriction() { return m_friction; }
	
	/**
	 * @return The energy lost in collisions as a percentage between 0.0 and 1.0, inclusive
	 */
	public double GetCoefficientOfRestitution() { return m_coefficientRestitution; }
		
	/**
	 * @return All PhysicsObject currently registered to be updated
	 */
	public Iterator<PhysicsObject> GetPhysicsObjectIterator() { return m_objects.iterator(); }
}
