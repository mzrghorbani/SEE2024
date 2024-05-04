import java.io.File;
import core.LunarSpacePortFederate;
import core.LunarSpacePortFederateAmbassador;
import skf.config.ConfigurationFactory;
import model.LunarSpacePort;
import model.Position;
import model.Quaternion;


public class LunarSpacePortMain {
	
	private static final File conf = new File("config/conf.json");
	
	public static void main(String[] args) throws Exception {
		
		LunarSpacePort lunarRover = new LunarSpacePort(
				"LunarSpacePort", 
				"LunarSpacePort", 
				"AitkenBasinLocalFixed", 
				new Position(-817582.939286128, -296194.936333636, -1498534.0),
				new Quaternion(0, 0, 0, 1));
		
		LunarSpacePortFederateAmbassador ambassador = new LunarSpacePortFederateAmbassador();
		LunarSpacePortFederate federate = new LunarSpacePortFederate(ambassador, lunarRover);
		
		// start execution
		federate.configureAndStart(new ConfigurationFactory().importConfiguration(conf));
	}

}
