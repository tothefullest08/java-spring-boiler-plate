package harry.boilerplate.shop.query.infrastructure.mapper;

import harry.boilerplate.shop.query.application.readModel.*;
import harry.boilerplate.shop.command.domain.aggregate.Menu;
import harry.boilerplate.shop.command.domain.entity.OptionGroup;
import harry.boilerplate.shop.command.domain.valueObject.Option;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Entity → ReadModel 변환 매퍼
 * 도메인 엔티티를 Query용 ReadModel로 변환
 */
@Component
public class MenuReadModelMapper {
    
    /**
     * Menu 엔티티를 MenuSummaryReadModel로 변환
     */
    public MenuSummaryReadModel toSummaryReadModel(Menu menu) {
        return new MenuSummaryReadModel(
            menu.getId().getValue(),
            menu.getShopId().getValue(),
            menu.getName(),
            menu.getDescription(),
            menu.getBasePrice().getAmount(),
            menu.isOpen(),
            menu.getOptionGroups().size()
        );
    }
    
    /**
     * Menu 엔티티를 MenuDetailReadModel로 변환
     */
    public MenuDetailReadModel toDetailReadModel(Menu menu) {
        List<OptionGroupReadModel> optionGroups = menu.getOptionGroups().stream()
            .map(this::toOptionGroupReadModel)
            .collect(Collectors.toList());
            
        return new MenuDetailReadModel(
            menu.getId().getValue(),
            menu.getShopId().getValue(),
            menu.getName(),
            menu.getDescription(),
            menu.getBasePrice().getAmount(),
            menu.isOpen(),
            optionGroups,
            menu.getCreatedAt(),
            menu.getUpdatedAt()
        );
    }
    
    /**
     * OptionGroup을 OptionGroupReadModel로 변환
     */
    public OptionGroupReadModel toOptionGroupReadModel(OptionGroup optionGroup) {
        List<OptionReadModel> options = optionGroup.getOptions().stream()
            .map(this::toOptionReadModel)
            .collect(Collectors.toList());
            
        return new OptionGroupReadModel(
            optionGroup.getId().getValue(),
            optionGroup.getName(),
            optionGroup.isRequired(),
            options
        );
    }
    
    /**
     * Option을 OptionReadModel로 변환
     */
    public OptionReadModel toOptionReadModel(Option option) {
        return new OptionReadModel(
            option.getName(),
            option.getPrice().getAmount()
        );
    }
}