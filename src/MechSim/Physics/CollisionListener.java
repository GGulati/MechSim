package MechSim.Physics;

/**
 * CollisionListener that is notified of the collisions of a particular PhysicsObject 
 * @author Gurwinder Gulati
 *
 */
public interface CollisionListener
{
	/**
	 * Notifies the CollisionListener of a collision
	 * @param alpha PhysicsObject the CollisionListener is subscribed to
	 * @param beta PhysicsObject that collided with alpha
	 */
	public void NotifyOfCollision(PhysicsObject alpha, PhysicsObject beta);
}
