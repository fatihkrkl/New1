Readme

1.We used round robin strategy for static and least connection strategy for dynamic load balancing.

2.We used udp for our protocol.

3.We have faced the challenge of setting the servers

4.We have not tested the project.

5.The projects runs as follows; the load balancer opens 3 static and 3 dynamic servers and uses a virtual ip
 to send the load balancer a request and the project choses which server to use with the strategy given above
 depending on the function, it has 4 functions that are as follows;

A. Directory listing (what content that server has: this is likely a fast request )

B. File transfer (transferring of a chosen file from server to client who is interested in: this could be fast
or slow depending on the file size)

C. Computation (keeping server busy: this will be medium to slow)

D. Video streaming (constant bit rate for a given time)
the user has to type in one the letters D, F ,C, S to chose from these functions then the input is sent to the 
server and if the user wrote the right input the server asks a question and re-sends it to the client and the 
client answers the question and sends it back to the server server completes the funtion and send a completed message.