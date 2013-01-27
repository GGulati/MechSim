package MechSim.Physics;

/**
 * A geometric circle
 * @author Gurwinder Gulati
 *
 */
public final class Circle
{
	public double X, Y, Radius;
	
	/**
	 * Constructs a new geometric circle using computer screen coordinates
	 * X = 0
	 * Y = 0
	 * Radius = 0
	 */
	public Circle()
	{
		X = Y = Radius = 0.0;
	}
	
	/**
	 * Constructs a new geometric circle using computer screen coordinates
	 * @param x X position of the center
	 * @param y Y position of the center
	 * @param radius Must be at least 0; if it is less, it will be clamped to 0
	 */
	public Circle(double x, double y, double radius)
	{
		X = x;
		Y = y;
		Radius = radius < 0 ? 0 : radius;
	}
	
	/**
	 * @param other Circle to check against
	 * @return Whether or not the two circles intersect
	 */
	public boolean Intersects(Circle other)
	{
		return (other.X - X) * (other.X - X) + (other.Y - Y) * (other.Y - Y) <= (Radius + other.Radius) * (Radius + other.Radius);
	}
	
	/**
	 * @param other Rectangle to check against
	 * @return Whether or not the Circle and Rectangle intersect
	 */
	public boolean Intersects(Rectangle other)
	{
		//clamp the circle's center to rectangle bounds to find the point in the circle nearest to the circle
		double x = X < other.m_x ? other.m_x : X > other.m_right ? other.m_right : X;
		double y = Y < other.m_y ? other.m_y : Y > other.m_bottom ? other.m_bottom : Y;

		//then check the distance between the nearest point on the rectangle compared to the center of the circle		
		return (X - x) * (X - x) + (Y - y) * (Y - y) <= Radius * Radius;
	}
	
	void Nudge(Circle other)
	{
		//find the ratio between current separation distance and what the separation distance should be (the sum of the radii)
		double xDiff = X - other.X, yDiff = Y - other.Y;
		double mag = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
		
		//move to make the separation distance equal to what it should be (the sum of the radii)
		X = other.X + xDiff / mag * (Radius + other.Radius);
		Y = other.Y + yDiff / mag * (Radius + other.Radius);
	}
	
	void Nudge(Rectangle other)
	{
		//clamp the center to the nearest point in the rectangle
		double x = X < other.m_x ? other.m_x : X > other.m_right ? other.m_right : X;
		double y = Y < other.m_y ? other.m_y : Y > other.m_bottom ? other.m_bottom : Y;

		double dX = X - x, dY = Y - y;
		
		if (dX != 0.0 || dY != 0.0)
		{
			//calculate ratio between current separation distance and the radius of the circle
			double mult = Radius / Math.sqrt(dX * dX + dY * dY);

			//then move the circle to make the separation distance equal to the radius of the circle
			X += dX * mult - dX;
			Y += dY * mult - dY;
		}
	}
}
