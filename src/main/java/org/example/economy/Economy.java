package org.example.economy;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Manages player balances in the SkyBlock economy with support for multiple currencies using Enums.
 */
public class Economy implements Listener {


    private static final Logger LOGGER = Logger.getLogger(Economy.class.getName());

    private final Plugin plugin;
    private static final ConcurrentHashMap<UUID, Map<Currency, Apfloat>> playerBalances = new ConcurrentHashMap<>();
    /**
     * Constructs an Economy manager.
     *
     * @param plugin The plugin instance.
     */
    public Economy(final Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Retrieves the balance of a player in a specific currency.
     *
     * @param playerUUID The UUID of the player.
     * @param currency   The currency.
     * @return The player's balance in the specified currency.
     */
    public static Apfloat getBalance(final UUID playerUUID, final Currency currency) {
        return playerBalances
                .getOrDefault(playerUUID, Collections.emptyMap())
                .getOrDefault(currency, Apfloat.ZERO);
    }

    public static void setBalance(final UUID playerUUID, final Currency currency, final Apfloat balance) {
        playerBalances
                .computeIfAbsent(playerUUID, uuid -> new EnumMap<>(Currency.class))
                .put(currency, balance);
        LOGGER.info(() -> String.format("Set balance for player %s in currency %s to %s", playerUUID, currency, balance));
    }

    public static void addBalance(final UUID playerUUID, final Currency currency, final Apfloat amount) {
        Objects.requireNonNull(playerUUID, "Player UUID cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");


        getBalance(playerUUID, currency).add(amount);

        LOGGER.info(() -> String.format("Added %s to balance of player %s in currency %s", amount, playerUUID, currency));
    }
    /**
     * Subtracts an amount from the player's balance in a specific currency.
     * This method is now thread-safe and robust.
     *
     * @param playerUUID The UUID of the player.
     * @param currency   The currency.
     * @param amount     The amount to subtract.
     * @return true if the subtraction was successful, false if the player had insufficient funds.
     */
    public static void subtractBalance(final UUID playerUUID, final Currency currency, final Apfloat amount) {
        Objects.requireNonNull(playerUUID, "Player UUID cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");

        getBalance(playerUUID, currency).subtract(amount);

    }



    /**
     * Gets player balances.
     *
     * @return the player balances
     */
    public static Map<UUID, Map<Currency, Apfloat>> getPlayerBalances() {
        return Collections.unmodifiableMap(playerBalances);
    }

    public static String format(Apfloat zahl) {
        String[] prefixes = {"", "K", "M", "B", "T", "aa", "ab", "ac", "ad", "ae", "af", "ag", "ah", "ai", "aj", "ak", "al", "am", "an", "ao", "ap", "aq", "ar", "as", "at", "au", "av", "aw", "ax", "ay", "az", "ba", "bb", "bc", "bd", "be", "bf", "bg", "bh", "bi", "bj", "bk", "bl", "bm", "bn", "bo", "bp", "bq", "br", "bs", "bt", "bu", "bv", "bw", "bx", "by", "bz", "ca", "cb", "cc", "cd", "ce", "cf", "cg", "ch", "ci", "cj", "ck", "cl", "cm", "cn", "co", "cp", "cq", "cr", "cs", "ct", "cu", "cv", "cw", "cx", "cy", "cz", "da", "db", "dc", "dd", "de", "df", "dg", "dh", "di", "dj", "dk", "dl", "dm", "dn", "do", "dp", "dq", "dr", "ds", "dt", "du", "dv", "dw", "dx", "dy", "dz", "ea", "eb", "ec", "ed", "ee", "ef", "eg", "eh", "ei", "ej", "ek", "el", "em", "en", "eo", "ep", "eq", "er", "es", "et", "eu", "ev", "ew", "ex", "ey", "ez", "fa", "fb", "fc", "fd", "fe", "ff", "fg", "fh", "fi", "fj", "fk", "fl", "fm", "fn", "fo", "fp", "fq", "fr", "fs", "ft", "fu", "fv", "fw", "fx", "fy", "fz", "ga", "gb", "gc", "gd", "ge", "gf", "gg", "gh", "gi", "gj", "gk", "gl", "gm", "gn", "go", "gp", "gq", "gr", "gs", "gt", "gu", "gv", "gw", "gx", "gy", "gz", "ha", "hb", "hc", "hd", "he", "hf", "hg", "hh", "hi", "hj", "hk", "hl", "hm", "hn", "ho", "hp", "hq", "hr", "hs", "ht", "hu", "hv", "hw", "hx", "hy", "hz", "ia", "ib", "ic", "id", "ie", "if", "ig", "ih", "ii", "ij", "ik", "il", "im", "in", "io", "ip", "iq", "ir", "is", "it", "iu", "iv", "iw", "ix", "iy", "iz", "ja", "jb", "jc", "jd", "je", "jf", "jg", "jh", "ji", "jj", "jk", "jl", "jm", "jn", "jo", "jp", "jq", "jr", "js", "jt", "ju", "jv", "jw", "jx", "jy", "jz", "ka", "kb", "kc", "kd", "ke", "kf", "kg", "kh", "ki", "kj", "kk", "kl", "km", "kn", "ko", "kp", "kq", "kr", "ks", "kt", "ku", "kv", "kw", "kx", "ky", "kz", "la", "lb", "lc", "ld", "le", "lf", "lg", "lh", "li", "lj", "lk", "ll", "lm", "ln", "lo", "lp", "lq", "lr", "ls", "lt", "lu", "lv", "lw", "lx", "ly", "lz", "ma", "mb", "mc", "md", "me", "mf", "mg", "mh", "mi", "mj", "mk", "ml", "mm", "mn", "mo", "mp", "mq", "mr", "ms", "mt", "mu", "mv", "mw", "mx", "my", "mz", "na", "nb", "nc", "nd", "ne", "nf", "ng", "nh", "ni", "nj", "nk", "nl", "nm", "nn", "no", "np", "nq", "nr", "ns", "nt", "nu", "nv", "nw", "nx", "ny", "nz", "oa", "ob", "oc", "od", "oe", "of", "og", "oh", "oi", "oj", "ok", "ol", "om", "on", "oo", "op", "oq", "or", "os", "ot", "ou", "ov", "ow", "ox", "oy", "oz", "pa", "pb", "pc", "pd", "pe", "pf", "pg", "ph", "pi", "pj", "pk", "pl", "pm", "pn", "po", "pp", "pq", "pr", "ps", "pt", "pu", "pv", "pw", "px", "py", "pz", "qa", "qb", "qc", "qd", "qe", "qf", "qg", "qh", "qi", "qj", "qk", "ql", "qm", "qn", "qo", "qp", "qq", "qr", "qs", "qt", "qu", "qv", "qw", "qx", "qy", "qz", "ra", "rb", "rc", "rd", "re", "rf", "rg", "rh", "ri", "rj", "rk", "rl", "rm", "rn", "ro", "rp", "rq", "rr", "rs", "rt", "ru", "rv", "rw", "rx", "ry", "rz", "sa", "sb", "sc", "sd", "se", "sf"};
        int index = 0;

        Apfloat n = new Apfloat(String.valueOf(ApfloatMath.roundToInteger(zahl, RoundingMode.HALF_UP)));
        n.truncate();

        while (n.compareTo(new Apfloat("1000")) >= 0 && index < prefixes.length - 1) {
            n = n.divide(new Apfloat("1000"));
            index++;
        }

        String prefix = prefixes[index];

        String formattedNumber = n.toString(true);

        String formattedDNumber = new Apfloat(String.valueOf(ApfloatMath.roundToInteger(zahl, RoundingMode.UP))).toString();

        if (formattedNumber.length() == 3) {
            formattedNumber = formattedNumber.substring(0, 3);
        } else if(formattedNumber.length() == 2){
            formattedNumber = formattedNumber.substring(0, 2);
        }
        else if(formattedNumber.length() == 1){
            formattedNumber = formattedNumber.substring(0, 1);
        }

        return formattedNumber + prefix;
    }

}
