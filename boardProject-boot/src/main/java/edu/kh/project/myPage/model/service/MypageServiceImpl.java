package edu.kh.project.myPage.model.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.common.util.Utility;
import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;
import edu.kh.project.myPage.model.mapper.MypageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor = Exception.class) // 모든 예외 발생 시 롤백 / 아니면 커밋
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:/config.properties")
public class MypageServiceImpl implements MypageService{

	private final MypageMapper mapper;
	
	// Bcrypt 암호화 객체 의존성 주입(SecurityConfig 참고)
	private final BCryptPasswordEncoder bcrypt;
	
	@Value("${my.profile.web-path}")
	private String profileWebPath; // /myPage/profile/
	
	@Value("${my.profile.foler-path}")
	private String profileFolerPath; // C:/uploadFiles/profile/

	@Override
	public int updateInfo(Member inputMember, String[] memberAddress) {
		
		// 입력된 주소가 있을 경우
		// memberAddress A^^^B^^^C 형태로 가공
		
		// 주소 입력 X -> inputMember.getMemberAddress() -> ",,"
		if(inputMember.getMemberAddress().equals(",,")) {
			// 주소에 null 대입
			inputMember.setMemberAddress(null);
		} else { //주소 입력 O
			String address = String.join("^^^", memberAddress);
			inputMember.setMemberAddress(address);
		}
		
		return mapper.updateInfo(inputMember);
	}

	// 비밀번호 변경 서비스
	@Override
	public int changePw(Map<String, Object> paramMap, int memberNo) {
		
		String originPw = mapper.selectPw(memberNo);
		
		// 다를 경우
		if(!bcrypt.matches((String)paramMap.get("currentPw"), originPw)) {
			return 0;
		}
		
		// 새 비밀번호 암호화
		String encPw = bcrypt.encode((String)paramMap.get("newPw"));
		
		// 진행후 DB에 업데이트
		// SQL 전달해야하는 데이터 2개 (암호화한 새 비밀번호, 회원번호)
		// -> SQL 전달 인자 1개뿐!
		// -> 묶어서 전달 (paramMap 재활용)
		
		paramMap.put("encPw", encPw);
		paramMap.put("memberNo", memberNo);
		
		return mapper.changePw(paramMap);
	}

	//  회원 탈퇴
	@Override
	public int secession(String memberPw, int memberNo) {
		
		// 현재 로그인한 회원의 암호화된 비밀번호 DB에서 조회
		String originPw = mapper.selectPw(memberNo);
		
		// 다를 경우
		if(!bcrypt.matches(memberPw, originPw)) {
			return 0;
		}
		
		// 같을 경우		
		return mapper.secession(memberNo);
	}

	@Override
	public String fileUpload1(MultipartFile uploadFile) throws Exception {
		// MultipartFile이 제공하는 메소드
		// - getSize() : 파일 크기
		// - isEmpty() : 업로드한 파일이 없을 경우 true / false
		// - getOriginalFileName() : 원본 파일명
		// - transferTo(경로) : 
		//	 메모리 또는 임시 저장 경로에 업로드된 파일을
		//   원하는 경로에 실제로 전송(서버 어떤 폴더에 저장할지 지정)
		
		if(uploadFile.isEmpty()) { // 업로드한 파일이 없을 경우
			return null;
		}
		
		// 업로드한 파일이 있을 경우
		// C:/uploadFiles/test/파일명 으로 서버에 저장
		uploadFile.transferTo(new File("C:/uploadFiles/test/" + uploadFile.getOriginalFilename()));
		
		// 웹 에서 해당 파일에 접근할 수 있는 경로를 반환
		
		// 서버 : C:/uploadFiles/test/A.jpg
		// 웹 접근 주소 : /myPage/file/A.jpg
		
		return "/myPage/file/" + uploadFile.getOriginalFilename();
	}

	// 파일 업로드 테스트2 (+DB)
	@Override
	public int fileUpload2(int memberNo, MultipartFile uploadFile) throws Exception {
		
		// 업로드된 파일이 없다면
		// == 선택된 파일이 없을 경우
		if(uploadFile.isEmpty()) {
			return 0;
		}
		
		/* DB에 파일 저장이 가능은 하지만
		 * DB 부하를 줄이기 위해서
		 * 
		 * 1) DB에는 서버에 저장할 파일 경로를 저장
		 * 2) DB 삽입/수정 성공 후 서버에 파일을 저장
		 * 3) 만약에 파일 저장 실패 시
		 *   -> 예외 발생
		 *   -> @Transactional 을 이용해서 rollback 수행
		 * 
		 * */
		
		// 1. 서버에 저장할 파일 경로 만들기
		// 파일이 저장될 서버 폴더 경로
		String folerPath = "C:/uploadFiles/test/";
		
		// 클라이언트가 파일이 저장된 폴더에 접근할 수 있는 주소(정적리소스 요청 주소)
		String webPath = "/myPage/file/";
		
		// 2. DB에 전달할 데이터를 DTO 로 묶어서 INSERT 호출하기
		// webPath, memberNo, 원본 파일명, 변경된 파일명
		String fileRename = Utility.fileRename(uploadFile.getOriginalFilename());
		
		//log.debug("fileRename : " + fileRename);
		
		// Builder 패턴을 이용해서 UploadFile 객체 생성
		// 장점 1) 반복되는 참조변수명, set 구문 생략
		// 장점 2) method chaining을 이용하여 한 줄로 작성 가능
		UploadFile uf = UploadFile.builder()
						.memberNo(memberNo)
						.filePath(webPath)
						.fileOriginalName(uploadFile.getOriginalFilename())
						.fileRename(fileRename)
						.build();
		
		int result = mapper.insertUploadFile(uf);
		
		// 3. 삽입 (INSERT) 성공 시 파일을 지정된 서버 폴더에 저장
		
		// 삽입 실패 시
		if(result == 0) return 0;
		
		// 삽입 성공 시
		
		// C:/uploadFiles/test/변경된파일명 으로
		// 파일을 서버 컴퓨터에 저장!
		uploadFile.transferTo(new File(folerPath + fileRename));
		
		return result;
	}

	// 파일 목록 조회 서비스
	@Override
	public List<UploadFile> fileList(int memberNo) {
		
		return mapper.fileList(memberNo);
	}

	// 여러 파일 업로드 서비스
	@Override
	public int fileUpload3(List<MultipartFile> aaaList, 
			List<MultipartFile> bbbList, 
			int memberNo) throws Exception{
		
		// 1. aaaList 처리
		int result1 = 0;
		
		// 업로드된 파일이 없을 경우를 제외하고 업로드
		for(MultipartFile file : aaaList) {
			if(file.isEmpty()) { // 파일이 없으면 다음 파일
				continue;
			}
			// fileUpload2() 메서드 호출(재활용)
			// -> 파일 하나ㅏ 업로드 + DB INSERT
			result1 += fileUpload2(memberNo, file);
		}
		
		// 2. bbbList 처리
		int result2 = 0;
		
		// 업로드된 파일이 없을 경우를 제외하고 업로드
		for(MultipartFile file : bbbList) {
			if(file.isEmpty()) {
				continue;
			}
			
			result2 += fileUpload2(memberNo, file);
		}
		
		
		return result1 + result2;
	}

	// 프로필 이미지 변경 서비스
	@Override
	public int profile(MultipartFile profileImg, Member loginMember) throws Exception {
		
		// 프로필 이미지 경로 (수정할 경로)
		String updatePath = null;
		
		// 변경명 저장
		String rename = null;
		
		// 업로드한 이미지가 있을 경우
		// - 있을 경우 : 경로 조합 (클라이언트 접근 경로 + 리네임파일명)
		if(!profileImg.isEmpty()) {
			// updatePath 경로 조합
			
			// 1. 파일명 변경
			rename = Utility.fileRename(profileImg.getOriginalFilename());
			
			// 2. /myPage/profile/변경된파일명
			updatePath = profileWebPath + rename;
		}
		
		// 수정된 프로필 이미지 경로 + 회원 번호를 저장할 DTO 객체
		Member mem = Member.builder()
				.memberNo(loginMember.getMemberNo())
				.profileImg(updatePath)
				.build();
		
		// UPDATE 수행
		int result = mapper.profile(mem);
		
		if(result > 0) { // DB에 수정 성공
			// 프로필 이미지를 없앤 경우(NULL로 수정한 경우)를 제회
			// -> 업로드한 이미지가 있을 경우
			if(!profileImg.isEmpty()) {
				// 파일을 서버 지정된 폴더에 저장
				profileImg.transferTo(new File(profileFolerPath + rename));
				  				// C:/uploadFiles/profile/변경한이름
			}
			// 세션 회원 정보에서 프로필 이미지 경로를 
			// 업데이트한 경로로 변경
			loginMember.setProfileImg(updatePath);
		} 
					
		
		return result;
	}

	
	
	
	
	
	
	
	
	
	
}
