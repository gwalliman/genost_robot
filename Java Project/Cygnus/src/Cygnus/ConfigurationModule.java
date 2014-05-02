package Cygnus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
/*import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;*/
import java.util.Scanner;
import java.util.ArrayList;
import com.phidgets.*;

import jssc.SerialPortException;
import robotinterpreter.RobotListener;

public class ConfigurationModule implements RobotListener {

	private static ArrayList<Motor> motors = new ArrayList<Motor>();
	private static ArrayList<Sensor> sensors = new ArrayList<Sensor>();
	private static UART comms = new UART();
	private static InterfaceKitPhidget phidget;

	ConfigurationModule() throws InterruptedException, SerialPortException, PhidgetException {
		initConfig();
	}	

	public static void test() throws InterruptedException, SerialPortException, PhidgetException {
		ConfigurationModule config = new ConfigurationModule();
	}


	// ******************************************** FILE I/O SECTION
	// ***********************************************************
	// TODO Corey will complete the fileIO section
	/***************************************************
	 * Constructor for creating from file
	 ***************************************************/
	ConfigurationModule(String filepath) throws IOException {
		Scanner scan = new Scanner(new File(filepath)); // delimiter is newline
														// char
		boolean motor = false;
		boolean sensor = false;

		// parse line by line
		while (scan.hasNext()) // still has lines to read from file
		{
			String parseMe = scan.next(); // string to parse from file

			// setting the flags for parsing
			if (parseMe.contains("motors:")) // motor data is coming next line
			{
				sensor = false; // just in case. The motor data should always be
								// first
				motor = true;
			} else if (parseMe.contains("sensors:")) // sensor data is coming
														// next line
			{
				motor = false;
				sensor = true;
			}

			// create a motor
			if (motor) {
				// parse drive
				int index = parseMe.indexOf('\t');
				String drive = parseMe.substring(0, index);
				parseMe = parseMe.substring(index + 1);

				// parse turn
				index = parseMe.indexOf('\t');
				String turn = parseMe.substring(0, index);
				parseMe = parseMe.substring(index + 1);

				// parse oriented
				index = parseMe.indexOf('\t');
				String oriented = parseMe.substring(0, index);

				// parse pinNumber
				String pinNumber = parseMe.substring(index).trim();

				// convert the strings to the correct data types
				boolean Drive = Boolean.valueOf(drive);
				boolean Turn = Boolean.valueOf(turn);
				String PinNumber = String.valueOf(pinNumber);
				orientation Orient = orientation.valueOf(oriented);

				// add motor to ArrayList for ConfigModule
				this.motors.add(new Motor(Drive, Turn, Orient, PinNumber));
			}

			else if (sensor) // creating sensors
			{
				// TODO once sensor objects are decided, create them from file
				// here
			}
		}
		scan.close();
	}

	/*********************************************************************************
	 * Description: Saves the ConfigurationModule to file
	 * 
	 * Parameters: filepath - the path including filename where the
	 * configuration file is to be saved
	 * 
	 * @throws IOException
	 *********************************************************************************/
/*	public void SaveConfiguration(String filepath) throws IOException {
		// get output stream writer
		Path file = Paths.get(filepath);
		OutputStream out = new BufferedOutputStream(Files.newOutputStream(file,
				StandardOpenOption.APPEND));

		// write motors to file
		for (int i = 0; motors.get(i) != null; i++) {
			Motor writeMe = motors.get(i);

			// drive

			// turn

			// oriented

			// pin number
		}

	}*/

	// ************************************************************ FILE I/O END
	// ***********************************************

	/***************************************************
	 * Initializes the ConfigurationModule
	 * 
	 * @throws SerialPortException
	 * @throws InterruptedException
	 * @throws PhidgetException 
	 ***************************************************/
	public static void initConfig() throws InterruptedException,
			SerialPortException, PhidgetException {
		comms.initUart();
		phidget = new InterfaceKitPhidget();
		phidget.open(325751);
		phidget.waitForAttachment();

		motors.add(new Motor(true, true, orientation.clockwise, "\b"));
		motors.add(new Motor(true, true, orientation.counterclockwise, "\r"));

		sensors.add(new Sensor(sensorType.sonar, 0, 17
			, 2));
		sensors.add(new Sensor(sensorType.sonar, 0, 17, 6));
		sensors.add(new Sensor(sensorType.sonar, 0, 17, 7));
		//Sensor.initPhidget(325751);

		new Phidget(sensors, motors, comms, phidget);
	}

	// ********************* ROBOT LISTENER REQUIRED METHODS FROM IMPLEMENTING
	// *****************************
	public void print(String s) {
		System.out.print(s);
	}

	public void println(String s) {
		System.out.println(s);
	}

	public void error(String var, String e) {
		System.out.println(e);
	}

	/***************************************************************
	 * Description: Moves the robot forward indefinitely
	 ***************************************************************/
	public void driveForward() {
		try {
			for (int i = 0; i < motors.size(); i++) {
				comms.sendString(motors.get(i).Forward());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	/***************************************************************
	 * Description: Moves the robot forward a specified distance
	 * 
	 * @throws SerialPortException
	 * @throws InterruptedException
	 ***************************************************************/
	public void driveForward(int distance) throws InterruptedException,
			SerialPortException // TODO how do we want distance measured?
	{
		for (int i = 0; motors.get(i) != null; i++) {
			comms.sendString(motors.get(i).Forward());
		}

		// TODO use the sensor data to detect condition to stop
		for (int i = 0; motors.get(i) != null; i++) {
			comms.sendString(motors.get(i).Stop());
		}
	}

	/***************************************************************
	 * Description: Moves the robot forward a specified distance or time
	 * 
	 * @throws SerialPortException
	 * @throws InterruptedException
	 ***************************************************************/
	public void driveForward(int length, boolean time)
			throws InterruptedException, SerialPortException // TODO how do we
																// want distance
																// or time
																// measured?
	{
		for (int i = 0; motors.get(i) != null; i++) {
			comms.sendString(motors.get(i).Forward());
		}

		if (time) // if measured by time
		{
			// TODO use the sensor data to detect condition to stop
			for (int i = 0; motors.get(i) != null; i++) {
				comms.sendString(motors.get(i).Stop());
			}
		} else // measured by distance
		{
			// TODO use the sensor data to detect condition to stop
			for (int i = 0; motors.get(i) != null; i++) {
				comms.sendString(motors.get(i).Stop());
			}
		}
	}

	/***************************************************************
	 * Description: Moves the robot backward indefinitely
	 * 
	 * @throws SerialPortException
	 * @throws InterruptedException
	 ***************************************************************/
	public void driveBackwards() {
		for (int i = 0; i < motors.size(); i++) {
			try {
				comms.sendString(motors.get(i).Backward());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}
	}

	/***************************************************************
	 * Description: Moves the robot backward by a provided distance or time
	 * 
	 * @throws SerialPortException
	 * @throws InterruptedException
	 ***************************************************************/
	public void driveBackwards(int length, boolean time)
			throws InterruptedException, SerialPortException {
		for (int i = 0; motors.get(i) != null; i++) {
			comms.sendString(motors.get(i).Backward());
		}

		if (time) // if measured by time
		{
			// TODO use the sensor data to detect condition to stop
			for (int i = 0; motors.get(i) != null; i++) {
				comms.sendString(motors.get(i).Stop());
			}
		} else // measured by distance
		{
			// TODO use the sensor data to detect condition to stop
			for (int i = 0; motors.get(i) != null; i++) {
				comms.sendString(motors.get(i).Stop());
			}
		}
	}

	public void turnLeft() {
		for (int i = 0; i < motors.size(); i++) {
			try {
				comms.sendString(motors.get(i).Left());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}
	}

	public void turnRight() {
		for (int i = 0; i < motors.size(); i++) {
			try {
				comms.sendString(motors.get(i).Right());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		for (int i = 0; i < motors.size(); i++) {
			try {
				comms.sendString(motors.get(i).Stop());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}
	}

	public int getSonarData(int num) {
		int distance = 0;
		try {
			distance = (int)sensors.get(num).pollSensor(phidget);
		} catch (PhidgetException e) {
			e.printStackTrace();
		}
		return distance;
	}

	public int getBearing() {
		// TODO
		return (0); // DEBUG temp placeholder
	}

	// positive forward neg back driveForward(dist);
	// TODO this is a sample of how we would use it depending upon how the
	// sensor is used and what
	// values are recieved from reading. Could implement this another way. need
	// to test first
	public void driveDistance(int dist) {
		// decide whether to drive forward or back
		if (dist > 0) // drive forward
		{
			// get position before moving forward
			int placeHolder = 0; // DEBUG replace with correct sensor int value
			int start = getSonarData(0);

			// begin moving forward
			for (int i = 0; i < motors.size(); i++) {
				try {
					comms.sendString(motors.get(i).Forward());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
			}

			// wait until robot has moved 'dist' forward
			int travelled = 0;
			while (travelled < dist) {
				travelled = start - getSonarData(0);
			}

			// stop moving forward
			for (int i = 0; i < motors.size(); i++) {
				try {
					comms.sendString(motors.get(i).Stop());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
			}
		} else if (dist < 0) {
			// get position before moving backward
			int placeHolder = 0; // DEBUG replace with correct sensor int value
			int start = getSonarData(0);

			// begin moving backward
			for (int i = 0; i < motors.size(); i++) {
				try {
					comms.sendString(motors.get(i).Backward());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
			}

			// wait until robot has moved 'dist' backward
			int travelled = 0;
			while (travelled > dist) {
				travelled = start - getSonarData(0);
			}

			// stop moving backward
			for (int i = 0; i < motors.size(); i++) {
				try {
					comms.sendString(motors.get(i).Stop());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// pos angle right , neg left
	public void turnAngle(int angle) {
		if (angle > 0) {
			// get start bearing angle
			int bearing = getBearing();

			// begin turning right
			for (int i = 0; i < motors.size(); i++) {
				try {
					comms.sendString(motors.get(i).Right());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
			}

			// decide when to stop turning
			int finalBearing = bearing + angle;
			int currentBearing = Integer.valueOf(bearing); // deep copy so we
															// dont have a
															// pointer
			while (currentBearing < finalBearing) {
				currentBearing = getBearing() - bearing;
			}

			// stop turning
			for (int i = 0; i < motors.size(); i++) {
				try {
					comms.sendString(motors.get(i).Stop());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void turnToBearing(int bearing) {
		// TODO turns to absolute direction
	}

	// ********************* ROBOT LISTENER REQUIRED METHODS END
	// *******************************************

	// TODO this may need to change how it works depending on what is needed
	// This scheme was used to provide more accurate functionality in
	// case that is desired.
	/***************************************************************
	 * Params: int degrees - provide number between 0 and 360
	 * 
	 * Description: Turns the robot a specified number of degrees corresponding
	 * to the coordinate plane
	 * 
	 * Notes: Imagine the robot on the origin of a coordinate plane facing the y
	 * axis at 90 degrees. To turn 90 degrees to the right, supply a value of
	 * '0' or '360' for parameter 'degrees'
	 * 
	 * @throws SerialPortException
	 * @throws InterruptedException
	 ***************************************************************/
	public void Turn(int degrees) throws InterruptedException,
			SerialPortException {
		// deciding which direction the wheels need to turn
		if (degrees > 90) {
			if (degrees > 270) // turn robot clockwise
			{
				for (int i = 0; motors.get(i) != null; i++) {
					comms.sendString(motors.get(i).Right()); // TODO note: i use
																// both
																// clockwise
																// wording and
																// right/left
																// wording to
																// see which the
																// group likes
																// better and
																// needs to be
																// standardized
				}
			} else // turn robot counterclockwise
			{
				for (int i = 0; motors.get(i) != null; i++) {
					comms.sendString(motors.get(i).Left());
				}
			}
		} else // degrees < 90 so turn clockwise
		{
			for (int i = 0; motors.get(i) != null; i++) {
				comms.sendString(motors.get(i).Right());
			}
		}

		// TODO use sensors to calculate condition to stop the motors

	}
}
