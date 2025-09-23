package kr.hhplus.be.infrastructure.sse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SseEmitterService {

	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	public SseEmitter addEmitter(String taskId) {
		SseEmitter emitter = new SseEmitter(5 * 60 * 1000L); // 5분
		this.emitters.put(taskId, emitter);
		log.info("SSE Emitter 추가: {}", taskId);

		emitter.onCompletion(() -> removeEmitter(taskId));
		emitter.onTimeout(() -> removeEmitter(taskId));

		return emitter;
	}

	public void removeEmitter(String taskId) {
		this.emitters.remove(taskId);
		log.info("SSE Emitter 제거: {}", taskId);
	}

	public void sendToUser(String taskId, String eventName, Object data) {
		SseEmitter emitter = emitters.get(taskId);

		if (emitter != null) {
			try {
				emitter.send(SseEmitter.event().name(eventName).data(data));
				log.info("SSE 메시지 전송 성공. taskId: {}, event: {}", taskId, eventName);
				emitter.complete();
			} catch (IOException e) {
				log.error("SSE 메시지 전송 실패. taskId: {}", taskId, e);
				removeEmitter(taskId);
			}
		} else {
			log.info("SSE 메시지 전송 대상 없음 (클라이언트 미연결). taskId: {}", taskId);
		}
	}
}