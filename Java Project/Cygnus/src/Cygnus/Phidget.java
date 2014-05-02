package Cygnus;

import com.phidgets.*;
import java.util.ArrayList;
import jssc.SerialPortException;

class Phidget implements Runnable {

	Thread t;
	private ArrayList<Motor> motors;
	private ArrayList<Sensor> sensors;
	private UART comms;
	private InterfaceKitPhidget phidget;
	
	public Phidget(ArrayList<Sensor> sensors, ArrayList<Motor> motors, UART comms, InterfaceKitPhidget phidget) throws PhidgetException {
		this.sensors = sensors;
		this.motors = motors;
		this.comms = comms;
		this.phidget = phidget;
		t = new Thread(this, "Sensor Thread");
		t.start();
	}
	
	@Override
	public void run() {
		while (true) {
			for(int i = 0; i < this.sensors.size(); i++) {
				try {
					if(sensors.get(i).pollSensor(phidget) == -1) {
						for (int j = 0; j < motors.size(); j++) {
							try {
								comms.sendString(motors.get(j).Stop());
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (SerialPortException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (PhidgetException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}