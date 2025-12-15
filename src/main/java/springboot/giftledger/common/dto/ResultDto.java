package springboot.giftledger.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import springboot.giftledger.entity.Event;

@AllArgsConstructor
@Getter
public class ResultDto<T> {

	private String result;
	private T data;
	
	public static<T> ResultDto<T> of (String result, T data){
		return new ResultDto<>(result, data);
	}
	
}
