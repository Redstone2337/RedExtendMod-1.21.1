package net.redstone.redextent.ability;

import com.pixelmonmod.api.pokemon.requirement.impl.FormRequirement;
import com.pixelmonmod.api.pokemon.requirement.impl.SpeciesRequirement;
import com.pixelmonmod.api.registry.RegistryValue;
import com.pixelmonmod.pixelmon.api.battles.AttackCategory;
import com.pixelmonmod.pixelmon.api.pokemon.ability.AbstractAbility;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.pokemon.species.Stats;
import com.pixelmonmod.pixelmon.api.pokemon.type.Type;
import com.pixelmonmod.pixelmon.api.spawning.AbstractSpawner;
import com.pixelmonmod.pixelmon.api.spawning.SpawnInfo;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.pokemon.SpawnInfoPokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.tools.MutableBoolean;

public class DragonRise extends AbstractAbility {
    private int turnCount = 0;
    private boolean activated = false;
    private float specialAttackMultiplier = 1.0f;
    private float attackMultiplier = 1.0f;
    private float speedMultiplier = 1.0f;

    @Override
    public void applySwitchInEffect(PixelmonWrapper newPokemon) {
        // 检查宝可梦是否为龙属性
        if (newPokemon.isType(Type.DRAGON)) {
            this.activated = true;
            this.turnCount = 1; // 第一回合

            // 初始增益：特攻+20%，物攻+30%，速度+10%
            this.specialAttackMultiplier = 1.2f;
            this.attackMultiplier = 1.3f;
            this.speedMultiplier = 1.1f;

            newPokemon.bc.sendToAll("pixelmon.abilities.dragon_rise.activate",
                    new Object[]{newPokemon.getDisplayName(), this.getTranslatedName()});
            this.applyDragonRiseEffect(newPokemon);
        }
    }

    @Override
    public void applyStartOfTurnEffect(PixelmonWrapper pw) {
        if (this.activated && pw.isType(Type.DRAGON)) {
            if (this.turnCount <= 3) { // 效果持续三回合
                // 每回合增加：特攻+5%，物攻+10%，速度+5%
                this.specialAttackMultiplier += 0.05f;
                this.attackMultiplier += 0.10f;
                this.speedMultiplier += 0.05f;

                this.turnCount++;
                this.applyDragonRiseEffect(pw);
            } else {
                // 三回合后效果结束
                this.activated = false;
                this.specialAttackMultiplier = 1.0f;
                this.attackMultiplier = 1.0f;
                this.speedMultiplier = 1.0f;
                pw.bc.sendToAll("pixelmon.abilities.dragon_rise.end",
                        new Object[]{pw.getDisplayName()});
            }
        }
    }

    private void applyDragonRiseEffect(PixelmonWrapper pw) {
        pw.bc.sendToAll("pixelmon.abilities.dragon_rise.boost",
                new Object[]{
                        pw.getDisplayName(),
                        (int)((this.specialAttackMultiplier - 1.0f) * 100),
                        (int)((this.attackMultiplier - 1.0f) * 100),
                        (int)((this.speedMultiplier - 1.0f) * 100),
                        this.turnCount
                });
    }

    @Override
    public int[] modifyPowerAndAccuracyUser(int power, int accuracy, PixelmonWrapper user, PixelmonWrapper target, Attack a) {
        if (this.activated && user.isType(Type.DRAGON)) {
            int newPower = power;

            // 根据攻击类型应用不同的增益
            if (a != null) {
                if (a.getAttackCategory() == AttackCategory.SPECIAL) {
                    // 特殊攻击使用特攻增益
                    newPower = (int)(power * this.specialAttackMultiplier);
                } else if (a.getAttackCategory() == AttackCategory.PHYSICAL) {
                    // 物理攻击使用物攻增益
                    newPower = (int)(power * this.attackMultiplier);
                }

                // 检查是否是飞行系的画龙点睛技能，提供额外加成
                if (a.getType().is(Type.FLYING) && "dragon_ascent".equalsIgnoreCase(a.getType().getRegisteredName())) {
                    // 画龙点睛获得双倍增益（特攻和物攻都应用）
                    float combinedMultiplier = (this.specialAttackMultiplier + this.attackMultiplier) / 2.0f;
                    newPower = (int)(newPower * combinedMultiplier);
                    user.bc.sendToAll("pixelmon.abilities.dragon_rise.dragon_ascent",
                            new Object[]{user.getDisplayName()});
                }
            }

            return new int[]{newPower, accuracy};
        }
        return new int[]{power, accuracy};
    }

    @Override
    public float modifyPriority(PixelmonWrapper pokemon, float priority, MutableBoolean fractional) {
        if (this.activated && pokemon.isType(Type.DRAGON)) {
            // 应用速度增益
            return priority * this.speedMultiplier;
        }
        return priority;
    }

    @Override
    public void applySwitchOutEffect(PixelmonWrapper oldPokemon) {
        // 换下时重置效果
        this.activated = false;
        this.turnCount = 0;
        this.specialAttackMultiplier = 1.0f;
        this.attackMultiplier = 1.0f;
        this.speedMultiplier = 1.0f;
    }

    @Override
    public float getMultiplier(AbstractSpawner spawner, SpawnInfo spawnInfo, float sum, float rarity) {
        if (!(spawnInfo instanceof SpawnInfoPokemon spawnInfoPokemon)) {
            return 1.0F;
        }

        RegistryValue<Species> specSpecies = spawnInfoPokemon.getPokemonSpec().getValue(SpeciesRequirement.class).orElse(null);
        if (spawnInfoPokemon.getPokemonSpec() != null && specSpecies != null && specSpecies.isInitialized()) {
            Species species = spawnInfoPokemon.getSpecies();
            if (species == null) {
                return 1.0F;
            }

            String form = spawnInfoPokemon.getPokemonSpec().getValue(FormRequirement.class).orElse("");
            Stats stats = species.getForm(form);
            if (stats != null && stats.getTypes() != null) {
                // 龙属性宝可梦更可能拥有此特性
                return stats.getTypes().contains(Type.DRAGON) ? 3.0F : 1.0F;
            }
        }
        return 1.0F;
    }

    @Override
    public boolean canBeIgnored() {
        return false;
    }

    @Override
    public boolean isNegativeAbility() {
        return false;
    }
}