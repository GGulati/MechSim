package MechSim.Physics;

/**
 * A Vector2 representing an X and Y quantity
 * @author Gurwinder Gulati
 *
 */
public final class Vector2
{
	/**
	 * Vector2 of (0, 0)
	 */
	public static final Vector2 Zero = new Vector2(0.0, 0.0);
	
	/**
	 * Vector2 of (-1, 0)
	 */
	public static final Vector2 Left = new Vector2(-1.0, 0.0);
	
	/**
	 * Vector2 of (1, 0)
	 */
	public static final Vector2 Right = new Vector2(1.0, 0.0);
	
	/**
	 * Vector2 of (0, -1)
	 */
	public static final Vector2 Up = new Vector2(0.0, -1.0);
	
	/**
	 * Vector2 of (0, 1)
	 */
	public static final Vector2 Down = new Vector2(0.0, 1.0);
	
	static final double UNCALCULATED_MAGNITUDE = -1.0;
	
	double m_x, m_y, m_magnitude;
	
	/**
	 * Creates a Vector2
	 * @param x X value of the Vector2
	 * @param y Y value of the Vector2
	 */
	public Vector2(double x, double y)
	{
		m_x = x;
		m_y = y;
		m_magnitude = UNCALCULATED_MAGNITUDE;
	}
	
	private Vector2(double x, double y, double magnitude)
	{
		m_x = x;
		m_y = y;
		m_magnitude = magnitude;
	}
	
	/**
	 * Creates a copy of the Vector2
	 * @return Copied Vector2
	 */
	public Vector2 Copy()
	{
		return new Vector2(m_x, m_y, m_magnitude);
	}
	
	/**
	 * @param x New X value of the Vector2
	 */
	public void SetX(double x) { m_x = x; m_magnitude = UNCALCULATED_MAGNITUDE; }

	/**
	 * @param y New Y value of the Vector2
	 */
	public void SetY(double y) { m_y = y; m_magnitude = UNCALCULATED_MAGNITUDE; }
	
	/**
	 * @return X value of the Vector2
	 */
	public double GetX() { return m_x; }

	/**
	 * @return Y value of the Vector2
	 */
	public double GetY() { return m_y; }
	
	/**
	 * @return Magnitude of the Vector2
	 */
	public double GetMagnitude()
	{
		if (m_magnitude == UNCALCULATED_MAGNITUDE)
			m_magnitude = Math.sqrt(m_x * m_x + m_y * m_y);
		return m_magnitude;
	}
	
	/**
	 * Scales the Vector2 so it becomes a unit vector
	 * @return Scaled unit vector
	 */
	public Vector2 Normalize()
	{
		if (m_magnitude == UNCALCULATED_MAGNITUDE)
			m_magnitude = Math.sqrt(m_x * m_x + m_y * m_y);
		if (m_magnitude == 0.0)
			return new Vector2(0.0, 0.0);
		return new Vector2(m_x / m_magnitude, m_y / m_magnitude);
	}
	
	/**
	 * Scales the Vector2 so it becomes a unit vector
	 */
	public void NormalizeInPlace()
	{
		if (m_magnitude == UNCALCULATED_MAGNITUDE)
			m_magnitude = Math.sqrt(m_x * m_x + m_y * m_y);
		
		m_x /= m_magnitude;
		m_y /= m_magnitude;
		m_magnitude = 1.0;
	}

	/**
	 * Multiplies the X and Y values by a scalar
	 * @param scalar Value to scale Vector2 by
	 */
	public void Scale(double scalar)
	{
		m_x *= scalar;
		m_y *= scalar;
		m_magnitude = UNCALCULATED_MAGNITUDE;
	}
	
	/**
	 * Adds the X and Y values of two Vector2s
	 * @param other Vector2 to add
	 */
	public void Add(Vector2 other)
	{
		m_x += other.m_x;
		m_y += other.m_y;
	}
	
	/**
	 * Subtracts the X and values of the second Vector2 from this one's
	 * @param other Vector2 to subtract
	 */
	public void Subtract(Vector2 other)
	{
		m_x -= other.m_y;
		
	}
	
	/**
	 * Calculates the Cross Product between two Vector2s
	 * @param other Other Vector2 involved in operation
	 * @return Cross product
	 */
	public double GetCrossProduct(Vector2 other)
	{
		return m_x * other.m_y - m_y * other.m_x;
	}
	
	/**
	 * Sets the Vector2 as though it reflected off the given normal
	 * @param normal Vector2 normal to the collision
	 */
	public void Reflect(Vector2 normal)
	{
		double scale = 2.0 * (m_x * normal.m_x + m_y * normal.m_y) / (normal.m_x * normal.m_x + normal.m_y * normal.m_y);
		m_x = m_x - scale * normal.m_x;
		m_y = m_y - scale * normal.m_y;
	}
}
