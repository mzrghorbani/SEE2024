import java.io.File;

import core.CommunicationHubFederate;
import core.CommunicationHubFederateAmbassador;
import skf.config.ConfigurationFactory;
import model.CommunicationHub;
import model.Position;


public class CommunicationHubMain {
	
	private static final File conf = new File("config/conf.json");
	
	public static void main(String[] args) throws Exception {
		
		CommunicationHub communicationHub = new CommunicationHub("CommunicationHub", "AitkenBasinLocalFixed", new Position(0, 0, 0));
		
		CommunicationHubFederateAmbassador ambassador = new CommunicationHubFederateAmbassador();
		CommunicationHubFederate federate = new CommunicationHubFederate(ambassador, communicationHub);
		
		// start execution
		federate.configureAndStart(new ConfigurationFactory().importConfiguration(conf));
	}

}
