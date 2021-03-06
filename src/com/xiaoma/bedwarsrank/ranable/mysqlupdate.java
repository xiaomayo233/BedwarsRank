package com.xiaoma.bedwarsrank.ranable;

import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class mysqlupdate extends BukkitRunnable {
    //声明变量
    public static Inventory gui;
    public static ArrayList<ItemStack> item;
    @Override
    public void run() {

        //声明config.yml文件路径
        File configfile = new File("plugins\\BedwarsRank", "config.yml");
        //声明lang.yml文件路径
        File langfile = new File("plugins\\BedwarsRank", "lang.yml");

        //加载config.yml文件
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configfile);
        //加载lang.yml文件
        YamlConfiguration lang = YamlConfiguration.loadConfiguration(langfile);

        String color = config.getString("color");


        try {
            //获得config.yml文件里的信息
            String host = config.getString("host");
            int port = config.getInt("port");
            String database = config.getString("database");
            boolean SSL = config.getBoolean("useSSL");
            String url = host + ":" + port + "/" + database + "?useSSL=" + SSL;
            String user = config.getString("user");
            String password = config.getString("password");
            String type = config.getString("type");
            String table = config.getString("table");


            //JDBC连接数据库
            //加载JDBC驱动
            Class.forName("com.mysql.jdbc.Driver");
            //进行连接
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + url, user, password);
            Statement st = conn.createStatement();
            //发送MySQL的查询信息和排名的语句
            ResultSet rs = st.executeQuery("SELECT kills,name,wins,destroyedBeds from "+table+" ORDER BY " + type + " DESC");
            //声明变量
            item = new ArrayList<>();

            int times = 0;

            //获取MySQL里的信息
            while (rs.next()) {
                int kills = rs.getInt("kills");
                int wins = rs.getInt("wins");
                String name = rs.getString("name");
                int destroyedBeds = rs.getInt("destroyedBeds");
                //设置/rank排行榜的物品信息
                ItemStack num = setitem(name,kills,wins,destroyedBeds);
                //添加到列表里
                item.add(num);
                times+=1;
            }
            while(times<10){
                //设置/rank排行榜的物品信息补白
                ItemStack num = setitem("null",0,0,0);
                //添加到列表里
                item.add(num);
                times+=1;
            }
            //创建GUI
            gui = Bukkit.createInventory(null, 45, ChatColor.BLACK + lang.getString("RankGUIName"));
            //设置物品
            if(item.size()>=1) gui.setItem(4, item.get(0));
            if(item.size()>=2) gui.setItem(22, item.get(1));
            if(item.size()>=3) gui.setItem(30, item.get(2));
            if(item.size()>=4) gui.setItem(31, item.get(3));
            if(item.size()>=5) gui.setItem(32, item.get(4));
            if(item.size()>=6) gui.setItem(38, item.get(5));
            if(item.size()>=7) gui.setItem(39, item.get(6));
            if(item.size()>=8) gui.setItem(40, item.get(7));
            if(item.size()>=9) gui.setItem(41, item.get(8));
            if(item.size()>=10) gui.setItem(42, item.get(9));
            //调用方法(我不怎么用方法，这里是因为我觉得塞进来很乱(这里本来就很乱(括号套娃真好玩)))
            hologram_update(color);
            //关闭与MySQL的连接
            rs.close();
            st.close();
            conn.close();
            //获得服务器所有在线玩家并存在一个列表里
            Collection<? extends Player> player = Bukkit.getOnlinePlayers();
            //遍历服务器所有在线玩家
            for(Player p:player){
                try {
                    //对/rank排行榜进行更新
                    if (p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK+lang.getString("RankGUIName"))) {
                        p.openInventory(gui);
                    }
                } catch(Exception ignored){
                }
            }
        } catch (SQLException | ClassNotFoundException ignored) {
            System.out.println("数据库连接失败");
            Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("BedwarsRank"));
        }
    }
    private void hologram_update(String color){
        //加载lang.yml文件(别问我为什么不用全局变量，后来出问题才改成这样的)
        File langfile = new File("plugins\\BedwarsRank\\", "lang.yml");
        YamlConfiguration lang = YamlConfiguration.loadConfiguration(langfile);

        //声明存放全息投影的文件夹
        File Hologram_Folder = new File("plugins\\BedwarsRank\\Hologram_Folder");
        //获取所有文件(文件名以全息投影名命名)
        File[] Hologram_Folder_List = Hologram_Folder.listFiles();
        //断言Hologram_Foler_List不为空
        assert Hologram_Folder_List != null;
        //检测是否有文件
        if(Hologram_Folder_List.length>0) {
            //遍历获取所有文件名
            for (File Hologram_name : Hologram_Folder_List) {
                if (!Hologram_name.isDirectory()) {
                    //获取全息投影信息
                    YamlConfiguration Hologram_Information = YamlConfiguration.loadConfiguration(Hologram_name);
                    World Hologram_Location_World = Bukkit.getWorld(Hologram_Information.getString("world"));
                    double Hologram_Location_X = Hologram_Information.getDouble("x");
                    double Hologram_Location_Y = Hologram_Information.getDouble("y");
                    double Hologram_Location_Z = Hologram_Information.getDouble("z");

                    //声明变量
                    int times = 0;

                    //更新排行榜
                    while (times < item.size()) {
                        //声明一个位置，这个位置是全息投影的创建位置
                        Location Hologram_Location = new Location(Hologram_Location_World, Hologram_Location_X, Hologram_Location_Y, Hologram_Location_Z);
                        //加载区块，否则下面的是无法获取任何实体
                        Hologram_Location.getChunk().load(true);
                        //获得半径为1个方块内的所有实体
                        Collection<? extends Entity> entities = Hologram_Location_World.getNearbyEntities(Hologram_Location, 1.0, 1.0, 1.0);
                        //遍历实体
                        for (Entity entity : entities) {
                            //检测是否是盔甲架(添加玩家是因为，玩家会干扰到下面的，导致无法检测到盔甲架，从而使全息投影创建的时候文件立刻被删除而导致创建失败)
                            if (entity instanceof ArmorStand || entity instanceof Player) {
                                try {
                                    //对信息为空的盔甲架进行过滤
                                    if (!entity.getCustomName().equals(color + times + 1 + "   null   " + lang.getString("kills") + ": 0   " + lang.getString("wins") + ": 0   " + lang.getString("destroyedBeds") + ": 0") && entity.getCustomName() != null) {
                                        //获取实体位置
                                        World Entity_Location_World = entity.getLocation().getWorld();
                                        double Entity_Location_X = entity.getLocation().getX();
                                        double Entity_Location_Y = entity.getLocation().getY();
                                        double Entity_Location_Z = entity.getLocation().getZ();
                                        //检测实体是否符合要求
                                        if (Entity_Location_World == Hologram_Location_World & Entity_Location_X == Hologram_Location_X & Entity_Location_Y == Hologram_Location_Y & Entity_Location_Z == Hologram_Location_Z) {
                                            String name = item.get(times).getItemMeta().getDisplayName().replace("§f", "");
                                            String kills = item.get(times).getItemMeta().getLore().get(0).replace("§9", "");
                                            String wins = item.get(times).getItemMeta().getLore().get(1).replace("§9", "");
                                            String destroyedBeds = item.get(times).getItemMeta().getLore().get(2).replace("§9", "");
                                            times += 1;
                                            entity.setCustomName(color + times + "   " + name + "   " + kills + "   " + wins + "   " + destroyedBeds);
                                            //y向下减继续更新全息投影的信息直到循环结束
                                            Hologram_Location_Y -= 0.5F;
                                        }
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        }//如果检测不到全息投影则删除文件防止死循环导致服务器崩溃(测试的时候就崩了好几次)
                        if (times == 0) {
                            Hologram_name.delete();
                            System.out.println("未找到名为: " + Hologram_name.getName().replace(".yml", " ") + "的全息投影，自动删除名为:" + Hologram_name.getName().replace(".yml", " ") + "的全息投影文件!");
                            times = 10;
                        }
                    }
                }
            }
        }
    }
    private ItemStack setitem(String name,int kills,int wins,int destroyedBeds){

        //声明lang.yml（每次都说好累）
        File langfile = new File("plugins\\BedwarsRank", "lang.yml");
        YamlConfiguration lang = YamlConfiguration.loadConfiguration(langfile);

        //获得lang.yml文件里的信息
        String killsmsg = lang.getString("kills");
        String winsmsg = lang.getString("wins");
        String destroyedBedsmsg = lang.getString("destroyedBeds");

        //设置物品补白
        ItemStack num = new ItemStack(Material.SKULL_ITEM, 1);
        ItemMeta num_meta = num.getItemMeta();
        num_meta.setDisplayName(ChatColor.WHITE + name);
        ArrayList<String> num_meta_lore = new ArrayList<>();
        num_meta_lore.add(ChatColor.BLUE + killsmsg + ": " + kills);
        num_meta_lore.add(ChatColor.BLUE + winsmsg + ": " + wins);
        num_meta_lore.add(ChatColor.BLUE + destroyedBedsmsg + ": " + destroyedBeds);
        num_meta.setLore(num_meta_lore);
        num.setDurability(Short.parseShort("3"));
        num.setItemMeta(num_meta);

        return num;
    }
}