package com.martinbrook.tesseractuhc.command;

import org.bukkit.entity.Player;

import com.martinbrook.tesseractuhc.TesseractUHC;
import com.martinbrook.tesseractuhc.UhcSpectator;



public class CreatespawnCommand extends UhcCommandExecutor{

	public CreatespawnCommand(TesseractUHC plugin) {
		super(plugin);
		
	}

	@Override
	protected String runAsAdmin(UhcSpectator sender, String[] args)  {
		if(instance.getWorldEditPlugin() ==null){
			return ERROR_COLOR +"World Edit is not installed.";
		}else{
		Player p = sender.getPlayer().getPlayer();
		p.performCommand("/schematic load spawn");
		p.performCommand("/paste");
		
		
		return OK_COLOR +"Spawn Pasted.";
		}
	}

}

