package Comp3334Project;
import java.io.IOException;
import java.net.*;
public class UDPClient {
    private DatagramSocket clientSocket;
    private InetAddress serverAddress;
    private int serverPort;
    public UDPClient(String serverAddress, int serverPort) throws SocketException, UnknownHostException {
        this.clientSocket = new DatagramSocket();
        this.serverAddress = InetAddress.getByName(serverAddress);
        this.serverPort = serverPort;
    }
    public String sendRequest(String request) throws IOException {
        byte[] requestData = request.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(requestData, requestData.length, serverAddress, serverPort);
        clientSocket.send(sendPacket);
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        clientSocket.receive(receivePacket);
        byte[] responseData = receivePacket.getData();
        int responseDataLength = receivePacket.getLength();
        return new String(responseData, 0, responseDataLength);
    }
    public void close() {
        clientSocket.close();
    }
    public static void main(String[] args) {
        try {
            UDPClient client = new UDPClient("clientVip", 9000);
            String request = "Hello, server!";
            String response = client.sendRequest(request);
            System.out.println("Response: " + response);

            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}