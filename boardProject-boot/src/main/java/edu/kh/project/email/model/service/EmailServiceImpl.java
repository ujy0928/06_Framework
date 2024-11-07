package edu.kh.project.email.model.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import edu.kh.project.email.model.mapper.EmailMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

	private final EmailMapper mapper; 
	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine; //SpringTemplateEngine : 타임리프(템플릿 엔진)를 이용해서 html 코드-> java코드 변환
	
	
	@Override
	public String sendEmail(String htmlName, String email) {
		
		// 1. 인증키 생성 및 DB 저장
		String authKey = createAuthKey();
		log.debug("authKey : " + authKey);
		
		Map<String, String> map = new HashMap<>();
		map.put("authKey", authKey);
		map.put("email", email);
		
		log.debug("map : " + map);
		
		// DB 저장 시도 - 실패 시 해당 메서드 종료
		if(!storeAuthKey(map)) {
			return null;
		}
		
		// 이메일 발송 코드 작성
		//2. DB 저장이 성공된 경우에만 메일 발송 시도
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		// 메일 발송 시 사용하는 객체
		
		// 메일 발송을 도와주는 Helper 클래스 (파일첨부, 템플릿 설정 등 쉽게 처리)
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			// mimeMessage : MimeMessage 객체로, 이메일 메시지의 내용을 담고있음
			// 			이메일의 본문, 제목, 수신자 정보 등 포함
			// - true : 파일 첨부를 사용할 것인지 여부 지정(파일첨부 및 내부 이미지 삽입 가능)
			// - "UTF-8" : 이메일 내용이 "UTF-8" 인코딩으로 전송
			
			// 메일 기본 정보 설정
			helper.setTo(email); // 받는 사람(수신자)
			helper.setSubject("[boardProject] 회원 가입 인증번호 입니다.");
			helper.setText(loadHtml(authKey, htmlName), true); // HTML 내용 설정
			// 인증번호입니다 : 
			//helper.setText("인증번호입니다 : " + authKey);
			// 메일에 이미지 첨부(로고)
			helper.addInline("logo", new ClassPathResource("static/images/logo.jpg"));
		
			// 실제 메일 발송
			mailSender.send(mimeMessage);
			
			return authKey; // 모든 작업 성공시 인증키 반환
		
		} catch (MessagingException e) {
			e.printStackTrace();
			return null; //메일 발송 실패 시 null 반환
		}		
	}


	// HTML 템플릿에 데이터를 바인딩하여 최종 HTML 생성
	private String loadHtml(String authKey, String htmlName) {
		// org.thymeleaf.context.Context
		// Context : Thymeleaf에서 제공하는 HTML 템플릿에 데이터를 전달하기 위해 사용하는 클래스
		Context context = new Context();
		context.setVariable("authKey", authKey);
		return templateEngine.process("email/" + htmlName, context);
	}


	// 인증키와 이메일을 DB에 저장하는 메서드
	@Transactional(rollbackFor = Exception.class) // 메서드 레벨에서도 이용 가능(해당 메서드에서만 트랜잭션 커밋/롤백)
	private boolean storeAuthKey(Map<String, String> map) {
		
		// 1. 기존 이메일에 대한 인증키 update 수행
		int result = mapper.updateAuthKey(map);
		
		// 2. update 실패(== 기존 데이터 없음) 시 insert 수행
		if(result == 0) {
			result = mapper.insertAuthKey(map);
		}
		
		return result > 0; // 성공 여부 반환(true/false)
		
	}


	// 인증번호 발급 메서드
	// UUID를 사용하여 인증키 생성
	// (Universally Unique IDentifier) : 전 세계에서 고유한 식별자를 생성하기 위한 표준
	// 매우 낮은 확률로 중복되는 식별자르르 생성
	// 주로 데이터베이스 기본 키, 고유한 식별자를 생성해야할 때 사용
	private String createAuthKey() {
		// TODO Auto-generated method stub
		return UUID.randomUUID().toString().substring(0, 6);
	}


	// 입력받은 이메일, 인증번호가 DB에 있는지 조회
	@Override
	public int checkAuthKey(Map<String, String> map) {
		// TODO Auto-generated method stub
		return mapper.checkAuthKey(map);
	}
	
	
}
