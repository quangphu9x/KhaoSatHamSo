package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Canvas extends JPanel {

	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private String text;
	private boolean drawImage = false;
	private Font font = new Font("Times New Roman", Font.PLAIN, 20);

	public Canvas() {
		setPreferredSize(new Dimension(1000, 2000));
	}

	public void loadImage() {
		try {
			image = ImageIO.read(new File("output.png"));
			drawImage = true;
			repaint();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void loadImage(File file) {
		try {
			image = ImageIO.read(file);
			drawImage = true;
			repaint();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void showText(String text) {
		this.text = text;
		drawImage = false;
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);
		if (drawImage) {
			if (image == null)
				return;
			g.drawImage(image, 0, 0, null);
		} else {
			if(text == null)
				return;
			g.setFont(font);
			g.setColor(Color.BLUE);
			g.drawString(text, 20, 50);
		}
	}

}
