package white_test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class sequence_struct_test {

	@Test
	public void sequence_test()
	{   
		List<Student>mm=new ArrayList<Student>();
		//name为""
		Student sequence_struct=new Student("",50,mm);
		assertEquals("Student is ", sequence_struct.toString());
		//name为"tianxueyang"
		Student sequence_struct1=new Student("tianxueyang",50,mm);
		assertEquals("Student is tianxueyang", sequence_struct1.toString());
		//name为" "
		Student sequence_struct2=new Student(" ",50,mm);
		assertEquals("Student is  ", sequence_struct2.toString());
		//name为null
		Student sequence_struct3=new Student(null,50,mm);
		assertEquals("Student is null", sequence_struct3.toString());
		
	}

}
