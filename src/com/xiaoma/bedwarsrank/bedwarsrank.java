package com.xiaoma.bedwarsrank;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.xiaoma.bedwarsrank.event.clickgui;
import com.xiaoma.bedwarsrank.ranable.mysqlupdate;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class bedwarsrank
        extends JavaPlugin {

    public void onEnable() {
        //注册命令
        getCommand("rank").setExecutor(new com.xiaoma.bedwarsrank.command.rankgui());
        getCommand("bedwarsrank").setExecutor(new com.xiaoma.bedwarsrank.command.bedwarsrankcommand());
        //注册Runnable
        new mysqlupdate().runTaskTimer(this,0,getConfig().getInt("delay(second)")*20);
        //注册监听器
        getServer().getPluginManager().registerEvents(new clickgui(), this);
        //声明config.yml,lang.yml的文件目录

        File configfile = new File(getDataFolder(), "config.yml");
        File langfile = new File(getDataFolder(), "lang.yml");
        //声明全息投影存放的目录
        File Hologram_Folder = new File(getDataFolder()+"\\Hologram_Folder");
        //检测config.yml是否存在，不存在则复制
        if (!configfile.exists()) {
            Bukkit.getServer().getPluginManager().getPlugin("BedwarsRank").saveDefaultConfig();
        }
        //config.yml复制时会自动生成一个文件夹，所以直接生成lang.yml不会报错
        //生成lang.yml并写入数据
        if (!langfile.exists()) {
            try {
                langfile.createNewFile();
                YamlConfiguration lang = YamlConfiguration.loadConfiguration(langfile);
                lang.set("PluginEnable","插件已加载");
                lang.set("PluginDisable","插件已卸载");
                lang.set("PluginReload","插件已重载");
                lang.set("kills", "击杀数");
                lang.set("wins", "胜场");
                lang.set("destroyedBeds", "摧毁床数");
                lang.set("RankGUIName", "起床战争排行榜");
                lang.set("NotPlayer","该命令只有玩家可以使用");
                lang.set("CreateSuccess","创建成功");
                lang.set("CreateFail","创建失败");
                lang.set("NameExists","名字已存在");
                lang.set("DeleteSuccess","删除成功");
                lang.set("DeleteFail","名为: %hologram_name% 的全息投影不存在");
                lang.set("NotPermission","你没有使用该命令的权限");
                //保存数据
                lang.save(langfile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //加载lang.yml文件
        YamlConfiguration lang = YamlConfiguration.loadConfiguration(langfile);

        //检测存放全息投影的目录是否存在，不存在则创建
        if(!Hologram_Folder.exists()){
            Hologram_Folder.mkdir();
        }
        //向控制台发送信息插件已加载
        getLogger().info(lang.getString("PluginEnable"));
    }

    public void onDisable() {
        //声明lang.yml文件位置
        File langfile = new File(getDataFolder(), "lang.yml");
        //加载lang.yml文件
        YamlConfiguration lang = YamlConfiguration.loadConfiguration(langfile);
        //向控制台发送信息插件已卸载
        getLogger().info(lang.getString("PluginDisable"));
        //获得服务器所有在线玩家并存储在一个列表里
        Collection<? extends Player> player = Bukkit.getOnlinePlayers();
        //遍历所有在线玩家
        for(Player p:player){
            try {
                //关闭打开/rank的玩家的GUI界面
                if (p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK+lang.getString("RankGUIName"))) {
                    p.closeInventory();
                }
            } catch(Exception ignored){
            }
        }
    }
}