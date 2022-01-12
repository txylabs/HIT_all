package third;
import third.cal3Easy;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;

public class Menu extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Menu frame = new Menu();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Menu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 774, 530);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel title = new JLabel("加减法练习");
		title.setFont(new Font("楷体", Font.PLAIN, 44));
		title.setBounds(259, 72, 244, 51);
		contentPane.add(title);
		
		JButton btnNewButton = new JButton("练习模式");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Cal3Train train = new Cal3Train();
				train.setVisible(true);
			}
		});
		btnNewButton.setFont(new Font("宋体", Font.PLAIN, 26));
		btnNewButton.setBounds(288, 159, 177, 51);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("简单模式");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cal3Easy easy = new cal3Easy();
				easy.setVisible(true);
			}
		});
		btnNewButton_1.setFont(new Font("宋体", Font.PLAIN, 26));
		btnNewButton_1.setBounds(288, 220, 177, 51);
		contentPane.add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("困难模式");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cal3Hard hard = new cal3Hard();
				hard.setVisible(true);
			}
		});
		btnNewButton_2.setFont(new Font("宋体", Font.PLAIN, 26));
		btnNewButton_2.setBounds(288, 281, 177, 51);
		contentPane.add(btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("挑战模式");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cal3Challenge challenge = new cal3Challenge();
				challenge.setVisible(true);
			}
		});
		btnNewButton_3.setFont(new Font("宋体", Font.PLAIN, 26));
		btnNewButton_3.setBounds(288, 342, 177, 51);
		contentPane.add(btnNewButton_3);
		
		JButton btnNewButton_4 = new JButton("错题练习");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Cal3Wrong wrong = new Cal3Wrong();
				wrong.setVisible(true);
			}
		});
		btnNewButton_4.setFont(new Font("宋体", Font.PLAIN, 26));
		btnNewButton_4.setBounds(288, 403, 177, 51);
		contentPane.add(btnNewButton_4);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("back1.jfif"));
		lblNewLabel.setBounds(0, 0, 760, 493);
		contentPane.add(lblNewLabel);
	}
}
