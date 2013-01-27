package MechSim.Graphics;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

/**
 * An object that renders itself
 * @author Gurwinder Gulati
 *
 */
public interface SelfRendering
{
	/**
	 * Renders itself
	 * @param g Graphics2D used to draw the resource
	 * @param renderer ImageObserver used to optimize drawing by not drawing off-screen
	 */
	public void Render(Graphics2D g, ImageObserver renderer);
}
