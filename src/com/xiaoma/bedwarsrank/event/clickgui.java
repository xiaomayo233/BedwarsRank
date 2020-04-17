package com.xiaoma.bedwarsrank.event;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;

public class clickgui implements Listener {
    //����lang.yml��λ��
    private final File langfile = new File("plugins\\bedwarsrank", "\\lang.yml");
    //����lang.yml
    private final YamlConfiguration lang = YamlConfiguration.loadConfiguration(langfile);

    @EventHandler
    //������ҵ��GUI�¼�
    public void clickgui(InventoryClickEvent event){
        //���GUI�����Ƿ���/rank��GUI����
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + lang.getString("RankGUIName"))){
            //�����¼�ȡ��
            event.setCancelled(true);
        }
    }
}
