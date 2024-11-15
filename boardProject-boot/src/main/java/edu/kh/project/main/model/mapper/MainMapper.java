package edu.kh.project.main.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MainMapper {

	/** 메인 페이지 멤버 리스트 조회
	 * @return List<Member>
	 */
	List<Member> selectMemberList();

	/** 멤버 비밀번호 초기화
	 * @param map
	 * @return result
	 */
	int pwReset(Map<String, Object> map);

	/** 회원 탈퇴 복구
	 * @param memberNo
	 * @return
	 */
	int memberRestore(int memberNo);

}
