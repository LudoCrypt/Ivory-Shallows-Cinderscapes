package com.terraformersmc.cinderscapes.decorator;

import com.terraformersmc.cinderscapes.decorator.config.CountSafelistRangeDecoratorConfig;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorContext;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CountSafelistRangeFloorDecorator extends Decorator<CountSafelistRangeDecoratorConfig> {
    public CountSafelistRangeFloorDecorator() {
        super(CountSafelistRangeDecoratorConfig.CODEC);
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random random, CountSafelistRangeDecoratorConfig config, BlockPos pos) {
        return IntStream.range(0, config.count).mapToObj((i) -> {
            int x = random.nextInt(16) + pos.getX();
            int z = random.nextInt(16) + pos.getZ();
            BlockPos.Mutable testPos = new BlockPos.Mutable(x, 0, z);
            BlockPos.Mutable offsetPos = new BlockPos.Mutable(x, 0, z);
            List<Integer> ys = IntStream.range(config.bottomOffset, config.maximum - config.topOffset).filter((y) -> {
                testPos.setY(y);
                offsetPos.setY(y - 1);

                BlockState testState = context.getBlockState(testPos);
                BlockState offsetState = context.getBlockState(offsetPos);

                return testState.isAir() && offsetState.isOpaque() && config.safelist.contains(offsetState);
            }).boxed().collect(Collectors.toList());
            if (ys.size() <= 0) return new BlockPos(x, 0, z);
            testPos.setY(ys.get(random.nextInt(ys.size())));
            return testPos;
        });
    }
}
