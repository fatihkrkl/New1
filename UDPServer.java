package Comp3334Project;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class UDPServer {
    private DatagramSocket socket;
    public UDPServer(int port) throws IOException {
        this.socket = new DatagramSocket(port);
    }
    public void start() throws IOException, InterruptedException {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        while (true) {
            socket.receive(receivePacket);
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            byte[] requestData = receivePacket.getData();
            int requestDataLength = receivePacket.getLength();
            // Process the request
            String request = new String(requestData, 0, requestDataLength);
            String response = processRequest(request, clientAddress, clientPort);
            // Send the response
            byte[] responseData = response.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,
                    receivePacket.getAddress(), receivePacket.getPort());
            socket.send(responsePacket);
        }
    }
    private String processRequest(String request, InetAddress clientAddress, int clientPort) throws IOException, InterruptedException {
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        String sentence;
        if (request.substring(0, 1).equals("D")) {
            sendData = "Enter Server Directory Path(for example:                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   c:\\test): ".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            socket.send(sendPacket);
            DatagramPacket receiveAnswerPacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receiveAnswerPacket);
            sentence = new String(request).trim();
            System.out.println(sentence.length());
            System.out.println("Request from client: " + sentence);
            Path path= Path.of(sentence);
            System.out.println(Files.exists(path));
            File directory = new File(sentence);
            File[] files = directory.listFiles();
            if (files == null || files.length == 0) {
                return "Server directory is empty.";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Server directory contents:\n");
            for (File file : files) {
                sb.append(file.getName()).append("\n");
            }
            return sb.toString();
        } else if (request.substring(0, 1).equals("F")) {
            sendData = "Enter The Directory Path of File That Will Be Transferred: ".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            socket.send(sendPacket);
            DatagramPacket receiveAnswerPacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receiveAnswerPacket);
            sentence  = new String(request).trim();
            System.out.println("Request from client: " + sentence);
            String FILE_TO_TRANSFER= sentence;
            Path filePath = Paths.get(FILE_TO_TRANSFER);
            if (!Files.exists(filePath)) {
                return "File not found on the server.";
            }
            byte[] fileBytes = Files.readAllBytes(filePath);
            return new String(fileBytes).trim();
        } else if (request.substring(0, 1).equals("C")) {
            sendData = "Enter The Duration: ".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            socket.send(sendPacket);
            DatagramPacket receiveAnswerPacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receiveAnswerPacket);
            sentence  = new String(request);
            String[] latValues = sentence.split(" ");
            float sum = 0;
            for (int i = 0; i < latValues.length; i++) {              
                if (!latValues[i].equals("null"))
                    sum = sum + Float.valueOf(latValues[i].trim()).floatValue();
            }
            float Answerrequest=sum*1000;
            float COMPUTATION_DURATION_MS=(Answerrequest);
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < COMPUTATION_DURATION_MS) {
                // Keep the server busy with computation
            }
            return "Computation completed.";
        } else if (request.substring(0, 1).equals("S")) {
            sendData = "Enter Video File Directory Path: ".getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            socket.send(sendPacket);
            DatagramPacket receiveAnswerPacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receiveAnswerPacket);
            sentence  = new String(request).trim();
            System.out.println("Request from client: " + sentence);
            String VIDEO_FILE= sentence;
            int VIDEO_CHUNK_SIZE = 1024;
            Path videoPath = Paths.get(VIDEO_FILE);
            if (!Files.exists(videoPath)) {
                return "Video file not found on the server";
            }
            byte[] buffer = new byte[VIDEO_CHUNK_SIZE];
            try (InputStream inputStream = Files.newInputStream(videoPath)) {
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    DatagramPacket packet = new DatagramPacket(buffer, bytesRead, clientAddress, clientPort);
                    socket.send(packet);
                    Thread.sleep(1000); // Adjust the delay as needed for desired video streaming rate
                }
            }
        } else {
            return "Invalid request.";
        }
        return "Response to: " + request;
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            UDPServer server = new UDPServer(8000);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}