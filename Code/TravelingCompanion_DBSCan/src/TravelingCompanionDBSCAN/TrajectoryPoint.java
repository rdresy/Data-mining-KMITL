package TravelingCompanionDBSCAN;

public class TrajectoryPoint {

	private double x;
	private double y;
	private int tid; //trajectory id
	private long time; //timestamp
	private int cid; //for free movement: hilbertIndex, for road network:edge_id
	private double reachabilitydistance;
	
	public TrajectoryPoint(int tid, int x, int y, long time) {
		this.tid=tid;
		this.x=x;
		this.y=y;
		this.time=time;
	}
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public TrajectoryPoint(int tid, int x, int y, long time, int cid) {
		this.tid=tid;
		this.x=x;
		this.y=y;
		this.time=time;
		this.cid=cid;
	}
	
	public TrajectoryPoint(int x, int y) {
		this.x=x;
		this.y=y;
	}
	
	public TrajectoryPoint() {
	}
	
	public double getX() {
		return x;
	}

	public void setX(double d) {
		this.x = d;
	}

	public double getY() {
		return y;
	}

	public void setY(double d) {
		this.y = d;
	}
	
	public int getTid() {
		return tid;
	}

	public double getReachabilitydistance() {
		return reachabilitydistance;
	}

	public void setReachabilitydistance(double reachabilitydistance) {
		this.reachabilitydistance = reachabilitydistance;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}
	
	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}


	public void swapxy() {
		 double t = x;
	     x = y;
	     y = t;		
	}
	
	
}
