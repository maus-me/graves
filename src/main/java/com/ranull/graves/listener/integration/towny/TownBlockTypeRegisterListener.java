package com.ranull.graves.listener.integration.towny;

import com.ranull.graves.integration.Towny;
import org.bukkit.event.Listener;

public class TownBlockTypeRegisterListener implements Listener {
    private final Towny towny;

    public TownBlockTypeRegisterListener(Towny towny) {
        this.towny = towny;
    }

}
