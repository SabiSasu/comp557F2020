/**
 * COMP 557 - Assig 1
 * @author Sabina Sasu, 260803977
 */
package comp557.a1;

import javax.swing.JTextField;

import mintools.parameters.BooleanParameter;

public class CharacterMaker {

	static public String name = "Bob - Sabina Sasu 260803977";
	
	// TODO: Objective 8: change default of load from file to true once you start working with xml
	static BooleanParameter loadFromFile = new BooleanParameter( "Load from file (otherwise by procedure)", false );
	static JTextField baseFileName = new JTextField("data/a1data/MyCharacter");
	
	/**
	 * Creates a character, either procedurally, or by loading from an xml file
	 * @return root node
	 */
	static public GraphNode create() {
		
		if ( loadFromFile.getValue() ) {
			// TODO: Objectives 6: create your character in the character.xml file 
			return CharacterFromXML.load( baseFileName.getText() + ".xml");
		} else {
			// TODO: Objective 3,4,5,6: test DAG nodes by creating a small DAG in the CharacterMaker.create() method 
			FreeJoint f = new FreeJoint("root");
			
			f.add(new RotaryJoint("rotary child1", new double[] {1.0, 0, 0}, "0", 0, 90) );
			f.add(new FreeJoint("free child1"));
			// Use this for testing, but ultimately it will be more interesting
			// to create your character with an xml description (see example).
			
			// Here we just return null, which will not be very interesting, so write
			// some code to create a test or partial character and return the root node.

			return f;
		}
	}
}
