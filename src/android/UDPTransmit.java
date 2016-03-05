/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

// This plugin sends UDP packets.

 package edu.uic.udptransmit;

 import org.apache.cordova.CordovaPlugin;
 import org.apache.cordova.CallbackContext;

 import java.io.IOException;

 import java.net.DatagramPacket;
 import java.net.DatagramSocket;
 import java.net.NetworkInterface;
 import java.net.InterfaceAddress;
 import java.net.InetAddress;
 import java.net.SocketException;
 import java.net.UnknownHostException;

 import java.util.Enumeration;

 import android.util.Log;

 import org.json.JSONArray;
 import org.json.JSONException;

 public class UDPTransmit extends CordovaPlugin {

  DatagramPacket datagramPacket;
  DatagramSocket datagramSocket;
  boolean successInitializingTransmitter = false;
  
  // Constructor
  public UDPTransmit() {
  }

  // Handles and dispatches "exec" calls from the JS interface (udptransmit.js)
  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if("initialize".equals(action)) {
      final String host = args.getString(0);
      final int port = args.getInt(1);
                  // Run the UDP transmitter initialization on its own thread (just in case, see sendMessage comment)
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          UDPTransmit.this.initialize(host, port, callbackContext);
        }
      });
      return true;
    }
    else if("sendMessage".equals(action)) {
      final String message = args.getString(0);
                      // Run the UDP transmission on its own thread (it fails on some Android environments if run on the same thread)
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          UDPTransmit.this.sendMessage(message, callbackContext);
        }
      });
      return true;
    }
    else if("resolveHostName".equals(action)) {
      final String url = args.getString(0);
                      // Run the UDP transmission on its own thread (it fails on some Android environments if run on the same thread)
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          UDPTransmit.this.resolveHostName(url, callbackContext);
        }
      });
      return true;
    }
    else if("enableBroadcast".equals(action)) {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          UDPTransmit.this.enableBroadcast(callbackContext);
        }
      });
      return true;
    }
    else if("onReceiveMessage".equals(action)) {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          UDPTransmit.this.onReceiveMessage(callbackContext);
        }
      });
      return true;
    }
    else if("close".equals(action)) {
      cordova.getThreadPool().execute(new Runnable() {
        public void run() {
          UDPTransmit.this.close(callbackContext);
        }
      });
      return true;
    }
    return false;
  }

  private void initialize(String host, int port, CallbackContext callbackContext) {
    boolean successResolvingIPAddress = false;
            // create packet
    InetAddress address = null;
    try {
                    // 'host' can be a ddd.ddd.ddd.ddd or named URL, so doesn't always resolve
      address = InetAddress.getByName(host);
      successResolvingIPAddress = true;
    } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
      e.printStackTrace();
    }               
            // If we were able to resolve the IP address from the host name, we're good to try to initialize
    if (successResolvingIPAddress) {
      byte[] bytes= new byte[0];
      datagramPacket = new DatagramPacket(bytes, 0, address, port);
                    // create socket
      try {
        datagramSocket = new DatagramSocket();
        successInitializingTransmitter = true;

      } catch (SocketException e) {
                            // TODO Auto-generated catch block
        e.printStackTrace();
      }      
    }  
    if (successInitializingTransmitter)
      callbackContext.success("Success initializing UDP transmitter using datagram socket: " + datagramSocket);
    else
      callbackContext.error("Error initializing UDP transmitter using datagram socket: " + datagramSocket);                 
  }

  private void sendMessage(String data, CallbackContext callbackContext) {
    boolean messageSent = false;
                                // Only attempt to send a packet if the transmitter initialization was successful
    if (successInitializingTransmitter) {
      byte[] bytes = data.getBytes();
      datagramPacket.setData(bytes);
      try {
        datagramSocket.send(datagramPacket);
        messageSent = true;
      } catch (IOException e) {
                                                // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    if (messageSent)
      callbackContext.success("Success transmitting UDP packet: " + datagramPacket);
    else
      callbackContext.error("Error transmitting UDP packet: " + datagramPacket);                        
  }

  private void resolveHostName(String url, CallbackContext callbackContext) {
    boolean hostNameResolved = false;
    InetAddress address = null;
    try {
                        // 'host' can be a ddd.ddd.ddd.ddd or named URL, so doesn't always resolve
      address = InetAddress.getByName(url);
      hostNameResolved = true;
    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
      e.printStackTrace();
    }               
    if (hostNameResolved)
      callbackContext.success(address.getHostAddress());
    else
      callbackContext.error("Error resolving host name: " + url);                       
  }

  private void onReceiveMessage(CallbackContext callbackContext) {
    byte[] recvBuf = new byte[15000];
    DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);

    Log.i("UDPTransmit", "Ping!!!");
    try {
      datagramSocket.receive(receivePacket);
      String serverMessage = new String(receivePacket.getData()).trim();
      String ip = receivePacket.getAddress().toString();
      Log.i("UDPTransmit", "Received " + serverMessage);
        callbackContext.success(ip + ":" + serverMessage);
    }
    catch (IOException ex) {
      callbackContext.error(ex.getMessage());
    }
  }

  private void close(CallbackContext callbackContext) {
    datagramSocket.close();
    callbackContext.success("done");
  }

  private void enableBroadcast(CallbackContext callbackContext) {
    try {
      datagramSocket.setBroadcast(true);
      callbackContext.success("done");

      /*
      byte[] bytes = message.getBytes();
      datagramPacket.setData(bytes);
      datagramPacket.setLength(bytes.length);
      datagramPacket.setAddress(InetAddress.getByName("255.255.255.255"));
      datagramSocket.send(sendPacket);
      callbackContext.success("done");
      */

      // Broadcast the message over all the network interfaces
      /*
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface networkInterface = interfaces.nextElement();

        if (networkInterface.isLoopback() || !networkInterface.isUp()) {

          continue; // Don't want to broadcast to the loopback interface
        }
        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
          InetAddress broadcast = interfaceAddress.getBroadcast();
          if (broadcast == null) {
            continue;
          }

          // Send the broadcast package!
          try {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, port);
            c.send(sendPacket);
          } catch (Exception e) {}

        }
      }*/

    } catch (IOException ex) {
      callbackContext.error(ex.getMessage());
    }
  }
}
