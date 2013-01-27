package MechSim.Graphics;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

/**
 * Renders an image that can be scaled, rotated and/or translated
 * @author Gurwinder Gulati
 *
 */
public final class TransformedImage implements SelfRendering, ImageObserver
{
	Image m_image;

	double m_originX, m_originY;
	double m_translateX, m_translateY;
	double m_rotation;//angle in radiians
	double m_scaleX, m_scaleY;
	
	AffineTransform m_transformCache;
	boolean m_cacheIsInvalid;
	
	/**
	 * Renders an image that can be scaled, rotated and/or translated
	 * @param img Image to be transformed; can be null
	 */
	public TransformedImage(Image img)
	{
		m_image = img;
		m_scaleX = 1.0;
		m_scaleY = 1.0;
		
		if (img != null)
		{
			img.getWidth(this);
			img.getHeight(this);
		}

		m_transformCache = new AffineTransform();
		m_cacheIsInvalid = true;
	}
	
	/**
	 * Translates the image by a certain amount when the image is rendered.
	 * @param x Pixels in the x-axis to translate by
	 * @param y Pixels in the y-axis to translate by
	 * @return Whether or not the operation was successful
	 */
	public boolean SetTranslation(double x, double y)
	{
		m_translateX = x;
		m_translateY = y;
		m_cacheIsInvalid = true;
		return true;
	}
	
	/**
	 * Adjusts the current translate of the image by a certain amount.
	 * @param xDelta Change in x translation, in pixels
	 * @param yDelta Change in y translation, in pixels
	 */
	public void Translate(double xDelta, double yDelta)
	{
		m_translateX += xDelta;
		m_translateY += yDelta;
		m_cacheIsInvalid = true;
	}
	
	/**
	 * Rotates the image by a certain amount when the image is rendered. The image is always rotated about the origin.
	 * @param rotation Rotation in radiians (clockwise)
	 * @return Whether or not the operation was successful
	 */
	public boolean SetRotation(double rotation)
	{
		m_rotation = rotation;
		m_cacheIsInvalid = true;
		return true;
	}
	
	/**
	 * Rotates the current image clockwise
	 * @param rotation Rotation in radiians (clockwise)
	 */
	public void Rotate(double rotation)
	{
		m_rotation += rotation;
		m_cacheIsInvalid = true;
	}
	
	/**
	 * Sets the origin of the image. The image is always rotated about the origin.
	 * @param originX X-position of the origin in pixels, where 0 is the left edge of the image
	 * @param originY Y-position of the origin in pixels, where 0 is the top edge of the image
	 * @return Whether or not the operation was successful
	 */
	public boolean SetOrigin(double originX, double originY)
	{
		if (originX < 0.0 || originY < 0.0)
			return false;
		
		m_originX = originX;
		m_originY = originY;
		m_cacheIsInvalid = true;
		return true;
	}

	/**
	 * Sets the scalar of the image. They determine how much the image is "stretched" while being drawn.
	 * @param scaleX Scale in the x-axis, where 1 is the original image width and 2 is twice the width
	 * @param scaleY Scale in the y-axis, where 1 is the original image height and 2 is twice the height
	 * @return Whether or not the operation was successful
	 */
	public boolean SetScalar(double scaleX, double scaleY)
	{
		if (scaleX <= 0.0 || scaleY <= 0.0)
			return false;
		
		m_scaleX = scaleX;
		m_scaleY = scaleY;
		m_cacheIsInvalid = true;
		return true;
	}

	/**
	 * @return Origin of the image in the x-axis, where 0 is the left edge of the image
	 */
	public double GetOriginX() { return m_originX; }

	/**
	 * @return Origin of the image in the y-axis, where 0 is the top edge of the image
	 */
	public double GetOriginY() { return m_originY; }

	/**
	 * @return Amount the image is scaled in the X-dimension, where 1 is the original image width and 2 is twice the width
	 */
	public double GetScaleX() { return m_scaleX; }

	/**
	 * @return Amount the image is scaled in the Y-dimension, where 1 is the original image height and 2 is twice the height
	 */
	public double GetScaleY() { return m_scaleY; }
	
	/**
	 * @return Amount the image is rotated clockwise, in radiians
	 */
	public double GetRotation() { return m_rotation; }

	/**
	 * @return Amount the image is moved from (0, 0) in screen coordinates in the X direction (right)
	 */
	public double GetTranslationX() { return m_translateX; }

	/**
	 * @return Amount the image is moved from (0, 0) in screen coordinates in the Y direction (down)
	 */
	public double GetTranslationY() { return m_translateY; }
	
	//BEGIN SelfRendering
	@Override
	public void Render(Graphics2D g, ImageObserver renderer)
	{
		if (m_image != null)
		{
			if (m_cacheIsInvalid)
			{
				m_transformCache.setToTranslation(m_translateX, m_translateY);
				m_transformCache.scale(m_scaleX, m_scaleY);
				m_transformCache.rotate(m_rotation, m_originX, m_originY);
				
				m_cacheIsInvalid = false;
			}
			g.drawImage(m_image, m_transformCache, renderer);
		}
	}
	//END SelfRendering

	//BEGIN ImageObserver
	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
	{
		if ((infoflags & ImageObserver.WIDTH) == ImageObserver.WIDTH)
		{
			m_originX = width / 2.0;
			m_cacheIsInvalid = true;
		}
		if ((infoflags & ImageObserver.HEIGHT) == ImageObserver.HEIGHT)
		{
			m_originY = height / 2.0;
			m_cacheIsInvalid = true;
		}
		
		return false;
	}
	//END ImageObserver
}
