package edu.kh.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.common.TestService;

// JUnit
// Java 애플리케이션에서 테스트 코드를 작성하고 실행하기 위한 가장 널리 사용되는 테스트 프레임워크
// 통합/단위 테스트 지원 : 각 메서드의 동작을 독립적으로 테스트할 수 있는 환경을 제공
// 어노테이션 기반 : @Test, @BeforeEach, @AfterEach, @DisplayName...
// 대부분의 Java 개발 환경에서 기본 제공함

@SpringBootTest // Spring Boot 컨텍스트를 로드하여  애플리케이션의 전반적인 테스트를 수행할 수 있도록 설정
@Transactional // 각각의 테스트 메서드에 대해 트랜잭션을 시작하고, 테스트가 종료되면 롤백(데이터 베이스 사용 시)
public class AppTest {

	@Autowired
	private TestService testService;
	// 내가 테스트할 코드가 작성된 class를 주입받아 테스트에서 이용
	
	// 매 테스트 실행 전 반복 호출(각 테스트마다 실행)
	@BeforeEach // 테스트 전처리
	public void before() {
		System.out.println("Test Before");
	}

	// 매 테스트 실행 후 반복 호출(각 테스트마다 실행)
	@AfterEach // 테스트 후처리
	public void after() {
		System.out.println("Test After");
	}
	
	// 모든 테스트 메소드들이 실행되기 전에 딱 한 번만 실행
	// * static 메서드에서만 사용 가능 *
	@BeforeAll
	public static void testStart() {
		System.out.println("테스트 시작");
		// 데이터베이스 연결
		// 테스트 데이터 초기화
		// 공통 리소스 설정 등..
	}
	
	// 모든 테스트 메도들이 실행된 후에 딱 한 번만 실행
	// * static 메서드에서만 사용 가능 *
	@AfterAll
	public static void testComplete() {
		System.out.println("모든 테스트 완료");
		// 데이터베이스 연결 종료
		// 사용한 리소스 정리 등..
	}
	
	/*
	 * BDD (Behavior-Driven Development) : 소프트웨어 개발 방법론 중 하나로, 소프트웨어의 행동(Behavior)에
	 * 초점을 맞춰 개발을 진행하는 방식.
	 * 
	 * BDD 스타일에 의한 테스트 메서드의 구성은 Given, When, Then으로 구성됨. 
	 * - Given: 주어진 상황을 설정하는 부분. 
	 * - When: 테스트할 기능을 호출하고 실행하는 부분.  
	 * - Then: 기대한 결과를 검증하는 부분. 특정한 결과나 동작이 예상대로 수행되었는지 확인.
	 */

	
	
	/*
	 * JUnit 제공 메서드(간단한 값 비교 시 적합함)
	 * 					
	 * [작성법]
	 * assertEquals(예상값, 실제값); : 예상 값과 실제 값이 같으면 테스트 통과
	 * 
	 * assertNotEquals(예상값, 실제값); : 예상 값과 실제 값이 다르면 테스트 통과
	 * 
	 * assertThrows(발생할예외클래스, 예외를발생시킬 람다표현식); : 예외가 발생하는지 검증할 때 사용하는 메서드
	 * 람다표현식(Lambda Expression) : 함수형 프로그래밍을 지원하는 문법 
	 * [작성법] : (매개변수...) -> { 실행문 }
	 * 주의사항 ! 일반적인 클래스를 람다식으로 사용 X 
	 *  		 함수형 인터페이스를 구현할 때 사용가능 (메소드가 하나만 있는 인터페이스)
	 * 
	*/
	
	// 테스트 메서드
	@Test
	@DisplayName("두 수가 일치해야 성공")
	public void test() {
		// Given : 유효한 입력 데이터 설정
		int number = 0;
		
		// When : 테스트 대상 메서드를 호출
		int result = testService.checkZero();
		
		// Then : 결과 검증
		// assertEquals(예상값, 실제값)
		assertEquals(number, result);
	}
	
	@Test
	@DisplayName("두 수가 일치하지 않아야 성공")
	public void test2() {
		// Given
		int number = 100;
		
		// When
		int result = testService.checkZero();
		
		// Then
		// assertNotEquals(예상값, 실제값);
		assertNotEquals(number, result);
	}
	
	@Test
	@DisplayName("0으로 나누기 예외 테스트")
	public void test3() {
		assertThrows(IllegalArgumentException.class, () -> testService.divide(5, 0));
	}
	
}
