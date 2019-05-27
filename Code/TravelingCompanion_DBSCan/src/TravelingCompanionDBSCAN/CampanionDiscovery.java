package TravelingCompanionDBSCAN;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;






public class CampanionDiscovery {
	private int can_id=1;
	PrintWriter pwcandidate;
	PrintWriter pwresult,pwresult1;
	ArrayList<CampanionCandidate>  currentCandidatelist=new  ArrayList<CampanionCandidate> ();
	ArrayList<CampanionCandidate>  resultCandidatelist=new  ArrayList<CampanionCandidate> ();
	ArrayList<CampanionCandidate>  unqualifiedcanlist=new  ArrayList<CampanionCandidate> ();

	public CampanionDiscovery() throws FileNotFoundException
	{	//for trucks
		/*pwcandidate=new PrintWriter("D:/All/Truck/DBSCAN/CandidateList.txt");
		 pwresult=new PrintWriter("D:/All/Truck/DBSCAN/CandidateResultList.txt");
		 pwresult1=new PrintWriter("D:/All/Truck/DBSCAN/CandidateResultList1.txt");
*/

		//for t-drive
		pwcandidate=new PrintWriter("/home/mandresy/Bureau/Data-mining-KMITL/Code/T_drive/CandidateList.txt");
		 pwresult=new PrintWriter("/home/mandresy/Bureau/Data-mining-KMITL/Code/T_drive/CandidateResultList.txt");
		 pwresult1=new PrintWriter("/home/mandresy/Bureau/Data-mining-KMITL/Code/T_drive/GroundTruthResultList.txt");
		 

		 //for synthetic
		 
		/*	pwcandidate=new PrintWriter("D:/All/Synthetic/DBSCAN/CandidateList.txt");
			 pwresult=new PrintWriter("D:/All/Synthetic/DBSCAN/CandidateResultList.txt");
			 pwresult1=new PrintWriter("D:/All/Synthetic/DBSCAN/CandidateResultList1.txt");*/
	}

	
	public ArrayList<DCluster<TrajectoryPoint>>  FindCampanion(ArrayList<CampanionCandidate> candidateCampanion,ArrayList<DCluster> clusterlist,int r_candidateSize,int r_lifetime)
	{
		  ArrayList<DCluster<TrajectoryPoint>> addClustertoCandidate=new ArrayList<>();

		ArrayList<TrajectoryPoint> resutlist;
		Iterator<CampanionCandidate> can_list=candidateCampanion.iterator();
		currentCandidatelist.clear();//******Important
		while(can_list.hasNext())
		{	
			CampanionCandidate r_candidate=can_list.next();
			boolean found=false;
			boolean findextension=false;
			for(DCluster cluster:clusterlist)
			{	if(r_candidate.getCampanionsize()>r_candidateSize)
				{
					resutlist=findIntersection(r_candidate,cluster,r_candidateSize);// find the intersection and return satisfy the size threshold
				
					
					if(resutlist!=null)
					{	findextension=true;
						int time1=r_candidate.getTime();
						time1++;
						int canid=r_candidate.getCandidate_id();
						
						CampanionCandidate c_candidate=new CampanionCandidate(canid,resutlist,time1,resutlist.size());
						//currentCandidatelist.add(c_candidate);//add to next snapshot
						can_id++;
						if(time1>=r_lifetime)
						{
							//System.out.println(" Result candidate ");
							CampanionCandidate canresult=GetCloneCampanionCandidate(c_candidate);
							
							resultCandidatelist.add(canresult);// add to qualified campanion
						}
						else
							currentCandidatelist.add(c_candidate);//add to next snapshot
					
						int diffsize=r_candidate.getCampanionsize()-resutlist.size();
						if(diffsize<r_candidateSize)
						{
							found=true;
							//System.out.print(" found ");
						}
						r_candidate.removeCandidate(resutlist);//remove from candidate;
						
						//if(r_candidate!=null)System.out.println(" After removing candidate size is: "+r_candidate.getCandidate_member().size());
						boolean statetest=testClusterSize(cluster,resutlist,r_candidateSize);
						if(statetest)
						{
							addClustertoCandidate.add(cluster);
						}
						
						
						
					}
					/*else
					{
						int time1=r_candidate.getTime();
						if(time1>=r_lifetime)
						{
							//System.out.println(" Result candidate ");
							CampanionCandidate canresult=GetCloneCampanionCandidate(r_candidate);
							
							resultCandidatelist.add(canresult);// add to qualified campanion
						}
						
					}*/
				}
			if(!findextension)
			{
				unqualifiedcanlist.add(r_candidate);
			}
			if(found)break;
				

			}//end for each cluster
			
			/*// current candidate cannot be extended are added to the list
			if(!findextension)
			{
				int time1=r_candidate.getTime();
				if(time1>=r_lifetime)
				{
					//System.out.println(" Result candidate ");
					CampanionCandidate canresult=GetCloneCampanionCandidate(r_candidate);
					
					resultCandidatelist.add(canresult);// add to qualified campanion
				}
			}*/
			
			//System.out.println(" \nEnd for one candidate");
		}//end while
		if(!resultCandidatelist.isEmpty())
		{
			System.out.println("  result list in current snapshot "+resultCandidatelist.size());
			PrintReultCampanion();
		}
		return addClustertoCandidate;
	}
	public boolean testClusterSize(DCluster cluster, ArrayList<TrajectoryPoint> result, int r_candidateSize)
	{
    ArrayList<TrajectoryPoint> tlist=new ArrayList<TrajectoryPoint>();
    ArrayList<TrajectoryPoint> list=new ArrayList<TrajectoryPoint>();
	 Collection<ClusterObjectDBScan> pointlist=cluster.getMember_object();
		
		
	 for(ClusterObjectDBScan obj:pointlist)
		{
			TrajectoryPoint p=((TrajectoryPoint)obj.getOriginalObject());
			tlist.add(p);
		}
	      for(TrajectoryPoint p:result)
	      {
	    	  if(tlist.contains(p)) list.add(p);//find similar object that alredy include in candidate(result) list
	      }
	      
	      tlist.removeAll(list);// remove  trajectory point that are already added to candidatelist
		if(tlist.size()>=r_candidateSize)
			return true;
		else 
			return false;
				
	}
	public 	ArrayList<CampanionCandidate>  ClusterAddToCandidate(ArrayList<DCluster> clusters,int r_candidateSize)
	{
		
		int starttime=1;
		
		for (DCluster eachcluster: clusters) 
		{	
			  ArrayList<ClusterObjectDBScan> pointlist=eachcluster.getMember_object();
		      ArrayList<TrajectoryPoint> tlist=new ArrayList<TrajectoryPoint>();
	
		      for(ClusterObjectDBScan obj:pointlist)
				{
					TrajectoryPoint p=((TrajectoryPoint)obj.getOriginalObject());
					tlist.add(p);
				}
		    
		      if(tlist.size()>=r_candidateSize)
		      {
		    	  CampanionCandidate c_candidate=new CampanionCandidate(can_id,tlist,starttime,tlist.size());
		    	  currentCandidatelist.add(c_candidate);
		    	  can_id++;
		      }
		}
		return currentCandidatelist;

	}
	
	public static  ArrayList<TrajectoryPoint> findIntersection(CampanionCandidate r_candidate,DCluster cluster,int r_candidateSize)
	{	
		//System.out.println(" candidate size :"+candidatelist.size()+"   each cluster size "+cluster.getClustermeber_size());
		ArrayList<ClusterObjectDBScan> clusterlsit= cluster.getMember_object();
		ArrayList<TrajectoryPoint> resultcandidate = new ArrayList<>();
		ArrayList<TrajectoryPoint> candidateTrajectory=new ArrayList<>();
		candidateTrajectory.addAll(r_candidate.getCandidate_member());
		ArrayList<TrajectoryPoint> clusterTrajectory=new ArrayList<>();

		
		for(ClusterObjectDBScan obj:clusterlsit)
		{
			TrajectoryPoint p=((TrajectoryPoint)obj.getOriginalObject());
			clusterTrajectory.add(p);
		}

		//System.out.println(" candidate size :"+candidateTrajectory.size()+"   each cluster size "+clusterTrajectory.size());
		//find intersection
		for (TrajectoryPoint t : candidateTrajectory) {
			for(TrajectoryPoint cp:clusterTrajectory){
				if(t.getTid()==cp.getTid()) {
					resultcandidate.add(t);
				}
			}
		}
		//System.out.println(" Intersection count is :"+resultcandidate.size());

		if(resultcandidate.size()>=r_candidateSize)
		{
			return resultcandidate;
		}
		else{
			return null;
		}


	}
	public ArrayList<CampanionCandidate> CandidateFiltering()
	{ArrayList<CampanionCandidate> candidatelist=new ArrayList<>();
	candidatelist.addAll(currentCandidatelist);
	
	ArrayList<CampanionCandidate> removelist=new ArrayList<>();
	Iterator it=candidatelist.iterator();
	System.out.print("before candidate flilter:"+candidatelist.size());
	for(CampanionCandidate c:currentCandidatelist)
	{
		
		
		while(it.hasNext())
		{
			CampanionCandidate cand= (CampanionCandidate) it.next();
			
			
			if(c.getCandidate_id()!=cand.getCandidate_id() )
			{ CampanionCandidate resultcand = null;
			
				if(FindSameList(c,cand) )//test for (all members are matched or subset of each other)
				{	//System.out.println(" yes  ");
					if(c.getTime()>cand.getTime())
						{removelist.add(cand);it.remove();}
					else if(cand.getTime()>c.getTime())
						{removelist.add(c);}
					
				}
				else if((resultcand=SubsetList(c,cand))!=null) //test for subset of each other and return lifetime is greater 
				{
					if(resultcand.getCandidate_id()==cand.getCandidate_id()) //if remove candidate is cand
					{ removelist.add(cand); it.remove();}
					else
					{
						removelist.add(c);
					}
					
				
				
				}
				
			}
		}
	}
	
	
	System.out.print("before Candidate removing :"+currentCandidatelist.size()+"\t remove candidate list : "+removelist.size());
	currentCandidatelist.removeAll(removelist);
	System.out.println("\t After removing :"+currentCandidatelist.size());
	
	
	return currentCandidatelist;
	
	}
	public boolean FindSameList(CampanionCandidate c,CampanionCandidate cand)
	{
		boolean flag=false;
		List<Integer> list = new ArrayList<>();
		List<Integer> list1 = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();
		
		
		
		
		for(TrajectoryPoint p:c.getCandidate_member())
		{
			list1.add(p.getTid());
		}
		for(TrajectoryPoint pt:cand.getCandidate_member())
		{
			list2.add(pt.getTid());
		}
		
		if(list1.size()==list2.size())
		{
			for (Integer t : list1) {
				if(list2.contains(t)) {
					list.add(t);
				}
			}
       
		}
		if(list.size()==list1.size() && list.size()==list2.size())
		{
			return true;
		}
		else 
			return false;
	}
	public CampanionCandidate SubsetList(CampanionCandidate c,CampanionCandidate cand)
	{
		boolean flag=false;
		List<Integer> list = new ArrayList<>();
		List<Integer> list1 = new ArrayList<>();
		List<Integer> list2 = new ArrayList<>();
		
		
		
		
		for(TrajectoryPoint p:c.getCandidate_member())
		{
			list1.add(p.getTid());
		}
		for(TrajectoryPoint pt:cand.getCandidate_member())
		{
			list2.add(pt.getTid());
		}
		
		if(list1.size()>=list2.size())
		{
			for (Integer t : list2) {
				if(list1.contains(t)) {
					list.add(t);
				}
			}
			
		}
		else 
		{
			for (Integer t : list1) {
				if(list2.contains(t)) {
					list.add(t);
				}
			}
			
		}
		CampanionCandidate candresult=null;
		if(list2.size()==list.size() )// list2 is subset of list1 
		{
			if(c.getTime()>=cand.getTime() ) candresult=cand;// list1"c" is big and lifetime is longer
			else candresult=c; // list1" c" is big but its lifetime is short
			
		}
		else if( list1.size()==list.size())// list1 is subset of list2
		{
			if(cand.getTime()>=c.getTime() ) candresult=c;// list2"cand" is big and lifetime is longer
			else candresult=cand; // list2"cand" is big but its lifetime is short
		}
		
		
		 return candresult;
	}
	public void CompanionFiltering()
	{
		ArrayList<CampanionCandidate> resultlist=new ArrayList<>();
		resultlist.addAll(resultCandidatelist);
		ArrayList<CampanionCandidate> samelist=new ArrayList<>();
		Iterator it=resultlist.iterator();
		System.out.print("before flilter:"+resultlist.size());
		for(CampanionCandidate c:resultCandidatelist)
		{
			
			
			while(it.hasNext())
			{
				CampanionCandidate cand= (CampanionCandidate) it.next();
				
				
				if(c.getCandidate_id()!=cand.getCandidate_id() )
				{
				
					if(FindSameList(c,cand))
					{	//System.out.println(" yes  ");
						
						samelist.add(cand);
						it.remove();
					}
				}
			}
		}
		System.out.println("Before removing "+resultCandidatelist.size());
		resultCandidatelist.removeAll(samelist);
		System.out.println("\tsame list:"+samelist.size()+"   after filtering :"+resultCandidatelist.size());
	}
	
	public long getCandidateSize()
	{ long cansize=0;
	ArrayList<CampanionCandidate> resultlist=new ArrayList<>();
	resultlist.addAll(resultCandidatelist);
	resultlist.addAll(currentCandidatelist);
    resultlist.addAll(unqualifiedcanlist);
		for(CampanionCandidate c:resultlist)
		{
			
			
			
			
				
				
				cansize+=c.getCandidate_member().size();
			
		}
		return cansize;
	}
	public CampanionCandidate GetCloneCampanionCandidate(CampanionCandidate rcandidate)
	{
		CampanionCandidate clonecanidate=new CampanionCandidate();
		clonecanidate.setCandidate_id(rcandidate.getCandidate_id());
		clonecanidate.setTime(rcandidate.getTime());
		clonecanidate.setCandidate_cluster(rcandidate.getCandidate_cluster());
		clonecanidate.setCampanionsize(rcandidate.getCampanionsize());
		ArrayList<TrajectoryPoint> tlist=new ArrayList<>();
		tlist.addAll(rcandidate.getCandidate_member());
		clonecanidate.setCandidate_member(tlist);
		
		return clonecanidate;
	}
	public void PrintCandidateCampanion()
	{ 	
		if(currentCandidatelist!=null)
		{
			for(CampanionCandidate c:currentCandidatelist)
			{
				pwcandidate.print("Id "+c.getCandidate_id()+" Time:"+c.getTime()+" Size: "+c.getCampanionsize()+" Member :");
				for(TrajectoryPoint t:c.getCandidate_member())
				{
					pwcandidate.print(t.getTid()+"\t");
				}
				pwcandidate.println();
			}
			pwcandidate.println("############## End for one snapshot ");

		}

	}
	public void PrintReultCampanion()
	{	System.out.println("All  Result Campanion is:"+resultCandidatelist.size());
		
		for(CampanionCandidate c:resultCandidatelist)
		{
		pwresult.print("Id "+c.getCandidate_id()+" Time:"+c.getTime()+" Size: "+c.getCampanionsize()+" Member :");
	//	pwresult1.print(c.getCandidate_id()+" ");

		for(TrajectoryPoint t:c.getCandidate_member())
		{
			pwresult.print(t.getTid()+"\t");
			pwresult1.print(t.getTid()+",");

		}
		pwresult.println();
		pwresult1.println();

		}
		pwresult.println(" Result Campanion "+resultCandidatelist.size());
		pwresult.println("############## End for one snapshot ");
	}
	public void fileClose()
	{
		pwcandidate.close();
		pwresult.close();
		pwresult1.close();

	}
}
