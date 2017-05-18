package com.wing.mapp.sample;

import java.io.CharConversionException;
import java.io.IOException;

public interface StudentInfoService {
	String findInfo(String studentName, long no) throws IOException, CharConversionException;
	/*StudentInfoService getNo(int index, StudentInfoService self) throws IOException;
	Float say();*/
}
