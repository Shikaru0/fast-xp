package com.example.examplemod.config;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class ModMenuIntegration implements IConfigScreenFactory {
    @Override
    public Screen createScreen(ModContainer container, Screen parent) {
        return new FastxpConfigScreen(parent);
    }
}