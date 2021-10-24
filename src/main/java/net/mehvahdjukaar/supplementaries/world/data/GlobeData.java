package net.mehvahdjukaar.supplementaries.world.data;

import net.mehvahdjukaar.supplementaries.client.renderers.GlobeTextureManager;
import net.mehvahdjukaar.supplementaries.network.NetworkHandler;
import net.mehvahdjukaar.supplementaries.network.SyncGlobeDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlobeData extends SavedData {
    private static final int TEXTURE_H = 16;
    private static final int TEXTURE_W = 32;
    public static final String DATA_NAME = "supplementariesGlobeData";

    public final byte[][] globePixels;
    public final long seed;

    //generate new from seed
    public GlobeData(long seed) {
        this.seed = seed;
        this.globePixels = GlobeDataGenerator.generate(this.seed);
    }

    //from tag
    public GlobeData(CompoundTag tag) {
        this.globePixels = new byte[TEXTURE_W][TEXTURE_H];
        for (int i = 0; i < TEXTURE_H; i++) {
            this.globePixels[i] = tag.getByteArray("colors_" + i);
        }
        this.seed = tag.getLong("seed");
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        for (int i = 0; i < globePixels.length; i++) {
            nbt.putByteArray("colors_" + i, this.globePixels[i]);
        }
        nbt.putLong("seed", this.seed);
        return nbt;
    }

    //call after you modify the data value
    public void sendToClient(Level world) {
        this.setDirty();
        if (!world.isClientSide)
            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncGlobeDataPacket(this));
    }

    //data received from network is stored here
    private static GlobeData clientSide = null;

    @Nullable
    public static GlobeData get(Level world) {
        if (world instanceof ServerLevel server) {
            return world.getServer().overworld().getDataStorage().computeIfAbsent(GlobeData::new,
                    () -> new GlobeData(server.getSeed()),
                    DATA_NAME);
        } else {
            return clientSide;
        }
    }

    public static void set(ServerLevel level, GlobeData pData) {
        level.getServer().overworld().getDataStorage().set(DATA_NAME, pData);
    }

    public static void setClientData(GlobeData data) {
        clientSide = data;
        GlobeTextureManager.INSTANCE.update();
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide) {
            GlobeData data = GlobeData.get(event.getPlayer().level);
            if (data != null)
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()),
                        new SyncGlobeDataPacket(data));
        }
    }
}



