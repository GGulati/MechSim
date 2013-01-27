package MechSim.Physics;

/**
 * Defines a geometric rectangle that cannot be rotated
 * @author Gurwinder Gulati
 *
 */
public final class Rectangle 
{
	double m_x, m_y;
	double m_width, m_height;
	double m_right, m_bottom;//cache
	
	/**
	 * Defines a geometric rectangle that cannot be rotated
	 * @param x X position of the rectangle's upper left hand corner
	 * @param y Y position of the rectangle's upper left hand corner
	 * @param width Width of the rectangle
	 * @param height Height of the rectangle
	 */
	public Rectangle(double x, double y, double width, double height)
	{
		m_x = x;
		m_y = y;
		m_width = width < 0.0 ? 0.0 : width;
		m_height = height < 0.0 ? 0.0 : height;
		m_right = m_x + m_width; 
		m_bottom = m_y + m_height;
	}

	/**
	 * @param other Circle to check against
	 * @return Whether or not the Circle and Rectangle intersect
	 */
	public boolean Intersects(Circle other)
	{
		//clamp the circle's center to rectangle bounds to find the point in the circle nearest to the circle 
		double x = other.X < m_x ? m_x : other.X > m_right ? m_right : other.X;
		double y = other.Y < m_y ? m_y : other.Y > m_bottom ? m_bottom : other.Y;
		
		//then check the distance between the nearest point on the rectangle compared to the center of the circle
		return (other.X - x) * (other.X - x) + (other.Y - y) * (other.Y - y) <= other.Radius * other.Radius;
	}
	
	/**
	 * @param other Rectangle to check against
	 * @return Whether or not the two Rectangles intersect
	 */
	public boolean Intersects(Rectangle other)
	{
		//this code is optimized with the assumption that rectangles rarely intersect
		return !(m_x > other.m_right || m_right < other.m_x || m_y > other.m_bottom || m_bottom < other.m_y);
	}
	
	void Nudge(Circle other)
	{
		//clamp the circle's center to rectangle bounds to find the point in the circle nearest to the circle
		double x = other.X < m_x ? m_x : other.X > m_right ? m_right : other.X;
		double y = other.Y < m_y ? m_y : other.Y > m_bottom ? m_bottom : other.Y;
		
		double dX = x - other.X, dY = y - other.Y;
		
		if (dX != 0.0 || dY != 0.0)
		{
			//calculate ratio between current separation distance and the radius of the circle
			double mult = other.Radius / Math.sqrt(dX * dX + dY * dY);
			
			//then move the rectangle to make the separation distance equal to the radius of the circle
			m_x += dX * mult - dX;
			m_y += dY * mult - dY;
			m_right = m_x + m_width;
			m_bottom = m_y + m_height;
		}
	}
	
	void Nudge(Rectangle other)
	{
		//calculate the difference in position between the centers of the two rectangles
		double dX = other.m_x + other.m_width * 0.5 - m_x - m_width * 0.5,
				dY = other.m_y + other.m_height * 0.5 - m_y - m_height * 0.5;
		
		//find the depth of intersection in the X and Y dimensions - that's the amount that must be nudged
		dX = dX < 0.0 ? (dX + m_width * 0.5 + other.m_width * 0.5) : (dX + m_width * -0.5 + other.m_width * -0.5);
		dY = dY < 0.0 ? (dY + m_height * 0.5 + other.m_height * 0.5) : (dY + m_height * -0.5 + other.m_height * -0.5);
		
		//nudge in the X or the Y direction, whichever is lesser (in terms of magnitude)
		if ((dX < 0.0 ? -dX : dX) < (dY < 0.0 ? -dY : dY))
		{
			m_x += dX;
			m_right = m_x + m_width;
		}
		else
		{
			m_y += dY;
			m_bottom = m_y + m_height;
		}
	}
	
	public double GetX() { return m_x; }
	
	public double GetY() { return m_y; }
	
	public double GetLeft() { return m_x; }
	
	public double GetTop() { return m_y; }
	
	public double GetRight() { return m_right; }
	
	public double GetBottom() { return m_bottom; }
	
	public double GetWidth() { return m_width; }
	
	public double GetHeight() { return m_height; }

	public void SetX(double x)
	{
		m_x = x;
		m_right = m_x + m_width;
	}
	
	public void SetY(double y)
	{
		m_y = y;
		m_bottom = m_y + m_height;
	}
	
	public void SetWidth(double width) 
	{
		m_width = width < 0.0 ? 0.0 : width;
		m_right = m_x + m_width; 
	}
	
	public void SetHeight(double height)
	{
		m_height = height < 0.0 ? 0.0 : height;
		m_bottom = m_y + m_height;
	}
}
