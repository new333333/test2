package org.kabling.teaming.install.client.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

/**
 * Default resources for spinning arrows.
 */
public interface SpinnerResources extends ClientBundle
{
	/**
	 * Arrow down.
	 * 
	 * @return the image resource
	 */
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("arrowDown.png")
	ImageResource arrowDown();

	/**
	 * Arrow down disabled.
	 * 
	 * @return the image resource
	 */
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("arrowDownDisabled.png")
	ImageResource arrowDownDisabled();

	/**
	 * Arrow down hover.
	 * 
	 * @return the image resource
	 */
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("arrowDownHover.png")
	ImageResource arrowDownHover();

	/**
	 * Arrow down pressed.
	 * 
	 * @return the image resource
	 */
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("arrowDownPressed.png")
	ImageResource arrowDownPressed();

	/**
	 * Arrow up.
	 * 
	 * @return the image resource
	 */
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("arrowUp.png")
	ImageResource arrowUp();

	/**
	 * Arrow up disabled.
	 * 
	 * @return the image resource
	 */
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("arrowUpDisabled.png")
	ImageResource arrowUpDisabled();

	/**
	 * Arrow up hover.
	 * 
	 * @return the image resource
	 */
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("arrowUpDisabled.png")
	ImageResource arrowUpHover();

	/**
	 * Arrow up pressed.
	 * 
	 * @return the image resource
	 */
	@ImageOptions(repeatStyle = RepeatStyle.Both)
	@Source("arrowUpPressed.png")
	ImageResource arrowUpPressed();
}