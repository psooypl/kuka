package pbl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.kuka.common.ThreadUtil;
import com.kuka.generated.ioAccess.MediaFlangeIOGroup;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.*;
import static com.kuka.roboticsAPI.motionModel.MMCMotions.*;
import com.kuka.roboticsAPI.conditionModel.BooleanIOCondition;
import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.roboticsAPI.controllerModel.sunrise.mapping.HandGuidingMapper;
import com.kuka.roboticsAPI.controllerModel.sunrise.mapping.HandGuidingModule;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.CartDOF;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.geometricModel.Tool;
import com.kuka.roboticsAPI.ioModel.AbstractIO;
import com.kuka.roboticsAPI.motionModel.HandGuidingMotion;
import com.kuka.roboticsAPI.motionModel.RelativeLIN;
import com.kuka.roboticsAPI.motionModel.controlModeModel.CartesianImpedanceControlMode;
import com.kuka.roboticsAPI.motionModel.controlModeModel.HandGuidingControlMode;
import com.kuka.roboticsAPI.uiModel.ApplicationDialogType;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKey;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyBar;
import com.kuka.roboticsAPI.uiModel.userKeys.IUserKeyListener;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyAlignment;
import com.kuka.roboticsAPI.uiModel.userKeys.UserKeyEvent;

public class TeachingByDemonstration extends RoboticsAPIApplication {

	private LBR lbr;
	private MediaFlangeIOGroup mediaFlange;
	private Controller kuka_Sunrise_Cabinet_1;
	private AbstractIO greenButton;
	private HandGuidingMotion handGuide;
    private BooleanIOCondition greenButton_active;
	private Frame nextFrame;
	private boolean flag, movingFlag;
	private boolean userButton;
	private ArrayList<Frame> frameList;
	private int licznik = 1;
	private int stiffness = 500;
	private CartesianImpedanceControlMode impedanceControlMode;
	
	public void initialize() {

		lbr = getContext().getDeviceFromType(LBR.class);
		kuka_Sunrise_Cabinet_1 = getController("KUKA_Sunrise_Cabinet_1"); // inicjalizacja
		// kontrolera
		mediaFlange = new MediaFlangeIOGroup(kuka_Sunrise_Cabinet_1);
		mediaFlange.setLEDBlue(false);
		handGuide = new HandGuidingMotion();
		greenButton = mediaFlange.getInput("UserButton");
		greenButton_active = new BooleanIOCondition(greenButton, true);

		frameList = new ArrayList<Frame>();

		impedanceControlMode = new CartesianImpedanceControlMode();
		impedanceControlMode.parametrize(CartDOF.X).setStiffness(stiffness);
		impedanceControlMode.parametrize(CartDOF.Y).setStiffness(stiffness);
		impedanceControlMode.parametrize(CartDOF.Z).setStiffness(stiffness);



		IUserKeyBar userBar = getApplicationUI().createUserKeyBar("User bar"); // stworzenie paska uzytkownika
		IUserKeyListener listMoving = new IUserKeyListener() {
			@Override
			public void onKeyEvent(IUserKey key, UserKeyEvent event) {
				if (event == UserKeyEvent.KeyDown) {
					getLogger().info("Robot rozpocznie poruszanie siê po trajektorii");
					movingFlag = true;
				}
			}
		};

		IUserKey key0 = userBar.addUserKey(0, listMoving, true);
		key0.setText(UserKeyAlignment.Middle, "Move trajectory");
		userBar.publish();

	}
 
	public void run() {

		lbr.move(handGuide);
		
		while (true) {

			
			userButton = mediaFlange.getUserButton();

			if (!userButton){
				if(flag){
					lbr.move(handGuide);
					flag = false;
				}
			}

			if(userButton){
				if(!flag){
					getLogger().info("Ramka nr: " + Integer.toString(licznik) + " zosta³a dodana.");
					nextFrame = lbr.getCurrentCartesianPosition(lbr.getFlange());
					frameList.add(nextFrame);
					licznik++;
				}
				flag = true;
			}

			if(movingFlag && !userButton && !flag){
				for (int i = 0; i < licznik - 1; i++){
					lbr.move(ptp(frameList.get(i)).setJointVelocityRel(0.1).setMode(impedanceControlMode));
				}
				movingFlag = false;
				getLogger().info("Flaga ruchu wylaczona");
			}

		}
	}
}















