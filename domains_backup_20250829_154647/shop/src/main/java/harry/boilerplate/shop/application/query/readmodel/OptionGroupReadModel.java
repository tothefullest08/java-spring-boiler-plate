package harry.boilerplate.shop.application.query.readmodel;

import java.util.List;

/**
 * OptionGroup Read Model
 * 옵션그룹 조회 시 사용되는 불변 데이터 객체
 */
public class OptionGroupReadModel {
    private final String id;
    private final String name;
    private final boolean required;
    private final List<OptionReadModel> options;
    
    public OptionGroupReadModel(String id, String name, boolean required, List<OptionReadModel> options) {
        this.id = id;
        this.name = name;
        this.required = required;
        this.options = options;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public List<OptionReadModel> getOptions() {
        return options;
    }
}