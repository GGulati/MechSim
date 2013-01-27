package MechSim.Physics;

/**
 * Ways a given PhysicsObject can interact with other PhysicsObject
 * @author Gurwinder Gulati
 *
 */
public enum InteractionType
{
	/**
	 * Is affected by collisions and checks for collisions
	 */
	Kinetic,
	/**
	 * Is affected by collisions but does not check for collisions
	 */
	Passive,
	/**
	 * Can collide with other PhysicsObjects, and is affected by them but does not check for collisions
	 */
	Static,
	/**
	 * Is not affected by collisions and does not check for collisions
	 */
	Ghost
}
