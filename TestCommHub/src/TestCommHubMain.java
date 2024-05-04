import java.io.File;

import core.TestCommHubFederate;
import core.TestCommHubFederateAmbassador;
import skf.config.ConfigurationFactory;
import model.TestCommHub;
import model.Position;


public class TestCommHubMain {
	
	private static final File conf = new File("config/conf.json");
	
	public static void main(String[] args) throws Exception {
		
		TestCommHub testCommHub = new TestCommHub("TestCommHub", "AitkenBasinLocalFixed", new Position(0, 0, 0));
		
		TestCommHubFederateAmbassador ambassador = new TestCommHubFederateAmbassador();
		TestCommHubFederate federate = new TestCommHubFederate(ambassador, testCommHub, args);
		
		// start execution
		federate.configureAndStart(new ConfigurationFactory().importConfiguration(conf));
	}

}
