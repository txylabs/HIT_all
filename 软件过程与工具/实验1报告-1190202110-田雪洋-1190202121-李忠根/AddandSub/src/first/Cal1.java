package first;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import second.CalProcess;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.CardLayout;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class Cal1 extends JFrame {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cal1 frame = new Cal1();
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
	public Cal1() {
		setForeground(Color.PINK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 884, 551);
		getContentPane().setLayout(null);
		getContentPane().setLayout(null);
		
		JLabel Title = new JLabel("加减法练习");
		Title.setFont(new Font("楷体", Font.BOLD, 34));
		Title.setBounds(363, 10, 175, 58);
		getContentPane().add(Title);
		
		
		JLabel AllNum = new JLabel("总题数：");
		AllNum.setName("AllNum");
		AllNum.setFont(new Font("微软雅黑", Font.PLAIN, 25));
		AllNum.setBounds(43, 103, 193, 50);
		getContentPane().add(AllNum);
		
		JLabel CorrectNum = new JLabel("答对题数：");
		CorrectNum.setName("CorrectNum");
		CorrectNum.setFont(new Font("微软雅黑", Font.PLAIN, 25));
		CorrectNum.setBounds(288, 103, 193, 50);
		getContentPane().add(CorrectNum);
		
		JLabel ErrorNum = new JLabel("答错题数：");
		ErrorNum.setName("ErrorNum");
		ErrorNum.setFont(new Font("微软雅黑", Font.PLAIN, 25));
		ErrorNum.setBounds(562, 103, 158, 50);
		getContentPane().add(ErrorNum);
		
		JLabel Par1 = new JLabel("0");
		Par1.setName("Par1");
		Par1.setHorizontalAlignment(SwingConstants.RIGHT);
		Par1.setFont(new Font("宋体", Font.PLAIN, 26));
		Par1.setBounds(154, 186, 90, 64);
		getContentPane().add(Par1);
		
		JLabel Operator = new JLabel("+/-");
		Operator.setName("Operator");
		Operator.setFont(new Font("宋体", Font.PLAIN, 26));
		Operator.setBounds(92, 280, 45, 44);
		getContentPane().add(Operator);
		
		JLabel Par2 = new JLabel("0");
		Par2.setName("Par2");
		Par2.setHorizontalAlignment(SwingConstants.RIGHT);
		Par2.setFont(new Font("宋体", Font.PLAIN, 26));
		Par2.setBounds(154, 290, 90, 25);
		getContentPane().add(Par2);
		
		JLabel Line = new JLabel("--------------------------------------------------------");
		Line.setBounds(36, 334, 286, 15);
		getContentPane().add(Line);
		
		JLabel Result = new JLabel("答对与否");
		Result.setName("Result");
		Result.setFont(new Font("宋体", Font.PLAIN, 20));
		Result.setBounds(601, 422, 198, 44);
		getContentPane().add(Result);
		
		JLabel Ans = new JLabel("0");
		Ans.setName("Ans");
		Ans.setHorizontalAlignment(SwingConstants.RIGHT);
		Ans.setFont(new Font("宋体", Font.PLAIN, 26));
		Ans.setBounds(190, 359, 54, 25);
		getContentPane().add(Ans);
		
		JButton Start = new JButton("开始");
		Start.setName("Start");
		Start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AllNum.setText("总题数："+"0");
				CorrectNum.setText("答对题数："+"0");
				ErrorNum.setText("答错题数："+"0");
				Par1.setText("0");
				Par2.setText("0");
				Ans.setText("0");
				Result.setText("答对与否");
				if(Math.random()>0.5) {
					Operator.setText("+");
					CalProcess a = new CalProcess();
					a.GenerateOneAdd(Par1, Par2, Operator);
				}
				else {
					Operator.setText("-");
					CalProcess a = new CalProcess();
					a.GenerateOneSub(Par1, Par2, Operator);
				}
			}
		});
		Start.setFont(new Font("宋体", Font.PLAIN, 20));
		Start.setBounds(401, 268, 105, 25);
		getContentPane().add(Start);
		
		JButton Zero = new JButton("0");
		Zero.setName("Zero");
		Zero.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = Integer.parseInt(Ans.getText());
				ans = ans*10 + 0;
				Ans.setText(ans+"");
			}
		});
		Zero.setFont(new Font("宋体", Font.PLAIN, 20));
		Zero.setBounds(43, 432, 45, 25);
		getContentPane().add(Zero);
		
		JButton One = new JButton("1");
		One.setName("One");
		One.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = Integer.parseInt(Ans.getText());
				ans = ans*10 + 1;
				Ans.setText(ans+"");
			}
		});
		One.setFont(new Font("宋体", Font.PLAIN, 20));
		One.setBounds(92, 432, 45, 25);
		getContentPane().add(One);
		
		JButton Two = new JButton("2");
		Two.setName("Two");
		Two.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = Integer.parseInt(Ans.getText());
				ans = ans*10 + 2;
				Ans.setText(ans+"");
			}
		});
		Two.setFont(new Font("宋体", Font.PLAIN, 20));
		Two.setBounds(141, 432, 45, 25);
		getContentPane().add(Two);
		
		JButton Three = new JButton("3");
		Three.setName("Three");
		Three.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = Integer.parseInt(Ans.getText());
				ans = ans*10 + 3;
				Ans.setText(ans+"");
			}
		});
		Three.setFont(new Font("宋体", Font.PLAIN, 20));
		Three.setBounds(191, 432, 45, 25);
		getContentPane().add(Three);
		
		JButton Four = new JButton("4");
		Four.setName("Four");
		Four.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = Integer.parseInt(Ans.getText());
				ans = ans*10 + 4;
				Ans.setText(ans+"");
			}
		});
		Four.setFont(new Font("宋体", Font.PLAIN, 20));
		Four.setBounds(239, 432, 45, 25);
		getContentPane().add(Four);
		
		JButton Five = new JButton("5");
		Five.setName("Five");
		Five.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = Integer.parseInt(Ans.getText());
				ans = ans*10 + 5;
				Ans.setText(ans+"");
			}
		});
		Five.setFont(new Font("宋体", Font.PLAIN, 20));
		Five.setBounds(288, 432, 45, 25);
		getContentPane().add(Five);
		
		JButton Six = new JButton("6");
		Six.setName("Six");
		Six.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = Integer.parseInt(Ans.getText());
				ans = ans*10 + 6;
				Ans.setText(ans+"");
			}
		});
		Six.setFont(new Font("宋体", Font.PLAIN, 20));
		Six.setBounds(340, 432, 45, 25);
		getContentPane().add(Six);
		
		JButton Seven = new JButton("7");
		Seven.setName("Seven");
		Seven.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = Integer.parseInt(Ans.getText());
				ans = ans*10 + 7;
				Ans.setText(ans+"");
			}
		});
		Seven.setFont(new Font("宋体", Font.PLAIN, 20));
		Seven.setBounds(388, 432, 45, 25);
		getContentPane().add(Seven);
		
		JButton Eight = new JButton("8");
		Eight.setName("Eight");
		Eight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = Integer.parseInt(Ans.getText());
				ans = ans*10 + 8;
				Ans.setText(ans+"");
			}
		});
		Eight.setFont(new Font("宋体", Font.PLAIN, 20));
		Eight.setBounds(436, 432, 45, 25);
		getContentPane().add(Eight);
		
		JButton Nine = new JButton("9");
		Nine.setName("Nine");
		Nine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = Integer.parseInt(Ans.getText());
				ans = ans*10 + 9;
				Ans.setText(ans+"");
			}
		});
		Nine.setFont(new Font("宋体", Font.PLAIN, 20));
		Nine.setBounds(482, 432, 45, 25);
		getContentPane().add(Nine);
		
		JButton Submit = new JButton("提交");
		Submit.setName("Submit");
		Submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = Integer.parseInt(Ans.getText());
				int par1 = Integer.parseInt(Par1.getText());
				int par2 = Integer.parseInt(Par2.getText());
				String operator = Operator.getText();
				int all_num = Integer.parseInt(AllNum.getText().split("：")[1]);
				int cor_num = Integer.parseInt(CorrectNum.getText().split("：")[1]);
				int err_num = Integer.parseInt(ErrorNum.getText().split("：")[1]);
				
				all_num++;
				AllNum.setText("总题数："+all_num);
				if(operator.equals("+")) {
					if(ans == (par1+par2)) {
						Result.setText("恭喜你答对了！");
						cor_num++;
						CorrectNum.setText("答对题数："+cor_num);
					}
					else {
						Result.setText("很遗憾你答错了！");
						err_num++;
						ErrorNum.setText("答错题数："+err_num);
					}
				}
				else {
					if(ans == (par1-par2)) {
						Result.setText("恭喜你答对了！");
						cor_num++;
						CorrectNum.setText("答对题数："+cor_num);
					}
					else {
						Result.setText("很遗憾你答错了！");
						err_num++;
						ErrorNum.setText("答错题数："+err_num);
					}
				}
			}
		});
		Submit.setFont(new Font("宋体", Font.PLAIN, 20));
		Submit.setBounds(601, 208, 105, 25);
		getContentPane().add(Submit);
		
		JButton ShowAns = new JButton("答案");
		ShowAns.setName("ShowAns");
		ShowAns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int par1 = Integer.parseInt(Par1.getText());
				int par2 = Integer.parseInt(Par2.getText());
				String operator = Operator.getText();
				if(operator.equals("+")) {
					Ans.setText((par1+par2)+"");
				}
				else {
					Ans.setText((par1-par2)+"");
				}
			}
		});
		ShowAns.setFont(new Font("宋体", Font.PLAIN, 20));
		ShowAns.setBounds(601, 265, 105, 25);
		getContentPane().add(ShowAns);
		
		JButton NextOne = new JButton("下一题");
		NextOne.setName("NextOne");
		NextOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Par1.setText("0");
				Par2.setText("0");
				Ans.setText("0");
				Result.setText("答对与否");
				if(Math.random()>0.5) {
					Operator.setText("+");
					CalProcess a = new CalProcess();
					a.GenerateOneAdd(Par1, Par2, Operator);
				}
				else {
					Operator.setText("-");
					CalProcess a = new CalProcess();
					a.GenerateOneSub(Par1, Par2, Operator);
				}
			}
		});
		NextOne.setFont(new Font("宋体", Font.PLAIN, 20));
		NextOne.setBounds(601, 324, 105, 25);
		getContentPane().add(NextOne);
		
		JButton NextOne_1 = new JButton("退格");
		NextOne_1.setName("Back");
		NextOne_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ans = Ans.getText();
				if(ans.length()==1) {
					Ans.setText("0");
				}
				else {
					Ans.setText(ans.substring(1));
				}
			}
		});
		NextOne_1.setFont(new Font("宋体", Font.PLAIN, 20));
		NextOne_1.setBounds(401, 330, 105, 25);
		getContentPane().add(NextOne_1);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setIcon(new ImageIcon("back1.jfif"));
		lblNewLabel_1.setBounds(0, 0, 870, 514);
		getContentPane().add(lblNewLabel_1);
		
		
	}
}