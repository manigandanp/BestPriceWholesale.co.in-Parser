package BestPriceWholesale.co.in;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GuiClass extends JFrame {

	private static final long serialVersionUID = 1L;

	String username;
	String password;
	JTextField usertxt;
	JPasswordField passtxt;

	public GuiClass() {
		this.setLayout(null);

		JLabel user = new JLabel("Username :");
		user.setBounds(60, 30, 100, 25);

		usertxt = new JTextField(20);
		usertxt.setBounds(150, 30, 150, 25);
		JLabel pass = new JLabel("Password :");
		pass.setBounds(60, 60, 100, 25);
		passtxt = new JPasswordField(20);
		passtxt.setBounds(150, 60, 150, 25);

		this.add(user);
		this.add(usertxt);
		this.add(pass);
		this.add(passtxt);

		JButton listPagebtn = new JButton("Grab Product Urls");
		listPagebtn.setBounds(40, 110, 150, 30);
		this.add(listPagebtn);
		JButton detailsBtn = new JButton("Grab Product Details");
		detailsBtn.setBounds(200, 110, 155, 30);
		this.add(detailsBtn);

		// this.add(panel);
		this.setTitle("BestPriceWholeSale.co.in - Parser");

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(400, 400);
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height
				/ 2 - this.getSize().height / 2);
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		listPagebtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				username = usertxt.getText().trim();
				char[] chr = passtxt.getPassword();
				password = new String(chr).trim();
				if (username.length() != 0 && password.length() != 0) {
					new ListPageParser(username, password);
				} else {
					new ListPageParser();
				}
			}
		});

		detailsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				username = usertxt.getText().trim();
				char[] chr = passtxt.getPassword();
				password = new String(chr).trim();
				if (username.length() != 0 && password.length() != 0) {
					new ProductPageParsing(username, password);
				} else {
					new ProductPageParsing();
				}
			}
		});
	}
}
