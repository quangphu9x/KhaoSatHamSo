package main;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Handle {
	
	// Xuất ra chuỗi kí tự trong hình ảnh
	private String getText(File file) {
		Tesseract instance = new Tesseract();
		instance.setDatapath("E:\\javaworkspace\\KhaoSatHamSo\\tessdata");

		try {

			String result = instance.doOCR(file);
			return result;

		} catch (TesseractException e) {
			System.err.println(e.getMessage());
		}
		
		return null;
	}
	
	// Xuất ra yêu cầu đề bài đã được chuẩn hóa
	public Request getRequest(File file) {
		String text = getText(file);
		text = text.toLowerCase();
		text = text.replace(" ", "");
		text = text.replace("—", "-");
		System.out.println(text);
		
		Request request = new Request();
		String[] split = splitLines(text);
		
		if(split.length < 2)
			return null;
		
		// xác định yêu cầu
		if(split[0].matches("((ks(hs)?)|(khaosat(hamso)?))"))
			request.setType(Request.KHAOSATHAMSO);
		else if(split[0].matches("(viet)?(pttt|(phuongtrinhtieptuyen))(voi)?(hamso)?.+")) {
			boolean hasPoint = false;
			boolean hasNumber = false;
			boolean hasLine = false;
			if(split[0].matches(".+tai(?:diem)?\\((\\d+(?:\\.\\d+)?);(\\d+(?:\\.\\d+)?)\\)")) {
				request.setType(Request.PTTTTAI1DIEM);
				hasPoint = true;
			} else if(split[0].matches(".+(?:di)?qua(?:diem)?\\((\\d+(?:\\.\\d+)?);(\\d+(?:\\.\\d+)?)\\)")) {
				request.setType(Request.PTTTDIQUA);
				hasPoint = true;
			} else if(split[0].matches(".+tai(diemcohoanhdo)?x0=(\\d+(\\.\\d+)?)")) {
				request.setType(Request.PTTTTAIX0);
				hasNumber = true;
			} else if(split[0].matches(".+co(hesogoc)?k=(\\d+(\\.\\d+)?)")) {
				request.setType(Request.PTTTVOIHESOGOC);
				hasNumber = true;
			} else if(split[0].matches(".+songsongvoi(duongthang)?(d:)?[xy0-9\\+\\-\\*/=]+")) {
				request.setType(Request.PTTTSONGSONG);
				hasLine = true;
			} else if(split[0].matches(".+vuonggocvoi(duongthang)?(d:)?[xy0-9\\+\\-\\*/\\.=]+")) {
				request.setType(Request.PTTTVUONGGOC);
				hasLine = true;
			}
			
			if(hasPoint) { // tìm điểm xuất hiện trong đề bài
				Pattern pattern = Pattern.compile("\\((\\d+(?:\\.\\d+)?);(\\d+(?:\\.\\d+)?)\\)");
				Matcher matcher = pattern.matcher(split[0]);
				while(matcher.find()) {
					String x = matcher.group(1);
					String y = matcher.group(2);
					Point point = new Point();
					point.setLocation(
							Double.parseDouble(x),
							Double.parseDouble(y)
							);
					
					request.setPoint(point);
				}
			} else if(hasNumber) { // tìm số x0, k xuất hiện trong đề bài
				Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)");
				Matcher matcher = pattern.matcher(split[0]);
				while(matcher.find()) {
					String number = matcher.group(1);
					double num = Double.parseDouble(number);
					request.setNumber(num);
				}
			} else if(hasLine) { // tìm đường thẳng xuất hiện trong đề bài
				Pattern pattern = Pattern.compile("([xy0-9\\+\\-\\*/\\.=]+)");
				Matcher matcher = pattern.matcher(split[0]);
				while(matcher.find()) {
					String line = matcher.group(1);
					line = fixFunction(line);
					request.setLine(line);
				}
			} else
				return null;
		} else
			return null; // không xác định được yêu cầu bài toán
		
		// Xác định hàm số
		String func = null;
		if(split.length == 2) // hàm số nằm trên 1 dòng
			func = fixFunction(split[1]);
		else if(split.length == 3) { // hàm số nằm trên 2 dòng, dạng phân thức
			String numerator = fixFunction(split[1]);
			String denominator = fixFunction(split[2]);
			func = "(" + numerator + ") / (" + denominator + ")";
		} else
			return null; // hàm số không được hỗ trợ
		
		request.setFunc(func);
		return request;
		
	}
	
	// tách các dòng của đề bài
	private String[] splitLines(String str) {
		String[] result;
		ArrayList<String> arr = new ArrayList<>();
		String[] split = str.split("\n");
		for(String s: split)
			if(s != null && !s.equals(""))
				arr.add(s);
		
		result = new String[arr.size()];
		for(int i = 0; i < arr.size(); i++)
			result[i] = arr.get(i);
		
		return result;
		
	}
	
	public static String fixFunction(String func) {
		func = func.replaceAll("x(\\d+(?:\\.\\d+)?)", "x^$1");
		func = func.replaceAll("(\\d+(?:\\.\\d+)?)x", "$1*x");
		func = func.replaceAll("(\\d+(?:\\.\\d+)?)y", "$1*y");
		func = func.replaceAll("(\\S)(\\+|-|/)(\\S)", "$1 $2 $3");
		return func;
	}
	
	private void connectToMaple() {
        BufferedWriter bw = null;
        
        // lay ket qua tu maple
        String answer = "";
        System.out.println("Connecting to maple...");
        try {
            Process process = new ProcessBuilder("run.bat").start();
            InputStream processInputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(processInputStream));
            
            String str;
            // loai bo cac dong thua
            while((str = reader.readLine()) != null) {
                if(str.lastIndexOf("showSolution") >=0)
                    break;
            }
            
            // bat dau lay ket qua
            while((str = reader.readLine()) != null) {
                if(str.lastIndexOf("memory") >= 0) {
                    int begin = str.indexOf("memory");
                    System.out.print("fixed: " + str);
                    int end = str.indexOf("time");
                    end = str.indexOf(" ", end + 1);
                    if(end < 0) {
                        if(begin <= 0)
                            continue;
                        end = str.length() - 1;
                    }
                    String s = str.substring(begin, end);
                    str = str.replace(s, "");
                    System.out.println(" to " + str);
                }
                
                if(str.lastIndexOf("quit") >= 0) {
                    System.out.println("deleted:" + str);
                    break;
                }
                answer += (str + "\n");
            }
            
            process.destroy();
            // loai bo cac dong thua o cuoi loi giai
            //for(int i = 0; i < 3; i++) {
            //    int pos = answer.lastIndexOf('\n');
            //    answer = answer.substring(0, pos);
            //}
        } catch(IOException e)  {
            e.printStackTrace();
        }
        
        if(answer.equals("")) {
        	Main.printTextOnCanvas("Có lỗi xảy ra, không nhận được phản hồi từ Maple");
        	return;
        }
        // ghi noi dung maple tra ve vao file latex
        System.out.println("Converting to Latex...");
        bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("output.tex"));
            bw.write("\\documentclass[preview,14pt]{standalone}");
            bw.newLine();
            bw.write("\\usepackage{amsmath}");
            bw.newLine();
            bw.write("\\usepackage{tikz}");
            bw.newLine();
            bw.write("\\usepackage[utf8]{vietnam}");
            bw.newLine();
            bw.write("\\usepackage{graphicx}");
            bw.newLine();
            bw.write("\\usepackage{tkz-tab}");
            bw.newLine();
            bw.write("\\begin{document}");
            bw.newLine();
            bw.write(answer); // ghi loi giai vao file latex
            bw.newLine();
            //bw.newLine();
            //bw.write("\\includegraphics[scale=.5]{plot.jpg}"); // ve do thi
            //bw.newLine();
            bw.write("\\end{document} ");
            bw.flush();
            bw.close();
        } catch(IOException e) {
            if(bw != null) {
                try {
                    bw.close();
                } catch(IOException ex) {}
            }
        }
        
        // chuyen file .tex sang .pdf va .png
        try {
            System.out.println("Creating image...");
            Process process = new ProcessBuilder("createOutputImage.bat").start();
            InputStream processInputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(processInputStream));
            
            while(reader.readLine() != null);
            process.destroy();
            System.out.println("output.pdf and output.jpg is created");
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
        
    }
	
	public void findSolution(Request request) {
		int type = request.getType();
		String command = getCommand(request);
		System.out.println(command);
		// ghi lenh maple vao file
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter("input.mpl"));
			// xuat ket qua duoi dang 1D
			// bw.write("interface(prettyprint = 0);");
			// bw.newLine();
			if(type == Request.KHAOSATHAMSO) {
				bw.write(
						"packageDir := cat(currentdir(), kernelopts(dirsep), \"KhaoSatHamSo.mla\"):");
				bw.newLine();
				bw.write("march('open', packageDir):");
				bw.newLine();
				bw.write("with(KhaoSatHamSo):");
			} else {
				bw.write(
						"packageDir := cat(currentdir(), kernelopts(dirsep), \"PhuongTrinhtiepTuyen.mla\"):");
				bw.newLine();
				bw.write("march('open', packageDir):");
				bw.newLine();
				bw.write("with(PhuongTrinhTiepTuyen):");
			}
			bw.newLine();
			bw.write(command);
			bw.flush();
			bw.close();
			connectToMaple();
		} catch (IOException e) {
			e.printStackTrace();
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	// lấy câu lệnh trong maple để giải bài toán
	private String getCommand(Request request) {
		int type = request.getType();
		String function = request.getFunc();
		String command = null; // lệnh
		String var; // biến tương ứng khi đưa vào maple, vd: đường thẳng song song (d), tiếp điểm (M)
		
		switch(type) {
			case Request.KHAOSATHAMSO:
				command = "y := " + function + ";\nshowSolution(y);";
				break;
			case Request.PTTTTAI1DIEM: case Request.PTTTDIQUA:
				var = "M";
				if(type == Request.PTTTDIQUA)
					var = "N";
				
				Point p = request.getPoint();
				String x = getStringOfNumber(p.getX());
				String y = getStringOfNumber(p.getY());
				command = "showSolution("
						+ "{fx, " + var + "},"
						+ " {fx = " + function + ", " + var + " = [" + x +  ", " + y + "]});";
				break;
			case Request.PTTTTAIX0: case Request.PTTTVOIHESOGOC:
				var = "x_0";
				if(type == Request.PTTTVOIHESOGOC)
					var = "k";
				
				String number = getStringOfNumber(
						request.getNumber());
				command = "showSolution("
						+ "{fx, " + var + "}, "
						+ "{fx = " + function + ", " + var + " = " + number + "});";
				break;
			case Request.PTTTSONGSONG: case Request.PTTTVUONGGOC:
				var = "d";
				if(type == Request.PTTTVUONGGOC)
					var = "d_1";
				
				String line = request.getLine();
				command = "showSolution("
						+ "{fx, " + var + "}, "
						+ "{fx = " + function + ", " + var + " = \"" + line + "\"});";
				break;
		}
		return command;
	}
	
	// Lấy chuỗi tương ứng với số double, chuyển về kiểu nguyên nếu có thể
	public static String getStringOfNumber(double n) {
		int intVal = (int)n;
		if(n == intVal)
			return Integer.toString(intVal);
		return Double.toString(n);
	}

}
