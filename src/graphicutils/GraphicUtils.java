package graphicutils;

import javax.swing.*;
import java.awt.*;

public abstract class GraphicUtils
{
	// only static methods, don't instantiate

	public static void constrain(Container container, Component component, int grid_x, int grid_y, int grid_width, int grid_height){
		constrain(container, component, grid_x, grid_y, grid_width, grid_height, GridBagConstraints.NONE, GridBagConstraints.NORTHWEST, 0.0, 0.0, 5, 5, 5, 5);
	}
	public static void constrain(Container container, Component component, int grid_x, int grid_y, int grid_width, int grid_height, int align){
		constrain(container, component, grid_x, grid_y, grid_width, grid_height, GridBagConstraints.NONE, align, 0.0, 0.0, 5, 5, 5, 5);
	}
	public static void constrain(Container container, Component component, int grid_x, int grid_y, int grid_width, int grid_height,
		int fill, int anchor, double weight_x, double weight_y, int top, int left, int bottom, int right)
	{
		GridBagConstraints c=new GridBagConstraints();
		c.gridx=grid_x;
		c.gridy=grid_y;
		c.gridwidth=grid_width;
		c.gridheight=grid_height;
		c.fill=fill;
		c.anchor=anchor;
		c.weightx=weight_x;
		c.weighty=weight_y;
		if(top+bottom+left+right>0){
			c.insets=new Insets(top, left, bottom, right);
		}
		((GridBagLayout)container.getLayout()).setConstraints(component, c);
		container.add(component);
	}
}