package ovh.excale.mc.advcraft.exceptions;

public class MissingPathException extends AdvancedCraftingException {

	public MissingPathException(String path, String fileName) {
		super("Missing attribute \"" + path + "\" in file \"" + fileName + "\"");
	}

}
