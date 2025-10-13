package net.redstone.redextent.ability;

import com.pixelmonmod.pixelmon.api.pokemon.ability.AbstractAbility;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.api.pokemon.type.Type;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import net.redstone.redextent.Config;

import java.util.List;

public class BiomeBlessingSwamp extends AbstractAbility {
    private boolean isInSwamp = false;
    private boolean isGhostInList = false;
    private float attackMultiplier = 1.0f;
    private float specialAttackMultiplier = 1.0f;
    private float speedMultiplier = 1.0f;

    @Override
    public void applySwitchInEffect(PixelmonWrapper newPokemon) {
        // 检查当前群系是否为沼泽
        String biomeName = String.valueOf(newPokemon.bc.onBattlefield(newPokemon));
        this.isInSwamp = biomeName.contains("swamp") || biomeName.contains("marsh") || biomeName.contains("bog");

        if (this.isInSwamp) {
            // 检查宝可梦是否为幽灵系
            boolean isGhostType = newPokemon.isType(Type.GHOST);

            // 获取配置中的幽灵宝可梦名单
            List<String> ghostPokemonNames = Config.getOnGhostPokemonNames();
            String currentPokemonName = newPokemon.getSpecies().getName();

            // 检查当前宝可梦是否在配置列表中
            this.isGhostInList = isGhostType && ghostPokemonNames.stream()
                    .anyMatch(name -> name.equalsIgnoreCase(currentPokemonName));

            // 根据条件设置不同的加成
            if (this.isGhostInList) {
                // 幽灵系且在列表中的宝可梦：特攻+10%，物攻+10%，速度+15%
                this.specialAttackMultiplier = 1.1f;
                this.attackMultiplier = 1.1f;
                this.speedMultiplier = 1.15f;

                newPokemon.bc.sendToAll("pixelmon.abilities.biome_blessing_swamp.ghost_boost",
                        new Object[]{newPokemon.getDisplayName(), this.getTranslatedName()});
            } else {
                // 其他宝可梦：特攻+5%，物攻+5%，攻击总计+30%，速度+15%
                this.specialAttackMultiplier = 1.05f;
                this.attackMultiplier = 1.05f;
                this.speedMultiplier = 1.15f;

                newPokemon.bc.sendToAll("pixelmon.abilities.biome_blessing_swamp.activate",
                        new Object[]{newPokemon.getDisplayName(), this.getTranslatedName()});
            }
        } else {
            // 不在沼泽群系，重置加成
            resetBonuses();
        }
    }

    @Override
    public int[] modifyStats(PixelmonWrapper user, int[] stats) {
        if (this.isInSwamp) {
            int attackIndex = BattleStatsType.ATTACK.getStatIndex();
            int specialAttackIndex = BattleStatsType.SPECIAL_ATTACK.getStatIndex();
            int speedIndex = BattleStatsType.SPEED.getStatIndex();

            // 应用物攻加成
            stats[attackIndex] = (int) (stats[attackIndex] * this.attackMultiplier);

            // 应用特攻加成
            stats[specialAttackIndex] = (int) (stats[specialAttackIndex] * this.specialAttackMultiplier);

            // 应用速度加成
            stats[speedIndex] = (int) (stats[speedIndex] * this.speedMultiplier);

            // 如果是幽灵系且在列表中，应用额外的攻击加成（总计30%）
            if (this.isGhostInList) {
                // 已经应用了10%的加成，需要再增加20%以达到总计30%
                float additionalAttackMultiplier = 1.2f;
                stats[attackIndex] = (int) (stats[attackIndex] * additionalAttackMultiplier);
                stats[specialAttackIndex] = (int) (stats[specialAttackIndex] * additionalAttackMultiplier);
            }
        }
        return stats;
    }

    @Override
    public void applySwitchOutEffect(PixelmonWrapper oldPokemon) {
        // 换下时重置所有状态
        resetBonuses();
        this.isInSwamp = false;
        this.isGhostInList = false;
    }

    private void resetBonuses() {
        this.attackMultiplier = 1.0f;
        this.specialAttackMultiplier = 1.0f;
        this.speedMultiplier = 1.0f;
    }

    @Override
    public boolean canBeIgnored() {
        return false;
    }

    @Override
    public boolean isNegativeAbility() {
        return false;
    }

    @Override
    public boolean needNewInstance() {
        return true;
    }
}