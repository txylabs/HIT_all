package third;

import javax.swing.JLabel;

public class CalProcessEasy{
	
	private int par1;
	private int par2;
	private char operator;
	
	/**
	 * 生成加减法
	 */
	public void GenerateOneAdd(JLabel Par1, JLabel Par2, JLabel Operator) {
		int max=100, min=1;
		this.operator = '+';
		this.par1 = (int) (Math.random()*(max-min)+min);
		this.par2 = (int) (Math.random()*(max-min)+min);
		while(this.par1+this.par2>=100 || (this.par1%10)+(this.par2%10)>=10) {
			this.par1 = (int) (Math.random()*(max-min)+min);
			this.par2 = (int) (Math.random()*(max-min)+min);
		}
		Par1.setText(this.par1+"");
		Par2.setText(this.par2+"");
		Operator.setText(this.operator+"");
	}
	
	public void GenerateOneSub(JLabel Par1, JLabel Par2, JLabel Operator) {
		int max=100, min=1;
		this.operator = '-';
		this.par1 = (int) (Math.random()*(max-min)+min);
		this.par2 = (int) (Math.random()*(max-min)+min);
		while(this.par1-this.par2<0 || (this.par1%10)-(this.par2%10)<0) {
			this.par1 = (int) (Math.random()*(max-min)+min);
			this.par2 = (int) (Math.random()*(max-min)+min);
		}
		Par1.setText(this.par1+"");
		Par2.setText(this.par2+"");
		Operator.setText(this.operator+"");
	}
}
