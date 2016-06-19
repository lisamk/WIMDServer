package wimd;

import java.net.*;

import java.io.*;

class Server {

    private String mac1;
    private String mac2;

    private int ts1;
    private int ts2;

    private String location1;
    private String location2;

    private int id;

    private ServerSocket serverSocket = null;
    protected static final String SEPARATOR = "#";
    private boolean running = true; 

    public Server() {
        try {
            serverSocket = new ServerSocket(4031);
            
            System.out.println("Server started.");
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
						Thread.sleep(7200000); // Server is 2h online
						running = false;
					} catch (InterruptedException e) {
						System.out.println("Server is shutting down...");
						return;
					}
					
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while(running) {
                            Socket socket = serverSocket.accept();
                            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
                            BufferedReader br = new BufferedReader(isr);

                            setFields(br.readLine());

                            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                            if(id==0) pw.println(location2 + SEPARATOR + ts2);
                            else if(id==1) pw.println(location1 + SEPARATOR + ts1);
                            pw.close();
                        }
                        System.out.println("Server is shutting down...");
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void setFields(String line) {
    	System.out.println("Data received: " + line);
    	
        String[] fields = line.split(SEPARATOR);

        id = 0;
        if(mac1==null) mac1 = fields[0];
        else if(!mac1.equals(fields[0])) {
            id = 1;
            if(mac2==null) mac2 = fields[0];
            else if(!mac2.equals(fields[0])) {
                System.err.print("Too many connections");
                return;
            }
        }

        if(id==0) {
            ts1 = Integer.parseInt(fields[1]);
            location1 = fields[1];
        }
        else {
            ts2 = Integer.parseInt(fields[1]);
            location2 = fields[1];
        }
    }

    public static void main(String[] args) {
		new Server();
	}
    
    
}
