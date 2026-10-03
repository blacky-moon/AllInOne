package miyucomics.hexpose.utils;

import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import miyucomics.hexpose.iotas.TextIota;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ServerChatEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ChatHandler {
    private static final RingBuffer<Message> chatLog = new RingBuffer<>(32);
    private static final ChatHandler INSTANCE = new ChatHandler();

    public static void init() {
        NeoForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        chatLog.add(new Message(
                event.getPlayer().getDisplayName(),
                event.getMessage(),
                Instant.now()
        ));
    }

    public static List<Iota> getLog() {
        Instant now = Instant.now();
        List<Iota> result = new ArrayList<>();
        for (Message msg : chatLog.buffer()) {
            result.add(new ListIota(msg.intoHex(now)));
        }
        return result;
    }

    public static List<Iota> getLast() {
        Message last = chatLog.last();
        if (last == null) {
            return List.of(new NullIota());
        }
        return last.intoHex(Instant.now());
    }

    private static class Message {
        private final Component sender;
        private final Component message;
        private final Instant timestamp;

        public Message(Component sender, Component message, Instant timestamp) {
            this.sender = sender;
            this.message = message;
            this.timestamp = timestamp;
        }

        public List<Iota> intoHex(Instant now) {
            return List.of(
                    new TextIota(sender),
                    new TextIota(message),
                    new DoubleIota(Math.floor(Duration.between(now, timestamp).toMillis() / -50.0))
            );
        }
    }
}