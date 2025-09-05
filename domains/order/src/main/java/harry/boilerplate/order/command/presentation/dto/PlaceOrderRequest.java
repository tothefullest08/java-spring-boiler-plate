package harry.boilerplate.order.command.presentation.dto;

import harry.boilerplate.order.command.application.dto.PlaceOrderCommand;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 주문 생성 요청 DTO
 * Requirements: 6.1, 6.2
 */
@Schema(description = "주문 생성 요청")
public class PlaceOrderRequest {
    
    // 현재는 사용자 ID만 필요하며, 이는 헤더나 인증 정보에서 추출
    // 추후 배송 주소, 결제 정보 등이 추가될 수 있음
    
    // 기본 생성자
    public PlaceOrderRequest() {}
    
    /**
     * Command 객체로 변환
     */
    public PlaceOrderCommand toCommand(String userId) {
        return new PlaceOrderCommand(userId);
    }
}