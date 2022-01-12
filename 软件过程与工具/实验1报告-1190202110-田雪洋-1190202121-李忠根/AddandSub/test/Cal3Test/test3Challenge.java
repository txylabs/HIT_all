package Cal3Test;

import third.cal3Challenge;
import third.CalProcess;

import static org.junit.Assert.*;

import org.junit.Test;

import first.Cal1;

import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import javax.swing.JLabel;


public class test3Challenge {

	private FrameFixture frame;
	
	@Before
	public void setUp() {
		frame = new FrameFixture(new cal3Challenge());
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
		frame.label("AllNum").requireText("剩余题数：19");
		frame.label("CorrectNum").requireText("答对题数：0");
		frame.label("ErrorNum").requireText("答错题数：0");
		frame.label("Time").requireText("05:00");
	}

	

	
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
		if(c.equals("+")) {
			frame.label("Ans").requireText("0");
		}
		else if(c.equals("-")) {
			frame.label("Ans").requireText("0");
		}
		frame.button("Submit").click();
		frame.button("NextOne").click();
		frame.button("Zero").click();
		frame.label("Ans").requireText("0");
		frame.button("Back").click();
		frame.label("Ans").requireText("0");
		frame.label("Result").requireText("答对与否");
		frame.label("AllNum").requireText("剩余题数：18");
		frame.label("CorrectNum").requireText("答对题数：0");
		frame.label("ErrorNum").requireText("答错题数：1");
		frame.label("Time").requireText("04:59");
	}
	
	
}
