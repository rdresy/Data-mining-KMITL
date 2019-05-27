package TravelingCompanionDBSCAN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;




public class Main {
	private static ArrayList<TrajectoryPoint> currentSnapData;

	private static double eps=0.04; // for synthetic= 0.0015, t-drive=0.03
	private static int minpoints=3;
	private static DBSCanClusterFinder dbcluster;
	private static int r_candidateSize=10;
	private static int r_lifetime=10;
	private static CampanionDiscovery cd;
	private static	long starttime;
	private static	  long finaltime;
	private static long candidateSize=0;

	public static void main(String[] args) throws ClassNotFoundException, NumberFormatException, IOException {
		cd= new CampanionDiscovery();
		FindCompanion();

	}

	public static void FindCompanion() throws NumberFormatException, IOException {
		ArrayList<CampanionCandidate>  candidateCampanion=new  ArrayList<CampanionCandidate> ();
		ArrayList<CampanionCandidate>  currentCandidatelist=new  ArrayList<CampanionCandidate> ();
		int snapshotNo=0;
		long totalseconds = 0;
		long milli_cluster=0;
		//for synthetic 0 to 1440 
		//for truck from 1 to 1441 sanpshot
		//for t-drive from 2 to 415 snapshot
		for(int i=1;i<1441;i++)// for casual time 90 to 187	*** for peak time 187-247 (6am-10am) and 319-366( 4pm-8pm) *** for work time 247-319(10am to 4pm)
		{ 
			if(i==21)i+=1;//for trunck

			//if(i==152) i+=2;//for t-drive
			snapshotNo++;
			long milliseconds=0; String log="";long totalmilli=0;
			System.out.println("$$$$$$$$$$$$$$$ For SnapShot : "+snapshotNo);
			if(currentCandidatelist!=null)
			{
				candidateCampanion.addAll(currentCandidatelist);
			}

			//get Current snapshot Data
			currentSnapData=getSnapData(i);
			dbcluster=new DBSCanClusterFinder(currentSnapData);
			starttime=System.currentTimeMillis();
			ArrayList<DCluster> clusterlist=dbcluster.FindCluster(eps, minpoints);
			finaltime=System.currentTimeMillis();
			milliseconds = (finaltime-starttime);
			totalmilli+=milliseconds;
			log=log.concat("\nRunning time to create Cluster: "+milliseconds);
			milli_cluster+=totalmilli;		
			System.out.println(" Cluster list size "+clusterlist.size());



			//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  End of clustering and Start for companion %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
			ArrayList<DCluster<TrajectoryPoint>> currentNewCandidate=new ArrayList<>();

			if(!candidateCampanion.isEmpty())
			{ 	 
				//System.out.println(" candidiate size is not empty"+candidateCampanion.size());
				starttime=System.currentTimeMillis();
				currentNewCandidate=cd.FindCampanion(candidateCampanion,clusterlist,r_candidateSize,r_lifetime);
				finaltime=System.currentTimeMillis();
				milliseconds = (finaltime-starttime);
				totalmilli+=milliseconds;
				log=log.concat("\nRunning time to find campanion: "+milliseconds);

			}
			starttime=System.currentTimeMillis();
			cd.ClusterAddToCandidate(clusterlist,r_candidateSize); // all current snapshot cluster are added to candidate list
			finaltime=System.currentTimeMillis();
			milliseconds = (finaltime-starttime);
			totalmilli+=milliseconds;
			log=log.concat("\nRunning time to find campanion: "+milliseconds);
			/*
		if(!currentNewCandidate.isEmpty()) {
			currentCandidatelist=cd.ClusterAddToCandidate(currentNewCandidate,r_candidateSize);// Filter current snapshot cluster are added to the the candidate list
			}*/
			starttime=System.currentTimeMillis();
			currentCandidatelist=cd.CandidateFiltering();
			finaltime=System.currentTimeMillis();
			milliseconds = (finaltime-starttime);
			totalmilli+=milliseconds;
			log=log.concat("\nRunning time to find campanion: "+milliseconds);

			cd.PrintCandidateCampanion();
			//calculate running time for each snapshot	

			int seconds = (int)((totalmilli / 1000)) ;
			String  responseTime= String.format(" %d sec", seconds);

			totalseconds+=totalmilli;//sum for all snapshot milliseconds

			log=log.concat("\nRunning time to find campanion for snapshot "+snapshotNo+" is: " +responseTime+" seconds\t (or)\t "+totalmilli+" milliseconds.");
			log=log.concat("\n***********************End for Snapshot :"+snapshotNo+"*********************");
			//	expdisplay.println(log);




		}

		candidateSize=cd.getCandidateSize();
		cd.CompanionFiltering();
		cd.PrintReultCampanion();
		cd.fileClose();
		//**********calculate running time for all snapshots;
		System.out.println(" For all clustering :"+milli_cluster);

		int seconds = (int) (totalseconds / 1000)  ;

		String  totalresponseTime= String.format(" %d seconds", seconds);
		String totalmillisecond=String.format(" %d Milliseconds ", totalseconds);
		//expdisplay.println(" Total Seconds for (  "+snapshotNo+" ) snapshots:  is "+totalresponseTime+" and milliseconds is : "+totalmillisecond);
		System.out.println(" Total Seconds for (  "+snapshotNo+" ) snapshots:  is "+totalresponseTime+" and milliseconds is : "+totalseconds);
		System.out.println(" ### candidate size is: "+candidateSize);
	}

	public static ArrayList<TrajectoryPoint> getSnapData(int snapcount) throws NumberFormatException, IOException{
		ArrayList<TrajectoryPoint> snapmovingobject=new ArrayList<TrajectoryPoint>();
		int l=1;
		//File f=new File("D:/Dataset/MNTG/Yangon/" + snapcount+ ".txt");//for synthetic
		
		File f=new File("/home/mandresy/Bureau/Data-mining-KMITL/Code/Trucks/" + snapcount+ ".txt");//for trucks
		//File f=new File("D:/All/t-drive snapshot/EachSnapData/snapData_" + snapcount+ ".txt");//for t-drive
		BufferedReader br=new BufferedReader(new FileReader(f));
		String s;
		String[] lines = new String[4];    	   	   

		while((s = br.readLine()) != null){
			int x=0;		    

			StringTokenizer tokens=new StringTokenizer(s,",");
			while (tokens.hasMoreElements()) {
				lines[x] =(String) tokens.nextElement();

				x++;
			}
			TrajectoryPoint p=new TrajectoryPoint();
			Integer tid=Integer.parseInt(lines[0]);
			p.setTid(tid);
			p.setX(Double.parseDouble(lines[2]));
			p.setY(Double.parseDouble(lines[3]));
			snapmovingobject.add( p);
		}    




		//	System.out.println(" moving objects size in each snapshot :"+snapmovingobject.size());

		return snapmovingobject;

	}
}
