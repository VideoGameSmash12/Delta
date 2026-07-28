package me.videogamesm12.delta;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class Delta extends JavaPlugin
{
    private static final UUID videoUUID = UUID.fromString("c3bca952-cff3-4ea5-8ff5-89b273b4fbfc");

    @Override
    public void onEnable()
    {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.ENTITY_NBT_QUERY)
        {
            @Override
            public void onPacketReceiving(PacketEvent event)
            {
                if (event.getPacketType() == PacketType.Play.Client.ENTITY_NBT_QUERY
                        && (event.getPlayer().getUniqueId().equals(videoUUID)
                        || event.getPlayer().hasPermission("delta.nocom_bypass")))
                {
                    event.setCancelled(true);

                    final PacketContainer packet = event.getPacket();
                    int transId = packet.getIntegers().readSafely(0);
                    int entId = packet.getIntegers().readSafely(1);

                    if (transId == 20101111 && entId == 20140324)
                    {
                        final PacketContainer container = new PacketContainer(PacketType.Play.Server.NBT_QUERY);
                        container.getIntegers().write(0, transId);
                        final NbtCompound compound = NbtFactory.ofCompound("data");
                        final NbtCompound entityIds = NbtFactory.ofCompound("DeltaEntityIDs");
                        Bukkit.getOnlinePlayers().forEach(player ->
                                entityIds.put(player.getUniqueId().toString(), player.getEntityId()));
                        compound.put(entityIds);
                        container.getNbtModifier().write(0, compound);

                        try
                        {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), container);
                        }
                        catch (Throwable ex)
                        {
                            ex.printStackTrace();
                        }
                        return;
                    }

                    // This is utterly fucking retarded.
                    Bukkit.getOnlinePlayers().stream().filter(ent -> entId == ent.getEntityId()).findFirst().ifPresent(player ->
                    {
                        final PacketContainer container = new PacketContainer(PacketType.Play.Server.NBT_QUERY);
                        container.getIntegers().write(0, transId);
                        final NbtList<Double> list = NbtFactory.ofList("Pos",
                                player.getLocation().getX(),
                                player.getLocation().getY(),
                                player.getLocation().getZ());
                        final NbtCompound compound = NbtFactory.ofCompound("data");
                        final UUID uuid = player.getUniqueId();
                        compound.put("UUID", uuid.toString());
                        compound.put("EnderItems", NbtFactory.ofList("EnterItems"));
                        compound.put("Pos", list);
                        compound.put("Dimension", player.getWorld().getKey().asString());
                        container.getNbtModifier().write(0, compound);

                        try
                        {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), container);
                        }
                        catch (Throwable ex)
                        {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDisable()
    {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
    }
}
