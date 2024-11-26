package edu.kh.project.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


/*
 * @SessionAttributes({"key", "key", "key" ...})
 * - Model에 추가된 속성 중
 *   key값이 일치하는 속성을 session scope로 변경
 * 
 * */


@SessionAttributes({"loginMember"})
@Controller
@Slf4j
@RequestMapping("member")
public class MemberController {
	
	@Autowired
	private MemberService service;
	
	/*
	 * [로그인]
	 * - 특정 사이트에 아이디/비밀번호 등을 입력해서
	 *   해당 정보가 있으면 조회/서비스 이용
	 *   
	 * - 로그인 한 회원 정보를 session에 기록하여
	 *   로그아웃 또는 브라우저 종료(탭 종료X) 시 까지
	 *   해당 정보를 계속 이용할 수 있게 함
	 * 
	 * */
	
	/** 로그인
	 * @param inputMember : 커맨드 객체 (@ModelAttribute 생략)
	 * @param ra : 리다이렉트 시 request scope로 데이터 전달하는 객체(request -> session -> request)
	 * @param model : 데이터 전달용 객체 (기본 request scope 
	 * 				/ @SessionAttributes 어노테이션과 함께 사용시 session scope 이동)
	 * @param saveId
	 * @param resp
	 * @return
	 */
	@PostMapping("login")
	public String login(Member inputMember, 
						RedirectAttributes ra,
						Model model,
						@RequestParam(value="saveId", required=false) String saveId, 
						HttpServletResponse resp
			) {
		//체크박스
		//- 체크가 된 경우 : "on"
		//- 체크가 안된 경우 : null
		
		// 로그인 서비스 호출
		try {
			Member loginMember = service.login(inputMember);
			
			// 로그인 실패 시 
			if(loginMember == null) {
				ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
			} else {
				// Session scope에 loginMember 추가
				model.addAttribute("loginMember", loginMember);
				// 1단계 : request scope 에 세팅됨
				// 2단계 : 클래스 위에 @SessionAttribute() 어노테이션 작성하여 
				//         session scope로 이동
				// cookie를 사용한 이메일 저장
				// 쿠키 객체 생성 (K:V)
				Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail());
				// saveId=user01@kh.or.kr
				// 쿠키가 적용될 경로 설정
				//-> 클라이언트가 어떤 요청을 할 떄 쿠키가 첨부될지 지정
				//ex) "/" : IP 또는 도메인 또는 localhost
				// --> 메인페이지 + 그 하위 주소 모두
				cookie.setPath("/");
				// 쿠키의 만료 기간 지정
				if(saveId != null) { // 아이디 저장 체크 시
					cookie.setMaxAge(60 * 60 * 24 * 30); //30일 초 단위로 지정
				} else { //미체크 시
					cookie.setMaxAge(0);
				}
				
				// 응답 객체에 쿠키 추가 -> 클라이언트 전달
				resp.addCookie(cookie);
			}
		} catch(Exception e) {
			e.printStackTrace();
			log.info("로그인 중 예외 발생 try-catch로 예외처리");
		}
		
		
		return "redirect:/";
	}
	
	
	/** 로그아웃 : session 저장된 로그인된 회원 정보를 없앰
	 * @param sessionStatus : @SessionAttributes로 지정된 특정 속성을 세션에서 제거 기능 제공 객체
	 * @return
	 */
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		
		status.setComplete(); // 세션을 완료 시킴 (== 세션에서 @SessionAttributes로 등록된 세션 제거)
		
		return "redirect:/";
	}
	
	/** 회원 가입
	 * @return
	 */
	@GetMapping("signup")
	public String signupPage() {
		return "member/signup";
	}
	
	/**이메일 중복검사
	 * @return
	 */
	@ResponseBody //응답 본문(fetch)으로 돌려보냄
	@GetMapping("checkEmail") // Get요청 /member/checkEmail
	public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		return service.checkEmail(memberEmail);
	}
	
	/** 닉네임 중복 검사
	 * @return 중복 1, 아님 0
	 */
	@ResponseBody
	@GetMapping("checkNickname")
	public int checkNickname(@RequestParam("memberNickname") String memberNickname) {
		return service.checkNickname(memberNickname);
	}
	
	/** 회원 가입
	 * @param inputMemeber : 입력된 회원 정보(memberEmail, memberPw, memberNickname, memberTel,
	 * 										(memberAddress - 따로 배열로 받아서 처리))
	 * @param memerAddress : 입력한 주소 input 3개의 값을 배열로 전달[우편번호, 도로명/지번주소, 상세주소]
	 * @param ra : 리다이렉트 시 request scope로 데이터를 전달하는 객체
	 * @return
	 */
	@PostMapping("signup")
	public String signup(Member inputMember,
						@RequestParam("memberAddress") String[] memberAddress,
						RedirectAttributes ra) {
		log.debug("inputMember : " + inputMember);
		
		// 회원가입 서비스 호출
		int result = service.signup(inputMember, memberAddress);
		
		String path = null;
		String message = null;
		
		if(result > 0) { //성공 시 
			message = inputMember.getMemberNickname() + "님의 가입을 환영 합니다!";
			path = "/";
		} else { // 실패
			message = "회원 가입 실패...";
			path = "signup";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:" + path;
		// 성공 -> redirect:/
		// 실패 -> redirect:signup (상대경로)
				// 현재 주소 /member/signup (GET 방식 요청)
	}

}
