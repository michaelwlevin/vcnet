// used for changing prints to a textfield
package jar_input;

import java.io.*;
import java.awt.*;
import javax.swing.JTextArea;

public class TextOutputStream extends OutputStream
{
	private JTextArea text;

	public TextOutputStream(JTextArea c)
	{
		text=c;
	}
	public void write(int b)
	{
		// write the byte to the text component
		text.append(""+((char)b));
	}
}