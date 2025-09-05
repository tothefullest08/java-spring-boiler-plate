package harry.boilerplate.user.command.presentation.dto;

import harry.boilerplate.user.command.application.dto.CreateUserCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 사용자 생성 요청 DTO
 * Requirements 7.1: 사용자 생성 API 요청
 */
@Schema(description = "사용자 생성 요청")
public class CreateUserRequest {
    
    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    @NotBlank(message = "사용자 이름은 필수입니다")
    @Size(max = 100, message = "사용자 이름은 100자 이하여야 합니다")
    private String name;
    
    @Schema(description = "이메일 주소", example = "hong@example.com", required = true)
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 255, message = "이메일은 255자 이하여야 합니다")
    private String email;
    
    // 기본 생성자 (JSON 역직렬화용)
    public CreateUserRequest() {}
    
    public CreateUserRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    public CreateUserCommand toCommand() {
        return new CreateUserCommand(this.name, this.email);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return "CreateUserRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}