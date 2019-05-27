package TravelingCompanionDBSCAN;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;










public class DBSCanClusterFinder<T> {
	private Collection<T> candidate;
	public HashMap<T, ClusterObjectDBScan<T>> cos;
	
	public DBSCanClusterFinder(Collection<T> objlist)
	{
		this.candidate=objlist;
	}
	
	public ArrayList<DCluster> FindCluster(double eps, int minPoints) throws IOException
	{
		ArrayList<DCluster> clusterlist=new ArrayList<>();
		
		cos = new HashMap<T, ClusterObjectDBScan<T>>();
		
		//	log.debug("Creating the ClusterObjects...");
			for (T t : candidate) {
				cos.put(t, new ClusterObjectDBScan<T>(t));
			}
			
			System.out.println(" cos size "+cos.size() );
			int cluster = -1;
			
		
		T t;
		int countsize=0;
		while((t=getUnvisitedPoint(cos) )!= null)
		{
			ArrayList<ClusterObjectDBScan> clumemberlist=new ArrayList<>();
			ClusterObjectDBScan<T> current = cos.get(t);
			current.setProcessed(true);
			Collection<T> neighbors= neighbor(cos,t, eps);

			if(neighbors.size() < minPoints){
				current.setIdCluster(-1);
			}
			else if(neighbors.size() >= minPoints)
			{
				
				cluster++;					
				current.setIdCluster(cluster);
				current.setIsCore(true);
				current.reachabilityDistance=0;
				clumemberlist.add(current);
				
			//	log.debug(((TrajectoryAsSet) current.originalObject).getTid()+": isCore="+current.isCore+" cluster="+current.getIdCluster());
				
				//put on the csv the moving object core
								
				
				for (T t2 : neighbors) {
					//TODO set reachability distance
					
					if(!cos.get(t2).isProcessed() || cos.get(t2).getIdCluster()==-1) 
					{
						TrajectoryPoint tp=(TrajectoryPoint) cos.get(t2).getOriginalObject();
						cos.get(t2).setReachabilityDistance(getDistance((TrajectoryPoint) current.getOriginalObject(), tp));
					}
				}
				
				//expande os vizinhos passando as trajetorias como objetos de clusters
				ArrayList<ClusterObjectDBScan> memberlist=expandCluster(neighbors, cluster, eps, minPoints, t); //buffWrite
				if(memberlist!=null) {
					clumemberlist.addAll(memberlist);
					//System.out.println(" member list size for clu_id: "+cluster+" size :"+clumemberlist.size());
					countsize+=clumemberlist.size();
				}
				
				DCluster dclu=new DCluster(cluster,clumemberlist);
				clusterlist.add(dclu);
			}
			
			
			
		}//
		
		System.out.println(" total object size is original :"+cos.size()+" member object size :"+countsize);
		return clusterlist;
	}
private ArrayList<ClusterObjectDBScan>  expandCluster(Collection<T> objectsNeigh, int cluster, double eps, int minPoints, T previous) throws IOException{
		
		ArrayList<T> neighbors = new ArrayList<T>();		
		neighbors.addAll(objectsNeigh);
		ArrayList<ClusterObjectDBScan> memberlist=new ArrayList<>();
		
		T t2;
		boolean belongs;
		Collection<T> nextNeighbors;
		
		//System.out.println(" in expand cluster neigbours size is: "+ neighbors.size());
		for (int i = 0; i < neighbors.size(); i++) {
			//System.out.println(" count for i :"+i+" neighbour size "+neighbors.size());	
			T t = neighbors.get(i);
			ClusterObjectDBScan<T> next = cos.get(t);
			
			//expands each neighbor if it is not processed
			if(!next.isProcessed()){
				next.setProcessed(true);
				
				//TODO to hilbert curve
				//nextNeighbors = dm.neighbors(t,objects, eps);
				nextNeighbors = neighbor(cos,t, eps);
				
			//	System.out.println("For neighbour :" +((TrajectoryAsSet) t).getTid()+"  next neighbours size "+nextNeighbors.size());
				if(nextNeighbors.size() >= minPoints) {
					next.setIsCore(true);
					
					for(T t1: nextNeighbors){
						
						belongs = false;
						for (int j = 0; j < neighbors.size() && !belongs; j++) {
							t2 = neighbors.get(j);
							//test for nextNeighbor is already on neighbor set
							if(((TrajectoryPoint)t1).getTid()==((TrajectoryPoint)t2).getTid()) belongs=true;
						}
						if(!belongs){
							neighbors.add(t1);
							//System.out.println(" add to neighbours" );
							//TODO set reachability distance
							if(!cos.get(t1).isProcessed() || cos.get(t1).getIdCluster()==-1) cos.get(t1).setReachabilityDistance(getDistance((TrajectoryPoint)t,(TrajectoryPoint) t1));
						}
					}	
				}
			}
			if(next.idCluster==-1){
				next.setIdCluster(cluster);
				memberlist.add(next);					
			//	log.debug(((TrajectoryAsSet) next.originalObject).getTid()+": isCore="+next.isCore+" cluster="+next.getIdCluster()+" distance: "+dm.distance(t, previous));
				
				/*//put on the csv the moving object core
				time_array = ((TrajectoryAsSet)next.originalObject).getTsArray();
				coord_array = ((TrajectoryAsSet)next.originalObject).getCoordArray();
				
				//put on the csv the clusters
				for (int j = 0; j < coord_array.size(); j++) {
					buffWrite.write(((TrajectoryAsSet) next.originalObject).getTid()+";"+coord_array.get(j).y+";"
						+coord_array.get(j).x+";"+time_array.get(j)+";"+next.getIdCluster()+";"+next.isCore+"\n");
				}*/
			}
					
		}
		
		return memberlist;
	}

	public Collection<T> neighbor( HashMap<T, ClusterObjectDBScan<T>> cos2,T t,double eps)
	{
		 Collection<T> neighbourlist=new ArrayList<>();
				 
			Collection<ClusterObjectDBScan<T>> values = cos2.values();

			
			for (ClusterObjectDBScan<T> clusterObjectDBScan : values) {
				
				TrajectoryPoint p=((TrajectoryPoint)clusterObjectDBScan.getOriginalObject());
				if(p!=null &&!clusterObjectDBScan.isProcessed()) 
				{
					if(getDistance((TrajectoryPoint) t,p)<=eps)
					{
						neighbourlist.add(clusterObjectDBScan.getOriginalObject());
					}
				}
			}
		 return neighbourlist;
	}
	
	
	private T getUnvisitedPoint(HashMap<T, ClusterObjectDBScan<T>> cos) {

		Collection<ClusterObjectDBScan<T>> values = cos.values();
		
		for (ClusterObjectDBScan<T> clusterObjectDBScan : values) {
			if(((TrajectoryPoint)clusterObjectDBScan.getOriginalObject())!=null &&!clusterObjectDBScan.isProcessed()) return clusterObjectDBScan.getOriginalObject();
		}			

		return null;
	}
	
	
	
	
	public static double getDistance (TrajectoryPoint t, TrajectoryPoint t2)
	{

		double dx = t.getX()-t2.getX();

		double dy = t.getY()-t2.getY();

		double distance = Math.sqrt (dx * dx + dy * dy);

		return distance;

	}

}
