package org.kabling.teaming.install.client.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface InstallConfigImageBundle extends ClientBundle
{
	@Source("mast_head_filr_graphic.png")
	ImageResource loginFilrProductInfo();
	
	
	@Source("close_w_border.png")
	ImageResource closeBorder();

	@Source("busyanim16b.gif")
	ImageResource loading16();
	
	@Source("help_btn.png")
	ImageResource help16();
	
	@Source("warn_large.gif")
	ImageResource warnLarge();
}
