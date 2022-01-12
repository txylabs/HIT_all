package third;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import third.Menu;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;


public class cal3Challenge extends JFrame {
	
	private TimerThread timerThread = new TimerThread();//执行的线程程序部分
	private JLabel lblNewLabel;//时间显示
	private long s;//计时的秒
	private long m;//计时的分
	private Thread th;//线程对象
	private boolean flag=false;//指示错误键是否可用
	private int left_num=20;//剩余题目数量
	private JLabel lblNewLabel_1;
	private JButton Zero;
	private JButton One;
	private JButton Two;
	private JButton Three;
	private JButton Four;
	private JButton Five;
	private JButton Six;
	private JButton Seven;
	private JButton Eight;
	private JButton Nine;
	private JButton NextOne;
	private JButton NextOne_1;
	private JButton Submit;
	private static cal3Challenge frame;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
				    frame = new cal3Challenge();
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
	public cal3Challenge() {
		setBackground(new Color(211, 211, 211));
        getAccessibleContext();
		setForeground(Color.PINK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 884, 551);
		getContentPane().setLayout(null);
		getContentPane().setLayout(null);
		
		JLabel Title = new JLabel("\u6311\u6218\u6A21\u5F0F");
		Title.setBounds(362, 10, 175, 58);
		Title.setFont(new Font("楷体", Font.BOLD, 34));
		getContentPane().add(Title);
		
		JLabel AllNum = new JLabel("\u5269\u4F59\u9898\u6570\uFF1A");
		AllNum.setName("AllNum");
		AllNum.setBounds(36, 103, 193, 50);
		AllNum.setFont(new Font("微软雅黑", Font.PLAIN, 25));
		getContentPane().add(AllNum);
		
		JLabel CorrectNum = new JLabel("答对题数：");
		CorrectNum.setName("CorrectNum");
		CorrectNum.setBounds(225, 103, 193, 50);
		CorrectNum.setFont(new Font("微软雅黑", Font.PLAIN, 25));
		getContentPane().add(CorrectNum);
		
		JLabel ErrorNum = new JLabel("错误提交数：");
		ErrorNum.setName("ErrorNum");
		ErrorNum.setBounds(454, 103, 158, 50);
		ErrorNum.setFont(new Font("微软雅黑", Font.PLAIN, 25));
		getContentPane().add(ErrorNum);
		
		lblNewLabel = new JLabel("\u5012\u8BA1\u65F6:");
		lblNewLabel.setName("Time");
		lblNewLabel.setBounds(667, 103, 158, 50);
		lblNewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 25));
		getContentPane().add(lblNewLabel);
		
		JLabel Par1 = new JLabel("0");
		Par1.setName("Par1");
		Par1.setHorizontalAlignment(SwingConstants.RIGHT);
		Par1.setFont(new Font("宋体", Font.PLAIN, 26));
		Par1.setBounds(78, 186, 130, 64);
		getContentPane().add(Par1);
		
		JLabel Operator = new JLabel("+/-");
		Operator.setName("Operator");
		Operator.setFont(new Font("宋体", Font.PLAIN, 26));
		Operator.setBounds(63, 280, 45, 44);
		getContentPane().add(Operator);
		
		JLabel Par2 = new JLabel("0");
		Par2.setName("Par2");
		Par2.setHorizontalAlignment(SwingConstants.RIGHT);
		Par2.setFont(new Font("宋体", Font.PLAIN, 26));
		Par2.setBounds(118, 290, 90, 25);
		getContentPane().add(Par2);
		
		lblNewLabel_1 = new JLabel("5\u5206\u949F\u5B8C\u621020\u9898\uFF0C\u4F60\u80FD\u505A\u5230\u5417\uFF1F");
		lblNewLabel_1.setBounds(174, 59, 514, 44);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBackground(Color.LIGHT_GRAY);
		lblNewLabel_1.setFont(new Font("华文行楷", Font.BOLD, 30));
		getContentPane().add(lblNewLabel_1);
		
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
		Ans.setBounds(154, 359, 54, 25);
		getContentPane().add(Ans);
		
		//开始按钮
		JButton Start = new JButton("开始");
		Start.setLocation(401, 208);
		Start.setSize(105, 25);
		Start.setName("Start");
		Start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				s=0;
				m=5;
				th = new Thread(timerThread);
				th.start();
				//将开始按钮设为不可用
	        	Start.setEnabled(false);
				AllNum.setText("剩余题数："+"19");
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
		getContentPane().add(Start);
		//输入答案的键盘
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
		
		//选择做下一题
        NextOne = new JButton("下一题");
        NextOne.setName("NextOne");
		NextOne.setBounds(601, 268, 121, 25);
		NextOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int left=left_num-1;
				AllNum.setText("剩余题数："+left);
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
		getContentPane().add(NextOne);
		//输入错误，进行回退
        NextOne_1 = new JButton("退格");
        NextOne_1.setName("Back");
		NextOne_1.setBounds(401, 268, 105, 25);
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
		getContentPane().add(NextOne_1);
		
		//提交按钮
				Submit = new JButton("提交");
				Submit.setName("Submit");
				Submit.setBounds(601, 208, 121, 25);
				Submit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					
						int ans = Integer.parseInt(Ans.getText());
						int par1 = Integer.parseInt(Par1.getText());
						int par2 = Integer.parseInt(Par2.getText());
						String operator = Operator.getText();
						//int all_num = Integer.parseInt(AllNum.getText().split("：")[1]);
						int cor_num = Integer.parseInt(CorrectNum.getText().split("：")[1]);
						int err_num = Integer.parseInt(ErrorNum.getText().split("：")[1]);
						left_num--;
						if(left_num==10&&s!=0&&m!=0)lblNewLabel_1.setText("加油!你已经完成1/2了");		
						if(left_num==5&&s!=0&&m!=0)lblNewLabel_1.setText("还剩最后5道题，坚持就是胜利!");
						if(left_num==0&&s!=0&&m!=0)
						{
							lblNewLabel_1.setText("恭喜你完成挑战!");
							flag=true;
							Submit.setEnabled(!flag);
							NextOne.setEnabled(!flag);
							NextOne_1.setEnabled(!flag);
							Nine.setEnabled(!flag);
							Eight.setEnabled(!flag);
							Seven.setEnabled(!flag);
							Six.setEnabled(!flag);
							Five.setEnabled(!flag);
							Four.setEnabled(!flag);
							Three.setEnabled(!flag);
							Two.setEnabled(!flag);
							One.setEnabled(!flag);
							Zero.setEnabled(!flag);
							// 终止线程
							th.interrupt();
							timerThread = new TimerThread();
						}
						
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
								int res=par1+par2;
								String s=par1+"+"+par2+"="+res+"\n";
								try {
									WriteStringToFile(s);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
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
								int res=par1-par2;
								String s=par1+"-"+par2+"="+res+"\n";
								try {
									WriteStringToFile(s);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					}
				});
				Submit.setFont(new Font("宋体", Font.PLAIN, 20));
				getContentPane().add(Submit);
				
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setIcon(new ImageIcon("back1.jfif"));
		lblNewLabel_1.setBounds(0, 0, 870, 514);
		getContentPane().add(lblNewLabel_1);
		
	}
	//计时功能模块
	class TimerThread implements Runnable{
		@Override
		public void run() {
			while (true) {
				//线程休眠一秒，秒针+1
				// 判断这一分钟是否到末尾，若到末尾则分针减1，秒针回到59
				if(s<0 && m>0){
					s=59;
					m--;
				}
				// 规定时间显示的格式
				DecimalFormat f1 = new DecimalFormat("00");
				lblNewLabel.setText(f1.format(m)+":"+f1.format(s));
				
				// 判断时间是否走完，若走完则删除暂停按钮
				if(s == 0 && m == 0){
				    lblNewLabel_1.setText("挑战失败!");
				    flag=true;
				    NextOne.setEnabled(!flag);
					NextOne_1.setEnabled(!flag);
					Nine.setEnabled(!flag);
					Submit.setEnabled(!flag);
					Eight.setEnabled(!flag);
					Seven.setEnabled(!flag);
					Six.setEnabled(!flag);
					Five.setEnabled(!flag);
					Four.setEnabled(!flag);
					Three.setEnabled(!flag);
					Two.setEnabled(!flag);
					One.setEnabled(!flag);
					Zero.setEnabled(!flag);
					return;
				}
				
				try {
					Thread.sleep(1000);
					s--;
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
	//将错题读入错题集
	public void WriteStringToFile(String s) throws IOException {
		
			//打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
	            FileWriter writer = new FileWriter("wrong_problem.txt", true);  
	            writer.write(s);  
	            writer.close();  
	        

    }
}



