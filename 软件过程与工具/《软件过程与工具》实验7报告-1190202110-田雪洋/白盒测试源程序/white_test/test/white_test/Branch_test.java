package white_test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class Branch_test {

	@Test
	public void branch_test()
	{   
		List<Student>mm=new ArrayList<Student>();
		
		//score为90
		Student sequence_struct=new Student("tianxueyang",90,mm);
		assertEquals("优秀", sequence_struct.scor());
		//score为70
		Student sequence_struct1=new Student("tianxueyang",70,mm);
		assertEquals("良好", sequence_struct1.scor());
		//score为40
		Student sequence_struct2=new Student("tianxueyang",40,mm);
		assertEquals("不及格", sequence_struct2.scor());
		//score为101
		Student sequence_struct3=new Student("tianxueyang",101,mm);
		assertEquals("输入错误", sequence_struct3.scor());
		//score为-1
		Student sequence_struct4=new Student("tianxueyang",-1,mm);
		assertEquals("输入错误", sequence_struct4.scor());
		
	}

}
