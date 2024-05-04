package model;

public class RtiConfiguration {
    private String configurationName;
    private String rtiAddress;

    private RtiConfiguration() {}

    public static RtiConfiguration createConfiguration() {
        return new RtiConfiguration();
    }

    public RtiConfiguration withConfigurationName(String configurationName) {
        this.configurationName = configurationName;
        return this;
    }

    public RtiConfiguration withRtiAddress(String rtiAddress) {
        this.rtiAddress = rtiAddress;
        return this;
    }

    // Other methods to set additional configuration parameters if needed

    @Override
    public String toString() {
        return "{ configurationName: " + configurationName + ", rtiAddress: " + rtiAddress + " }";
    }
}