package model;

import org.siso.spacefom.frame.SpaceTimeCoordinateState;

import referenceFrame.coder.*;
import skf.coder.HLAunicodeStringCoder;
import skf.model.object.annotations.Attribute;
import skf.model.object.annotations.ObjectClass;

@ObjectClass(name = "PhysicalEntity.Spaceport")
public class Spaceport {
	
	@Attribute(name = "name", coder = HLAunicodeStringCoder.class)
	private String name = null;
	
	@Attribute(name = "parent_reference_frame", coder = HLAunicodeStringCoder.class)
	private String parent_name = null;
	
	@Attribute(name = "state", coder = SpaceTimeCoordinateStateCoder.class)
	private SpaceTimeCoordinateState state = null;
	
	@Attribute(name = "type", coder = HLAunicodeStringCoder.class)
	private String type = null;
	
	private Position position = null;
	
	public Spaceport(){}

	public Spaceport(String name, String parent_name, String type, Position position) {
		this.name = name;
		this.parent_name = parent_name;
		this.type = type;
		this.state = new SpaceTimeCoordinateState();
		this.position = position;
	}
	
	public String getType() {
	    return type;
	}

	public void setType(String type) {
	    this.type = type;
	}
	

	public SpaceTimeCoordinateState getState() {
	    return state;
	}

	public void setState(SpaceTimeCoordinateState state) {
	    this.state = state;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the parent_name
	 */
	public String getParent_name() {
		return parent_name;
	}

	/**
	 * @param parent_name the parent_name to set
	 */
	public void setParent_name(String parent_name) {
		this.parent_name = parent_name;
	}

	/**
	 * @return the position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Position position) {
		this.position = position;
	}

}
