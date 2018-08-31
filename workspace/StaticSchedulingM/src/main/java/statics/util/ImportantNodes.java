package statics.util;

//package util;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ImportantNodes {
//
//	public static Map<String, Character> getImportantNodes(String filename) {
//		if (filename.equals("Epigenomics_24.xml")) {
//			return getEpigenomics_24();
//		} else if (filename.equals("Epigenomics_46.xml")) {
//			return getEpigenomics_46();
//		} else if (filename.equals("Epigenomics_100.xml")) {
//			return getEpigenomics_100();
//		} else if (filename.equals("Epigenomics_997.xml")) {
//			return getEpigenomics_997();
//		} else if (filename.equals("Inspiral_30.xml")) {
//			return getInspiral_30();
//		} else if (filename.equals("Inspiral_50.xml")) {
//			return getInspiral_50();
//		} else if (filename.equals("Inspiral_100.xml")) {
//			return getInspiral_100();
//		} else if (filename.equals("Inspiral_1000.xml")) {
//			return getInspiral_1000();
//		} else {
//			Debug.INSTANCE.aPrintln("getImportantNodes not found: filename= "
//					+ filename);
//			return null;
//		}
//	}
//
//	public static Map<String, Character> getInspiral_30() {
//		Map<String, Character> importantNodes = new HashMap<String, Character>();
//		importantNodes.put("ID00015", 'A');
//		importantNodes.put("ID00030", 'Z');
//		return importantNodes;
//	}
//
//	public static Map<String, Character> getInspiral_50() {
//		Map<String, Character> importantNodes = new HashMap<String, Character>();
//		importantNodes.put("ID00025", 'A');
//		importantNodes.put("ID00050", 'Z');
//		return importantNodes;
//	}
//
//	public static Map<String, Character> getInspiral_100() {
//		Map<String, Character> importantNodes = new HashMap<String, Character>();
//		// importantNodes.put(43, 'K');
//
//		importantNodes.put("ID00047", 'A');
//		importantNodes.put("ID00048", 'G');
//		importantNodes.put("ID00049", 'Q');
//
//		importantNodes.put("ID00098", 'B');
//		importantNodes.put("ID00099", 'H');
//		importantNodes.put("ID00100", 'R');
//		return importantNodes;
//	}
//
//	public static Map<String, Character> getInspiral_1000() {
//		Map<String, Character> importantNodes = new HashMap<String, Character>();
//
//		for (int i = 459; i <= 478; ++i) {
//			importantNodes.put(String.format("ID%05d", i), 'A');
//		}
//
//		for (int i = 981; i <= 1000; ++i) {
//			importantNodes.put(String.format("ID%05d", i), 'Z');
//		}
//		return importantNodes;
//	}
//
//	public static Map<String, Character> getEpigenomics_24() {
//		Map<String, Character> importantNodes = new HashMap<String, Character>();
//		importantNodes.put("ID00001", 'A');
//		importantNodes.put("ID00022", 'Z');
//		importantNodes.put("ID00023", 'Z');
//		importantNodes.put("ID00024", 'Z');
//		return importantNodes;
//	}
//
//	public static Map<String, Character> getEpigenomics_46() {
//		Map<String, Character> importantNodes = new HashMap<String, Character>();
//		importantNodes.put("ID00001", 'A');
//		importantNodes.put("ID00022", 'Z');
//		importantNodes.put("ID00023", 'Z');
//		importantNodes.put("ID00024", 'Z');
//		return importantNodes;
//	}
//
//	public static Map<String, Character> getEpigenomics_100() {
//		Map<String, Character> importantNodes = new HashMap<String, Character>();
//		importantNodes.put("ID00001", 'A');
//		importantNodes.put("ID00098", 'Z');
//		importantNodes.put("ID00099", 'Z');
//		importantNodes.put("ID00100", 'Z');
//		return importantNodes;
//	}
//
//	public static Map<String, Character> getEpigenomics_997() {
//		Map<String, Character> importantNodes = new HashMap<String, Character>();
//		importantNodes.put("ID00001", 'A');
//		for (int i = 988; i < 996; ++i) {
//			importantNodes.put(String.format("ID%05d", i), 'Y');
//		}
//		importantNodes.put("ID00996", 'Z');
//		importantNodes.put("ID00997", 'Z');
//		return importantNodes;
//
//	}
//	
//	public static Map<String, Character> importantNodes = Collections.emptyMap(); 
//
//}
