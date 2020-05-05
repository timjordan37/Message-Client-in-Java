package ser321.assign6.tsjorda1.server;
import org.json.JSONArray;

import java.rmi.*;
import java.util.ArrayList;

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

public interface MsgServer {
    public Message getMessage(String header) throws RemoteException;
    public String[] getMessageFromHeaders(String toAUserName) throws RemoteException;
    public Message getMessageFromIndex(int index) throws RemoteException;
    public String getHeader(Message msg) throws RemoteException;
    public boolean deleteMessage(String header, String toAUserName) throws RemoteException;
    public boolean addMessage(Message m) throws RemoteException;
    public boolean loadJSON() throws RemoteException;
    public boolean writeFile() throws RemoteException;
}
