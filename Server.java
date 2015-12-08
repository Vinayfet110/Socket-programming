import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
public class Server extends JFrame implements ActionListener, Runnable {

	Container con;
	ServerSocket sc;
	Socket client;
	Socket soc;
	JButton b=new JButton("clear");
	JButton sendbutton;
	JButton b1;
	JTextArea jta;
	JTextArea jtsend;
	JScrollPane jsps,jspm;
	
	String name="",status,serverip,msg="",msgs[];
	InetAddress sip,cip;
	static int count=0;
	int clientport,serverport;
	static ArrayList<Integer> port=new ArrayList<Integer>();
	static ArrayList<String> ipaddr=new ArrayList<String>();
	static ArrayList<String> names=new ArrayList<String>();
	static ArrayList<String> cstatus=new ArrayList<String>();
	static ArrayList<Socket> clients=new ArrayList<Socket>();
	public void tsleep(int x){
		try{
			Thread.sleep(x);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	ArrayList<String> returnConnected(){
		ArrayList<String> connected=new ArrayList<String>();
		for(int i=0;i<count;i++){
			if(cstatus.get(i).equals("connect")){
				connected.add(names.get(i));
			}
		}
		for(String r:connected)
			System.out.println("elements connected "+r);
		return connected;
	}
	void setTextArea(){
		jta.setText("");
		jta.append("clients\tstatus\tserver-address\tclient-address\n");
		jta.append("\n-------------------------------------\n");
		for(int k=0;k<count;k++){
		jta.append(names.get(k)+"\t"+cstatus.get(k)+"\t"+serverip+"\t"+ipaddr.get(k)+"\t"+"\n");
		//System.out.println(names.get(k)+"\t"+cstatus.get(k));
		}
	}
	public void clientmsg(Socket c){
		Thread tc=new Thread(new Runnable(){
			public void run(){
	
			while(c.isConnected() && !c.isClosed()){
				try{
					DataInputStream in = new DataInputStream(c.getInputStream());
					//while(in.available()>0)
					msg=in.readUTF();
					//System.out.println("msg from router "+msg);
				}
				catch(Exception e){
					System.out.println("exception is :");
				e.printStackTrace();
				}
				
				//System.out.println("router connected..  .."+msg);
				msgs=msg.split(":");
				name=msgs[0];
				status=msgs[1];
				//System.out.println(name+"\t"+status);
				serverip=c.getLocalSocketAddress().toString();
				clientport=c.getLocalPort();
				serverport=sc.getLocalPort();
				//System.out.println(name+clientport+serverport);
				tsleep(1000);
				if(!names.contains(name)){
					names.add(count,name);
					cstatus.add(count,status);
					ipaddr.add(count++,c.getRemoteSocketAddress().toString());
					//System.out.println("inserted in array list");
				}
				else{
					int nameindex=names.indexOf(name);
					cstatus.set(nameindex,status);
				}
				if(status.equals("disconnect")){
					try{
						c.close();
						System.out.println("inside disconnect status");
						System.out.println(c.isConnected());
						System.out.println(c.isBound());
						System.out.println(c.isClosed());
							//c=null;
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				setTextArea();
				
				//DynamicRP d=new DynamicRP();
				ArrayList<String>l=new ArrayList<String>();
				l=returnConnected();
				System.out.print("dynamic path is :");
				//System.out.println(d.drp(l));
				msg=null;
			}
			}
		});
		tc.start();
	}
	public void run(){
		while(true){
			try{
				
				System.out.println("Waiting for router on port " +
				sc.getLocalPort() + "...");
				client = sc.accept();
				clients.add(client);
				DataInputStream in = new DataInputStream(client.getInputStream());
				clientmsg(client);
			}
			catch(Exception e){
				e.printStackTrace();
				break;
			}
		}
	}
	Server(String name){
		super(name);
		try{
			sc=new ServerSocket(8000);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		con=getContentPane();
		jta=new JTextArea("cleints\tstatus\n",5,35);
		b1=new JButton("cancel");
		jtsend=new JTextArea("",5,35);
		//JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
		jsps=new JScrollPane(jta);
		jspm=new JScrollPane(jtsend);
		sendbutton=new JButton("send");
		con.setLayout(new FlowLayout(FlowLayout.LEFT,5,20));
		//con.add(b);
		b.addActionListener(this);
		sendbutton.addActionListener(this);
		con.add(jsps);
		
		con.add(jspm);
		con.add(sendbutton);
		jta.append("\n-------------------------------------------------\n");
		setVisible(true);
		setSize(450,300);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void actionPerformed(ActionEvent e){
		String action=e.getActionCommand();
		if(action.equals("send")){
			System.out.println("sending");
			String data=jtsend.getText();
			for(Socket c:clients){
			try{
				if (c.isConnected() && !c.isClosed()){
					OutputStream os=c.getOutputStream();
					DataOutputStream dos=new DataOutputStream(os);
					dos.writeUTF(data); // writes messages to the client.
					jtsend.setText("");
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			}
		}
	}
	public static void main(String []args){
		Server c=new Server("Server");
		Thread t=new Thread(c);
		t.start();
	}


}
