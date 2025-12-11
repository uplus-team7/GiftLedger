package springboot.giftledger.common.dto;

import lombok.Builder;
import lombok.Getter;
import springboot.giftledger.event.dto.EventUpdateResponse;

@Builder
@Getter
public class ExceptionResult {

		private String result;
		private String message;
	
}
