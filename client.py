#!/usr/bin/python

import socket
class client:
    pass
if __name__=="__main__":
    client=socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_name=socket.gethostname()
    port=7777
    client.connect((client_name,port)) #connects to server
    while True:  #continuously receives messages from the server
        recv=client.recv(1024) 
        print "server says : "+recv
        if recv=="bye":
            #client.close()
	    break
    print "client closed"

