package net.redstone.redextent.ability;

import com.pixelmonmod.pixelmon.api.battles.AttackCategory;
import com.pixelmonmod.pixelmon.api.battles.attack.AttackRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.ability.AbstractAbility;
import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;

public class FastStart extends AbstractAbility {
    private int turnsRemaining = 3; // 快启动持续时间更短

    @Override
    public void applySwitchInEffect(PixelmonWrapper newPokemon) {
        newPokemon.bc.sendToAll("pixelmon.abilities.faststart", newPokemon.getNickname());
        this.turnsRemaining = 3;
    }

    @Override
    public void applySwitchOutEffect(PixelmonWrapper oldPokemon) {
        this.turnsRemaining = 3; // 退场时重置
    }

    @Override
    public int[] modifyStats(PixelmonWrapper user, int[] stats) {
        if (this.turnsRemaining > 0) {
            int speedIndex = BattleStatsType.SPEED.getStatIndex();
            stats[speedIndex] = (int) (stats[speedIndex] * 1.5); // 速度提升50%
        }
        return stats;
    }

    @Override
    public int[] modifyStatsCancellable(PixelmonWrapper user, int[] stats) {
        boolean moveIgnoresAbility = user.attack != null && user.attack.getMove().getIgnoresAbilities() && !user.isTempAttack;
        if (this.turnsRemaining > 0 && !moveIgnoresAbility) {
            int attackIndex = BattleStatsType.ATTACK.getStatIndex();
            stats[attackIndex] = (int) (stats[attackIndex] * 1.5); // 攻击提升50%
        }
        return stats;
    }

    @Override
    public void startMove(PixelmonWrapper user) {
        if (!user.bc.simulateMode && this.turnsRemaining > 0 && user.attack != null) {
            int[] stats = user.getBattleStats().getBattleStats();
            // 处理光子喷涌的特殊情况
            if (user.attack.isAttack(AttackRegistry.PHOTON_GEYSER) && user.isTempAttack) {
                if (stats[BattleStatsType.ATTACK.getStatIndex()] / 1.5 < stats[BattleStatsType.SPECIAL_ATTACK.getStatIndex()]) {
                    int attackIndex = BattleStatsType.ATTACK.getStatIndex();
                    stats[attackIndex] = (int) (stats[attackIndex] / 1.5);
                }
            } else if (user.attack.getMove().getAttackCategory() == AttackCategory.SPECIAL && user.usingZ) {
                int specialAttackIndex = BattleStatsType.SPECIAL_ATTACK.getStatIndex();
                stats[specialAttackIndex] = (int) (stats[specialAttackIndex] * 1.5);
            }
            user.getBattleStats().setStatsForTurn(stats);
        }
    }

    @Override
    public void applyRepeatedEffect(PixelmonWrapper pokemon) {
        int i = this.turnsRemaining - 1;
        this.turnsRemaining = i;
        if (i == 0) {
            pokemon.bc.sendToAll("pixelmon.abilities.faststartend", pokemon.getNickname());
        }
    }

    @Override
    public boolean needNewInstance() {
        return true;
    }
}