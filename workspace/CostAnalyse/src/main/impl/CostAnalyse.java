package main.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.File;

import javax.xml.bind.JAXB;

import main.generated.Trace;
import main.generated.Trace.Categories.Predictioncategory;
import main.generated.Trace.Categories.Predictioncategory.Deadlinecategories;
import main.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory;
import main.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run;
import main.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Dynamic;
import main.generated.Trace.Categories.Predictioncategory.Deadlinecategories.Deadlinecategory.Runs.Run.Static;

public class CostAnalyse {
	private static List<Predictioncategory> pcategories;
	
	
	public static void main(String[] args) {
		
	
	File xml=new File("C:\\SVNneu\\development\\scheduling\\data\\OutputFilesMartin\\large_output.xml");
	Trace trace=JAXB.unmarshal(xml, Trace.class);
    pcategories= trace.getCategories().getPredictioncategory();
    getLocalMisses();
    getGlobalMisses();
    getAverageCosts();

	

	
	}
	
	private static void getLocalMisses(){
		int staticMisses=0;
		int dynamicMisses=0;
		double costStatic=0;
		double costDynamic=0;
		
		for(Predictioncategory pc:pcategories){
			System.out.println(pc.getMethod()+"  "+pc.getValue());
			for (Deadlinecategory dc:pc.getDeadlinecategories().getDeadlinecategory()){
				System.out.append("  Deadlinefactor"+dc.getDeadlinefactor()+":");
				for (Run r:dc.getRuns().getRun()){
					Static s=r.getStatic();
					Dynamic d=r.getDynamic();
					if (s.getDeadline()<s.getMakespan()){
						staticMisses++;
						costStatic+=s.getCost();
					}
					if (d.getDeadline()<d.getMakespan()){
						dynamicMisses++;
						costDynamic+=d.getCost();
					}
					
				}
				if(staticMisses!=0 && dynamicMisses!=0){
					System.out.println("  local static misses:"+staticMisses+" local dynamic misses:"
										+dynamicMisses+"	local static average cost:"+(costStatic/staticMisses)
										+"local dynamic average cost:"+(costDynamic/dynamicMisses));
				}else if(staticMisses!=0 && dynamicMisses==0){
					System.out.println("  local static misses:"+staticMisses+" local dynamic misses:"
							+dynamicMisses+"	local static average cost:"+(costStatic/staticMisses)
							+" local dynamic average cost:0");
				}else if (dynamicMisses!=0 && staticMisses==0){
					System.out.println("  local static misses:"+staticMisses+" local dynamic misses:"
							+dynamicMisses+"	local static average cost:0"+" local dynamic average cost:"+(costDynamic/dynamicMisses));
				}else{
					System.out.println("  local static misses:"+staticMisses+" local dynamic misses:"
							+dynamicMisses+"	local static average cost:0"
							+" local dynamic average cost:0");
				}
				staticMisses=0;
				dynamicMisses=0;
			}
		}
		
		
			
	}
	
	private static void getGlobalMisses(){
		int staticMisses=0;
		int dynamicMisses=0;
		int[] misses;
		for(Predictioncategory pc:pcategories){			
			for (Deadlinecategory dc:pc.getDeadlinecategories().getDeadlinecategory()){
				for (Run r:dc.getRuns().getRun()){
					Static s=r.getStatic();
					Dynamic d=r.getDynamic();
					if (s.getDeadline()<s.getMakespan()){
						staticMisses++;
					}
					if (d.getDeadline()<d.getMakespan()){
						dynamicMisses++;
					}
					
				}
			
			}
		}
		System.out.println("global static misses:"+staticMisses+" global dynamic misses"+dynamicMisses);
		
	}
	
	
	private static void getAverageCosts(){
		double staticCosts=0;
		double dynamicCosts=0;
		int i=0;
		for(Predictioncategory pc:pcategories){			
			for (Deadlinecategory dc:pc.getDeadlinecategories().getDeadlinecategory()){
				for (Run r:dc.getRuns().getRun()){
					Static s=r.getStatic();
					Dynamic d=r.getDynamic();
					staticCosts+=s.getCost();
					dynamicCosts+=d.getCost();
					i++;
				}
			}
		}
					
		System.out.println("Average static costs:"+(staticCosts/i)+"  Average dynamic costs:"+(dynamicCosts/i));
	
	}
	
	private static void getStaticForDynamicMiss(){
		int staticCostByDynamicMissCount=0;
		int dynamicCostByStaticMissCount=0;
		double scostDMiss=0;
		double dcostSMiss=0;
		double dcostDMiss=0;
		double scostSMiss=0;
		for(Predictioncategory pc:pcategories){			
			for (Deadlinecategory dc:pc.getDeadlinecategories().getDeadlinecategory()){
				for (Run r:dc.getRuns().getRun()){
					Dynamic d=r.getDynamic();
					Static s=r.getStatic();
					if(d.getMakespan()<d.getDeadline()){
						scostDMiss+=s.getCost();
						dcostDMiss+=d.getCost();
						staticCostByDynamicMissCount++;
					}
					if(s.getMakespan()<s.getDeadline()){
						dcostSMiss+=d.getCost();
						scostSMiss+=s.getCost();
						dynamicCostByStaticMissCount++;
					}
				}
			}
		}
		
		
	}
	
		
}
	

