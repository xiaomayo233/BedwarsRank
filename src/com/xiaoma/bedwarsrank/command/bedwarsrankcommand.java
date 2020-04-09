package com.xiaoma.bedwarsrank.command;


import com.xiaoma.bedwarsrank.ranable.mysqlupdate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class bedwarsrankcommand implements CommandExecutor {
    //声明主类
    private final Plugin plugin = com.xiaoma.bedwarsrank.bedwarsrank.getPlugin(com.xiaoma.bedwarsrank.bedwarsrank.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //声明lang.yml的位置
        File langfile = new File("plugins\\BedwarsRank\\", "lang.yml");
        //加载lang.yml文件
        YamlConfiguration lang = YamlConfiguration.loadConfiguration(langfile);
        //判断命令
        //如果自变量个数等于0（既/bedwarsrank）则返回命令的使用方法
        if (args.length == 0) return false;
        //如果自变量个数等于1（即/bedwarsrank reload或/bedwasrank create等）
        if (args.length == 1) {
            //判断子命令是否是reload
            if (args[0].equalsIgnoreCase("reload")) {
                //检测命令发送者是否有权限
                if (sender.hasPermission("bedwarsrank.admin.reload")) {
                    //检测是否是玩家
                    if (sender instanceof Player) {
                        //是玩家则发送已重载（没有实际作用，只是想让控制台看起来更整洁）
                        sender.sendMessage(ChatColor.DARK_GREEN + lang.getString("PluginReload"));
                        return true;
                    }
                    //重载
                    plugin.onDisable();
                    plugin.onEnable();
                    //没有权限则发送：没权限
                } else sender.sendMessage(ChatColor.RED + lang.getString("NotPermission"));
            }
            //检测子命令是否是help
            if (args[0].equalsIgnoreCase("help")) return false;

            //检测子命令是否为list
            if (args[0].equalsIgnoreCase("list")) {
                //检测是否拥有权限
                if (sender.hasPermission("bedwarsrank.admin.list")){
                    //声明存放全息投影的文件夹
                    File Hologram_Folder = new File("plugins\\BedwarsRank\\Hologram_Folder");
                    //获取所有文件(文件名以全息投影名命名)
                    File[] Hologram_Folder_List = Hologram_Folder.listFiles();
                    //断定Hologram_Foler_List不为空
                    assert Hologram_Folder_List != null;
                    //检测是否有文件
                    if (Hologram_Folder_List.length > 0) {
                        //遍历获取所有文件名
                        for (File Hologram_name : Hologram_Folder_List) {
                            sender.sendMessage(ChatColor.DARK_GREEN + "找到的全息投影: " + Hologram_name.getName().replace(".yml",""));
                        }
                        //向命令发送者发送没有找到全息投影
                    }else sender.sendMessage(ChatColor.RED + lang.getString("Nothing"));
                    //向命令发送者没有全息
                }else sender.sendMessage(ChatColor.RED + lang.getString("NotPermission"));
            }
        }
        //检测自变量个数是否为2（即/bedwarsrank create test,/bedwarsrank delete test）
        if (args.length == 2) {
            //声明全息投影的保存位置
            File Hologram_File = new File("plugins\\BedwarsRank\\Hologram_Folder", args[1] + ".yml");
            //检测子命令是否为create
            if (args[0].equalsIgnoreCase("create")) {
                //检测命令发送者是否有权限
                if (sender.hasPermission("bedwarsrank.admin.create")) {
                    //检测发送者是否为玩家（这个判断不可删除，因为他无法在控制台旁边生成一个全息投影）
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        Location hologram_location = player.getLocation();
                        //检测玩家的y坐标是否够高，否则生成的全息投影可能会有部分在虚空导致死循环从而导致服务器崩溃
                        if (player.getLocation().getY() > 10) {
                            int times = 0;
                            //检测名字是否重复
                            if (!Hologram_File.exists()) {
                                try {
                                    //向文件写入全息投影信息
                                    Hologram_File.createNewFile();
                                    sender.sendMessage(ChatColor.DARK_GREEN + lang.getString("CreateSuccess"));
                                    YamlConfiguration Hologram_Infomation = YamlConfiguration.loadConfiguration(Hologram_File);
                                    Hologram_Infomation.set("x", player.getLocation().getX());
                                    Hologram_Infomation.set("y", player.getLocation().getY() - 0.5F);
                                    Hologram_Infomation.set("z", player.getLocation().getZ());
                                    Hologram_Infomation.set("world", player.getWorld().getName());
                                    //保存信息
                                    Hologram_Infomation.save(Hologram_File);
                                } catch (IOException ignored) {
                                    //向玩家发送创建失败的信息
                                    sender.sendMessage(ChatColor.RED + lang.getString("CreateFail"));
                                }
                                //创建全息投影
                                while (times < mysqlupdate.item.size() + 1) {
                                    //生成实体
                                    ArmorStand hologram = (ArmorStand) hologram_location.getWorld().spawnEntity(hologram_location, EntityType.ARMOR_STAND);
                                    //设置标题
                                    if (times == 0) hologram.setCustomName(lang.getString("RankGUIName"));
                                    //设置排行榜信息
                                    if (times >= 1) {
                                        //从rankgui的item列表中获取排行榜信息
                                        String name = mysqlupdate.item.get(times - 1).getItemMeta().getDisplayName();
                                        String kills = mysqlupdate.item.get(times - 1).getItemMeta().getLore().get(0).replace("§9", "§f");
                                        String wins = mysqlupdate.item.get(times - 1).getItemMeta().getLore().get(1).replace("§9", "§f");
                                        String destroyedBeds = mysqlupdate.item.get(times - 1).getItemMeta().getLore().get(2).replace("§9", "§f");
                                        //设置排行榜信息
                                        hologram.setCustomName(times + "   " + name + "   " + kills + "   " + wins + "   " + destroyedBeds);
                                    }
                                    //y向下减循环创建
                                    hologram_location.setY(hologram_location.getY() - 0.5F);
                                    //调用方法（设置盔甲架参数）
                                    setArmorStand(hologram);
                                    times += 1;
                                }
                                //对数据库为满10人的空缺位置进行空白补全
                                while (times < 11) {
                                    ArmorStand hologram = (ArmorStand) hologram_location.getWorld().spawnEntity(hologram_location, EntityType.ARMOR_STAND);
                                    hologram.setCustomName(times + "   null   " + lang.getString("kills") + ": 0   " + lang.getString("wins") + ": 0   " + lang.getString("destroyedBeds") + ": 0");
                                    setArmorStand(hologram);
                                    hologram_location.setY(hologram_location.getY() - 0.5F);
                                    times += 1;
                                }
                            } else {
                                //提示名字已存在
                                sender.sendMessage(ChatColor.RED + lang.getString("NameExists"));
                            }
                            //提示你的坐标y太低
                        } else sender.sendMessage(ChatColor.RED + "创建失败，你的坐标y太低了");
                        //提示该命令只有玩家可以执行
                    } else sender.sendMessage(ChatColor.RED + lang.getString("NotPlayer"));
                    //提示没有权限
                } else sender.sendMessage(ChatColor.RED + lang.getString("NotPermission"));

            }
            //检测子命令是否是delete
            if (args[0].equalsIgnoreCase("delete")) {
                //检测是否有权限
                if (sender.hasPermission("bedwarsrank.admin.delete")) {
                    //声明全息投影文件的存放位置
                    File Hologram_name = new File("plugins\\BedwarsRank\\Hologram_Folder", args[1] + ".yml");
                    //加载全息投影文件信息
                    YamlConfiguration Hologram_Information = YamlConfiguration.loadConfiguration(Hologram_name);
                    //判断文件是否存在
                    if (Hologram_name.exists()) {
                        //从存放全息投影文件里获取信息
                        World Hologram_Location_World = Bukkit.getWorld(Hologram_Information.getString("world"));
                        double Hologram_Location_X = Hologram_Information.getDouble("x");
                        double Hologram_Location_Y = Hologram_Information.getDouble("y") + 0.5F;
                        double Hologram_Location_Z = Hologram_Information.getDouble("z");
                        //删除存放全息投影文件
                        Hologram_name.delete();
                        sender.sendMessage(ChatColor.DARK_GREEN + lang.getString("DeleteSuccess"));

                        int times = 0;
                        //删除全息投影
                        while (times < 11) {
                            //声明全息投影的位置
                            Location Hologram_Location = new Location(Hologram_Location_World, Hologram_Location_X, Hologram_Location_Y, Hologram_Location_Z);
                            //获取半径为1个方块里的所有实体
                            Collection<? extends Entity> entities = Hologram_Location_World.getNearbyEntities(Hologram_Location, 1.0, 1.0, 1.0);
                            //遍历实体
                            for (Entity entity : entities) {
                                try {
                                    //对普通的盔甲架进行过滤
                                    if (entity instanceof ArmorStand && entity.getCustomName() != null) {
                                        //获取实体位置信息
                                        World Entity_Location_World = entity.getLocation().getWorld();
                                        double Entity_Location_X = entity.getLocation().getX();
                                        double Entity_Location_Y = entity.getLocation().getY();
                                        double Entity_Location_Z = entity.getLocation().getZ();
                                        //检测实体是否符合要求
                                        if (Entity_Location_World == Hologram_Location_World & Entity_Location_X == Hologram_Location_X & Entity_Location_Y == Hologram_Location_Y & Entity_Location_Z == Hologram_Location_Z) {
                                            entity.remove();
                                            times += 1;
                                            Hologram_Location_Y -= 0.5F;
                                        }
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    } else {
                        //发送名字不存在删除失败的信息
                        sender.sendMessage(ChatColor.RED + lang.getString("DeleteFail").replace("%hologram_name%", args[1]));
                    }
                    //发送没有权限信息
                } else sender.sendMessage(ChatColor.RED + lang.getString("NotPermission"));
            }
        }
        return args.length <= 2;
    }

    private void setArmorStand(ArmorStand hologram){
        //设置盔甲架参数
        hologram.setCustomNameVisible(true);
        hologram.setMarker(true);
        hologram.setVisible(false);
        hologram.setGravity(false);
        hologram.setSmall(true);
        hologram.setBasePlate(true);
        hologram.setArms(false);
    }
}
