import java.io.File;

import core.SpaceportFederate;
import core.SpaceportFederateAmbassador;
import skf.config.ConfigurationFactory;
import model.Spaceport;
import siso.smackdown.frame.FrameType;
import model.Position;


public class SpaceportMain {
	
	private static final File conf = new File("config/conf.json");
	
	public static void main(String[] args) throws Exception {
		
		Spaceport spaceport = new Spaceport("Spaceport", FrameType.AitkenBasinLocalFixed.toString(),
				"Port", new Position(0, 0, 0));
		
		SpaceportFederateAmbassador ambassador = new SpaceportFederateAmbassador();
		SpaceportFederate federate = new SpaceportFederate(ambassador, spaceport);
		
		// start execution
		federate.configureAndStart(new ConfigurationFactory().importConfiguration(conf));
	}

}
