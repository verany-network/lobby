package net.verany.lobbysystem.flagwars;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum Variant {

    TWOTIMESONE("2x1", 2, 45, 25, VariantType.SOLO),
    TWOTIMESTWO("2x2", 3, 50, 30, VariantType.DUO);
    /*TWOTIMESFOUR("2x4", 5, 40, 70, VariantType.SQUAD),
    TWOTIMESFIFE("2x5", 6, 50, 70, VariantType.SQUAD),
    TWOTIMESTEN("2x10", 14, 80, 80, VariantType.SQUAD),
    FOURTIMESONE("4x1", 3, 30, 60, VariantType.SOLO),
    FOURTIMESTWO("4x2", 4, 40, 60, VariantType.DUO),
    FOURTIMESTHREE("4x3", 6, 50, 70, VariantType.TRIO),
    FOURTIMESFOUR("4x4", 7, 50, 75, VariantType.SQUAD),
    FOURTIMESFIFE("4x5", 15, 60, 70, VariantType.SQUAD),
    FOURTIMESSIX("4x6", 15, 60, 60, VariantType.SQUAD),
    EIGHTTIMESONE("8x1", 3, 40, 60, VariantType.SOLO),
    EIGHTTIMESTWO("8x2", 6, 80, 70, VariantType.DUO),
    EIGHTTIMESTHREE("8x3", 6, 70, 60, VariantType.TRIO),
    SIXTEENTIMESONE("16x1", 7, 60, 60, VariantType.SOLO),
    SEXTEENTIMESFOUR("16x4", 30, 70, 60, VariantType.SQUAD),
    TWOTIMESTHIRTYTWO("2x32", 30, 120, 120, VariantType.EVENT),
    CLANWAR("ClanWar", 8, 60, 40, VariantType.CLANWAR);*/

    private final String name;
    private final int maxPlayers;
    private final int maxMinutes;
    private final int countdown;
    private final VariantType variantType;

    public int getMaxOnlinePlayers() {
        return getTeams() * getMaxPlayersInTeam();
    }

    public int getTeams() {
        return Integer.parseInt(name.split("x")[0]);
    }

    public int getMaxPlayersInTeam() {
        return Integer.parseInt(name.split("x")[1]);
    }

    public static List<Variant> getVariants(VariantType variantType) {
        return Arrays.stream(values()).filter(variant -> variant.getVariantType().equals(variantType)).collect(Collectors.toList());
    }

}
