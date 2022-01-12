package Cal1Test;
import static org.junit.Assert.*;

import org.junit.Test;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import javax.swing.JLabel;
import first.Cal1;
import first.CalProcess;

public class test1 {

	private FrameFixture frame;
	
	@Before
	public void setUp() {
		frame = new FrameFixture(new Cal1());
		frame.show();
	}
	
	@After
	public void tearDown() {
		frame.cleanUp();
	}
	
	//单元测试
	@Test
	public void testStart() {
		frame.button("Start").click();
		frame.label("Result").requireText("答对与否");
		frame.label("AllNum").requireText("总题数：0");
		frame.label("CorrectNum").requireText("答对题数：0");
		frame.label("ErrorNum").requireText("答错题数：0");
	}
	
	@Test
	public void testShowAns() {
		frame.button("Start").click();
		frame.button("NextOne").click();
		int a = Integer.parseInt(frame.label("Par1").text());
		int b = Integer.parseInt(frame.label("Par2").text());
		String c = frame.label("Operator").text();
		//System.out.println(frame.label("Par1").text());
		frame.button("ShowAns").click();
		//System.out.println(frame.label("Ans").text());
		if(c.equals("+")) {
			frame.label("Ans").requireText((a+b)+"");
		}
		else if(c.equals("-")) {
			frame.label("Ans").requireText((a-b)+"");
		}
	}
	
	@Test
	public void testSubmit() {
		frame.button("Start").click();
		frame.button("ShowAns").click();
		frame.button("Submit").click();
		frame.label("Result").requireText("恭喜你答对了！");	}
	
	@Test
	public void testCalAdd() {
		CalProcess cal = new CalProcess();
		JLabel Par1 = new JLabel();
		JLabel Par2 = new JLabel();
		JLabel Opera = new JLabel();
		cal.GenerateOneAdd(Par1, Par2, Opera);
		int par1 = Integer.parseInt(Par1.getText());
		int par2 = Integer.parseInt(Par2.getText());
		assertTrue(par1+par2<100);
	}
	
	@Test
	public void testCalSub() {
		CalProcess cal = new CalProcess();
		JLabel Par1 = new JLabel();
		JLabel Par2 = new JLabel();
		JLabel Opera = new JLabel();
		cal.GenerateOneSub(Par1, Par2, Opera);
		int par1 = Integer.parseInt(Par1.getText());
		int par2 = Integer.parseInt(Par2.getText());
		assertTrue(par1-par2<100);
		assertTrue(par1-par2>=0);
	}
	
	
	//集成测试
	@Test
	public void testIntergration() {
		frame.button("Start").click();
		int a = Integer.parseInt(frame.label("Par1").text());
		int b = Integer.parseInt(frame.label("Par2").text());
		String c = frame.label("Operator").text();
		frame.button("ShowAns").click();
		if(c.equals("+")) {
			frame.label("Ans").requireText((a+b)+"");
		}
		else if(c.equals("-")) {
			frame.label("Ans").requireText((a-b)+"");
		}
		frame.button("Submit").click();
		frame.button("NextOne").click();
		frame.button("Zero").click();
		frame.label("Ans").requireText("0");
		frame.button("Back").click();
		frame.label("Ans").requireText("0");
		frame.label("Result").requireText("答对与否");
		frame.label("AllNum").requireText("总题数：1");
		frame.label("CorrectNum").requireText("答对题数：1");
		frame.label("ErrorNum").requireText("答错题数：0");
	}
	
	
}
