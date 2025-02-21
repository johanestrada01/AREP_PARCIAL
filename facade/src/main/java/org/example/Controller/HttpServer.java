package org.example.Controller;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while(running) {
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            String[] data = null;
            boolean isFirstLine = true;
            while ((inputLine = in.readLine()) != null) {
                if(isFirstLine){
                    data = inputLine.split(" ");
                    isFirstLine = false;
                }
                System.out.println("Recibí: " + inputLine);
                if (!in.ready()) {
                    break;
                }
                if(inputLine.isEmpty()) break;
            }
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n"
                    + "<!DOCTYPE html>";
            if(data[1].equals("/")){
                outputLine += "<!DOCTYPE html>"
                        +"<html>"
                        +"<head>"
                        +"<title>Form Example</title>"
                        +"<meta charset=\"UTF-8\">"
                        +"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                        +"</head>"
                        +"<body>"
                        +"<div id=\"getrespmsg\"></div>"
                        +"<script>"
                        +"function loadGetMsg() {"
                        +"let nameVar = document.getElementById(\"name\").value;"
                        +"const xhttp = new XMLHttpRequest();"
                        +"xhttp.onload = function() {"
                        +"document.getElementById(\"getrespmsg\").innerHTML ="
                        +"this.responseText;"
                        +"}"
                        +"xhttp.open(\"GET\", \"/hello?name=\"+nameVar);"
                        +"xhttp.send();"
                        +"}"
                        +"</script>"

                        +"<h1>Comando</h1>"
                        +"<form action=\"/hellopost\">"
                        +"<label for=\"postname\">Comando:</label><br>"
                        +"<input type=\"text\" id=\"postname\" name=\"name\" value=\"\"><br><br>"
                        +"<input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">"
                        +"</form>"

                        +"<div id=\"postrespmsg\"></div>"

                        +"<script>"
                        +"function loadPostMsg(name){"
                        +"let url = \"/hellopost?name=\" + name.value;"

                        +"fetch (\"comand=\" + name.value, {method: 'GET'})"
                        +".then(x => x.text())"
                        +".then(y => document.getElementById(\"postrespmsg\").innerHTML = y);"
                        +"}"
                        +"</script>"
                        +"</body>"
                        +"</html>";
            }
            else{
                outputLine += HttpConnectionExample.consult(data[1]);
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
}