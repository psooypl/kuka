package pbl;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.kuka.generated.ioAccess.MediaFlangeIOGroup;
import com.kuka.roboticsAPI.applicationModel.tasks.CycleBehavior;
import com.kuka.roboticsAPI.applicationModel.tasks.RoboticsAPICyclicBackgroundTask;
import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.geometricModel.ObjectFrame;

/**
 * Implementation of a cyclic background task.
 * <p>
 * It provides the {@link RoboticsAPICyclicBackgroundTask#runCyclic} method 
 * which will be called cyclically with the specified period.<br>
 * Cycle period and initial delay can be set by calling 
 * {@link RoboticsAPICyclicBackgroundTask#initializeCyclic} method in the 
 * {@link RoboticsAPIBackgroundTask#initialize()} method of the inheriting 
 * class.<br>
 * The cyclic background task can be terminated via 
 * {@link RoboticsAPICyclicBackgroundTask#getCyclicFuture()#cancel()} method or 
 * stopping of the task.
 * @see UseRoboticsAPIContext
 * 
 */
public class TeachingBackgroundTask extends RoboticsAPICyclicBackgroundTask {
	@Inject
	Controller kuka_Sunrise_Cabinet_1;
	@Inject
	MediaFlangeIOGroup mediaFlange;
	@Inject 
	LBR lbr;


	//private ArrayList<Frame> zapis;
	//private ArrayList<Frame> odczyt;
	//private Frame ramka;
	//private ObjectOutputStream objectOutputStream;
	//private ObjectInputStream objectInputStream;
	private boolean userButton;

	public void initialize() {

		initializeCyclic(0, 100, TimeUnit.MILLISECONDS, CycleBehavior.BestEffort);

		//	mediaFlange = new MediaFlangeIOGroup(kuka_Sunrise_Cabinet_1);
		//	lbr = getContext().getDeviceFromType(LBR.class);
		//	kuka_Sunrise_Cabinet_1 = getController("KUKA_Sunrise_Cabinet_1");


		

		//zapis = new ArrayList<Frame>();
		//odczyt = new ArrayList<Frame>();


		//ramka = lbr.getCurrentCartesianPosition(lbr.getFlange());
		//zapis.add(ramka);
	}

	public void runCyclic() {

//		userButton = mediaFlange.getUserButton();
//
//		if(userButton == true){
//			mediaFlange.setLEDBlue(true);
//			try {
//				Thread.sleep(50);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		mediaFlange.setLEDBlue(false);

		/*	while(true){
			//if(userButton){

			try {
				mediaFlange.setLEDBlue(true);
				objectOutputStream.writeObject(zapis);
			} catch (IOException e) {
				System.out.println("IOException");
				e.printStackTrace();
			}
			//}
		}*/
	}
}
