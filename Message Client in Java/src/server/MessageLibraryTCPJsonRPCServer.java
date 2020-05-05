package ser321.assign6.tsjorda1.server;

import java.net.*;
import java.io.*;
import java.util.*;

/*
 * Copyright 2019 Tim Jordan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Purpose: This class supports a data structure that contains multiple messages
 * in an email client. It has methods that reads from JSON arrays.
 *
 * Ser321 Principles of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 *
 * @author Tim Jordan, tsjorda1@asu.edu
 * @version October 2019
 */

public class MessageLibraryTCPJsonRPCServer extends Thread {
    private Socket conn;
    private int id;
    private MessageLibrarySkeleton skeleton;

    public MessageLibraryTCPJsonRPCServer(Socket sock, int id, MessageLibrary msgLib) {
        this.conn = sock;
        this.id = id;
        skeleton = new MessageLibrarySkeleton(msgLib);
    }

    public void run() {
        try {
            OutputStream outSock = conn.getOutputStream();
            InputStream inSock = conn.getInputStream();
            byte clientInput[] = new byte[1024];
            int numr = inSock.read(clientInput,0,1024);
            if (numr != -1) {
                String request = new String(clientInput,0,numr);
                System.out.println("Request is: " + request);
                String response = skeleton.callMethod(request);
                byte clientOut[] = response.getBytes();
                outSock.write(clientOut,0,clientOut.length);
                System.out.println("Response is: " + response);
            }
            inSock.close();
            outSock.close();
            conn.close();
        } catch(IOException e) {
            System.out.println("I/O exception occurred for the connection: \n" + e.getMessage());
        }
    }

    public static void main (String args[]) {
        Socket sock;
        MessageLibrary msgLib = new MessageLibrary();
        int id = 0;
        int portNo = 8080;
        try {
            if (args.length >= 1) {
                portNo = Integer.parseInt(args[1]);
            }
            if (portNo <= 1024) portNo=8080;
            ServerSocket serv = new ServerSocket(portNo);
            // accept client requests. For each request create a new thread to handle
            while (true) {
                System.out.println("Student server waiting for connects on port " + portNo);
                sock = serv.accept();
                System.out.println("Student server connected to client: " + id);
                MessageLibraryTCPJsonRPCServer myServerThread = new MessageLibraryTCPJsonRPCServer(sock, id++, msgLib);
                myServerThread.start();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
