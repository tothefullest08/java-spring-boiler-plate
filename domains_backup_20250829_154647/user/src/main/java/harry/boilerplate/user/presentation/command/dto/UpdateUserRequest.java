package harry.boilerplate.user.presentation.command.dto;

import harry.boilerplate.user.application.command.dto.UpdateUserCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 사용자 정보 수정 요청 DTO
 * Requirements 7.1: 사용자 정보 수정 API 요청
 */
@Schema(description = "사용자 정보 수정 요청")
public class UpdateUserRequest {
    
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
    public UpdateUserRequest() {}
    
    public UpdateUserRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    public UpdateUserCommand toCommand(String userId) {
        return new UpdateUserCommand(userId, this.name, this.email);
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
        return "UpdateUserRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}