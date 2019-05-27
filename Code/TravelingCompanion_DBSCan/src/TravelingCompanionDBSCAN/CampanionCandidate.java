package TravelingCompanionDBSCAN;

import java.util.ArrayList;

public class CampanionCandidate {
	private int candidate_id;
	private ArrayList<TrajectoryPoint> candidate_member=new ArrayList<>();

	private DCluster candidate_cluster;
	private int time;
	private int campanionsize;
	public CampanionCandidate()
	{
		
	}
	public CampanionCandidate(int candidate_id1,ArrayList<TrajectoryPoint> candidate_member,int time1,int campanionsize){
		//this.candidate_cluster=candidate_cluster;
	//	this.candidate_member.clear();
		this.candidate_id=candidate_id1;
		this.candidate_member.addAll(candidate_member);
		this.time=time1;
		this.campanionsize=campanionsize;
	}
	
	public int getCandidate_id() {
		return candidate_id;
	}

	public void setCandidate_id(int candidate_id) {
		this.candidate_id = candidate_id;
	}

	public ArrayList<TrajectoryPoint> getCandidate_member() {
		return candidate_member;
	}

	public void setCandidate_member(ArrayList<TrajectoryPoint> candidate_member) {
		this.candidate_member = candidate_member;
		
	}

	public DCluster getCandidate_cluster() {
		return candidate_cluster;
	}
	public void setCandidate_cluster(DCluster candidate_cluster) {
		this.candidate_cluster = candidate_cluster;
	}
	//remove Candidate
	public void removeCandidate(ArrayList<TrajectoryPoint> removelist)
	{
		this.candidate_member.removeAll(removelist);
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getCampanionsize() {
		return campanionsize;
	}
	public void setCampanionsize(int campanionsize) {
		this.campanionsize = campanionsize;
	}
	
	
	
}
