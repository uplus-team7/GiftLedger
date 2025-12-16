package springboot.giftledger.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import springboot.giftledger.event.dto.EventResultDto;
import springboot.giftledger.event.service.EventService;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class deleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    EventResultDto setResultData() {
        return EventResultDto.builder()
                .result("success")
                .build();
    }

    @Test
    @DisplayName("이벤트(선물) 삭제 성공 테스트")
    @WithMockUser(username = "user3@test.com", roles = "USER")
    void deleteEvent_success() throws Exception {
        // [Given]
        Long giftId = 1L;
        EventResultDto mockResponse = setResultData();

        given(eventService.deleteEvent(any(), any()))
                .willReturn(mockResponse);

        // [When] DELETE 요청
        MvcResult result = mockMvc.perform(delete("/events/{giftId}", giftId)
                        .with(csrf()) // [중요] DELETE는 상태를 바꾸므로 CSRF 필수
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // [Then]
        String jsonString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        EventResultDto responseBody = objectMapper.readValue(jsonString, EventResultDto.class);

        assertEquals("success", responseBody.getResult());
    }
}
