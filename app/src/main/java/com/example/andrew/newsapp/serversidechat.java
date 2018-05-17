package com.example.andrew.newsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

public class serversidechat extends AppCompatActivity {

    TextView info, infoip, msg;
    String message = "";
    ServerSocket serverSocket;
    EditText messagereply;
    String  messagereplyadmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serversidechat);

        info = (TextView) findViewById(R.id.info);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        messagereply = (EditText)findViewById(R.id.message) ;
        messagereplyadmin = messagereply.getText().toString();

        infoip.setText(getIpAddress());

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

        private class SocketServerThread extends Thread {

            static final int SocketServerPORT = 8080;
            int count = 0;
            String str;
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(SocketServerPORT);
                    serversidechat.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            info.setText("I'm waiting here: "
                                    + serverSocket.getLocalPort());
                        }
                    });

                    while (true) {
                        Socket socket = serverSocket.accept();
                        PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

                        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        while ((str = br.readLine()) != null) {
                            System.out.println("The message: " + str);

                            if (str.equals("bye")) {
                                pw.println("bye");
                                break;
                            } else {
                                str = "Server returns " + str;
                                pw.println(str);
                            }
                        }

                        count++;
                        message += "#" + count + " from " + socket.getInetAddress()
                                + ":" + socket.getPort() + "message" + str +  "\n";

                        serversidechat.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                msg.setText(message);
                            }
                        });

                        SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(
                                socket, count);
                        socketServerReplyThread.run();

                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        private class SocketServerReplyThread extends Thread {

            private Socket hostThreadSocket;
            int cnt;

            SocketServerReplyThread(Socket socket, int c) {
                hostThreadSocket = socket;
                cnt = c;
            }

            @Override
            public void run() {
                OutputStream outputStream;
                String msgReply = messagereplyadmin + cnt;

                try {
                    outputStream = hostThreadSocket.getOutputStream();
                    PrintStream printStream = new PrintStream(outputStream);
                    printStream.print(msgReply);
                    printStream.close();

                    message += "replayed: " + msgReply + "\n";

                    serversidechat.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            msg.setText(message);
                        }
                    });

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    message += "Something wrong! " + e.toString() + "\n";
                }

                serversidechat.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        msg.setText(message);
                    }
                });
            }

        }

        private String getIpAddress() {
            String ip = "";
            try {
                Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                        .getNetworkInterfaces();
                while (enumNetworkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = enumNetworkInterfaces
                            .nextElement();
                    Enumeration<InetAddress> enumInetAddress = networkInterface
                            .getInetAddresses();
                    while (enumInetAddress.hasMoreElements()) {
                        InetAddress inetAddress = enumInetAddress.nextElement();

                        if (inetAddress.isSiteLocalAddress()) {
                            ip += "SiteLocalAddress: "
                                    + inetAddress.getHostAddress() + "\n";
                        }

                    }

                }

            } catch (SocketException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                ip += "Something Wrong! " + e.toString() + "\n";
            }

            return ip;
        }
}
