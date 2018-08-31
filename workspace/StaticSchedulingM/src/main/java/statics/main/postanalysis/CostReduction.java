//package statics.main.postanalysis;
//
//import java.io.File;
//import java.util.Scanner;
//
//public class CostReduction {
//
//	public static void main(String[] args) throws Throwable {
//
//		Scanner sc = new Scanner(new File("bin\\main\\postanalysis\\1412002531831_subcost_txt"));
//		
//		sc.useDelimiter("\\Z");
//		String content = sc.next();
//		sc.close();
////		System.out.println(content);
//	
//		String[] packages = content.split("-----------------------------\n");
//		
//		
//		System.out.printf("%60s\t%10s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s%n", "Workflow", "deadline", "scaling","chunky","fine","dept","post","final");
//		for(String pack:packages) {
//			String[] lines = pack.split("\n");
//			
//			String workflow = lines[1];
//			String deadline = lines[3];
//			String scaling = lines[5];
//			String chunky = lines[7];
//			String fine = lines[9];
//			String dept = lines[11];
//			String post = lines[13];
//			String finalC = lines[15];
//		
//			
//			int i = Math.min(workflow.length(),50);
//			workflow = workflow.substring(0,i);
//			i = Math.min(deadline.length(),10);
//			deadline = deadline.substring(0,i);
//			
//			System.out.printf("%60s\t%10s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s%n", workflow, deadline, scaling,chunky,fine,dept,post,finalC);
//					
//			
//			
//			
//		}
//		
//		
//	}
//
//}
