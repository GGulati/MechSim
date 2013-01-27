package MechSim.Physics;

/**
 * A geometric ray for LOS and distance checks
 * @author Gurwinder Gulati
 *
 */
public final class Ray
{	
	double m_originX, m_originY, m_headingX, m_headingY;
	
	/**
	 * Creates a ray for LOS checks
	 * @param x X position of the origin of the ray
	 * @param y Y position of the origin of the ray
	 * @param headingX
	 * @param headingY
	 */
	public Ray(double x, double y, double headingX, double headingY)
	{		
		m_originX = x;
		m_originY = y;
		
		if (Math.abs(headingX) <= PhysicsModel.EPSILON)
			headingX = 0.0;
		if (Math.abs(headingY) <= PhysicsModel.EPSILON)
			headingY = 0.0;
		
		//normalize heading
		double divisor = Math.sqrt(headingX * headingX + headingY * headingY);
		m_headingX = headingX / divisor;
		m_headingY = headingY / divisor;
	}
	
	/**
	 * @param circle Circle to check intersection against
	 * @return Whether or not the ray intersects with the Circle
	 */
	public boolean Intersects(Circle circle)
	{
		double yDiff = m_originY + m_headingY * (circle.X - m_originX) / m_headingX - circle.Y;
		return yDiff * yDiff <= circle.Radius * circle.Radius;
	}
	
	/**
	 * @param other Rectangle to check intersection against
	 * @return Whether or not the ray intersects with the Rectangle
	 */
	public boolean Intersects(Rectangle other)
	{
		double dist = 0.0;
		double maxValue = Double.MAX_VALUE;
		
		if (Math.abs(m_headingX) < PhysicsModel.EPSILON && (m_originX < other.m_x || m_originX > other.m_right))
		{
			return false;
		}
		else 
		{
			double left = (other.m_x - m_originX) * 1.0 / m_headingX;
			double right = (other.m_right - m_originX) * 1.0 / m_headingX;
			if (left > right)
			{
				double cache = left;
				left = right;
				right = cache;
			}
			dist = left > dist ? left : dist;
			maxValue = right < maxValue ? right : maxValue;
			if (dist > maxValue)
				return false;
		}
		
		if (Math.abs(m_headingY) < PhysicsModel.EPSILON && (m_originY < other.m_y || m_originY > other.m_bottom))
		{
			return false;
		}
		else 
		{
			double top = (other.m_y - m_originY) * 1.0 / m_headingY;
			double bottom = (other.m_bottom - m_originY) * 1.0 / m_headingY;
			if (top > bottom)
			{
				double cache = top;
				top = bottom;
				bottom = cache;
			}
			dist = top > dist ? top : dist;
			maxValue = bottom < maxValue ? bottom : maxValue;
			if (dist > maxValue)
				return false;
		}
		 
		return true;
	}
	
	/**
	 * @param other CollisionBounds to check intersection against
	 * @return Whether or not the ray intersects with the CollisionBounds
	 */
	public boolean Intersects(CollisionBounds other)
	{
		if (other.m_boundsType == BoundsType.Circle)
			return Intersects(other.m_circle);
		else
			return Intersects(other.m_rect);
	}
	
	/**
	 * Calculates the distance between the origin of the ray and the exterior of the Circle
	 * @param circle Circle to check against
	 * @return Distance if the two intersect, and Double.MAX_VALUE otherwise
	 */
	public double DistanceTo(Circle circle)
	{
		if (Intersects(circle))
		{
			double xDiff = m_originX - circle.X, yDiff = m_originY - circle.Y;
			double b = -xDiff * m_headingX - yDiff * m_headingY;
			double det = b * b - xDiff * xDiff - yDiff * yDiff + circle.Radius * circle.Radius;
			if (det <= 0.0)//misses the circle
				return Double.MAX_VALUE;
			
			det = Math.sqrt(det);
			
			double cache = b - det;
			return cache < 0 ? b + det : cache;
		}
		return Double.MAX_VALUE;
	}
	
	/**
	 * Calculates the distance from the ray's origin to a Rectangle's bounds
	 * @param other Rectangle to check against
	 * @return Distance if the two intersect, and Double.MAX_VALUE otherwise
	 */
	public double DistanceTo(Rectangle other)
	{
		double dist = 0.0;
		double maxDist = Double.MAX_VALUE;
		
		if (Math.abs(m_headingX) < PhysicsModel.EPSILON && (m_originX < other.m_x || m_originX > other.m_right))
		{
			return Double.MAX_VALUE;
		}
		else 
		{
			double left = (other.m_x - m_originX) / m_headingX;
			double right = (other.m_right - m_originX) / m_headingX;
			if (left > right)
			{
				double cache = left;
				left = right;
				right = cache;
			}
			dist = left > dist ? left : dist;
			maxDist = right < maxDist ? right : maxDist;
			if (dist > maxDist)
				return Double.MAX_VALUE;
		}
		
		if (Math.abs(m_headingY) < PhysicsModel.EPSILON && (m_originY < other.m_y || m_originY > other.m_bottom))
		{
			return Double.MAX_VALUE;
		}
		else 
		{
			double top = (other.m_y - m_originY) / m_headingY;
			double bottom = (other.m_bottom - m_originY) / m_headingY;
			if (top > bottom)
			{
				double cache = top;
				top = bottom;
				bottom = cache;
			}
			dist = top > dist ? top : dist;
			maxDist = bottom < maxDist ? bottom : maxDist;
			if (dist > maxDist)
				return Double.MAX_VALUE;
		}
		 
		return dist;
	}

	/**
	 * Calculates the distance between the origin of the ray and the exterior of the CollisionBounds
	 * @param other CollisionBounds to check against
	 * @return Distance if the two intersect, and Double.MAX_VALUE otherwise
	 */
	public double DistanceTo(CollisionBounds other)
	{
		if (other.m_boundsType == BoundsType.Circle)
			return DistanceTo(other.m_circle);
		else
			return DistanceTo(other.m_rect);
	}
}
