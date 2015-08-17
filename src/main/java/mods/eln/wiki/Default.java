package mods.eln.wiki;

import net.minecraft.client.gui.GuiScreen;
import mods.eln.gui.GuiButtonEln;
import mods.eln.gui.GuiHelper;
import mods.eln.gui.GuiScreenEln;
import mods.eln.gui.GuiTextFieldEln;
import mods.eln.gui.IGuiObject;
import mods.eln.misc.Utils;
import mods.eln.misc.UtilsClient;

public class Default extends GuiScreenEln{
	public Default(GuiScreen preview) {
		this.preview = preview;
	}
	
	GuiScreen preview;
	GuiHelper helper;
	GuiButtonEln previewBt;
	GuiTextFieldEln searchText;
	
	@Override
	protected GuiHelper newHelper() {
		
		return helper = new GuiHelper(this, 240, 166);
	}

	protected GuiVerticalExtender extender;
	
	@Override
	public void initGui() {
		
		super.initGui();
		
		extender = new GuiVerticalExtender(6, 28, helper.xSize-12, helper.ySize-28-8,helper);
		add(extender);
		
		previewBt = newGuiButton(6, 6, 56, "Previous");

		searchText = newGuiTextField(6+56+6, 10, helper.xSize - 6-56-6-10);
	
	}
	
	@Override
	public void guiObjectEvent(IGuiObject object) {
		
		super.guiObjectEvent(object);
		
		if(object == previewBt){
			UtilsClient.clientOpenGui(preview);
		}
    	else if(object == searchText){
    		UtilsClient.clientOpenGui(new Search(searchText.getText()));	
    	}
		
	}
	
}
