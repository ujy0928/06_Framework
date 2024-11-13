package edu.kh.project.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

// 프로그램 전체적으로 사용된 유용한 기능 모음
public class Utility {

	public static int seqNum = 1; // 1~99999 반복
	
	public static String fileRename(String originalFileName) {
		
		// 20241112100105_00004.jpg
		
		// SimpleDateFormat : 시간을 원하는 형태의 문자열로 간단히 변경
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		
		// java.util.Date() : 현재 시간을 저장한 자바 객체
		String date = sdf.format(new Date());
		
		String number = String.format("%05d", seqNum);
		
		seqNum++; // 1증가
		if(seqNum == 100000) seqNum = 1;
		
		// 확장자 구하기
		// "문자열".substring(인덱스)
		// - 문자열을 인덱스부터 끝까지 잘라낸 결과를 반환
		
		// "문자열".lastIndexOf(".")
		// - 문자열에서 마지막 "."의 인덱스를 반환
		
		String ext = 
				originalFileName.substring(originalFileName.lastIndexOf("."));
		
		return date + "_" + number + ext; // 20241112100105_00004.jpg
	}
}
