package net.redstone.redextent.ability;

import com.pixelmonmod.pixelmon.api.battles.AttackCategory;
import com.pixelmonmod.pixelmon.api.pokemon.ability.AbstractAbility;
import com.pixelmonmod.pixelmon.api.pokemon.type.Type;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;

public class FightingDivinity extends AbstractAbility {
    private boolean isFightingType = false;
    private float specialAttackBonus = 1.0f;
    private float attackBonus = 1.0f;

    @Override
    public void applySwitchInEffect(PixelmonWrapper newPokemon) {
        // 检查宝可梦是否为格斗系
        this.isFightingType = newPokemon.isType(Type.FIGHTING);

        if (this.isFightingType) {
            // 格斗系宝可梦上场时，特攻和物攻伤害各加10%
            this.specialAttackBonus = 1.1f;
            this.attackBonus = 1.1f;

            newPokemon.bc.sendToAll("pixelmon.abilities.fighting_divinity.activate",
                    new Object[]{newPokemon.getDisplayName(), this.getTranslatedName()});
        } else {
            // 非格斗系宝可梦没有额外加成
            this.specialAttackBonus = 1.0f;
            this.attackBonus = 1.0f;
        }
    }

    @Override
    public int[] modifyPowerAndAccuracyUser(int power, int accuracy, PixelmonWrapper user, PixelmonWrapper target, Attack attack) {
        if (attack != null && attack.getType().is(Type.FIGHTING)) {
            // 格斗系技能威力大幅度提升
            // 格斗系技能威力提升50%
            float fightingMoveMultiplier = 1.5f;
            int newPower = (int)(power * fightingMoveMultiplier);

            // 如果宝可梦是格斗系，再应用特攻/物攻加成
            if (this.isFightingType) {
                if (attack.getAttackCategory() == AttackCategory.SPECIAL) {
                    newPower = (int)(newPower * this.specialAttackBonus);
                } else if (attack.getAttackCategory() == AttackCategory.PHYSICAL) {
                    newPower = (int)(newPower * this.attackBonus);
                }

                user.bc.sendToAll("pixelmon.abilities.fighting_divinity.boost",
                        new Object[]{user.getDisplayName(), attack.getMove().getTranslatedName()});
            }

            return new int[]{newPower, accuracy};
        }
        return new int[]{power, accuracy};
    }

    @Override
    public void applySwitchOutEffect(PixelmonWrapper oldPokemon) {
        // 换下时重置状态
        this.isFightingType = false;
        this.specialAttackBonus = 1.0f;
        this.attackBonus = 1.0f;
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