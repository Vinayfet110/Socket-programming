#!/usr/bin/python
import socket 
import threading
class sserver:
    client=None
    server=None
    client_address=None
    data=""

def sending(): # thread for broadcasting messages to the client
    while True:
        sserver.data=raw_input(":")
        for c in clients:
            c.sendall(sserver.data)
        if sserver.data=="bye":
            break


def accepting(): #thread for  accepting  conncetion from client
    while sserver.data!="bye":
        sserver.client,sserver.client_address=sserver.server.accept()
        clients.append(sserver.client) 
	
        #print "client is : "+str(sserver.client)
        #print type(client)
        #print "client address is "
        #print type(sserver.client_address)
        
clients=[]  
t_sending=threading.Thread(target=sending)
t_accepting=threading.Thread(target=accepting)
t_accepting.setDaemon(True)

if __name__=="__main__":
    sserver.server=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_name=socket.gethostname()
    port=7777
    sserver.server.bind((server_name, port))
    sserver.server.listen(5)
    
    t_accepting.start() #creates thread which accepts connection
    t_sending.start() #creates a thread which broadcasts messages
    print type(sserver.server)
    print type(server_name)
    print str(server_name)+" : "+str(sserver.server)
    while True: 

	if sserver.data=="bye":
        #break
		sserver.server.close() #closes server connection 
		break
       
        #sserver.client.sendall(sserver.data)
        #if sserver.data=="bye":
         #   break
    sserver.server.close()
    print "server closed"
