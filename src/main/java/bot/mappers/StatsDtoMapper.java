package bot.mappers;

import bot.JDASingleton;
import bot.dtos.PlayerDto;
import bot.dtos.StatsDto;
import bot.entities.StatsEntity;
import net.dv8tion.jda.api.entities.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import othello.utils.BotUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

public class StatsDtoMapper
{
    private final ModelMapper modelMapper = new ModelMapper();

    public StatsDtoMapper() {
        modelMapper.typeMap(StatsEntity.class, StatsDto.class).addMappings(mapper -> {
            Converter<Long, PlayerDto> converter = (ctx) -> new PlayerDto(ctx.getSource());
            mapper.using(converter).map(StatsEntity::getPlayerId, StatsDto::setPlayer);
        });

        modelMapper.validate();
    }

    public StatsDto map(StatsEntity entity) {
        System.out.println(entity.getPlayerId());
        // fetch tag from jda and assign
        String tag = JDASingleton.fetchUser(entity.getPlayerId()).complete().getAsTag();
        // map entity to dto
        StatsDto dto = modelMapper.map(entity, StatsDto.class);
        dto.getPlayer().setName(tag);
        return dto;
    }

    public List<StatsDto> mapAll(List<StatsEntity> entityList) {
        // fetch each tag from jda using futures
        List<CompletableFuture<User>> futures = new ArrayList<>();
        for (StatsEntity entity : entityList) {
            if (!BotUtils.isBotId(entity.getPlayerId())) {
                futures.add(JDASingleton.fetchUser(entity.getPlayerId()).submit());
            } else {
                futures.add(CompletableFuture.completedFuture(null));
            }
        }
        CompletableFuture.allOf((futures.toArray(new CompletableFuture[0]))).join();
        // map each entity to dto
        List<StatsDto> dtoList = new ArrayList<>();
        for(int i = 0; i < futures.size(); i++){
            // retrieve tag from completed future
            User user = futures.get(i).join();
            String tag = user != null ? user.getAsTag() : BotUtils.getBotName(entityList.get(i).getPlayerId());
            // map entity to dto and add to dto list
            StatsDto dto = modelMapper.map(entityList.get(i), StatsDto.class);
            dto.getPlayer().setName(tag);
            dtoList.add(dto);
        }
        return dtoList;
    }
}
