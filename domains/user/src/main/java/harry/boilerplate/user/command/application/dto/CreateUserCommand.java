package harry.boilerplate.user.command.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 사용자 생성 Command DTO
 * Requirements 7.1: 사용자 생성 기능
 */
public class CreateUserCommand {
    
    @NotBlank(message = "사용자 이름은 필수입니다")
    @Size(max = 100, message = "사용자 이름은 100자 이하여야 합니다")
    private final String name;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 255, message = "이메일은 255자 이하여야 합니다")
    private final String email;
    
    public CreateUserCommand(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    @Override
    public String toString() {
        return "CreateUserCommand{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}