package edu.kh.project.common;

import org.springframework.stereotype.Service;

@Service
public class TestService {
	
	public int checkZero() {
		return 0;
	}
	
	public int divide(int a, int b) {
		
		if(b == 0 ) {
			throw new IllegalArgumentException("0으로 나눌 수 없음");
		}
		
		return a / b;
	}
	
}
