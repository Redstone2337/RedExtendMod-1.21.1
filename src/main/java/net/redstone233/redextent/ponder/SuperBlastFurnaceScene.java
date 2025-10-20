package net.redstone233.redextent.ponder;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.redstone233.redextent.RedExtendMod;
import org.joml.Vector3d;

public class SuperBlastFurnaceScene {

    public static void superBlastFurnace(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("super_blast_furnace", "超级高炉建造指南");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        scene.overlay().showText(80)
                .colored(PonderPalette.WHITE)
                .text("超级高炉：高效冶炼矿石的特殊结构")
                .pointAt(util.vector().centerOf(3, 1, 3));
        scene.idle(90);

        // 第1层：铁块基底
        scene.overlay().showText(60)
                .colored(PonderPalette.BLUE)
                .text("第1层：3x3铁块基底")
                .pointAt(util.vector().centerOf(3, 1, 3));
        scene.idle(70);

        // 显示第1层铁块 (位于5x5底座中央的3x3区域)
        for (int x = 2; x <= 4; x++) {
            for (int z = 2; z <= 4; z++) {
                scene.world().showSection(util.select().position(x, 1, z), Direction.DOWN);
                // 添加粒子效果
                Vec3 pos = util.vector().centerOf(x, 1, z);
                scene.effects().emitParticles(
                        pos,
                        scene.effects().simpleParticleEmitter(ParticleTypes.ELECTRIC_SPARK, new Vec3(0, 0.1, 0)),
                        3, 2
                );
                scene.idle(1);
            }
        }
        scene.idle(20);

        // 第2层建造过程
        scene.overlay().showText(60)
                .colored(PonderPalette.GREEN)
                .text("第2层：铁块包围高炉")
                .pointAt(util.vector().centerOf(3, 2, 3));
        scene.idle(70);

        // 显示第2层周围的铁块
        for (int x = 2; x <= 4; x++) {
            for (int z = 2; z <= 4; z++) {
                if (!(x == 3 && z == 3)) {
                    scene.world().showSection(util.select().position(x, 2, z), Direction.DOWN);
                    Vec3 pos = util.vector().centerOf(x, 2, z);
                    scene.effects().emitParticles(
                            pos,
                            scene.effects().simpleParticleEmitter(ParticleTypes.ELECTRIC_SPARK, new Vec3(0, 0.1, 0)),
                            2, 1
                    );
                    scene.idle(1);
                }
            }
        }
        scene.idle(10);

        // 高亮高炉位置
        scene.overlay().showText(50)
                .colored(PonderPalette.RED)
                .attachKeyFrame()
                .text("中心放置高炉")
                .pointAt(util.vector().centerOf(3, 2, 3));
        scene.idle(60);

        // 显示高炉并添加特效
        Vec3 furnacePos = util.vector().centerOf(3, 2, 3);
        scene.effects().emitParticles(
                furnacePos,
                scene.effects().simpleParticleEmitter(ParticleTypes.FLAME, new Vec3(0, 0.2, 0)),
                10, 20
        );
        scene.world().showSection(util.select().position(3, 2, 3), Direction.DOWN);
        scene.idle(20);

        // 第3层：平滑石顶盖
        scene.overlay().showText(60)
                .colored(PonderPalette.BLUE)
                .text("第3层：3x3平滑石顶盖")
                .pointAt(util.vector().centerOf(3, 3, 3));
        scene.idle(70);

        // 显示第3层并添加粒子效果
        for (int x = 2; x <= 4; x++) {
            for (int z = 2; z <= 4; z++) {
                scene.world().showSection(util.select().position(x, 3, z), Direction.DOWN);
                Vec3 pos = util.vector().centerOf(x, 3, z);
                scene.effects().emitParticles(
                        pos,
                        scene.effects().simpleParticleEmitter(ParticleTypes.CLOUD, new Vec3(0, 0.1, 0)),
                        2, 1
                );
                scene.idle(1);
            }
        }
        scene.idle(20);

        // 结构完成特效
        scene.overlay().showText(80)
                .colored(PonderPalette.FAST)
                .attachKeyFrame()
                .text("与普通高炉对比，超级高炉效率更高")
                .pointAt(util.vector().centerOf(3, 2, 3));

        // 完成时的粒子环绕效果
        Vec3 center = util.vector().centerOf(3, 2, 3);
        for (int i = 0; i < 12; i++) {
            double angle = i * Math.PI / 6;
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
                    3, 5
            );
        }

        // 旋转展示
        scene.rotateCameraY(90);
        scene.idle(30);
        scene.rotateCameraY(90);
        scene.idle(30);
        scene.rotateCameraY(90);
        scene.idle(30);
        scene.rotateCameraY(90);
        scene.idle(30);

        scene.markAsFinished();
    }


    public static void init() {
        RedExtendMod.LOGGER.info("Initialized SuperBlastFurnaceScene");
    }
}