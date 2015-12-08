import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
class Client extends JFrame implements ActionListener,Runnable{
	JRadioButton rc,rdc;
	Container con;
	ButtonGroup bg;
	Socket client;
	JTextArea jta;
	JScrollPane jsp;
	static String serverName,clientName;
	static int port;
	static Thread clientThr;
	Client(String s){
		super(s);
		rc=new JRadioButton("connect");
		rdc=new JRadioButton("disconnect");
		
		bg=new ButtonGroup();
		bg.add(rc);
		bg.add(rdc);
		jta=new JTextArea(5,35);
		jsp=new JScrollPane(jta);
		con=getContentPane();
		con.add(rc);
		con.add(rdc);
		con.add(jsp);
		con.setLayout(new FlowLayout());
		rc.setSelected(true);
		rc.addActionListener(this);
		rdc.addActionListener(this);
		setVisible(true);
		setSize(500,200);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void condiscon(DataOutputStream d,String status){
		try{
			d.writeUTF(clientName+":"+status);
			d.flush();
			System.out.println("sent disconnect msg to server");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void run(){
		try{
			System.out.println("connecting to "+serverName+" on "+port);
			client=new Socket("localhost",8000);
			System.out.println("just connected to "+client.getRemoteSocketAddress());
			InputStream is=client.getInputStream();
			DataInputStream dis=new DataInputStream(is);
			OutputStream os=client.getOutputStream();
			DataOutputStream dos=new DataOutputStream(os);
			//condiscon(dos,"connect");
			dos.writeUTF(clientName+":connect");
			dos.flush();
			//dos.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		Runnable listener=new Runnable(){
			public void run(){
				while(client.isConnected() && !client.isClosed()){ // Continuously receives messages from the server, untill the connection is closed. 
				try{
					InputStream is=client.getInputStream();
					DataInputStream dis=new DataInputStream(is);
					String data=dis.readUTF();// reads the messages sent by the server.
					System.out.println("server says :"+data);
					jta.append("server says :"+data+"\n");
					//if(data.equalsIgnoreCase("quit")) // If server sends quit, connection will be closed
						//c.close();
				}
				catch(Exception e){
					System.out.println("server closed:");
					//client.close();
					break;
		//e.printStackTrace();
				}
				}
			}
		};
		Thread tlistener=new Thread(listener);
		tlistener.start();
	}
	public void actionPerformed(ActionEvent e){
		String item=e.getActionCommand();
				//System.out.println("thread in ap is "+clientThr.getName());
		if(item.equals("connect")){
			//Router r=new Router("Router-"+serverName);
			Thread t=new Thread(this,clientName);
			clientThr=t;
			t.start();
			System.out.println("connected to controller.....");
		}
		else if(item.equals("disconnect")){
			try{
				OutputStream os=client.getOutputStream();
				DataOutputStream dos=new DataOutputStream(os);
				condiscon(dos,"disconnect");
				System.out.println("thread name is "+clientThr.getName());
				System.out.println("router disconnected");
				Thread.sleep(1000);
				//client.close();
				System.out.println("isConnected:"+client.isConnected());
				//System.out.println("isBound:"+client.isBound());
				System.out.println("isClosed:"+client.isClosed());
			}
			catch(Exception ex){
				ex.printStackTrace();
				System.out.println(e.toString());
			}
			//System.out.println("disconnected from server....");
		}
	}
	public static void main(String[] args){
		serverName=args[0];
		port=Integer.parseInt(args[1]);
		clientName="Client-"+args[2];
		Client rc=new Client("Client-"+args[2]);
		Thread tc=new Thread(rc,clientName);
		clientThr=tc;
		
		System.out.println("thread in main is "+tc.getName());
		System.out.println("thread in main is "+Thread.currentThread().getName());
		tc.start();
		
	}
}