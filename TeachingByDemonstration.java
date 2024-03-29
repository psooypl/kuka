package pbl;


import java.util.ArrayList;
import com.kuka.generated.ioAccess.MediaFlangeIOGroup;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.*;
import com.kuka.roboticsAPI.conditionModel.BooleanIOCondition;
import com.kuka.roboticsAPI.controllerModel.Controller;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.CartDOF;
import com.kuka.roboticsAPI.geometricModel.Frame;
import com.kuka.roboticsAPI.ioModel.AbstractIO;
import com.kuka.roboticsAPI.motionModel.HandGuidingMotion;
import com.kuka.roboticsAPI.motionModel.controlModeModel.CartesianImpedanceControlMode;
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
		kuka_Sunrise_Cabinet_1 = getController("KUKA_Sunrise_Cabinet_1"); 
		mediaFlange = new MediaFlangeIOGroup(kuka_Sunrise_Cabinet_1);
		mediaFlange.setLEDBlue(false);
		handGuide = new HandGuidingMotion();

		impedanceControlMode = new CartesianImpedanceControlMode();
		impedanceControlMode.parametrize(CartDOF.X).setStiffness(stiffness);
		impedanceControlMode.parametrize(CartDOF.Y).setStiffness(stiffness);
		impedanceControlMode.parametrize(CartDOF.Z).setStiffness(stiffness);

		frameList = new ArrayList<Frame>();

		IUserKeyBar userBar = getApplicationUI().createUserKeyBar("User bar"); // stworzenie paska uzytkownika
		IUserKeyListener listMoving = new IUserKeyListener() {
			@Override
			public void onKeyEvent(IUserKey key, UserKeyEvent event) {
				if (event == UserKeyEvent.KeyDown) {
					getLogger().info("Robot rozpocznie poruszanie si� po trajektorii");
					flag = false;
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

			if(userButton){
				if(!flag){
					getLogger().info("Ramka nr: " + Integer.toString(licznik) + " zosta�a dodana.");
					nextFrame = lbr.getCurrentCartesianPosition(lbr.getFlange());
					frameList.add(nextFrame);
					licznik++;

				flag = true;
			}

			if(movingFlag){
				for (int i = 0; i < licznik - 1; i++){
					lbr.move(ptp(frameList.get(i)).setJointVelocityRel(0.5).setMode(impedanceControlMode));
				}
				movingFlag = false;
				getLogger().info("Flaga ruchu wylaczona");

			}

			if (!userButton){
				if(flag){
					lbr.move(handGuide);
					flag = false;
				}
			}

		}
	}
}