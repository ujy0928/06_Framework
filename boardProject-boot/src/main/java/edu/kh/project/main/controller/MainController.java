package edu.kh.project.main.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.main.model.service.MainService;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
	private final MainService service;
	
	@RequestMapping("/") //	"/" 요청 매핑
	public String mainPage() {
		
		// 접두사/접미사 제외
		// classpath:/templates/
		//.html
		return "common/main";
	}
	
	// LoginFilter -> loginError 리다이렉트
	// -> message 만들어서 메인페이지로 리다이렉트
	@GetMapping("loginError")
	public String loginError(RedirectAttributes ra) {
		ra.addFlashAttribute("message", "로그인 후 이용해 주세요");
		return "redirect:/";
	}
	
	
	@ResponseBody
	@RequestMapping("main/list")
	public List<Member> memberList() {
		
		List<Member> memberList = service.selectMemberList();
		
		return memberList;
	}
	
	@ResponseBody
	@RequestMapping("main/pwReset")
	public int pwReset(@RequestBody int memberNo) {
		
		int result = service.pwReset(memberNo);
		
		
		return result;
	}
	
	@ResponseBody
	@PutMapping("main/restore")
	public int restore(@RequestBody int memberNo) {
		
		int result = service.memberRestore(memberNo);
		
		return result;
	}
	
}
