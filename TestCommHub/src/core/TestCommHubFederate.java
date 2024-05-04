package core;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.FederateHandleSaveStatusPair;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.FederateRestoreStatus;
import hla.rti1516e.FederationExecutionInformationSet;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.MessageRetractionHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.ResignAction;
import hla.rti1516e.RestoreFailureReason;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.SaveFailureReason;
import hla.rti1516e.SynchronizationPointFailureReason;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.exceptions.AlreadyConnected;
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.AttributeNotOwned;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.CouldNotCreateLogicalTimeFactory;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.ErrorReadingFDD;
import hla.rti1516e.exceptions.FederateAlreadyExecutionMember;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.FederateIsExecutionMember;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateOwnsAttributes;
import hla.rti1516e.exceptions.FederateServiceInvocationsAreBeingReportedViaMOM;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.IllegalName;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.InteractionClassNotDefined;
import hla.rti1516e.exceptions.InteractionClassNotPublished;
import hla.rti1516e.exceptions.InteractionParameterNotDefined;
import hla.rti1516e.exceptions.InvalidInteractionClassHandle;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.InvalidResignAction;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectClassNotPublished;
import hla.rti1516e.exceptions.ObjectInstanceNameInUse;
import hla.rti1516e.exceptions.ObjectInstanceNameNotReserved;
import hla.rti1516e.exceptions.ObjectInstanceNotKnown;
import hla.rti1516e.exceptions.OwnershipAcquisitionPending;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.exceptions.SynchronizationPointLabelNotAnnounced;
import hla.rti1516e.exceptions.UnsupportedCallbackModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.TimeZone;

import model.TestCommHub;
import model.Participant;
import model.Position;
import model.RtiConfiguration;
import siso.smackdown.frame.FrameType;
import skf.config.Configuration;
import skf.core.SEEAbstractFederate;
import skf.core.SEEAbstractFederateAmbassador;
import skf.exception.PublishException;
import skf.exception.SubscribeException;
import skf.exception.UnsubscribeException;
import skf.exception.UpdateException;
import skf.model.interaction.modeTransitionRequest.ModeTransitionRequest;
import skf.model.object.annotations.ObjectClass;
import skf.model.object.executionConfiguration.ExecutionConfiguration;
import skf.model.object.executionConfiguration.ExecutionMode;
import skf.synchronizationPoint.SynchronizationPoint;
import skf.utility.JulianDateType;

public class TestCommHubFederate extends SEEAbstractFederate implements Observer, FederateAmbassador {

	private static final int MAX_WAIT_TIME = 10000;
	
	
	private TestCommHub testCommHub = null;
	private String local_settings_designator = null;
	
	private ModeTransitionRequest mtr = null;
	
	private SimpleDateFormat format = null;
	
	public String federateName = "TestCommHub";
    private final String FEDERATION_NAME = "SEE 2024";
    private BufferedReader _systemInput;
    private RTIambassador _rtiAmbassador;
    private InteractionClassHandle _communicationClassHandle;
    private ParameterHandle _messageParameterHandle;
    private ParameterHandle _senderParameterHandle;
    private ObjectInstanceHandle _userInstanceHandle;
    private AttributeHandle _nameAttributeHandle;
    private String _username;
    private volatile boolean _reservationComplete;
    private volatile boolean _reservationSucceeded;
    private final Object _reservationSemaphore = new Object();
    private EncoderFactory _encoderFactory;
    private final Map<ObjectInstanceHandle, Participant> _knownObjects = new HashMap<>();

	
	public TestCommHubFederate(SEEAbstractFederateAmbassador seefedamb, TestCommHub testCommHub, String[] var1) {
		super(seefedamb);
		this.testCommHub  = testCommHub;
		this.mtr = new ModeTransitionRequest();
		
		this.format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		new ArrayList<String>(List.of(var1));
		this._systemInput = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void run() throws Exception {
		RtiFactory var1;
		var1 = RtiFactoryFactory.getRtiFactory();
		try {
			this._rtiAmbassador = var1.getRtiAmbassador();
			this._encoderFactory = var1.getEncoderFactory();
		} catch (Exception var19) {
			throw new Exception("Unable to create RTI ambassador: " + var19);
		}

		RtiConfiguration config = RtiConfiguration.createConfiguration().withConfigurationName("TestCommHub")
				.withRtiAddress("192.168.1.199:8989");

		this._rtiAmbassador.connect(this, CallbackModel.HLA_IMMEDIATE, config.toString());

		File var5 = new File("foms/TestCommHub.xml");

		try {
			this._rtiAmbassador.createFederationExecution("SEE 2024", new URL[] { var5.toURI().toURL() },
					"HLAfloat64Time");
			System.out.println("Federation execution created successfully.");
		} catch (FederationExecutionAlreadyExists var15) {
			System.out.println("Federation exist already. Joining " + this.FEDERATION_NAME);
		}

		this._rtiAmbassador.joinFederationExecution("TestCommHub", "SEE 2024", new URL[] { var5.toURI().toURL() });
		this._communicationClassHandle = this._rtiAmbassador.getInteractionClassHandle("Communication");
		this._messageParameterHandle = this._rtiAmbassador.getParameterHandle(this._communicationClassHandle,"Message");
		this._senderParameterHandle = this._rtiAmbassador.getParameterHandle(this._communicationClassHandle, "Sender");
		this._rtiAmbassador.subscribeInteractionClass(this._communicationClassHandle);
		this._rtiAmbassador.publishInteractionClass(this._communicationClassHandle);

		ObjectClassHandle var6 = this._rtiAmbassador.getObjectClassHandle("Participant");
		this._nameAttributeHandle = this._rtiAmbassador.getAttributeHandle(var6, "Name");
		AttributeHandleSet var7 = this._rtiAmbassador.getAttributeHandleSetFactory().create();
		var7.add(this._nameAttributeHandle);
		this._rtiAmbassador.subscribeObjectClassAttributes(var6, var7);
		this._rtiAmbassador.publishObjectClassAttributes(var6, var7);

		do {
			this._username = this.federateName;
			try {
				this._reservationComplete = false;
				this._rtiAmbassador.reserveObjectInstanceName(this._username);
				synchronized (this._reservationSemaphore) {
					while (!this._reservationComplete) {
						try {
							this._reservationSemaphore.wait();
						} catch (InterruptedException var14) {
						}
					}
				}

				if (!this._reservationSucceeded) {
					System.out.println("Federate already taken, try again.");
				}
			} catch (IllegalName var21) {
				System.out.println("Illegal name. Try again.");
			} catch (RTIexception var22) {
				System.err.println("RTI exception when reserving name: " + var22.getMessage());
				return;
			}
		} while (!this._reservationSucceeded);

		this._userInstanceHandle = this._rtiAmbassador.registerObjectInstance(var6, this._username);
		AttributeHandleValueMap var8 = this._rtiAmbassador.getAttributeHandleValueMapFactory().create(1);
		HLAunicodeString var9 = this._encoderFactory.createHLAunicodeString(this._username);
		var8.put(this._nameAttributeHandle, var9.toByteArray());
		this._rtiAmbassador.updateAttributeValues(this._userInstanceHandle, var8, (byte[]) null);
		System.out.println("Type messages you want to send. To exit, type . <ENTER>");

		while (true) {
			System.out.print("> ");
			String var10 = this._systemInput.readLine();
			if (var10.equals(".")) {
				this._rtiAmbassador.resignFederationExecution(ResignAction.DELETE_OBJECTS_THEN_DIVEST);

				try {
					this._rtiAmbassador.destroyFederationExecution("SEE 2024");
				} catch (FederatesCurrentlyJoined var13) {
				}

				this._rtiAmbassador.disconnect();
				this._rtiAmbassador = null;
				return;
			}

			ParameterHandleValueMap var11 = this._rtiAmbassador.getParameterHandleValueMapFactory().create(1);
			HLAunicodeString var12 = this._encoderFactory.createHLAunicodeString();
			var12.setValue(var10);
			var11.put(this._messageParameterHandle, var12.toByteArray());
			var11.put(this._senderParameterHandle, var9.toByteArray());
			this._rtiAmbassador.sendInteraction(this._communicationClassHandle, var11, (byte[]) null);
		}
	}

	@SuppressWarnings("unchecked")
	public void configureAndStart(Configuration config) throws ConnectionFailed, InvalidLocalSettingsDesignator, UnsupportedCallbackModel, 
														CallNotAllowedFromWithinCallback, RTIinternalError, CouldNotCreateLogicalTimeFactory, 
														FederationExecutionDoesNotExist, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, 
														SaveInProgress, RestoreInProgress, NotConnected, MalformedURLException, 
														FederateNotExecutionMember, NameNotFound, InvalidObjectClassHandle, AttributeNotDefined, 
														ObjectClassNotDefined, InstantiationException, IllegalAccessException, IllegalName, 
														ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, ObjectClassNotPublished, 
														AttributeNotOwned, ObjectInstanceNotKnown, PublishException, UpdateException, 
														SubscribeException, InvalidInteractionClassHandle, InteractionClassNotDefined, 
														InteractionClassNotPublished, InteractionParameterNotDefined, UnsubscribeException, 
														InterruptedException, SynchronizationPointLabelNotAnnounced {
		// 1. configure the SKF framework
		super.configure(config);

		// 2. Connect on RTI
		/*
		 *For MAK local_settings_designator = "";
		 *For PITCH local_settings_designator = "crcHost=" + <crc_host> + "\ncrcPort=" + <crc_port>;
		 */
		local_settings_designator = "crcHost="+config.getCrcHost()+"\ncrcPort="+config.getCrcPort();
		super.connectToRTI(local_settings_designator);

		// 3. join the SEE Federation execution
		super.joinFederationExecution();
		
		// 4. Subscribe the Subject
		super.subscribeSubject(this);
		/*
		 * 5. Check if the federate is a Late Joiner Federate. 
		 * All the Federates of the SEE Teams must be Late Joiner.
		 */
		if(!SynchronizationPoint.INITIALIZATION_STARTED.isAnnounced()){
			
			// 6. Wait for the announcement of the Synch-Point "initialization_completed"
			super.waitingForAnnouncement(SynchronizationPoint.INITIALIZATION_COMPLETED, MAX_WAIT_TIME);

			/* 7. Wait for announcement of "objects_discovered", and Federation
			 * Specific Mutiphase Initialization Synch-Points
			 */
			// -> skipped
			
			/* 8. Subscribe Execution Control Object Class Attributes
			 * and wait for ExCO Discovery
			 */
			super.subscribeElement((Class<? extends ObjectClass>) ExecutionConfiguration.class);
			super.waitForElementDiscovery((Class<? extends ObjectClass>) ExecutionConfiguration.class, MAX_WAIT_TIME);
			
			// 9. Request ExCO update
			while(super.getExecutionConfiguration() == null){
				super.requestAttributeValueUpdate((Class<? extends ObjectClass>) ExecutionConfiguration.class);
				Thread.sleep(10);
			}
				
			// 10. Publish MTR Interaction
			super.publishInteraction(this.mtr);
			
			/* 11. Publish and Subscribe Object Class Attributes
			 * and Interaction Class Parameters
			 * 
			 * 12. Reserve All Federate Object Instance Names
			 * 
			 * 13. Wait for All Federate Object Instance Name Reservation
			 * Success/Failure Callbacks
			 * 
			 * 14. Register Federate Object Instances
			 */
			super.publishElement(testCommHub, "TestCommHub");
			super.subscribeReferenceFrame(FrameType.AitkenBasinLocalFixed);
			
			// 15. Wait for All Required Objects to be Discovered
			// -> Skipped
			
			/* 16. Setup HLA Time Management and Query GALT, Compute HLTB and 
			 * Time Advance to HLTB
			 */
			super.setupHLATimeManagement();
			
			// 17. Achieve "objects_discovered" Sync-Point and wait for synchronization
			// -> Skipped
		}
		else
			throw new RuntimeException("The federate " + config.getFederateName() + "is not a Late Joiner Federate");
		
		//18. Start simulation execution
		super.startExecution();
		
	}

	@Override
	protected void doAction() {
		
		/* Time information related to the Federate */
		System.out.println("getFederateJoinHLALogicalTimeValue "+this.getTime().getFederateJoinHLALogicalTimeValue());
		System.out.println("getFederateJoinTimeAsJulianDate(JulianDateType.TRUNCATED) "+ this.getTime().getFederateJoinTimeAsJulianDate(JulianDateType.TRUNCATED));
		System.out.println(format.format(this.getTime().getFederateJoinTimeAsCalendar().getTime()));
		System.out.println(this.getTime().getFederateTimeCycle());
		
		/* Time information related to the Federation */
		System.out.println("getFederationTime "+ this.getTime().getFederationTimeCycle());
		System.out.println(format.format(this.getTime().getFederationTimeAsCalendar().getTime()));
		System.out.println("getFederationTimeAsJulianDate "+this.getTime().getFederationTimeAsJulianDate(JulianDateType.DATE));
		System.out.println("getFederationTimeAsJulianDate "+this.getTime().getFederationTimeAsJulianDate(JulianDateType.MODIFIED));
		System.out.println("getFederationTimeAsJulianDate "+this.getTime().getFederationTimeAsJulianDate(JulianDateType.REDUCED));
		System.out.println("getFederationTimeAsJulianDate "+this.getTime().getFederationTimeAsJulianDate(JulianDateType.TRUNCATED));

		Position curr_pos = this.testCommHub.getPosition();
		curr_pos.setX(curr_pos.getX()); // update the x coordinate

		try {

			super.updateElement(this.testCommHub);
			System.out.println(curr_pos);

		} catch (FederateNotExecutionMember | NotConnected | AttributeNotOwned
				| AttributeNotDefined | ObjectInstanceNotKnown | SaveInProgress
				| RestoreInProgress | RTIinternalError | IllegalName
				| ObjectInstanceNameInUse | ObjectInstanceNameNotReserved
				| ObjectClassNotPublished | ObjectClassNotDefined
				| UpdateException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void update(Observable arg0, Object arg1) {

		System.out.println("The TestCommHub has received an update");
		if(arg1 instanceof ExecutionConfiguration){
			super.setExecutionConfiguration((ExecutionConfiguration) arg1);
			
			/* Manage state transitions */
			if(super.getExecutionConfiguration().getCurrent_execution_mode() == ExecutionMode.EXEC_MODE_RUNNING &&
					super.getExecutionConfiguration().getNext_execution_mode() == ExecutionMode.EXEC_MODE_FREEZE){
				super.freezeExecution();
			}
			
			else if(super.getExecutionConfiguration().getCurrent_execution_mode() == ExecutionMode.EXEC_MODE_FREEZE &&
					super.getExecutionConfiguration().getNext_execution_mode() == ExecutionMode.EXEC_MODE_RUNNING){
				super.resumeExecution();
			}
			
			else if((super.getExecutionConfiguration().getCurrent_execution_mode() == ExecutionMode.EXEC_MODE_FREEZE || 
					super.getExecutionConfiguration().getCurrent_execution_mode() == ExecutionMode.EXEC_MODE_RUNNING ) &&
					super.getExecutionConfiguration().getNext_execution_mode() == ExecutionMode.EXEC_MODE_SHUTDOWN ){
				super.shudownExecution();
			}
			else
				System.out.println("ExecutionConfiguration status unknown");
			/* End Manage state transitions */
		}
		else
			System.out.println("unknown type");

	}

	@Override
	public void connectionLost(String faultDescription) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportFederationExecutions(FederationExecutionInformationSet theFederationExecutionInformationSet)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void synchronizationPointRegistrationSucceeded(String synchronizationPointLabel)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void synchronizationPointRegistrationFailed(String synchronizationPointLabel,
			SynchronizationPointFailureReason reason) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void announceSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationSynchronized(String synchronizationPointLabel, FederateHandleSet failedToSyncSet)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initiateFederateSave(String label) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initiateFederateSave(String label, LogicalTime time) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationSaved() throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationNotSaved(SaveFailureReason reason) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationSaveStatusResponse(FederateHandleSaveStatusPair[] response) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestFederationRestoreSucceeded(String label) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestFederationRestoreFailed(String label) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationRestoreBegun() throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initiateFederateRestore(String label, String federateName, FederateHandle federateHandle)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationRestored() throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationNotRestored(RestoreFailureReason reason) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationRestoreStatusResponse(FederateRestoreStatus[] response) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRegistrationForObjectClass(ObjectClassHandle theClass) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopRegistrationForObjectClass(ObjectClassHandle theClass) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnInteractionsOn(InteractionClassHandle theHandle) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnInteractionsOff(InteractionClassHandle theHandle) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectInstanceNameReservationSucceeded(String objectName) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectInstanceNameReservationFailed(String objectName) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void multipleObjectInstanceNameReservationSucceeded(Set<String> objectNames) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void multipleObjectInstanceNameReservationFailed(Set<String> objectNames) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass,
			String objectName) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass,
			String objectName, FederateHandle producingFederate) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport,
			SupplementalReflectInfo reflectInfo) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime,
			OrderType receivedOrdering, SupplementalReflectInfo reflectInfo) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalReflectInfo reflectInfo)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport,
			SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime,
			OrderType receivedOrdering, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalReceiveInfo receiveInfo)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			SupplementalRemoveInfo removeInfo) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			LogicalTime theTime, OrderType receivedOrdering, SupplementalRemoveInfo removeInfo)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle,
			SupplementalRemoveInfo removeInfo) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributesInScope(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributesOutOfScope(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void provideAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
			byte[] userSuppliedTag) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
			String updateRateDesignator) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnUpdatesOffForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void confirmAttributeTransportationTypeChange(ObjectInstanceHandle theObject,
			AttributeHandleSet theAttributes, TransportationTypeHandle theTransportation) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportAttributeTransportationType(ObjectInstanceHandle theObject, AttributeHandle theAttribute,
			TransportationTypeHandle theTransportation) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void confirmInteractionTransportationTypeChange(InteractionClassHandle theInteraction,
			TransportationTypeHandle theTransportation) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportInteractionTransportationType(FederateHandle theFederate, InteractionClassHandle theInteraction,
			TransportationTypeHandle theTransportation) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestAttributeOwnershipAssumption(ObjectInstanceHandle theObject,
			AttributeHandleSet offeredAttributes, byte[] userSuppliedTag) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestDivestitureConfirmation(ObjectInstanceHandle theObject, AttributeHandleSet offeredAttributes)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeOwnershipAcquisitionNotification(ObjectInstanceHandle theObject,
			AttributeHandleSet securedAttributes, byte[] userSuppliedTag) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeOwnershipUnavailable(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestAttributeOwnershipRelease(ObjectInstanceHandle theObject, AttributeHandleSet candidateAttributes,
			byte[] userSuppliedTag) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void confirmAttributeOwnershipAcquisitionCancellation(ObjectInstanceHandle theObject,
			AttributeHandleSet theAttributes) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void informAttributeOwnership(ObjectInstanceHandle theObject, AttributeHandle theAttribute,
			FederateHandle theOwner) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeIsNotOwned(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeIsOwnedByRTI(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeRegulationEnabled(LogicalTime time) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeConstrainedEnabled(LogicalTime time) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeAdvanceGrant(LogicalTime theTime) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestRetraction(MessageRetractionHandle theHandle) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

}
