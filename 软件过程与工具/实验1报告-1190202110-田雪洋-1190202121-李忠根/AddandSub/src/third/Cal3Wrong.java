package third;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;



public class Cal3Wrong extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TimerThread timerThread = new TimerThread();//执行的线程程序部分
	JLabel lblNewLabel;//时间显示
	long s;//计时的秒
	long m;//计时的分
	long h;//计时的小时
	Thread th;//线程对象
	boolean flag=false;//判断第几次点击开始按钮
	Cal_process process;
	ArrayList<String>wrongArrayList;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cal3Wrong frame = new Cal3Wrong();
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
	public Cal3Wrong() {
		setForeground(Color.PINK);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 884, 551);
		getContentPane().setLayout(null);
		getContentPane().setLayout(null);
		
		JLabel Title = new JLabel("错题重做");
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
		CorrectNum.setBounds(225, 103, 193, 50);
		getContentPane().add(CorrectNum);
		
		JLabel ErrorNum = new JLabel("答错题数：");
		ErrorNum.setName("ErrorNum");
		ErrorNum.setFont(new Font("微软雅黑", Font.PLAIN, 25));
		ErrorNum.setBounds(454, 103, 158, 50);
		getContentPane().add(ErrorNum);
		
		lblNewLabel = new JLabel("用时:");
		lblNewLabel.setName("Time");
		lblNewLabel.setFont(new Font("微软雅黑", Font.PLAIN, 25));
		lblNewLabel.setBounds(667, 103, 158, 50);
		getContentPane().add(lblNewLabel);
		
		JLabel Par1 = new JLabel("0");
		Par1.setName("Par1");
		Par1.setHorizontalAlignment(SwingConstants.RIGHT);
		Par1.setFont(new Font("宋体", Font.PLAIN, 26));
		Par1.setBounds(154, 186, 90, 64);
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
				process=new Cal_process();
				s=0;
				m=0;
				h=0;
				if(!flag)
				{
					th = new Thread(timerThread);
					th.start();
					flag=true;
	        	}
				else
				{
					// 终止线程
					th.interrupt();
					timerThread = new TimerThread();
					th = new Thread(timerThread);
					th.start();
				}
				wrongArrayList=process.getS();
				if(wrongArrayList.size()==1)
					{
						AllNum.setText("暂无错题");
					}
				else
				{
				AllNum.setText("总题数："+(wrongArrayList.size()-1));
				CorrectNum.setText("答对题数："+"0");
				ErrorNum.setText("答错题数："+"0");;
				Ans.setText("0");
				Result.setText("答对与否");
				int i= (int) (Math.random()*(wrongArrayList.size()-1));
				String string=wrongArrayList.get(i);
				process.Match_partener(string);
				Par1.setText(process.getPar1()+"");
				Par2.setText(process.getPar2()+"");
				Operator.setText(process.getOperator());
				}
				
				
			}
		});
		Start.setFont(new Font("宋体", Font.PLAIN, 20));
		Start.setBounds(401, 268, 105, 25);
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
		
		//提交按钮
		JButton Submit = new JButton("提交");
		Submit.setName("Submit");
		Submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ans = Integer.parseInt(Ans.getText());
				int par1 = Integer.parseInt(Par1.getText());
				int par2 = Integer.parseInt(Par2.getText());
				String operator = Operator.getText();
				int all_num=wrongArrayList.size()-1;
				int cor_num = Integer.parseInt(CorrectNum.getText().split("：")[1]);
				int err_num = Integer.parseInt(ErrorNum.getText().split("：")[1]);
				AllNum.setText("总题数："+all_num);
				if(operator.equals("+")) {
					if(ans == (par1+par2)) {
						Result.setText("恭喜你答对了！");
						process.deleteRight(par1,par2,operator);
						wrongArrayList=process.getS();
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
						process.deleteRight(par1,par2,operator);
						wrongArrayList=process.getS();
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
		//显示试题答案
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
		//选择做下一题
		JButton NextOne = new JButton("下一题");
		NextOne.setName("NextOne");
		NextOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(wrongArrayList.size()==1)
					{
						AllNum.setText("暂无错题");
					}
				else
				{
					Par1.setText("0");
				Par2.setText("0");
				Ans.setText("0");
				Result.setText("答对与否");
				int i= (int) (Math.random()*(wrongArrayList.size()-1));
				String string=wrongArrayList.get(i);
				process.Match_partener(string);
				Par1.setText(process.getPar1()+"");
				Par2.setText(process.getPar2()+"");
				Operator.setText(process.getOperator());
				}
				
			}
		});
		NextOne.setFont(new Font("宋体", Font.PLAIN, 20));
		NextOne.setBounds(601, 324, 105, 25);
		getContentPane().add(NextOne);
		//输入错误，进行回退
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
	//计时功能模块
	class TimerThread implements Runnable{
		@Override
		public void run() {
			while (true) {
				//线程休眠一秒，秒针+1
				if(s==60)
				{
					m++;
					s=0;
				}
				if(m==60)
				{
					h++;
					m=0;
				}
	            // 显示日期时间到JLabel标签中
				// 规定时间显示的格式
				DecimalFormat f1 = new DecimalFormat("00");
				lblNewLabel.setText(f1.format(h)+":"+f1.format(m)+":"+f1.format(s));
				try {
					Thread.sleep(1000);
					s++;
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

class Cal_process{
	
	private int par1;
	private int par2;
	private String operator;
	private ArrayList<String> s;
	
	
	public ArrayList<String> getS() {
		isLegalMagicSquare();
		return s;
	}
	public int getPar1() {
		return par1;
	}
	public int getPar2() {
		return par2;
	}
	public String getOperator()
	{
		return operator;
	}
	private void isLegalMagicSquare()
	{
        s=new ArrayList<String>();
	    try {
			File file=new File("wrong_problem.txt");
			InputStreamReader input=new InputStreamReader(new FileInputStream(file));
			BufferedReader bf=new BufferedReader(input);//按行读入
			String line;
			while((line=bf.readLine())!=null)
			{
				s.add(line);
			}
			bf.close();
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}//从文件中按行读入
	}
	public void Match_partener(String s)
	{
		String reg = "(\\d{1,2})([+-])(\\d{1,2})([=])(\\d{1,2})";
		Pattern pattern=Pattern.compile(reg);
        Matcher matcher=pattern.matcher(s);
        while(matcher.find())
        {
        	par1=Integer.valueOf(matcher.group(1)).intValue();
        	operator=matcher.group(2);
        	par2=Integer.valueOf(matcher.group(3)).intValue();
        }

	}
	//删除答对的题目
	public void deleteRight(int part1,int part2,String operators) {
		int res;
		if(operators.equals("+"))res=part1+part2;
		else res=part1-part2;
		String tempString=part1+operators+part2+"="+res;
		s.remove(tempString);
		//打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件  
        FileWriter writer;
		try {
			//先清空文件
			writer = new FileWriter("wrong_problem.txt", false);
			writer.write("");
			writer.flush();
			writer.close();
			//再写入文件
			writer = new FileWriter("wrong_problem.txt", true);
			for(int i=0;i<s.size();++i)
			{	  
				writer.write(s.get(i)+"\n");
			}
	        writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
}


