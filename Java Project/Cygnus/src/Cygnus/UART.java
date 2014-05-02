/*
 * The UART class is responsible for opening a serial communication serialPort and giving the
 * direct write statements to the motors.
 */

package Cygnus;

import jssc.*;

public class UART {
	public static final int FRONT_LEFT = 8;
	public static final int FRONT_RIGHT = 7;
	public static final int BACK_LEFT = 15;
	public static final int BACK_RIGHT = 0;
	private static SerialPort serialPort;

	public void initUart() throws InterruptedException, SerialPortException {
		// TODO Auto-generated method stub

		serialPort = initSerialPort("/dev/ttyUSB0");

		//String hm = "F\bB\r";
		//String back = "F\rB\b";
		
		initRobot();
		speedUp();
	}

	private static SerialPort initSerialPort(String wantedPortName)
	{
		// Instantiate a serial serialPort with desired name i.e "/dev/ttyUSB0"
		SerialPort serialPort = new SerialPort(wantedPortName);

		// The serial serialPort should be opened with the following parameters:
		// Baud Rate: 2400
		// Data Bits: 8
		// Stop Bits: 1
		// Parity: None
		try {
			serialPort.openPort();
			serialPort.setParams(2400, 8, 1, 0);

		} catch (SerialPortException ex) {
			System.out.println(ex);
		}
		
		return serialPort;
	}

	public static void closeSerialPort() throws SerialPortException {
		serialPort.closePort();
	}

	public void sendString(String sendMe) throws InterruptedException, SerialPortException
	{
		char[] sendArray = sendMe.toCharArray();
		
		for(int i = 0; i < sendMe.length(); i++)
		{
			serialPort.writeInt((int) sendArray[i]);
			Thread.sleep(10);
		}
	}	

	public static void moveForward() throws SerialPortException {
		serialPort.writeInt(70);
		serialPort.writeInt(8);
		serialPort.writeInt(66);
		serialPort.writeInt(13);
	}
	
	public static void moveBackward() throws SerialPortException {
		serialPort.writeInt(70);
		serialPort.writeInt(13);
		serialPort.writeInt(66);
		serialPort.writeInt(8);
	}

	public static void initRobot() throws SerialPortException {
		serialPort.writeInt(90);
	}

	public static void speedUp() throws SerialPortException {
		serialPort.writeInt(85);
	}

	public static void speedDown() throws SerialPortException {
		serialPort.writeInt(68);
	}

	public static void stop() throws SerialPortException {
		serialPort.writeInt(83);
		serialPort.writeInt(8);
		serialPort.writeInt(83);
		serialPort.writeInt(13);
	}

	/*public void stop(int pinNumber)
	{
		os.write('S');
		Thread.sleep(10);
		os.write(pinNumber);
		Thread.sleep(10);
	}*/
}