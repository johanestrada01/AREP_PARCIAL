package org.example;

import netscape.javascript.JSObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;


public class HttpServer {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            String[] data = null;
            boolean isFirstLine = true;
            while ((inputLine = in.readLine()) != null) {
                if (isFirstLine) {
                    data = inputLine.split(" ");
                    isFirstLine = false;
                }
                System.out.println("Recibí: " + inputLine);
                if (!in.ready()) {
                    break;
                }
                if (inputLine.isEmpty()) break;
            }
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n";
            String execution = data[1].replace("/", "").split("\\(")[0].replace("comand=", "");
            if(execution.equals("Class")){
                outputLine+=executionClass(data[1].split("\\[")[1].split("]")[0]);
            } else if (execution.equals("invoke")) {
                    String className = data[1].split(",")[0].split("\\[")[0].replace("//comand=invoke(", "");
                    String method = data[1].split(",")[1].replace(")", "");
                    outputLine += executionMethod(className, method);
            } else if (execution.equals("unaryInvoke")) {
                String className = data[1].split(",")[0].split("\\[")[0].replace("//comand=invoke(", "");
                String method = data[1].split(",")[1];
                String type = data[1].split(",")[2];
                String param = data[1].split(",")[3].replace(")", "");
                System.out.println(param);
            }
            System.out.println(outputLine);
            out.println(outputLine);
            clientSocket.close();
            out.close();
            in.close();
        }
    }

    public static String executionClass(String className) throws ClassNotFoundException {
        System.out.println(className);
        StringBuilder response = new StringBuilder("");
        Method[] methods = Class.forName(className).getDeclaredMethods();
        for(Method m : methods){
            response.append(m.getName() + " ");
        }
        response.append("\n");
        Field[] fields = Class.forName(className).getDeclaredFields();
        for(Field f : fields){
            response.append(f.getName()).append(" ");
        }
        return response.toString();
    }

    public static String executionMethod(String className, String methodName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class c = Class.forName(className);
        System.out.println(methodName);
        String result = String.valueOf(c.getMethod(methodName).invoke(null));
        return result;
    }

    public static String executionMethod(String className, String methodName, String type, String param) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class c = Class.forName(className);
        System.out.println(methodName);
        String[] params = new String[1];
        params[1] = type;
        //String result = String.valueOf(c.getMethod().invoke(null));
        return "";
    }

}
