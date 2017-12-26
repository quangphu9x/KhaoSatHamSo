package tess4J;

import java.io.*;
import net.sourceforge.tess4j.*;

public class test {
	public static void main(String[] args) throws IOException {
		File imageFile = new File("E:\\javaworkspace\\KhaoSatHamSo\\images\\test2.png");
		Tesseract instance = new Tesseract();
		instance.setDatapath("E:\\javaworkspace\\KhaoSatHamSo\\tessdata");

		try {

			String result = instance.doOCR(imageFile);
			System.out.println(result);

		} catch (TesseractException e) {
			System.err.println(e.getMessage());
		}
	}
}