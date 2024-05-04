import java.io.File;

import core.StorageFederate;
import core.StorageFederateAmbassador;
import skf.config.ConfigurationFactory;
import model.Storage;
import model.Position;


public class StorageMain {
	
	private static final File conf = new File("config/conf.json");
	
	public static void main(String[] args) throws Exception {
		
		Storage spaceport = new Storage("Spaceport", "AitkenBasinLocalFixed", new Position (0, 0, 0));
		
		StorageFederateAmbassador ambassador = new StorageFederateAmbassador();
		StorageFederate federate = new StorageFederate(ambassador, spaceport);
		
		// start execution
		federate.configureAndStart(new ConfigurationFactory().importConfiguration(conf));
	}

}
