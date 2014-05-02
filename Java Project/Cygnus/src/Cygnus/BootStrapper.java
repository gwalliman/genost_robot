package Cygnus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import com.phidgets.PhidgetException;
import robotinterpreter.RobotInterpreter;
import jssc.SerialPortException;
import org.tempuri.Service;
import org.tempuri.IService;

public class BootStrapper implements Runnable {

    static Thread t;
    static RobotInterpreter r;

    public static void main(String args[]) throws InterruptedException, SerialPortException, IOException, PhidgetException {
        System.out.println("Beginning Execution");
        Service robotService = new Service();
        IService robotServiceInterface = robotService.getBasicHttpBindingIService();

        ConfigurationModule config = new ConfigurationModule();
        r = new RobotInterpreter();

        r.addRobotListener(config);
        String code = "";
        t = new Thread(new BootStrapper(), "Network Thread");
        boolean isRunning = false;

        System.out.println("Pre loop");
        while(true) {
            r.load(robotServiceInterface.getCode());
            code = robotServiceInterface.getCode();
            System.out.println(code);
            if(robotServiceInterface.getStatus().equals("run") && !isRunning)
            {
                isRunning = true;
                if(t.isAlive()) {
                    t = null;
                    t = new Thread(new BootStrapper(), "Network Thread");
                }
                t.start();
            }
            if(!robotServiceInterface.getStatus().equals("run"))
            {
                isRunning = false;
                r.load("{\n"
                        + "	method stop ();\n"
                        + "}");
                r.execute();
            }
            Thread.sleep(1000);
        }
    }

    static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    @Override
    public void run() {
        r.execute();
    }

}

