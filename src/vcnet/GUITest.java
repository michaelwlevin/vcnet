package vcnet;
import javax.swing.*;
import java.awt.*;

public class GUITest
{
	public static void main(String[] args)
	{
		JTextField text=new JTextField(10);
		text.setText("Test");
		JFrame frame=new JFrame();
		frame.add(text);
		frame.pack();
		frame.setVisible(true);
	}
}