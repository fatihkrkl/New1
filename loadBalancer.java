package Comp3334Project;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class loadBalancer {
    public final InetAddress clientVip;
    private List<InetAddress> staticServerAddresses;
    private List<InetAddress> dynamicServerAddresses;
    private Map<InetAddress, Integer> serverConnections;
    private DatagramSocket clientSocket;
    public loadBalancer(List<InetAddress> staticServerAddresses, List<InetAddress> dynamicServerAddresses, int clientPort) throws SocketException, UnknownHostException {
        clientVip = InetAddress.getByName("clientVip");
        this.staticServerAddresses = staticServerAddresses;
        this.dynamicServerAddresses = dynamicServerAddresses;
        this.serverConnections = new HashMap<>();
        this.clientSocket = new DatagramSocket(clientPort);
    }
    public void start() throws IOException {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        InetAddress dynamicAdress = dynamicAlgo();
        InetAddress staticAddress = staticAlgo();
        InetAddress serverAddress = dynamicAdress;
        int i=0;
        while (true) {
            i++;
            if(i%2==0){
                serverAddress = dynamicAlgo();
                int currentConnections = serverConnections.getOrDefault(serverAddress, 0);
                serverConnections.put(serverAddress, currentConnections + 1);
                serverConnections.put(serverAddress, currentConnections);
            } else{
                serverAddress = staticAlgo();
            }
            clientSocket.receive(receivePacket);
            int clientPort = receivePacket.getPort();
            byte[] requestData = receivePacket.getData();
            int requestDataLength = receivePacket.getLength();
            forwardRequest(serverAddress, requestData, requestDataLength);
            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            clientSocket.receive(responsePacket);
            DatagramPacket responseToClientPacket = new DatagramPacket(responseBuffer, responsePacket.getLength(),
                    clientVip, clientPort);
            clientSocket.send(responseToClientPacket);
            clientSocket.receive(receivePacket);
            requestData = receivePacket.getData();
            requestDataLength = receivePacket.getLength();
            forwardRequest(serverAddress, requestData, requestDataLength);
            responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            clientSocket.receive(responsePacket);
            responseToClientPacket = new DatagramPacket(responseBuffer, responsePacket.getLength(),
                    clientVip, clientPort);
            clientSocket.send(responseToClientPacket);
        }
    }
    private InetAddress staticAlgo() {
        int serverIndex = (int) (Math.random() * staticServerAddresses.size());
        return staticServerAddresses.get(serverIndex);
    }
    private InetAddress dynamicAlgo(){
        InetAddress leastConnectionServer = null;
        int leastConnections = Integer.MAX_VALUE;
        for (InetAddress serverAddress : dynamicServerAddresses) {
            int connections = serverConnections.getOrDefault(serverAddress, 0);
            if (connections < leastConnections) {
                leastConnections = connections;
                leastConnectionServer = serverAddress;
            }
        }
        // In case of any failure, return the first server
        if (leastConnectionServer == null) {
            leastConnectionServer = dynamicServerAddresses.get(0);
        }

        return leastConnectionServer;
    }
    private void forwardRequest(InetAddress serverAddress, byte[] data, int dataLength) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(data, dataLength, serverAddress, 8000);
        clientSocket.send(sendPacket);
    }
    public static void main(String[] args) {
        try {
            List<InetAddress> stserverAddresses = new ArrayList<>();
            stserverAddresses.add(InetAddress.getByName("deneme1"));
            stserverAddresses.add(InetAddress.getByName("deneme2"));
            stserverAddresses.add(InetAddress.getByName("deneme3"));
            
            List<InetAddress> dyserverAddresses = new ArrayList<>();
            dyserverAddresses.add(InetAddress.getByName("deneme4"));
            dyserverAddresses.add(InetAddress.getByName("deneme5"));
            dyserverAddresses.add(InetAddress.getByName("deneme6"));
            loadBalancer loadBalancer = new loadBalancer(stserverAddresses, dyserverAddresses, 9000);
            for (InetAddress serverAddress : dyserverAddresses) {
                loadBalancer.serverConnections.put(serverAddress, 0);
            }
            loadBalancer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}