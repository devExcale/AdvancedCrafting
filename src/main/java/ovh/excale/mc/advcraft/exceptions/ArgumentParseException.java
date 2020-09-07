package ovh.excale.mc.advcraft.exceptions;

import ovh.excale.mc.advcraft.CraftingRecipe;

import java.io.File;

public class ArgumentParseException extends Exception {

	private final String craftKey;
	private final String argumentName;
	private final String argumentValue;

	public ArgumentParseException(CraftingRecipe recipe, String argumentName, String value) {
		super("Couldn't parse argument " + argumentName + ": " + value);
		this.argumentName = argumentName;
		argumentValue = value;
		craftKey = recipe.getKey();
	}

	public ArgumentParseException(CraftingRecipe recipe, String argumentName, String value, Throwable cause) {
		super("Couldn't parse argument " + argumentName + ": " + value, cause);
		this.argumentName = argumentName;
		argumentValue = value;
		craftKey = recipe.getKey();
	}

	public ArgumentParseException(File recipeFile, String argumentName, String value) {
		super("Couldn't parse argument " + argumentName + ": " + value);
		this.argumentName = argumentName;
		argumentValue = value;
		craftKey = recipeFile.getName();
	}

	public ArgumentParseException(File recipeFile, String argumentName, String value, Throwable cause) {
		super("Couldn't parse argument " + argumentName + ": " + value, cause);
		this.argumentName = argumentName;
		argumentValue = value;
		craftKey = recipeFile.getName();
	}

	public String getArgumentName() {
		return argumentName;
	}

	public String getArgumentValue() {
		return argumentValue;
	}

}
