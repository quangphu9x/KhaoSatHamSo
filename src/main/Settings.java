package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;

public class Settings extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField pathField;

	/**
	 * Create the dialog.
	 */
	public Settings() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 507, 152);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JLabel lblngDnCa = new JLabel("Đường dẫn của Maple:");
			lblngDnCa.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			contentPanel.add(lblngDnCa);
		}
		{
			pathField = new JTextField();
			pathField.setForeground(Color.BLUE);
			pathField.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			contentPanel.add(pathField);
			pathField.setColumns(35);
			pathField.setText(getSavedMaplePath());
		}
		{
			JButton buttonSelectPath = new JButton("Chọn");
			buttonSelectPath.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					buttonSelectPathActionPerformed();
				}
			});
			buttonSelectPath.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			contentPanel.add(buttonSelectPath);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	private String getSavedMaplePath() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("run.bat"));
			String text = br.readLine();
			br.close();
			
			Pattern pattern = Pattern.compile("\"(.+)\"");
			Matcher matcher = pattern.matcher(text);
			while(matcher.find()) {
				String path = matcher.group(1);
				return path;
			}
		} catch (IOException e) {
			e.printStackTrace();
			if(br != null)
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
		return null;
	}
	
	private void buttonSelectPathActionPerformed() {
		JFileChooser fileopen = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("exe file", "exe");
		fileopen.setFileFilter(filter);
		fileopen.setAcceptAllFileFilterUsed(false);
		fileopen.setApproveButtonText("Xong");
		fileopen.setDialogTitle("Chọn file cmaple");
		
		int ret = fileopen.showOpenDialog(null);
		
		if(ret == JFileChooser.APPROVE_OPTION) {
			File file = fileopen.getSelectedFile();
			String filePath = file.getPath();
			String fileName = file.getName();
			if(!fileName.equals("cmaple.exe")) {
				JOptionPane.showMessageDialog(this, "Hãy chọn đúng file cmaple.exe");
				return;
			}
			
			filePath = filePath.replace("\\", "/");
			pathField.setText(filePath);
		}
	}
	
	private void okButtonActionPerformed() {
		String path = pathField.getText();
		if(path == null || path.equals("")) {
			JOptionPane.showMessageDialog(this, "Bạn chưa chọn đường dẫn");
			return;
		}
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter("run.bat"));
			path = path.replace("\\", "/");
			bw.write("\"" + path + "\" input.mpl pause;");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		dispose();
		
	}

}
