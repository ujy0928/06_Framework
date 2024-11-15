package edu.kh.project.main.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.main.model.mapper.MainMapper;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MainServiceImpl implements MainService{
	
	private final MainMapper mapper;
	
	private final BCryptPasswordEncoder bcrypt;

	// 멤버 리스트 조회
	@Override
	public List<Member> selectMemberList() {
		
		return mapper.selectMemberList();
	}

	// 멤버 비밀번호 초기화
	@Override
	public int pwReset(int memberNo) {
		String password = bcrypt.encode("pass01!");
		
		Map<String, Object> map = new HashMap<>();
		map.put("memberNo", memberNo);
		map.put("password", password);
		
		return mapper.pwReset(map);
	}
	
	// 회원 탈퇴 복구
	@Override
	public int memberRestore(int memberNo) {
		
		return mapper.memberRestore(memberNo);
	}
}
