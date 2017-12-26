package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Main {

	private JFrame frame;
	private JTextField functionField;
	private JPanel panel;
	private JScrollPane displayArea;
	private JComboBox<String> selectComboBox;
	private static Canvas canvas;
	
	private File inputFile;
	private Handle handle;
	private Request request;
	private JTextField input2Field;
	private JMenuItem menuSettings;
	private JMenuItem mntmThot;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		canvas = new Canvas();
		handle = new Handle();
		initialize();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = frame.getWidth();
				int height = frame.getHeight();
				int panelHeight = panel.getHeight();
				int canvasHeight = 372;
				if(height > 500)
					canvasHeight = height - 128;
				
				if(width > 900) {
					panel.setSize(width - 35, panelHeight);
					displayArea.setSize(width - 35, canvasHeight);
				} else {
					panel.setSize(865, panelHeight);
					displayArea.setSize(865, canvasHeight);
				}
				frame.revalidate();
				frame.repaint();
			}
		});
		frame.setBounds(100, 100, 856, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		panel = new JPanel();
		panel.setBounds(10, 23, 820, 44);
		frame.getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel labelFunction = new JLabel("y = ");
		labelFunction.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		panel.add(labelFunction);
		
		functionField = new JTextField();
		functionField.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		panel.add(functionField);
		functionField.setColumns(25);
		
		selectComboBox = new JComboBox<String>();
		selectComboBox.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		selectComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Khảo sát hàm số", "Viết phương trình tiếp tuyến tại 1 điểm", "Viết PTTT tại điểm có hoành độ x0", "Viết phương trình tiếp tuyến đi qua 1 điểm", "Viết PTTT song song với 1 đường thẳng", "Viết PTTT vuông góc với 1 đường thẳng", "Viết PTTT có hệ số góc k"}));
		panel.add(selectComboBox);
		
		input2Field = new JTextField();
		input2Field.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		panel.add(input2Field);
		input2Field.setColumns(10);
		
		JButton submitButton = new JButton("Thực hiện");
		panel.add(submitButton);
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				submitButtonActionPerformed();
			}
		});
		submitButton.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		
		displayArea = new JScrollPane(canvas);
		displayArea.setBounds(10, 78, 820, 372);
		displayArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    displayArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		frame.getContentPane().add(displayArea);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(10, 0, 120, 21);
		frame.getContentPane().add(menuBar);
		
		JMenu menuFile = new JMenu("File");
		menuFile.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		menuBar.add(menuFile);
		
		JMenuItem menuSelectFile = new JMenuItem("Nhập bằng hình ảnh");
		menuSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectInputImage();
			}
		});
		menuSelectFile.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		menuFile.add(menuSelectFile);
		
		JMenuItem menuShowSelectedFile = new JMenuItem("Xem ảnh đã chọn");
		menuShowSelectedFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSelectedImage();
			}
		});
		menuShowSelectedFile.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		menuFile.add(menuShowSelectedFile);
		
		menuSettings = new JMenuItem("Cài đặt");
		menuFile.add(menuSettings);
		menuSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuSettingsActionPerformed();
			}
		});
		menuSettings.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		
		mntmThot = new JMenuItem("Thoát");
		mntmThot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mntmThot.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		menuFile.add(mntmThot);
	}
	
	private void selectInputImage() {
		JFileChooser fileopen = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("image files", "png", "jpg", "gif", "tiff");
		fileopen.setFileFilter(filter);
		fileopen.setAcceptAllFileFilterUsed(false);
		fileopen.setApproveButtonText("Xong");
		fileopen.setDialogTitle("Chọn hình ảnh");
		
		int ret = fileopen.showOpenDialog(null);
		
		if(ret == JFileChooser.APPROVE_OPTION) {
			inputFile = fileopen.getSelectedFile();
			imageHandling();
		}
	}
	
	private void showSelectedImage() {
		if(inputFile == null) {
			JOptionPane.showMessageDialog(frame, "Bạn chưa chọn hình ảnh");
			return;
		}
		ImageDialog imageDialog = new ImageDialog(inputFile);
		imageDialog.setVisible(true);
	}
	
	private void imageHandling() {
		request = handle.getRequest(inputFile);
		if(request == null) {
			JOptionPane.showMessageDialog(frame, "Dạng đề bài không được hỗ trợ hoặc sai định dạng");
			return;
		}
		
		functionField.setText(request.getFunc());
		int type = request.getType();
		switch(type) {
			case Request.PTTTTAI1DIEM: case Request.PTTTDIQUA:
				input2Field.setText(request.getPointAsString());
				break;
			case Request.PTTTTAIX0: case Request.PTTTVOIHESOGOC:
				input2Field.setText(Handle.getStringOfNumber(request.getNumber()));
				break;
			case Request.PTTTSONGSONG: case Request.PTTTVUONGGOC:
				input2Field.setText(request.getLine());
				break;
		}
		
		selectComboBox.setSelectedIndex(request.getType());
	}
	
	private void submitButtonActionPerformed() {
		String function = functionField.getText();
		if(function == null || function.equals("")) {
			JOptionPane.showMessageDialog(frame, "Bạn chưa nhập hàm số");
			return;
		}
		int type = selectComboBox.getSelectedIndex();
		String input2 = input2Field.getText();
		if(type > 0 &&(input2 == null || input2.equals(""))) {
			JOptionPane.showMessageDialog(frame, "Bạn chưa nhập tham số cho phương trình tiếp tuyến");
			return;
		}
		
		input2 = input2.replace(" ", "");
		request = new Request();
		switch(type) {
			case Request.PTTTDIQUA: case Request.PTTTTAI1DIEM:
					String[] split = input2.split(";");
					if(split.length < 2) {
						JOptionPane.showMessageDialog(frame, "Tọa độ điểm không hợp lệ. Tọa độ phải có dạng: x; y");
						return;
					}
					Point p = new Point();
					p.setLocation(
							Double.parseDouble(split[0]),
							Double.parseDouble(split[1])
					);
					request.setPoint(p);
				break;
			case Request.PTTTTAIX0: case Request.PTTTVOIHESOGOC:
					double number = Double.parseDouble(input2);
					request.setNumber(number);
				break;
			case Request.PTTTSONGSONG: case Request.PTTTVUONGGOC:
					String line = Handle.fixFunction(input2);
					request.setLine(line);
				break;
		}
		
		function = Handle.fixFunction(function);
		input2 = Handle.fixFunction(input2);
		
		request.setFunc(function);
		request.setType(type);
		
		handle.findSolution(request);
		canvas.loadImage();
	}
	
	private void menuSettingsActionPerformed() {
		new Settings().setVisible(true);
	}
	
	public static void printTextOnCanvas(String text) {
		canvas.showText(text);
	}
	
}
