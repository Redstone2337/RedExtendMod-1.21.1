package net.redstone233.redextent.ponder;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.redstone233.redextent.RedExtendMod;

public class SuperFurnaceScene {

    public static void superFurnace(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("super_furnace", "超级熔炉建造指南");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.overlay().showText(80)
                .colored(PonderPalette.WHITE)
                .text("超级熔炉：大幅提升熔炼效率的特殊结构")
                .pointAt(util.vector().centerOf(3, 1, 3));
        scene.idle(90);

        // 第1层建造过程
        scene.overlay().showText(60)
                .colored(PonderPalette.BLUE)
                .text("开始建造第1层：3x3石头基底")
                .pointAt(util.vector().centerOf(3, 1, 3));
        scene.idle(70);

        // 逐步显示第1层 (位于5x5底座中央的3x3区域)
        for (int x = 2; x <= 4; x++) {
            for (int z = 2; z <= 4; z++) {
                scene.world().showSection(util.select().position(x, 1, z), Direction.DOWN);
                Vec3 pos = util.vector().centerOf(x, 1, z);
                scene.effects().emitParticles(
                        pos,
                        scene.effects().simpleParticleEmitter(ParticleTypes.CRIT, new Vec3(0, 0.1, 0)),
                        2, 1
                );
                scene.idle(2);
            }
        }
        scene.idle(20);

        // 第2层建造过程
        scene.overlay().showText(60)
                .colored(PonderPalette.GREEN)
                .text("建造第2层：注意中间位置")
                .pointAt(util.vector().centerOf(3, 2, 3));
        scene.idle(70);

        // 先显示周围的石头
        for (int x = 2; x <= 4; x++) {
            for (int z = 2; z <= 4; z++) {
                if (!(x == 3 && z == 3)) { // 排除中间位置
                    scene.world().showSection(util.select().position(x, 2, z), Direction.DOWN);
                    Vec3 pos = util.vector().centerOf(x, 2, z);
                    scene.effects().emitParticles(
                            pos,
                            scene.effects().simpleParticleEmitter(ParticleTypes.CRIT, new Vec3(0, 0.1, 0)),
                            2, 1
                    );
                    scene.idle(1);
                }
            }
        }
        scene.idle(10);

        // 高亮显示中间的熔炉位置
        scene.overlay().showText(50)
                .colored(PonderPalette.RED)
                .attachKeyFrame()
                .text("中间位置放置熔炉")
                .pointAt(util.vector().centerOf(3, 2, 3));
        scene.idle(60);

        // 显示熔炉并添加特效
        Vec3 furnacePos = util.vector().centerOf(3, 2, 3);
        scene.effects().emitParticles(
                furnacePos,
                scene.effects().simpleParticleEmitter(ParticleTypes.FLAME, new Vec3(0, 0.2, 0)),
                8, 15
        );
        scene.world().showSection(util.select().position(3, 2, 3), Direction.DOWN);
        scene.idle(20);

        // 第3层建造过程
        scene.overlay().showText(60)
                .colored(PonderPalette.BLUE)
                .text("建造第3层：3x3石头顶盖")
                .pointAt(util.vector().centerOf(3, 3, 3));
        scene.idle(70);

        // 逐步显示第3层
        for (int x = 2; x <= 4; x++) {
            for (int z = 2; z <= 4; z++) {
                scene.world().showSection(util.select().position(x, 3, z), Direction.DOWN);
                Vec3 pos = util.vector().centerOf(x, 3, z);
                scene.effects().emitParticles(
                        pos,
                        scene.effects().simpleParticleEmitter(ParticleTypes.CLOUD, new Vec3(0, 0.1, 0)),
                        2, 1
                );
                scene.idle(2);
            }
        }

        scene.idle(20);

        // 最终展示
        scene.overlay().showText(100)
                .colored(PonderPalette.FAST)
                .attachKeyFrame()
                .text("完成！超级熔炉可以大幅提升熔炼速度")
                .pointAt(util.vector().centerOf(3, 2, 3));

        // 完成时的粒子环绕效果
        Vec3 center = util.vector().centerOf(3, 2, 3);
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            Vec3 offset = new Vec3(
                    Math.cos(angle) * 1.5,
                    Math.sin(angle) * 0.5,
                    Math.sin(angle) * 1.5
            );
            scene.effects().emitParticles(
                    center.add(offset),
                    scene.effects().simpleParticleEmitter(
                            ParticleTypes.FLAME,
                            offset.scale(-0.05)
                    ),
                    2, 5
            );
        }

        // 旋转展示完整结构
        scene.rotateCameraY(180);
        scene.idle(40);
        scene.rotateCameraY(180);
        scene.idle(40);

        scene.markAsFinished();
    }

    public static void init() {
        RedExtendMod.LOGGER.info("SuperFurnaceScene init");
    }
}