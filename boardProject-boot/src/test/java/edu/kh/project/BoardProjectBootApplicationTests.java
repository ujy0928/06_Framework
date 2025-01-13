package edu.kh.project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
// Spring Boot 테스트를 실행하기 위한 어노테이션
// 스프링 애플리케이션 컨텍스트를 로드하고, 애플리케이션의 전체 환경을 테스트 할 수 있게 해줌
class BoardProjectBootApplicationTests {

	@Test
	void contextLoads() {
		// 애플리케이션 컨텍스트가 제대로 로드되는지 확인하는 기본 테스트를 수행 메서드
		System.out.println("애플리케이션 컨텍스트 로드 완료");
	}

}
