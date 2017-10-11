package com.wing.mapp.sample;

import java.io.CharConversionException;
import java.io.IOException;
public class StudentInfoServiceImpl implements StudentInfoService{
	/*public StudentInfoService getNo(int index, StudentInfoService self) throws IOException {
		System.out.println("no " + index);
		self.findInfo("阿呆和阿瓜", 3);
		return this;
	}*/
	public String findInfo(String name, long no) throws IOException, CharConversionException {
		System.out.println("你目前输入的名字是:" + name);
		System.out.println("id " + no);
		//if(true) throw new CharConversionException();
		//throw new IOException("error");
		return "hailonglvmin|"+name;
	}
	/*public Float say() {
		System.out.println("ok");
		System.out.println("index " + 123.445);
		return new Float(123.445);
	}*/
}
