package main;

import java.awt.Point;

public class Request {
	
	private int type; // khảo sát hàm số, viết pttt
	private String func; // hàm số cần thao tác
	private Point point; // điểm được xét khi viết pttt
	private double number; // có thể là x0, hoặc k
	private String line; // đường thẳng song song, vuông góc với tiếp tuyến
	
	public static final int KHAOSATHAMSO = 0;
	public static final int PTTTTAI1DIEM = 1;
	public static final int PTTTTAIX0 = 2;
	public static final int PTTTDIQUA = 3;
	public static final int PTTTSONGSONG = 4;
	public static final int PTTTVUONGGOC = 5;
	public static final int PTTTVOIHESOGOC = 6;
	
	public Request() {
		type = -1;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getFunc() {
		return func;
	}
	public void setFunc(String func) {
		this.func = func;
	}
	public Point getPoint() {
		return point;
	}
	
	public String getPointAsString() {
		return Handle.getStringOfNumber(point.getX())
				+ "; " + Handle.getStringOfNumber(point.getY());
	}
	public void setPoint(Point point) {
		this.point = point;
	}

	public double getNumber() {
		return number;
	}

	public void setNumber(double number) {
		this.number = number;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}
	
}
