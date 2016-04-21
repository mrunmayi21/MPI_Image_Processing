import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.rmi.ServerException;


//boolean record_12,record_13,record_21,record_23,record_31,record_32 = false;

class Marker implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int ID;
	boolean seen;
	static int counter = 0;

	public Marker() {
		ID = counter;
		counter++;
	}
}

public class ChandyLamportProcess extends UnicastRemoteObject implements
		CommonInterface, Runnable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int portNo = 6544;
	int ID = 0;
	static int balance = 1000;
	static int balance_current=1000;
	static int balance_1 = 0;
	static int balance_2 = 0;
	static int balance_3 = 0;

	String selfName;
	String selfIP;

	int noOfReceivedMarkers = 0;

	String state = "";

	int incomingChannel;

	static boolean recordChannelState = false;
	static boolean record_12,record_13,record_21,record_23,record_31,record_32 = false;
	
	int channelState = 0;
	HashMap<String, String> processes = new HashMap<String, String>();
	HashMap<Integer, String> processesID = new HashMap<Integer, String>();

	static Marker m;
	static Marker receiveM;

	static int noOfObjects = 0;

	static int channel_12=0;
	static int channel_12r=0;
	static int channelState_12=0;
	
	static int channel_13=0;
	static int channel_13r=0;
	static int channelState_13=0;
	
	static int channel_23=0;
	static int channel_23r=0;
	static int channelState_23=0;
	
	static int channel_21=0;
	static int channel_21r=0;
	static int channelState_21=0;
	
	static int channel_32=0;
	static int channel_32r=0;
	static int channelState_32=0;
	
	static int channel_31=0;
	static int channel_31r=0;
	static int channelState_31=0;
	
	static int first_time_marker=0;

	boolean sentToComet = false;

	HashMap<Marker, Integer> seenMarkerObject = new HashMap<Marker, Integer>();

	public ChandyLamportProcess() throws RemoteException, UnknownHostException {
		super();

		processesID.put(1, "name1");
		processesID.put(0, "name0");
		processesID.put(2, "name2");
		

		if (InetAddress.getLocalHost().getHostName().equals("ip2")) {
			ID = 2;
		} else if (InetAddress.getLocalHost().getHostName().equals("ip1")) {
			ID = 1;
		} else if (InetAddress.getLocalHost().getHostName().equals("ip0")) {
			ID = 0;
		}

        processes.put("name0", "ip0");
		processes.put("name1", "ip1");
		processes.put("name2", "ip2");

		selfName = InetAddress.getLocalHost().getHostName();
		selfIP = InetAddress.getLocalHost().getHostAddress();

		if (noOfObjects == 0) {
			++noOfObjects;

			Registry reg = LocateRegistry.createRegistry(portNo);
			reg.rebind("chandyprocess", this);

			System.out.println("Object bound to the registry");

			/*
			 * for (Map.Entry<Integer, String> entry : processesID.entrySet()) {
			 * if (entry.getValue().equals(selfName)) { ID = entry.getKey();
			 * 
			 * if (ID == 0) { // process 1 needs to initiate the snapshot
			 * System.out.println(selfName +
			 * " is going to create a snapshot thread");
			 * this.createSnapshotThread(); } } }
			 */

			if (ID == 0) {
				System.out.println(selfName
						+ " is going to create a snapshot thread");
				this.createSnapshotThread();
			}
		}
	}

	private void createSnapshotThread() throws RemoteException,
			UnknownHostException {
		ChandyLamportProcess snapshotThread = new ChandyLamportProcess();
		Thread t = new Thread(snapshotThread, "snapshotThread");
		t.start();
	}

	public void send() throws RemoteException, NotBoundException,
			InterruptedException {
		while (true) {
			Thread.sleep(8000);
			Random r = new Random();
			int option = r.nextInt(3 - 0);
			while (option == ID) {
				option = r.nextInt(3 - 0);
				if (option != ID) {
					break;
				}
			}

			int amount = r.nextInt(100 - 0);
			if (balance_current - amount >= 0) {

				String conn = processesID.get(option);
				String IP = processes.get(conn);
				Registry reg = LocateRegistry.getRegistry(IP, portNo);
				CommonInterface chprocesssObj = (CommonInterface) reg
						.lookup("chandyprocess");

				chprocesssObj.connected(selfName);
				
				
				
				//balance = balance - amount;
				
				if(ID==0)
				{
					if(option==1)
					{
						channel_12-=amount;
						balance_current-=amount;
					}
					else if(option==2)
					{
						channel_13-=amount;
						balance_current-=amount;
					}
					
				}
				
				else if(ID==1)
				{
					if(option==0)
					{
						channel_21-=amount;
						balance_current-=amount;
					}
					else if(option==2)
					{
						channel_23-=amount;
						balance_current-=amount;
					}
					
				}
				
				else 
				{
					if(option==0)
					{
						channel_31-=amount;
						balance_current-=amount;
					}
					else if(option==1)
					{
						channel_32-=amount;
						balance_current-=amount;
					}
					
				}
				chprocesssObj.transmitMoney(amount,ID);
				
				/*
				if (option == 0) {
					if (ID == 1) {
						channel_12 = amount;
					} else if (ID == 2) {
						channel_13 = amount;
					}
				}
					
				if (option == 1) {
					if (ID == 2) {
						channel_23 = amount;
					} else if (ID == 0) {
						channel_21 = amount;
					}
				}
				
				if (option == 2) {
					if (ID == 1) {
						channel_32 = amount;
					} else if (ID == 0) {
						channel_31 = amount;
					}
				}
				*/
				System.out.println(this.ID + " sending to " + option);
				
				System.out.println("\n\n" + selfName
						+ " transferred an amount of: " + amount + " to : "
						+ conn);
				System.out.println("\n\n" + selfName + "'s balance = "
						+ balance_current);

				

			} else {
				System.out.println("\n\n" + "Insufficient balance");
			}
		}
	}

	public static void main(String[] args) throws RemoteException,
			UnknownHostException, NotBoundException, InterruptedException {
		ChandyLamportProcess clp = new ChandyLamportProcess();
		clp.send();
	}

	@Override
	public void connected(String name) throws RemoteException {
		System.out.println("\n\n" + selfName + " got connected to: " + name);
	}

	@Override
	public void receiveMoney(int amount,int sender_id) throws RemoteException {
		
/*
if (recordChannelState) {
			channelState = channelState + amount;
		}
		*/


	if(sender_id==0)
	{
		if(ID==1)
		{
		channel_21+=amount;
		balance_current+=amount;
		
		if(record_21)
		{
			channel_21r+=amount;
		}
			
		}
		else if(ID==2)
		{
		channel_31+=amount;
		balance_current+=amount;
		if(record_31)
		{
			channel_31r+=amount;
		}
			
		}
	}

	if(sender_id==1)
	{
		if(ID==0)
		{
		channel_12+=amount;
		balance_current+=amount;
		if(record_12)
		{
			channel_12r+=amount;
		}
			
			
		}
		else if(ID==2)
		{
			
		channel_32+=amount;
		balance_current+=amount;	
		if(record_32)
		{
			channel_32r+=amount;
		}
		
			
		}
	}
	if(sender_id==2)
	{
		if(ID==0)
		{
		channel_13+=amount;
		balance_current+=amount;
		if(record_13)
		{
			channel_13r+=amount;
		}
				
		}
		else if(ID==1)
		{
		channel_23+=amount;
		balance_current+=amount;
		if(record_23)
		{
			channel_23r+=amount;
		}
				
			
		
		}
	
		
	
	}
	
	


		//this.balance = this.balance + amount;
		//System.out.println("\n\n" + this.selfName + "'s updated balance = "
			//	+ this.balance);
	}

	@Override
	public void run() {
		//while (true) {
			try {
				//Thread.sleep(160000);
				Thread.sleep(45000);
				first_time_marker++;
				System.out.println("First Time Marker before calling snapshot" + first_time_marker);
				takeSnapshot();
				
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		//}
	}

	private void takeSnapshot() throws RemoteException, NotBoundException, UnknownHostException, InterruptedException {
		//recording it's own state
		
		System.out.println("First Time Marker" + first_time_marker);
		balance+=channel_12+channel_13;
		channel_12=0;
		channel_13=0;
		
		System.out.println("entered take snapshot function line:417");
		//recording its ownstate done
		
		//starts recording
		record_12=true;
		record_13=true;
		
		this.m = new Marker();

		state = "";
		
		state += selfName + " balance: " + balance;
		int ID;
		String hostName = "";
		String connectToIP = "";
		String channelValue="";
		String channelState = "";
		for (Map.Entry<Integer, String> entry : processesID.entrySet()) {
			ID = entry.getKey();
			if (ID != this.ID) {
				hostName = processesID.get(ID);
				connectToIP = processes.get(hostName);
				System.out.println("--debug print-- Line 244: connectToIP:"+connectToIP);
				Registry r = LocateRegistry.getRegistry(connectToIP, portNo);
				CommonInterface processObj = (CommonInterface) r
						.lookup("chandyprocess");
				System.out.println("Initiator Sending marker to ID:"+ID);
				processObj.assignMarkerObject(this.m,this.ID);
				
				/*
				state += processObj.sendState(); // sends the current balance
				System.out.println("--debug print-- Line 248: state:"+state);
				channelValue = processObj.collectChannelState();
				System.out.println("--debug print-- Line 250: channelValue:"+channelValue);
				processObj.sendM(selfIP);
				
				//channelState = "\nChannel state between " + this.selfName
						//+ " and " + hostName + " is: " + channelValue;
				channelState = channelValue;		
				state += channelState;
				*/
				
				
				
			}
		}
		
		
		/*
		state+= "channel state between process 1 and process 2:"+channel_21+"  channel state between process 1 and process 3"+channel_31;
		System.out.println("---------------STATE------------");
		
		System.out.println(state);
		System.out.println("---------------------------------");
		*/
		Thread.sleep(45000);
				// call a function here which will print global state when algo terminates
				printGlobalState();
	}
	
	private void printGlobalState() throws RemoteException, NotBoundException, UnknownHostException {
	System.out.println("Entered print Global State function ");
	String hostName = "";
	String connectToIP = "";
	boolean flag_print_global=true;
	int retValue_2[] = new int[5];
	int retValue_3[] = new int[5];	
	
	
	while(flag_print_global){
	System.out.println("record 12"+record_12+"record 13"+record_13);

	
	
	if(record_12==false && record_13==false)
	{
		System.out.println("Entered record_12==false && record_13==false ");
		flag_print_global=false;
	        for (int ID=0;ID<3;ID++) {
			
			if (ID != this.ID) {
				hostName = processesID.get(ID);
				connectToIP = processes.get(hostName);
				Registry r = LocateRegistry.getRegistry(connectToIP, portNo);
				CommonInterface processObj = (CommonInterface) r
						.lookup("chandyprocess");
				if(ID==1){
				
				
				
				
				do{
					retValue_2=processObj.collectChannelState();
					if(retValue_2[2]==1){
						this.record_21=true;
					}
					else{
						this.record_21=false;
					}
					if(retValue_2[3]==1){
						this.record_23=true;
					}
					else{
						this.record_23=false;
					}
				
					
				
				}while(!(record_21==false && record_23==false));
				System.out.println("Came out of  record_21==false && record_23==false Line:530");	
				
			    this.channelState_21=retValue_2[0];
				this.channelState_23=retValue_2[1];
				this.balance_2=retValue_2[4];
				
			    
				}
				else if(ID==2){
				
				
				
				do{
					retValue_3=processObj.collectChannelState();
					if(retValue_3[2]==1){
						this.record_31=true;
					}
					else{
						this.record_31=false;
					}
					if(retValue_3[3]==1){
						this.record_32=true;
					}
					else{
						this.record_31=false;
					}
				
				}while(!(record_31==false && record_32==false));
				System.out.println("Came out of  record_31==false && record_32==false  Line:559");
				
			    this.channelState_31=retValue_3[0];
				this.channelState_32=retValue_3[1];
				this.balance_3=retValue_3[4];
				
				System.out.println("At Line:565");
				
				
						
				
			}
		}
	
	}
	
	
	
	
	}
	}
	
	System.out.println("channelState_12 is"+channelState_12+"\n  channelState_13 is"+channelState_13+ 
	"\n channelState_21 is"+channelState_21+"\n  channelState_23 is"+channelState_23+"\n channelState_31 is"
	+channelState_31+"\n  channelState_32 is"+channelState_32+"process 1's balance is"+balance+"process 2's balance is"+balance_2
	+"process 3's balance is"+balance_3);
	
	}
	
	@Override
	public void sendM(String IP) throws RemoteException, NotBoundException {
		Registry r = LocateRegistry.getRegistry(IP, portNo);
		CommonInterface pObj = (CommonInterface) r.lookup("chandyprocess");

	}

	@Override
	public void assignMarkerObject(Marker mark, int sender_id) throws RemoteException,  UnknownHostException, NotBoundException, InterruptedException {

		//if (InetAddress.getLocalHost().getHostName().equals("DELL")) {	
		//	receiveM = mark;
		//	System.out.println("--debug print-- entered receiveM");
		//} else {
		//	System.out.println("--debug print-- did not enter receiveM")
	Thread.sleep(2000);
		
	System.out.println("received marker from ID"+sender_id);
	String hostName = "";
	String connectToIP = "";
	m = mark;
	System.out.println("First time Marker above if condition above" + first_time_marker);
	if(first_time_marker==0 && ID!=0)
	{
		first_time_marker++;
		System.out.println("First time Marker above if condition below" + first_time_marker);
	if(ID==0){

	if(sender_id==1)
	{
		balance+=channel_12+channel_13;   //records its own state
		channel_12=0;
		channel_13=0;
		channelState_12=0;
		record_13=true;
		System.out.println("Made record_13 "+record_13);
	}
	
	if(sender_id==2)
	{
		
		balance+=channel_13+channel_12; //records its own state
		channel_13=0;
		channel_12=0;
		channelState_13=0;
		record_12=true;
		System.out.println("Made record_12 "+record_12);
	}

}

else if(ID==1){

	if(sender_id==0)
	{
		balance+=channel_21+channel_23;  //records its own state
		channel_21=0;
		channel_23=0;
		channelState_21=0;
		record_23=true;
		System.out.println("Made record_23 "+record_23);
	}
	
	if(sender_id==2)
	{
		balance+=channel_23+channel_21;            //records its own state
		channel_23=0;
		channel_21=0;
		channelState_23=0;	
		record_21=true;	
		System.out.println("Made record_21 "+record_21);
	}




}

else if(ID==2){

	if(sender_id==0)
	{
		balance+=channel_31+channel_32;         //records its own state
		channel_31=0;
		channel_32=0;
		channelState_31=0;
		record_32 =true;
		System.out.println("Made record_32 "+record_32);
	}
	
	if(sender_id==1)
	{
		balance+=channel_32+channel_31;        //records its own state
		channel_32=0;
		channel_31=0;
		channelState_32=0;
		record_31=true;
		System.out.println("Made record_31 "+record_31);
	}


}
//TODO //Write code to send markers to other processes

for(int x=0;x<3;x++)
{
	if(x!=ID){
hostName = processesID.get(x);
connectToIP = processes.get(hostName);
Registry r = LocateRegistry.getRegistry(connectToIP, portNo);
				CommonInterface processObj = (CommonInterface) r
						.lookup("chandyprocess");
				processObj.assignMarkerObject(new Marker(),ID);
				System.out.println("Process:"+ID+" Sending Marker to process:"+x);
	}

}


}
else{
if(ID==0){

if(sender_id==1){
channelState_12=channelState_12+channel_12r;
channel_12r=0;
record_12=false;
System.out.println("Made record_12 "+record_12);
}
else if(sender_id==2){
channelState_13=channelState_13+channel_13r;
channel_13r=0;
record_13=false;
System.out.println("Made record_13 "+record_13);
}

}

else if(ID==1){
if(sender_id==2){
channelState_23=channelState_23+channel_23r;
channel_23r=0;
record_23=false;
System.out.println("Made record_23 "+record_23);
}
else if(sender_id==0){
channelState_21=channelState_21+channel_21r;
channel_21r=0;
record_21=false;
System.out.println("Made record_21 "+record_21);
}
}

else if(ID==2){
	if(sender_id==0){
channelState_31=channelState_31+channel_31r;
channel_31r=0;
record_31=false;
System.out.println("Made record_31 "+record_31);
	}
else if(sender_id==1){	
channelState_32=channelState_32+channel_32r;
channel_32r=0;
record_32=false;
System.out.println("Made record_32 "+record_32);
}
}

}
                                          
			
		//}
}

	@Override
	public String sendState() throws RemoteException {
		
		System.out.println("--debug printreturn value of sendStae() function"+"\n" + selfName + " balance = " + balance);
		
		return ("\n" + selfName + " balance = " + balance);
		
	}

	@Override
	public void getM(Marker mark) throws RemoteException {
		receiveM = mark;
		System.out.println(selfName + " has receiveM :" + receiveM
				+ " with ID = " + receiveM.ID);
	}

	@Override
	public int[] collectChannelState() throws RemoteException {

		//String retValue;
		//String retValue1;
		
		int retValue[] = new int[5];
		if (this.ID == 0) {
			retValue[0] = this.channelState_12;
			retValue[1] = this.channelState_13;
			retValue[2] = this.record_12?1:0;
			retValue[3] = this.record_13?1:0;
			retValue[4] = this.balance;
			
			//channel_12 = 0;
			//retValue1 = " channel state between process 3 and process 2:"+channel_32;
			//channel_32=0;
		} else if (this.ID == 1) {
			
			retValue[0] = this.channelState_21;
			retValue[1] = this.channelState_23;
			retValue[2] = this.record_21?1:0;
			retValue[3] = this.record_23?1:0;
			retValue[4] = this.balance;
		    /*
			retValue = "channel state between process 2 and process 3:"+channel_23;
			channel_23 = 0;
			retValue1= " channel state between process 1 and process 3::"+ channel_13;
			channel_13=0;
			*/
		} else {
			retValue[0] = this.channelState_31;
			retValue[1] = this.channelState_32;
			retValue[2] = this.record_31?1:0;
			retValue[3] = this.record_32?1:0;
			retValue[4] = this.balance;
		}
        //System.out.println("--debug print return value of collectChannelState() function"+retValue);
		return retValue;
	}

	@Override
	public void transmitMoney(int amount, int sender_id) throws RemoteException,InterruptedException {
		incomingChannel = amount;
		Thread.sleep(4000);
		this.receiveMoney(amount,sender_id);
	}

	@Override
	public int incomingChannelValue() {
		return incomingChannel;
	}
	}
