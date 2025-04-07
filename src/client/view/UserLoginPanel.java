package client.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import client.controller.GameController;

public class UserLoginPanel extends JPanel {

	JButton loginButton;
	JButton logoutButton;
	JButton signUpButton;
	JLabel usernameLabel;
	JLabel winCountLabel;
	JPanel leftPanel;
	JPanel rightPanel;

	ActionListener actionListener;

	public UserLoginPanel(GameController controller) {
		super(new BorderLayout());

		// 왼쪽 사용자 정보 패널
		leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		usernameLabel = new JLabel("user: ");
		usernameLabel.setVisible(false);
		winCountLabel = new JLabel("");
		winCountLabel.setVisible(false);
		leftPanel.add(usernameLabel);
		leftPanel.add(winCountLabel);

		// 오른쪽 버튼 패널
		rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		signUpButton = new JButton("sign up");
		loginButton = new JButton("login");
		logoutButton = new JButton("logout");
		logoutButton.setVisible(false);
		rightPanel.add(signUpButton);
		rightPanel.add(loginButton);
		rightPanel.add(logoutButton);

		// topPanel에 각각 부착
		add(leftPanel, BorderLayout.WEST);
		add(rightPanel, BorderLayout.EAST);

		actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String[] usernamePassword = new String[2];
				switch (e.getActionCommand()) {
				case "sign up":
					JTextField usernameField = new JTextField(10);
					JPasswordField passwordField = new JPasswordField(10);
					JPasswordField confirmPasswordField = new JPasswordField(10);

					JPanel signUpPanel = new JPanel();
					signUpPanel.setLayout(new BoxLayout(signUpPanel, BoxLayout.Y_AXIS));
					signUpPanel.add(new JLabel("Username:"));
					signUpPanel.add(usernameField);
					signUpPanel.add(Box.createVerticalStrut(8));
					signUpPanel.add(new JLabel("Password:"));
					signUpPanel.add(passwordField);
					signUpPanel.add(Box.createVerticalStrut(8));
					signUpPanel.add(new JLabel("Confirm Password:"));
					signUpPanel.add(confirmPasswordField);
					
					// username 입력 후 엔터 시 passwordField로 포커스 이동
					usernameField.addActionListener(e1 -> passwordField.requestFocusInWindow());
					
					// 비밀번호 입력 후 엔터 시 confirmPasswordField로 포커스 이동
					passwordField.addActionListener(e2 -> confirmPasswordField.requestFocusInWindow());
					
					// 비밀번호 확인 입력 시 엔터 누르면 다이얼로그 닫히는 기능도 추가할 수 있음
					confirmPasswordField.addActionListener(e3 -> {
						// 엔터 시 OK 클릭과 동일하게 처리하고 싶다면 아래와 같이.
						// 확인 버튼 없이 바로 처리하고 싶으면 여기에 로그인 로직 넣어도 됨.
						SwingUtilities.getWindowAncestor(confirmPasswordField).dispose();
					});

					// 로그인 패널이 뜰 때 usernameField에 자동 포커스
					SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
					
					int result = JOptionPane.showConfirmDialog(UserLoginPanel.this, signUpPanel, "Sign Up",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

					if (result == JOptionPane.OK_OPTION || result == JOptionPane.CLOSED_OPTION) {
						String username = usernameField.getText().trim();
						String password = new String(passwordField.getPassword());
						String confirmPassword = new String(confirmPasswordField.getPassword());

						if (!password.equals(confirmPassword)) {
							JOptionPane.showMessageDialog(UserLoginPanel.this, "비밀번호를 다시 확인해주세요.", "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}

						if (username.isEmpty() || password.isEmpty()) {
							JOptionPane.showMessageDialog(UserLoginPanel.this, "모든 필드를 채워주세요.", "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						usernamePassword[0] = username;
						usernamePassword[1] = password;
						controller.signup(usernamePassword);
					} else {
						JOptionPane.showMessageDialog(UserLoginPanel.this, "로그인 실패", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
					break;
				case "login":
					usernameField = new JTextField(10);
					passwordField = new JPasswordField(10);

					JPanel loginPanel = new JPanel();
					loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
					loginPanel.add(new JLabel("Username:"));
					loginPanel.add(usernameField);
					loginPanel.add(Box.createVerticalStrut(10)); // 여백
					loginPanel.add(new JLabel("Password:"));
					loginPanel.add(passwordField);

					// username 입력 후 엔터 시 passwordField로 포커스 이동
					usernameField.addActionListener(e1 -> passwordField.requestFocusInWindow());
					
					// 비밀번호 입력 시 엔터 누르면 다이얼로그 닫히는 기능도 추가할 수 있음
					passwordField.addActionListener(e2 -> {
						// 엔터 시 OK 클릭과 동일하게 처리하고 싶다면 아래와 같이.
						// 확인 버튼 없이 바로 처리하고 싶으면 여기에 로그인 로직 넣어도 됨.
						SwingUtilities.getWindowAncestor(passwordField).dispose();
					});

					// 로그인 패널이 뜰 때 usernameField에 자동 포커스
					SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
					
					result = JOptionPane.showConfirmDialog(UserLoginPanel.this, loginPanel, "Login",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

					if (result == JOptionPane.OK_OPTION || result == JOptionPane.CLOSED_OPTION) {
						String username = usernameField.getText();
						String password = new String(passwordField.getPassword());

						if (username.isEmpty() || password.isEmpty()) {
							JOptionPane.showMessageDialog(UserLoginPanel.this, "모든 필드를 채워주세요.", "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						usernamePassword[0] = username;
						usernamePassword[1] = password;
						controller.login(usernamePassword);
					} else {
						JOptionPane.showMessageDialog(UserLoginPanel.this, "로그인 실패", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
					break;
				case "logout":
					controller.logout();
					break;
				}
			}
		};

		signUpButton.addActionListener(actionListener);
		loginButton.addActionListener(actionListener);
		logoutButton.addActionListener(actionListener);
	}

	public void login(String username, int winCount) {
		loginButton.setVisible(false);
		logoutButton.setVisible(true);
		signUpButton.setVisible(false);

		usernameLabel.setText("user: " + username);
		winCountLabel.setText("win: " + winCount);
		usernameLabel.setVisible(true);
		winCountLabel.setVisible(true);
	}

	public void logout() {
		loginButton.setVisible(true);
		logoutButton.setVisible(false);
		signUpButton.setVisible(true);

		usernameLabel.setVisible(false);
		winCountLabel.setVisible(false);
	}
}
