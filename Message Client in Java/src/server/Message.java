package ser321.assign6.tsjorda1.server;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.Serializable;

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
 * Purpose: This class supports a data structure for a Message in an email client.
 * The client works with JSON files to create and display messages.
 *
 * Ser321 Principles of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 *
 * @author Tim Jordan, tsjorda1@asu.edu
 * @version October 2019
 */

public class Message implements Serializable {
    private String to;
    private String from;
    private String subject;
    private String date;
    private String content;
    private String status;
    private transient JSONObject obj;
    //private String name;

    public Message(JSONObject object) {
        try {
            obj = object;
            to = obj.getString("to");
            from = obj.getString("from");
            subject = obj.getString("subject");
            date = obj.getString("date");
            content = obj.getString("content");
            status = obj.getString("status");
           
        } catch (Exception e) {
            System.out.println("Exception importing from JSON: " + e.getMessage());
        }
    }
    public Message(String aTo, String aFrom, String aSubject, String aDate, String aContent, String aStatus) {
        to = aTo;
        from = aFrom;
        subject = aSubject;
        date = aDate;
        content = aContent;
        status = aStatus;
    }

    public JSONObject getJSON() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("to", to);
            jo.put("from", from);
            jo.put("subject", subject);
            jo.put("date", date);
            jo.put("content", content);
            jo.put("status", status);
        } catch (Exception ex) {
            System.out.println(this.getClass().getSimpleName() + ": error converting to json");
        }

        return jo;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

    public void setTo(String to) {
        this.to = to;
        obj.put("to", to);
    }

    public void setFrom(String from) {
        this.from = from;
        obj.put("from", from);
    }

    public void setSubject(String subject) {
        this.subject = subject;
        obj.put("subject", subject);
    }

    public void setDate(String date) {
        this.date = date;
        obj.put("date", date);
    }

    public void setContent(String content) {
        this.content = content;
        obj.put("content", content);
    }

    public void setStatus(String status) {
        this.status = status;
        obj.put("status", status);
    }
}
