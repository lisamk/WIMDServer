package wimd;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Server {

    private static final int PORT = 4031;
    private static final String SEPARATOR = "#";
    private static String[] locations = new String[2];
    private static int[] timestamps = new int[2];
    private static String[] macs = new String[2];
    private static PrintWriter[] writers = new PrintWriter[2];

    public static void main(String[] args) {
        System.out.println("The server is running.");
		ServerSocket socket;
		try {
			socket = new ServerSocket(PORT);
			while(true) {
				new Handler(socket.accept()).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
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
            try {
				socket.setSoTimeout(60000);
			} catch (SocketException e) {
				e.printStackTrace();
			}
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                String line = in.readLine();
                if(line!=null) {
	                String mac = line.split(SEPARATOR)[2];
	                
	                id = 0;
	                idPartner = 1;
	                if(writers[0]==null || macs[0].equals(mac)) {
	                	writers[0] = out;
	                	macs[0] = mac;
	                }
	                else if(writers[1]==null || macs[1].equals(mac)) {
	                	id = 1;
	                	idPartner = 0;
	                	writers[1] = out;
	                	macs[1] = mac;
	                }
	                else out.write("REFUSED"); //TODO 
	                
	                setFields(line);
	                writeFields();
                }
            } catch (IOException e) {
                return;
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
        	if(writers[id]!=null) {
	        	if(writers[idPartner]!=null) writers[id].println(locations[idPartner] + SEPARATOR + timestamps[idPartner]);
	        	else writers[id].println("Unkown" + SEPARATOR + Integer.MAX_VALUE);
        	}
		}

		private void setFields(String line) {
			System.out.println(line);
			String[] fields = line.split(SEPARATOR);
	        locations[id] = fields[0];
	        timestamps[id] = Integer.parseInt(fields[1]);
        }
    }
}