package edu.kh.project.email.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailMapper {

	int updateAuthKey(Map<String, String> map);

	/** 이메일과 인증번호 삽입
	 * @param map {email, authKey}
	 * @return int 행의 갯수
	 */
	int insertAuthKey(Map<String, String> map);

	/** 입력받은 이메일, 인증번호가 DB에 있는지 조회
	 * @param map {email, authKey}
	 * @return
	 */
	int checkAuthKey(Map<String, String> map);

}
