package ir.alijk.atomevents;

import ir.alijk.atomevents.commands.EventCommand;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public final class AtomEvents extends Plugin {
        private static AtomEvents instance;
        public String PREFIX = "&3[&bAtomEvents&3] ";
        public HashMap<UUID, String> eventQueue = new HashMap<>();
        public String eventServer = null;
        public int maxPlayers = 0;
        public boolean isCreated = false;
        public boolean isStarted = false;
        public boolean isRejoinEnabled = false;
        private LuckPerms luckPerms;

        @Override
        public void onEnable() {
                instance = this;


                luckPerms = LuckPermsProvider.get();

                ProxyServer.getInstance().getPluginManager().registerCommand(this, new EventCommand());
        }

        @Override
        public void onDisable() {

        }

        public static AtomEvents getInstance() {
                return instance;
        }

        public LuckPerms getLuckPerms() {
                return luckPerms;
        }
}
