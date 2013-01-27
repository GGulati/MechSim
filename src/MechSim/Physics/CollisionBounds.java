package MechSim.Physics;

/**
 * A union of a Circle and a Rectangle
 * @author Gurwinder Gulati
 *
 */
public final class CollisionBounds
{
	Circle m_circle;
	Rectangle m_rect;
	
	BoundsType m_boundsType;
	
	/**
	 * Constructs a CollisionBounds that uses a Circle for collision
	 * @param circle Collision circle
	 */
	public CollisionBounds(Circle circle)
	{
		m_circle = circle;
		m_boundsType = BoundsType.Circle;
	}
	
	/**
	 * Constructs a CollisionBounds that uses a Rectangle for collision
	 * @param rect Collision rectangle
	 */
	public CollisionBounds(Rectangle rect)
	{
		m_rect = rect;
		m_boundsType = BoundsType.Rectangle;
	}
	
	/**
	 * Checks for intersection of two shapes
	 * @param other CollisionBounds to check against
	 * @return Whether or not the two intersect
	 */
	public boolean Intersects(CollisionBounds other)
	{
		if (m_boundsType == BoundsType.Circle)
		{
			if (other.m_boundsType == BoundsType.Circle)
				return m_circle.Intersects(other.m_circle);
			else
				return m_circle.Intersects(other.m_rect);
		}
		else
		{
			if (other.m_boundsType == BoundsType.Circle)
				return other.m_circle.Intersects(m_rect);
			else
				return m_rect.Intersects(other.m_rect);
		}
	}
	
	/**
	 * Checks for intersection of two shapes
	 * @param other Circle to check against
	 * @return Whether or not the two intersect
	 */
	public boolean Intersects(Circle other)
	{
		return m_boundsType == BoundsType.Circle ? m_circle.Intersects(other) : m_rect.Intersects(other);
	}
	
	/**
	 * Checks for intersection of two shapes
	 * @param other Rectangle to check against
	 * @return Whether or not the two intersect
	 */
	public boolean Intersects(Rectangle other)
	{
		return m_boundsType == BoundsType.Circle ? m_circle.Intersects(other) : m_rect.Intersects(other);
	}
	
	/**
	 * @return Type of shape encapsulated in the CollisionBounds
	 */
	public BoundsType GetBoundsType() { return m_boundsType; }
	
	/**
	 * @return Collision circle. If CollisionBounds is not a Circle, it returns null instead.
	 */
	public Circle GetCollisionCircle() { return m_circle; }
	
	/**
	 * @return Collision rectangle. If CollisionBounds is not a Rectangle, it returns null instead.
	 */
	public Rectangle GetCollisionRectangle() { return m_rect; }
}
