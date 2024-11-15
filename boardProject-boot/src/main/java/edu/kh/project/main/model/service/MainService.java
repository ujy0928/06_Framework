package edu.kh.project.main.model.service;

import java.util.List;

import edu.kh.project.member.model.dto.Member;

public interface MainService {

	/** 메인 페이지 멤버 리스트 조회
	 * @return List<Member>
	 */
	List<Member> selectMemberList();

	/** 패스워드 초기화
	 * @param memberNo
	 * @return result
	 */
	int pwReset(int memberNo);

	/** 회원 복구
	 * @param int1
	 * @return result
	 */
	int memberRestore(int memberNo);

}
