package net.verany.lobbysystem.flagwars.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.verany.lobbysystem.flagwars.VariantType;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class QueueEntry {
    private final UUID uuid;
    private final VariantType variantType;
    private long timestamp = System.currentTimeMillis();
}
