package org.patryk3211.powergrid.compat.cc.clutch;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.patryk3211.powergrid.kinetics.generator.clutch.GeneratorClutchBlockEntity;
import org.patryk3211.powergrid.kinetics.generator.rotor.RotorBehaviour;

import java.util.Locale;

public class GeneratorClutchPeripheral implements IPeripheral {
    private final GeneratorClutchBlockEntity clutch;

    public GeneratorClutchPeripheral(GeneratorClutchBlockEntity clutch) {
        this.clutch = clutch;
    }

    @Override
    public @NotNull String getType() {
        return "powergrid_generator_clutch";
    }

    private ScrollOptionBehaviour<?> getModeBehaviour() {
        var clutchMode = clutch.getBehaviour(ScrollOptionBehaviour.TYPE);
        if (clutchMode instanceof ScrollOptionBehaviour<?> scroll) {
            return scroll;
        }
        return null;
    }

    private void setModeValue(GeneratorClutchBlockEntity.ClutchMode modeValue) {
        var modeBehaviour = getModeBehaviour();
        if (modeBehaviour == null) {
            return;
        }
        modeBehaviour.setValue(modeValue.ordinal());
    }

    @LuaFunction
    public String mode() {
        var modeBehaviour = getModeBehaviour();
        if (modeBehaviour == null) {
            return "unknown";
        }
        return modeBehaviour.get().toString().toLowerCase(Locale.ROOT);
    }

    @LuaFunction(mainThread = true)
    public void setMode(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException("mode must not be null");
        }
        var normalized = modeName.trim().toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "generator", "gen" -> setModeValue(GeneratorClutchBlockEntity.ClutchMode.GENERATOR);
            case "motor", "mot" -> setModeValue(GeneratorClutchBlockEntity.ClutchMode.MOTOR);
            default -> throw new IllegalArgumentException("Unknown clutch mode: " + modeName);
        }
    }

    @LuaFunction(mainThread = true)
    public void setGeneratorMode() {
        setModeValue(GeneratorClutchBlockEntity.ClutchMode.GENERATOR);
    }

    @LuaFunction(mainThread = true)
    public void setMotorMode() {
        setModeValue(GeneratorClutchBlockEntity.ClutchMode.MOTOR);
    }

    @LuaFunction(mainThread = true)
    public void toggleMode() {
        var modeBehaviour = getModeBehaviour();
        if (modeBehaviour == null) {
            return;
        }
        setModeValue(modeBehaviour.get() == GeneratorClutchBlockEntity.ClutchMode.GENERATOR
                ? GeneratorClutchBlockEntity.ClutchMode.MOTOR
                : GeneratorClutchBlockEntity.ClutchMode.GENERATOR);
    }

    @LuaFunction
    public double load() {
        return clutch.getLoad();
    }

    @LuaFunction
    public double rpm() {
        var rotorBehaviour = clutch.getBehaviour(RotorBehaviour.TYPE);
        if (rotorBehaviour == null) {
            return 0;
        }
        return rotorBehaviour.getAngularVelocity();
    }

    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (other instanceof GeneratorClutchPeripheral that) {
            return this.clutch == that.clutch;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return clutch.hashCode();
    }

}