package graphicutils;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MessageBox extends JFrame
{

	public MessageBox(String title, String message)
	{
		this(title, message, true);
	}
	public MessageBox(String title, String msg, boolean visible){
		super(title);

		JButton close=new JButton("OK");

		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setVisible(false);
				close();
			}
		});



		JPanel p=new JPanel();
		p.setLayout(new GridBagLayout());

		int loc=0;

		for(String x:msg.split("\n"))
		{
			GraphicUtils.constrain(p, new JLabel(x), 0, loc++, 1, 1);
		}
		GraphicUtils.constrain(p, close, 0, loc, 1 ,1, GridBagConstraints.CENTER);

		add(p);
		pack();
		setResizable(false);

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				setVisible(false);
				close();
			}
		});

		setVisible(visible);
	}

	public void close()
	{
	}
}