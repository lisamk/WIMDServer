package wimd;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Server {

    private static final int PORT = 4031;
    private static final String SEPARATOR = "#";
    private static String[] locations = new String[2];
    private static int[] timestamps = new int[2];
    private static PrintWriter[] writers = new PrintWriter[2];

    public static void main(String[] args) throws Exception {
        System.out.println("The server is running.");
        ServerSocket socket = new ServerSocket(PORT);
        try {
            while(true) {
                new Handler(socket.accept()).start();
            }
        } finally {
        	socket.close();
        }
    }

    private static class Handler extends Thread {
        private int id;
        private int idPartner;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                id = 0;
                idPartner = 1;
                if(writers[0]==null) writers[0] = out;
                else if(writers[1]==null) {
                	id = 1;
                	idPartner = 0;
                	writers[1] = out;
                }
                else out.write("REFUSED"); //TODO 
                
                setFields(in.readLine());
                writeFields();

                while(true) {
                	setFields(in.readLine());
                	writeFields();
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
            	writers[id] = null;
                locations[id] = null;
                timestamps[id] = 0;
                try {
                    socket.close();
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }
        }
        
        private void writeFields() {
        	if(writers[idPartner]!=null) writers[id].println(locations[idPartner] + SEPARATOR + timestamps[idPartner]);
        	else writers[id].println("Unkown" + SEPARATOR + Integer.MAX_VALUE);
		}

		private void setFields(String line) {
			System.out.println(line);
			String[] fields = line.split(SEPARATOR);
	        locations[id] = fields[0];
	        timestamps[id] = Integer.parseInt(fields[1]);
        }
    }
}