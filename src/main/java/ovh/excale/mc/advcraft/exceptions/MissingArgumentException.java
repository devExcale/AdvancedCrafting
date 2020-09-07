package ovh.excale.mc.advcraft.exceptions;

import ovh.excale.mc.advcraft.CraftingRecipe;

import java.io.File;

public class MissingArgumentException extends Exception {

	private final String craftKey;
	private final String argumentName;

	public MissingArgumentException(CraftingRecipe recipe, String argumentName) {
		super("Missing argument " + argumentName + " in recipe " + recipe.getKey());
		this.argumentName = argumentName;
		craftKey = recipe.getKey();
	}

	public MissingArgumentException(CraftingRecipe recipe, String argumentName, Throwable cause) {
		super("Missing argument " + argumentName + " in recipe " + recipe.getKey(), cause);
		this.argumentName = argumentName;
		craftKey = recipe.getKey();
	}

	public MissingArgumentException(File recipeFile, String argumentName) {
		super("Missing argument " + argumentName + " in recipe " + recipeFile.getName());
		this.argumentName = argumentName;
		craftKey = recipeFile.getName();
	}

	public MissingArgumentException(File recipeFile, String argumentName, Throwable cause) {
		super("Missing argument " + argumentName + " in recipe " + recipeFile.getName(), cause);
		this.argumentName = argumentName;
		craftKey = recipeFile.getName();
	}

	public String getArgumentName() {
		return argumentName;
	}

	public String getRecipeKey() {
		return craftKey;
	}

}
