package models;

import models.commands.Instruction;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Device {
    private Map<String, Integer> registries;

    private Map<Integer, Map<String, Integer>> regByCycle;
    private int cycle;

    public Device() {
        this.registries = new HashMap<>();
        this.regByCycle = new HashMap<>();
        this.cycle = 0;
    }

    public void setRegistry(String name, int value) {
        this.registries.put(name, value);
        saveCycleRegistries(cycle, registries);
    }

    public int getRegistryValueAtCycle(String name, int cycle) {
        if (regByCycle.containsKey(cycle)) {
            return this.regByCycle.get(cycle).get(name);
        } else {
            return this.regByCycle.get(this.cycle).get(name);
        }
    }

    public int getCycle() {
        return cycle;
    }

    public void performInstruction(Instruction instr, String registryName) {
        switch (instr) {
            case models.commands.Add addOp -> {
                // first cycle just copy the state
                for (int cycleAdd = 1; cycleAdd <= addOp.getNumberOfCycles(); cycleAdd++) {
                    if (cycleAdd == 2) {
                        var registryValue = this.registries.get(registryName);
                        this.registries.put(registryName, registryValue + addOp.getArgument());
                        cycle++;
                        saveCycleRegistries(cycle, registries);
                    } else {
                        cycle++;
                        saveCycleRegistries(cycle, registries);
                    }
                }

            }
            case models.commands.Noop noop -> {
                for (int cycleAdd = 0; cycleAdd < noop.getNumberOfCycles(); cycleAdd++) {
                    cycle++;
                    saveCycleRegistries(cycle, registries);
                }
            }
        }
    }

    private Map<String, Integer> saveCycleRegistries(int cycle, Map<String, Integer> registries) {
        var tmpMap = registries.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return this.regByCycle.put(cycle, tmpMap);
    }
}
