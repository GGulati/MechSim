package MechSim.Skeleton;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

import MechSim.Graphics.TransformedImage;
import MechSim.Physics.Circle;
import MechSim.Physics.CollisionBounds;
import MechSim.Physics.InteractionType;
import MechSim.Physics.PhysicsModel;
import MechSim.Physics.Rectangle;


/**
 * A GameEntity that is capable of moving itself around the world
 * @author Gurwinder Gulati
 *
 */
public abstract class GameEntity extends GameObject
{
	protected TransformedImage m_image;
	
	/**
	 * Creates a GameEntity that is capable of moving itself around the world
	 * @param physics PhysicsModel applied to the GameEntity
	 * @param mass Mass of the GameEntity, in kilograms
	 * @param image Image used to draw the GameEntity on the screen
	 * @param bounds Collision boundaries of the GameEntity
	 */
	public GameEntity(PhysicsModel physics, double mass, Image image, CollisionBounds bounds)
	{
		super(physics, InteractionType.Kinetic, mass, bounds);
		
		m_image = new TransformedImage(image);
	}

	/**
	 * Creates a GameEntity that is capable of moving itself around the world
	 * @param physics PhysicsModel applied to the GameEntity
	 * @param mass Mass of the GameEntity, in kilograms
	 * @param image Image used to draw the GameEntity on the screen
	 * @param bounds Collision boundaries of the GameEntity
	 */
	public GameEntity(PhysicsModel physics, double mass, Image image, Circle bounds)
	{
		super(physics, InteractionType.Kinetic, mass, bounds);

		m_image = new TransformedImage(image);
	}

	/**
	 * Creates a GameEntity that is capable of moving itself around the world
	 * @param physics PhysicsModel applied to the GameEntity
	 * @param mass Mass of the GameEntity, in kilograms
	 * @param image Image used to draw the GameEntity on the screen
	 * @param bounds Collision boundaries of the GameEntity
	 */
	public GameEntity(PhysicsModel physics, double mass, Image image, Rectangle bounds)
	{
		super(physics, InteractionType.Kinetic, mass, bounds);

		m_image = new TransformedImage(image);
	}
	
	/**
	 * Updates the GameEntity's physical properties
	 * @param timeDelta Time since last update
	 */
	public void Update(double timeDelta)
	{
		super.Update(timeDelta);
		
		m_image.SetTranslation(GetPhysicsObject().GetPositionX(), GetPhysicsObject().GetPositionY());
	}

	/**
	 * Draws the GameEntity on the screen as an Image
	 */
	@Override
	public void Render(Graphics2D g, ImageObserver renderer)
	{
		m_image.Render(g, renderer);
	}
	
	/**
	 * @return Information associated with the transformation of the Robot from world coordinates (0, 0) to its current position and orientation
	 */
	public TransformedImage GetTransformedData() { return m_image; }
}
