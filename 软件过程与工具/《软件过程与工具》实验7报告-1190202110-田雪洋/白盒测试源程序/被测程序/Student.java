package white_test;

import java.util.ArrayList;
import java.util.List;

public class Student {
	
	   private String name;
	   private int score;
	   private List<Student>members=new ArrayList<Student>();
	   public Student(String name,int score,List<Student>members) {
		// TODO Auto-generated constructor stub
		 this.name=name;
		 this.score=score;
		 this.members=members;
	}
	public String scor() {
		   String result;
		 if(score>80&&score<=100)
		 {
			 result="优秀";
			 return result;
		 }
		 else if (score<=80&&score>60) {
			result="良好";
			return result;
		}
		 else if(score<=60&&score>=0) {
			result="不及格";
			return result;
		}
		 result="输入错误";
		 return result;
	}
	/**
	 * 统计同一小组的同学的优秀人数
	 * */
	public int membersScore() {
		  int result=0;
		  for(int i=0;i<members.size();++i)
		  {
			  if(members.get(i).scor().equals("优秀"))result++;
		  }
		  return result;
	}
	   @Override
	public String toString() {
		// TODO Auto-generated method stub
		   String result;
		   result="Student is "+name;
		return result;
	}
}
