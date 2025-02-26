import java.io.File;

import core.HabitationFederate;
import core.HabitationFederateAmbassador;
import skf.config.ConfigurationFactory;
import model.Habitation;
import model.Position;


public class HabitationMain {
	
	private static final File conf = new File("config/conf.json");
	
	public static void main(String[] args) throws Exception {
		
		Habitation spaceport = new Habitation("Habitation", "AitkenBasinLocalFixed", new Position (0, 0, 0));
		
		HabitationFederateAmbassador ambassador = new HabitationFederateAmbassador();
		HabitationFederate federate = new HabitationFederate(ambassador, spaceport);
		
		// start execution
		federate.configureAndStart(new ConfigurationFactory().importConfiguration(conf));
	}

}
