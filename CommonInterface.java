import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface CommonInterface extends java.rmi.Remote {

	
	
	public void connected(String selfName) throws RemoteException;

	public void receiveMoney(int amount, int sender_id) throws RemoteException;

	public void assignMarkerObject(Marker m, int sender_id) throws RemoteException, UnknownHostException, NotBoundException, InterruptedException;

	public String sendState() throws RemoteException;

	public int[] collectChannelState() throws RemoteException;

	public void sendM(String IP) throws RemoteException, NotBoundException;

	public void getM(Marker m) throws RemoteException;

	public void transmitMoney(int amount, int ID) throws RemoteException, InterruptedException;

	public int incomingChannelValue() throws RemoteException;

}
