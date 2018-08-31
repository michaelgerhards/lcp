//package statics.main.postanalysis;
//
//import java.io.File;
//import java.util.Scanner;
//
//public class CostResourceReduction {
//
//	public static void main(String[] args) throws Throwable {
//
//		Scanner sc = new Scanner(new File(
//				"bin\\main\\postanalysis\\1412078641738_subcost_txt"));
//
//		sc.useDelimiter("\\Z");
//		String content = sc.next();
//		sc.close();
//		// System.out.println(content);
//
//		String[] packages = content.split("-----------------------------\n");
//
//		System.out
//				.printf("%60s\t%10s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%4s\t%4s\t%4s\t%4s\t%4s\t%4s%n",
//						"Workflow", "deadline", "scalinC", "chunkyC", "fineC",
//						"deptC", "postC", "finalC", "scalinR", "chunkyR",
//						"fineR", "deptR", "postR", "finalR");
//		for (String pack : packages) {
//			String[] lines = pack.split("\n");
//
//			String workflow = lines[1];
//			String deadline = lines[3];
//
//			String scalingC = lines[5];
//			String chunkyC = lines[8];
//			String fineC = lines[11];
//			String deptC = lines[14];
//			String postC = lines[17];
//			String finalC = lines[20];
//
//			int i = Math.min(workflow.length(), 50);
//			workflow = workflow.substring(0, i);
//			i = Math.min(deadline.length(), 10);
//			deadline = deadline.substring(0, i);
//
//			String scalingR = lines[6];
//			String chunkyR = lines[9];
//			String fineR = lines[12];
//			String deptR = lines[15];
//			String postR = lines[18];
//			String finalR = lines[21];
//
//			System.out
//					.printf("%60s\t%10s\t%7s\t%7s\t%7s\t%7s\t%7s\t%7s\t%4s\t%4s\t%4s\t%4s\t%4s\t%4s%n",
//							workflow, deadline, scalingC, chunkyC, fineC,
//							deptC, postC, finalC, scalingR, chunkyR, fineR,
//							deptR, postR, finalR);
//
//		}
//
//	}
//
//}
