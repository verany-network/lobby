package net.verany.lobbysystem.flagwars.voting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.verany.api.enumhelper.IdentifierType;
import org.bukkit.Material;

@AllArgsConstructor
@Getter
public enum VotingCategory implements IdentifierType<Material> {

    TEAMS(Material.BLUE_BANNER, null),
    MAPS(Material.GRASS_BLOCK, "map"),
    DIAMOND(Material.DIAMOND, "diamond"),
    BOW(Material.BOW,"bow"),
    ITEM_DROPS(Material.ITEM_FRAME, "item_drops"),
    COBWEB(Material.COBWEB, "cobweb");

    private final Material id;
    private final String votingKey;

}