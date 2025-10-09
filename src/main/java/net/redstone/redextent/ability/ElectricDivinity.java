package net.redstone.redextent.ability;

import com.pixelmonmod.pixelmon.api.battles.AttackCategory;
import com.pixelmonmod.pixelmon.api.pokemon.ability.AbstractAbility;
import com.pixelmonmod.pixelmon.api.pokemon.type.Type;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;

public class ElectricDivinity extends AbstractAbility {
    private boolean isElectricType = false;
    private float physicalAttackBonus = 1.0f;
    private float specialAttackBonus = 1.0f;
    private float speedBonus = 1.0f;

    @Override
    public void applySwitchInEffect(PixelmonWrapper newPokemon) {
        // 检查宝可梦是否为电系
        this.isElectricType = newPokemon.isType(Type.ELECTRIC);

        if (this.isElectricType) {
            // 电属性宝可梦上场后获得加成
            this.physicalAttackBonus = 1.2f;  // 物攻+20%
            this.specialAttackBonus = 1.25f;  // 特攻+25%
            this.speedBonus = 1.05f;          // 速度+5%

            // 应用属性加成到宝可梦状态
            applyStatBonuses(newPokemon);

            newPokemon.bc.sendToAll("pixelmon.abilities.electric_divinity.activate",
                    new Object[]{newPokemon.getDisplayName(), this.getTranslatedName()});
        } else {
            // 非电系宝可梦没有额外加成
            this.physicalAttackBonus = 1.0f;
            this.specialAttackBonus = 1.0f;
            this.speedBonus = 1.0f;
        }
    }

    /**
     * 应用属性加成到宝可梦的基础状态
     */
    private void applyStatBonuses(PixelmonWrapper pokemon) {
        // 这里可以通过修改宝可梦的基础状态来实现永久加成
        // 或者通过临时状态加成来实现
    }

    @Override
    public int[] modifyStats(PixelmonWrapper user, int[] stats) {
        if (this.isElectricType) {
            // 应用速度加成到战斗状态
            int speedIndex = BattleStatsType.SPEED.ordinal();
            if (speedIndex < stats.length) {
                stats[speedIndex] = (int)(stats[speedIndex] * this.speedBonus);
            }
        }
        return stats;
    }

    @Override
    public int modifyDamageUser(int damage, PixelmonWrapper user, PixelmonWrapper target, Attack a) {
        if (this.isElectricType && a != null) {
            // 根据技能类型应用物攻或特攻加成
            if (a.getAttackCategory() == AttackCategory.PHYSICAL) {
                return (int)(damage * this.physicalAttackBonus);
            } else if (a.getAttackCategory() == AttackCategory.SPECIAL) {
                return (int)(damage * this.specialAttackBonus);
            }
        }
        return damage;
    }

    @Override
    public int modifyDamageTarget(int damage, PixelmonWrapper user, PixelmonWrapper target, Attack a) {
        // 检查对手使用的技能是否克制电系
        if (this.isElectricType && a != null && isMoveSuperEffectiveAgainstElectric(a)) {
            // 克制电系的技能威力减少5%
            float reduction = 0.95f;

            // 根据技能类型应用不同的减伤
            if (a.getAttackCategory() == AttackCategory.PHYSICAL) {
                damage = (int)(damage * reduction);
            } else if (a.getAttackCategory() == AttackCategory.SPECIAL) {
                damage = (int)(damage * reduction);
            }

            target.bc.sendToAll("pixelmon.abilities.electric_divinity.reduce_damage",
                    new Object[]{target.getDisplayName(), a.getMove().getTranslatedName()});
        }
        return damage;
    }

    /**
     * 检查技能是否克制电系
     * 电系被地面系克制
     */
    private boolean isMoveSuperEffectiveAgainstElectric(Attack attack) {
        if (attack.getType() == null) return false;

        // 地面系技能克制电系
        return attack.getType().is(Type.GROUND);
    }

    @Override
    public void applySwitchOutEffect(PixelmonWrapper oldPokemon) {
        // 换下时重置状态
        this.isElectricType = false;
        this.physicalAttackBonus = 1.0f;
        this.specialAttackBonus = 1.0f;
        this.speedBonus = 1.0f;
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

    @Override
    public void onAllyFaint(PixelmonWrapper pokemon, PixelmonWrapper fainted, PixelmonWrapper source) {
        // 队友倒下时重新检查自身状态
        if (pokemon != null) {
            this.isElectricType = pokemon.isType(Type.ELECTRIC);
        }
    }

    @Override
    public void onSelfFaint(PixelmonWrapper pokemon, PixelmonWrapper source) {
        // 自身倒下时重置状态
        applySwitchOutEffect(pokemon);
    }
}