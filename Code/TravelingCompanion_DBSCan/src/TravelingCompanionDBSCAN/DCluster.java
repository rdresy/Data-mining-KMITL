package TravelingCompanionDBSCAN;

import java.util.ArrayList;

public class DCluster<T> {
	private int cluster_id;
	private ArrayList<ClusterObjectDBScan> member_object;
	public DCluster(int id,ArrayList<ClusterObjectDBScan> member_object)
	{
		this.cluster_id=id;
		this.member_object=member_object;
	}
	public int getCluster_id() {
		return cluster_id;
	}
	public void setCluster_id(int cluster_id) {
		this.cluster_id = cluster_id;
	}
	public ArrayList<ClusterObjectDBScan> getMember_object() {
		return member_object;
	}
	public void setMember_object(ArrayList<ClusterObjectDBScan> member_object) {
		this.member_object = member_object;
	}
	
	
	

}
