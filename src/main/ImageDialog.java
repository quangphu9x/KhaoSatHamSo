package main;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;

public class ImageDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private int width;
	private int height;
	private Canvas canvas;
	
	public ImageDialog(File file) {
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		width = image.getWidth();
		height = image.getHeight();
		if(width > 1200)
			width = 1200;
		if(height > 600)
			height = 600;
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.loadImage(file);
		add(canvas);
		
		setTitle("Xem ảnh đã chọn");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
	}

}
