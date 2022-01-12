package white_test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class look_test {

	@Test
	public void look_test()
	{   
		List<Student>mm=new ArrayList<Student>();
		
		//循环两次，一个优秀
		List<Student>mm1=new ArrayList<Student>();
		Student s1=new Student("Tom", 93, mm);
		Student s2=new Student("To", 44, mm);
		mm1.add(s1);
		mm1.add(s2);
		Student sequence_struct=new Student("tianxueyang",50,mm1);
		assertEquals(1, sequence_struct.membersScore());
		
		//循环0次
		Student sequence_struct1=new Student("tianxueyang",50,mm);
		assertEquals(0, sequence_struct1.membersScore());
		
		//循环1次，0个优秀
		List<Student>mm2=new ArrayList<Student>();
		mm2.add(s2);
		Student sequence_struct2=new Student("tianxueyang",50,mm2);
		assertEquals(0, sequence_struct2.membersScore());
		//循环两次，一个优秀
		List<Student>mm3=new ArrayList<Student>();
		Student s3=new Student("Tom", 77, mm);
		Student s4=new Student("To", 44, mm);
		mm3.add(s3);
		mm3.add(s4);
		Student sequence_struct3=new Student("tianxueyang",50,mm3);
		assertEquals(0, sequence_struct3.membersScore());
	
		//循环两次，2个优秀
		List<Student>mm4=new ArrayList<Student>();
		Student s5=new Student("Tom", 93, mm);
		Student s6=new Student("To", 85, mm);
		mm4.add(s6);
		mm4.add(s5);
		Student sequence_struct4=new Student("tianxueyang",50,mm4);
		assertEquals(2, sequence_struct4.membersScore());
		
	}

}
