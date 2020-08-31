package ovh.excale.mc.advcraft.exceptions;

public class AttributeParseException extends AdvancedCraftingException {

	public AttributeParseException(String path, String fileName) {
		super("Couldn't parse attribute \"" + path + "\" in file \"" + fileName + "\"");
	}

}
