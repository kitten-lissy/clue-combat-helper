package com.cluecombathelper;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ClueCombatHelperTest {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(ClueCombatHelperPlugin.class);
        RuneLite.main(args);
    }
}
