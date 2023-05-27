[![Java-badge](https://forthebadge.com/images/badges/made-with-java.svg)]
![Spigot-badge](https://user-images.githubusercontent.com/102294006/234592459-763d181e-43f1-47f2-972a-93b612bcb7fe.svg)


## ✨ Event-Core
A Minecraft Core Plugin That Helps You Create And Announce Minecraft Events. With Bungeecord Servers Support. Built For SpigotMC-1.19.4

## ⚙ How to install
Simply go to [The Spigot Page](https://spigotmc.org) and download the plugin for your server or compile the plugin by yourself.

After installing just run the server, this should spawn a Event-Core folder in plugins folder

When you successfully installed the plugin, you'll see a INFO log in your console that says the plugin has been enabled.

```
- This application is under MIT License & you cant resell this plugin -
```

## 🔹 Config
```yml
Prefix: "&c&lEVENT&f&l »"
EventServer: "event" # Your Bungeecord Event Server Name
Duration: 300 # Events Duration In Seconds
Messages:
  NoPermission: "&cYou Dont Have Enough Permissions." # eventcore.admin
  AlreadyStarted: "&cThere Is A Active Event."
  NoDescription: "&dA Normal Event Has Been Started. Use \"/Event Join\" To Participate."
  EventEnd: "&dEvent Has Been Ended. No One Can Join Now."
  NoActiveEvent: "&cNo Events Are Currently Available."
```
