package ir.alijk.atomevents.commands;

import ir.alijk.atomevents.AtomEvents;
import ir.alijk.atomevents.Common;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EventCommand extends Command {
        public EventCommand() {
                super("event");
        }
        private final List<String> ranksOrder = Arrays.asList(
                "rgb",
                "co-owner",
                "dev",
                "builder",
                "youtuber3",
                "streamer3",
                "aparater3",
                "atom",
                "mvp+",
                "mvp",
                "youtuber2",
                "streamer2",
                "aparater2",
                "vip+",
                "vip",
                "youtuber",
                "streamer",
                "aparater",
                "default"
        );

        @Override
        public void execute(CommandSender sender, String[] args) {
                if (!(sender instanceof ProxiedPlayer))
                        return;

                ProxiedPlayer player = (ProxiedPlayer) sender;

                if (args.length == 0) {
                        Common.send(sender, "&bBa estefade az &3/event join &bmitoonid vared safe event beshid!");
                        return;
                } else if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("join")) {
                                if (!AtomEvents.getInstance().isCreated) {
                                        Common.send(sender, "&cHich eventi dar hale bargozari nist!");
                                } else if (AtomEvents.getInstance().isStarted) {
                                  Common.send(sender, "&cEvent ghablan shoroo shode va emkan vared shodan digar vojud nadarad!");
                                } else if (AtomEvents.getInstance().eventQueue.containsKey(player.getUniqueId())) {
                                        Common.send(sender, "&cShoma az ghabl dakhel saf hastid va niazi be dobare join nist!");
                                } else {
                                        try {
                                                // User user = AtomEvents.getInstance().getLuckPerms().getUserManager().getUser(player.getUniqueId());
                                                // String rank = user.getPrimaryGroup();
                                                String playerRank = null;

                                                for (String rank: ranksOrder) {
                                                        if (player.hasPermission("atomevents." + rank)) {
                                                                playerRank = rank;
                                                                break;
                                                        }
                                                }

                                                if (playerRank == null) {
                                                        playerRank = "default";
                                                }

                                                AtomEvents.getInstance().eventQueue.put(player.getUniqueId(), playerRank);
                                                Common.send(sender, "&aBa movafaghiat vared safe event shodid!");
                                                System.out.println(player.getName() + " is " + playerRank);


                                        } catch (NullPointerException exception) {
                                                Common.send(sender, "&cMoshkeli dar join shodan be event vojood dasht, Lotfan dobare emtehan konid!");
                                        }
                                }
                                return;
                        } else if (args[0].equalsIgnoreCase("rejoin")) {
                                if (player.hasPermission("atomevents.rejoin")) {
                                        if (!AtomEvents.getInstance().isStarted) {
                                                Common.send(player, "&cEvent hanooz shoroo nashode...");
                                                return;
                                        }
                                        if (!AtomEvents.getInstance().isRejoinEnabled) {
                                                Common.send(player, "&cEmkan rejoin dar in event vojood nadare :(");
                                                return;
                                        }
                                        Common.send(player, "&aDarhale ersal shoma be server event :)...");

                                        ServerInfo eventServer = ProxyServer.getInstance().getServerInfo(AtomEvents.getInstance().eventServer);
                                        player.connect(eventServer);
                                } else {
                                        Common.send(player, "&cShoma rank donor (mesle vip, mvp, ...) nadarid!In command makhsoos hamian server hast.");
                                }
                        }
                }



                if (sender.hasPermission("atomevent.admin")) {
                        switch (args[0].toLowerCase()) {
                                case "create":
                                        if (args.length != 4) {
                                                Common.send(sender, "&cRavesh dorost estefade: &4/event create <EventServer> <MaxPlayers> <RejoinEnabled>");
                                                return;
                                        }
                                        if (!AtomEvents.getInstance().isCreated) {
                                                AtomEvents.getInstance().isCreated = true;
                                                AtomEvents.getInstance().eventServer = args[1];
                                                AtomEvents.getInstance().maxPlayers = Integer.parseInt(args[2]);
                                                AtomEvents.getInstance().isRejoinEnabled = args[3].equalsIgnoreCase("true");
                                                Common.send(sender, "&aEvent ijad shod ...");
                                        } else {
                                                Common.send(sender, "&cEvent ghablan sakhte shode, emkan dobare sazi nist!");
                                        }
                                        break;
                                case "destroy":
                                        if (AtomEvents.getInstance().isCreated) {
                                                AtomEvents.getInstance().eventServer = null;
                                                AtomEvents.getInstance().maxPlayers = 0;
                                                AtomEvents.getInstance().isCreated = false;
                                                AtomEvents.getInstance().isStarted = false;
                                                AtomEvents.getInstance().eventQueue.clear();
                                                Common.send(sender, "&aEvent ba movafaghiat az bein raft...");
                                        } else {
                                                Common.send(sender, "&cHich event i dar in server shoroo nashode!");
                                        }
                                        break;
                                case "start":
                                        if (!AtomEvents.getInstance().isStarted && AtomEvents.getInstance().isCreated) {
                                                AtomEvents.getInstance().isStarted = true;
                                                Common.send(sender, "&aEvent ba movafaghiat shoroo shod, dar hale ersal player ha...");
                                                startMovingPlayers();
                                                Common.send(sender, "&aTamami player ha ba movafaghiat ersal shodand...");
                                        } else {
                                                Common.send(sender, "&cHich event i dar in server sakhte nashode ya event ghablan start shode!");
                                        }
                                        break;
                                case "send":
                                        if (!AtomEvents.getInstance().isStarted && AtomEvents.getInstance().isCreated) {
                                                if (args.length != 2) {
                                                        Common.send(sender, "&cRavesh dorost estefade: &4/event send <Amount>");
                                                        return;
                                                }
                                                Integer amount = Integer.parseInt(args[1]);

                                                Common.send(sender, "&aDarhale ersal %amount% player be event...".replace("%amount%", amount.toString()));
                                                sendPlayers(amount);
                                                Common.send(sender, "&aTedad %amount% player be event ersal shodand...".replace("%amount%", amount.toString()));
                                        } else {
                                                Common.send(sender, "&cHich event i dar in server sakhte nashode ya event ghablan start shode!");
                                        }
                                        break;
                                case "count":
                                        if (AtomEvents.getInstance().isCreated) {
                                                int count = AtomEvents.getInstance().eventQueue.size();
                                                Common.send(sender, "&aTedad afradi ke dar safe event hastand: &2&l" + count);
                                        } else {
                                                Common.send(sender, "&cHich event i dar in server sakhte nashode!");
                                        }
                                        break;
                                case "togglerejoin":
                                        if (AtomEvents.getInstance().isRejoinEnabled) {
                                                AtomEvents.getInstance().isRejoinEnabled = false;
                                                Common.send(player, "&cRejoin gheire faal shod.");
                                        } else {
                                                AtomEvents.getInstance().isRejoinEnabled = true;
                                                Common.send(player, "&aRejoin faal shod.");
                                        }
                                        break;
                        }
                }
        }

        public HashMap<String, List<UUID>> sortByRanks() {
                HashMap<UUID, String> queuePlayers = AtomEvents.getInstance().eventQueue;
                HashMap<String, List<UUID>> queuePlayersOrganized = new HashMap<>();

                for (String rank: ranksOrder) {
                        queuePlayersOrganized.put(rank, new LinkedList<>());
                }

                queuePlayers.forEach((uuid, rank) -> {
                        if (queuePlayersOrganized.containsKey(rank)) {
                                queuePlayersOrganized.get(rank).add(uuid);
                        } else {
                                queuePlayersOrganized.get("default").add(uuid);
                        }
                });

                return queuePlayersOrganized;
        }

        public void sendPlayers(int amount) {
                HashMap<String, List<UUID>> sortedPlayers = sortByRanks();
                HashMap<String, List<UUID>> sentPlayers = new HashMap<>();
                for (String rank: ranksOrder) {
                        sentPlayers.put(rank, new LinkedList<>());
                }

                ServerInfo eventServer = ProxyServer.getInstance().getServerInfo(AtomEvents.getInstance().eventServer);

                AtomicInteger count = new AtomicInteger();

                sortedPlayers.forEach((rank, uuidList) -> {
                        for (UUID uuid: uuidList) {
                                if (count.intValue() < amount) {
                                        try {
                                                ProxiedPlayer user = ProxyServer.getInstance().getPlayer(uuid);
                                                user.connect(eventServer);
                                                Common.send(user, "&aDarhale enteghal shoma be server event...");
                                                count.getAndIncrement();
                                                sentPlayers.get(rank).add(uuid);
                                        } catch (NullPointerException ignored) {
                                                // Nothing
                                        }
                                } else {
                                        break;
                                }
                        }
                });

                // Cleaning up sent players
                sentPlayers.forEach((rank, uuidList) -> {
                        for (UUID uuid: uuidList) {
                                AtomEvents.getInstance().eventQueue.remove(uuid);
                        }
                });
        }

        public void startMovingPlayers() {
                HashMap<String, List<UUID>> sortedPlayers = sortByRanks();
                ServerInfo eventServer = ProxyServer.getInstance().getServerInfo(AtomEvents.getInstance().eventServer);

                int max = AtomEvents.getInstance().maxPlayers;
                AtomicInteger count = new AtomicInteger();

                sortedPlayers.forEach((rank, uuidList) -> {
                        for (UUID uuid: uuidList) {
                                if (count.intValue() < max) {
                                        try {
                                                ProxiedPlayer user = ProxyServer.getInstance().getPlayer(uuid);
                                                user.connect(eventServer);
                                                Common.send(user, "&aDarhale enteghal shoma be server event...");
                                                count.getAndIncrement();
                                                System.out.println(count.intValue() + " | " + max);
                                        } catch (NullPointerException ignored) {
                                                // Nothing
                                        }
                                } else {
                                        System.out.println("else");
                                        break;
                                }
                        }
                });
        }
}
