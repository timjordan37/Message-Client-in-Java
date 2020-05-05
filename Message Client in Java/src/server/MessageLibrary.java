package ser321.assign6.tsjorda1.server;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.rmi.server.*;
import java.rmi.*;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONTokener;

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

public class MessageLibrary extends Object implements MsgServer {
    ArrayList<Message> messages;
    ArrayList<JSONObject> json = new ArrayList<>();
    JSONObject obj;
    JSONArray arr;

    public MessageLibrary() {
        messages = new ArrayList<>();
        loadJSON();
        int i = 0;
        while (i < json.size()) {
            Message m = new Message(json.get(i));
            messages.add(m);
            i++;
        }
    }

    public boolean addMessage(Message m) {
        messages.add(m);
	    JSONObject addObj = m.getJSON();
        System.out.println("Adding message to: " + addObj.get("to"));
	    arr.put(addObj);
        writeFile();
        return true;
    }

    public boolean loadJSON() {
        try {
            String data = new String(Files.readAllBytes(Paths.get("messages.json")));
            arr = new JSONArray(data);
            int i = 0;

            while (i < arr.length()) {
                obj = arr.getJSONObject(i);
                json.add(obj);
                i++;
            }
            return true;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public String getHeader(Message msg) {
        return msg.getFrom() + "  " + msg.getDate();
    }

    public Message getMessageFromIndex(int index) {
        return messages.get(index);
    }

    // getMessageFromHeaders returns a string array of message headers being sent to toAUserName.
    // Headers returned are of the form: (from user name @ server and message date)
    // e.g., a message from J Buffett with header: Jimmy.Buffet  Tue 18 Dec 5:32:29 2018
    public String[] getMessageFromHeaders(String toAUserName) {
        String ret[] = new String[messages.size()];
        int i = 0;
        int j = 0;
        while (i < messages.size()) {
            Message m = messages.get(i);
            if (m.getTo().equals(toAUserName)) {
                ret[j] = m.getFrom() + "  " + m.getDate();
                j++;
            }
            i++;
        }

        return ret;
    }

    // getMessage returns the Message having the corresponding header. Assume headers are unique.
    // As above, the header has includes (from user name - server and message date)
    public Message getMessage(String header) {
        ArrayList<String> headers = new ArrayList<>();
        int i = 0;
        while (i < messages.size()) {
            Message m = messages.get(i);
            headers.add(m.getFrom() + "  " + m.getDate());
            i++;
        }

        i = 0;
        Message ret = null;
        while (i < messages.size()) {
            if (headers.get(i).equals(header)) {
                ret = messages.get(i);
            }
            i++;
        }
        return ret;
    }

    // deletes the message having the header (from user name - server and message date)
    public boolean deleteMessage(String header, String toAUserName) {
        ArrayList<String> headers = new ArrayList<>();
        int i = 0;
        while (i < messages.size()) {
            Message m = messages.get(i);
            headers.add(m.getFrom() + "  " + m.getDate());
            i++;
        }

        i = 0;
        while (i < messages.size()) {
            if (headers.get(i).equals(header) && toAUserName.equals(messages.get(i).getTo())) {
                messages.remove(messages.get(i));
                arr = new JSONArray(messages);
                writeFile();
                return true;
            }
            i++;
        }
        return false;
    }

    public boolean writeFile() {
        try {
            FileWriter file = new FileWriter("messages.json");
            arr.write(file);
            file.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }
}
