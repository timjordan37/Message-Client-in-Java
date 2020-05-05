package ser321.assign6.tsjorda1.client;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.*;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.json.JSONArray;

import org.json.JSONObject;
import ser321.assign2.lindquis.client.*;
import ser321.assign6.tsjorda1.server.*;

/*
 * Copyright 2019 Tim Jordan,
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
 * Purpose: This code is the controller for the MessageGui, demonstrating an
 * email client. Much of this code comes from SampleClient.java by Lindquist,
 * with some modifications to make it work with new purpose.
 *
 * This problem set uses a swing user interface to implement (secure) messaging.
 * Messages are communicated to/from message clients, via a common well-known.
 * server.
 * Messages can be sent in clear text, or using password based encryption
 * (last assignment). For secure messages, the message receiver must enter
 * the password (encrypted).
 * The Message tab has two panes. left pane contains a JList of messages
 * for the user. The right pane is a JTextArea, which can display the
 * contents of a selected message. This pane is also used to compose
 * messages that are to be sent.
 *
 * Ser321 Principles of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @author Tim Jordan, tsjorda1@asu.edu
 *         Modified from code by Tim Lindquist
 * @version October 2019
 */
public class Controller extends MessageGui
                           implements ActionListener, ListSelectionListener {

   private String user;   // originator of all message sent by this client.
   //private String serverHostPort; // such as lindquisrpi.local:8080
   private static final String patt = "EEE MMM d K:mm:ss yyyy";
   //private MessageLibrary messageLibrary;
   //MsgServer server;
   String host = "localhost";
   String port = "8080";
   String userId = "";
   ControllerTCPProxy sc;

   public Controller(String hostId, String regPort, String user) {
      super("Tim Jordan", user);

      try{
         host = hostId;
         port = regPort;
         userId = user;
         String url = "http://"+host+":"+port+"/";
         System.out.println("Opening connection to: " + url);
      }catch (Exception ex){
         ex.printStackTrace();
      }

      sc = (ControllerTCPProxy)new ControllerTCPProxy(host, Integer.parseInt(port));

      // add this object as an action listener for all menu items.
      for(int j=0; j<userMenuItems.length; j++){
         for(int i=0; i<userMenuItems[j].length; i++){
            userMenuItems[j][i].addActionListener(this);
         }
      }

      // add this object as an action listener for the view buttons
      deleteJB.addActionListener(this);
      replyJB.addActionListener(this);
      sendTextJB.addActionListener(this);
      sendCipherJB.addActionListener(this);

      // listen for the user to select a row in the list of messages.
      // When a selection is made, the method valueChanged will be called.
      messageListJL.addListSelectionListener(this);

      populateList();

      setVisible(true);
   }

   public void populateList() {
      String[] headers = sc.getMessageFromHeaders(userId);
      
      DefaultListModel<String> messageListModel = new DefaultListModel<>();

      for (int i = 0; i < headers.length; i++) {
         messageListModel.addElement(headers[i]);
      }
      messageListJL.setModel(messageListModel);
      messageListJL.setSelectedIndex(0);
   }

   public void valueChanged(ListSelectionEvent e) {
      // If you do something (in here or anywhere) that takes lots of time, you may want
      // to change the cursor to hourglass (waitcursor) while you're doing it. See
      // the setCursor calls in the actionPerformed method. Really, though you
      // should use a javax.swing.SwingWorker class to perform any long-lasting
      // operations in the background, so the UI is not frozen while a network
      // or other long lasting operation is performed.

      // the call to getvalueisadjusting determines whether we're being called
      // for the last in a sequence of related (event-generating) user actions.
      // we generally don't want to redo handling multiple times.
      if (!e.getValueIsAdjusting()){
         int selected = messageListJL.getSelectedIndex();
         if (selected < 0) {
            selected = 0;
            messageListJL.setSelectedIndex(0);
         }
         System.out.println(selected);
         Message current = null;
         current = sc.getMessageFromIndex(selected);
         
         messageContentJTA.setText(current.getContent());
         messageStatusJTA.setText(current.getStatus());
         fromJTF.setText(current.getFrom());
         toJTF.setText(current.getTo());
         subjectJTF.setText(current.getSubject());
         dateJTF.setText(current.getDate());
         //messageContentJTA.setText(" You selected messageList item: "
                                   //+ messageListJL.getSelectedIndex());
         System.out.println("You selected messageList item: "
                            + messageListJL.getSelectedIndex());
      }
   }

   public void actionPerformed(ActionEvent e) {
      // the actions in this method do NOT reflect what has to be done in handling
      // these different actions, but are designed to demonstrate how to access the
      // view objects.

      // If you do something (in here or anywhere) that takes lots of time, you may want
      // to change the cursor to an hourglass (waitcursor) while you're doing it. Really
      // though, you should use a javax.swing.SwingWorker class to perform any long-lasting
      // operations in the background, so the UI is not frozen while a network
      // or other long lasting operation is performed. Users don't like non-responsive apps.
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      if(e.getActionCommand().equals("Exit")) {
         System.exit(0);
      }else if(e.getActionCommand().equals("Reply")) {
         DefaultListModel<String> dlm = (DefaultListModel<String>)messageListJL.getModel();
         int selected = messageListJL.getSelectedIndex();
         Message current = null;
        
         current = sc.getMessageFromIndex(selected);

         Date today = new Date();
         SimpleDateFormat form = new SimpleDateFormat(patt);
         String todayStr = form.format(today);
         
         fromJTF.setText(current.getTo());
         toJTF.setText(current.getFrom());
         subjectJTF.setText("Re: " + current.getSubject());
         dateJTF.setText(todayStr);
         messageContentJTA.setText("\n\n" + current.getContent());
         messageStatusJTA.setText(" *(Insecure, sent as clear text.)*");
      }else if(e.getActionCommand().equals("Delete")) {
         DefaultListModel dlm = (DefaultListModel)messageListJL.getModel();
         int selected = messageListJL.getSelectedIndex();
         Message current = null;
         
         current = sc.getMessageFromIndex(selected);

         String fromNDateStr = selected>-1 ? (String)dlm.getElementAt(selected) : "no selection";
         System.out.println("request to delete message index: "+selected+" text: "+fromNDateStr);
         if(selected>-1){
            dlm.removeElementAt(selected);
            selected++;
            messageListJL.setSelectedIndex(selected);
         }
         //dlm.clear(); //use this to clear the entire list.
         fromJTF.setText(user);
         toJTF.setText("");
         subjectJTF.setText("");
         messageContentJTA.setText("");
         messageStatusJTA.setText("");
         
         String header = sc.getHeader(current);
         sc.deleteMessage(header, "tsjorda1");

         System.out.println("Delete from messageList success.");
      } else if (e.getActionCommand().equals("Send Text")) {
         JSONObject obj = new JSONObject();

         obj.put("to", toJTF.getText());
         obj.put("from", fromJTF.getText());
         obj.put("subject", subjectJTF.getText());
         obj.put("date", dateJTF.getText());
         obj.put("content", messageContentJTA.getText());
         obj.put("status", messageStatusJTA.getText());

         Message m = new Message(obj);

	     sc.addMessage(m);
      }

      // get rid of the waiting cursor
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }

   public static void main(String args[]) {
      String host = "localhost";
      String port = "8080";
      String userId = "";

      try {
         if (args.length >= 3) {
            host = args[0];
            port = args[1];
            userId = args[2];
         }
      } catch(Exception e) {
         System.out.println("Exception in Controller: " + e.getMessage());
      }
      Controller controller = new Controller(host, port, userId);
   }
}
